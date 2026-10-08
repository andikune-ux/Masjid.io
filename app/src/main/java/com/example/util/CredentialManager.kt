package dev.andikune.masjidio.util

import android.content.Context
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * CredentialManager — V1.04.429
 *
 * Mengelola kredensial sensitif (Token Fonnte, Group ID, GitHub PAT):
 *   - Enkripsi AES-256-GCM dengan passphrase terpisah
 *   - Simpan file terenkripsi di HP
 *   - Sync ke GitHub Gist (Secret)
 *
 * Passphrase TIDAK PERNAH ada di source code.
 * User yang menentukan passphrase-nya sendiri saat pertama setup.
 *
 * Kalau passphrase lupa → tidak bisa decrypt → harus reset credentials.
 */
object CredentialManager {

    private const val TAG = "CredentialManager"

    // ============================================================
    // FILE & PREFS
    // ============================================================
    private const val CREDENTIALS_FILE = "credentials.enc"
    private const val PREFS_NAME = "masjid_io_credentials"
    private const val KEY_PASSPHRASE_HASH = "passphrase_hash"
    private const val KEY_GIST_ID = "gist_id"
    private const val KEY_HAS_PASSPHRASE = "has_passphrase"

    // ============================================================
    // KRIPTOGRAFI
    // ============================================================
    private const val PBKDF2_ITERATIONS = 10000
    private const val PBKDF2_KEY_LENGTH = 256      // 256-bit
    private const val SALT_LENGTH = 16              // 16 byte
    private const val IV_LENGTH = 12                // 12 byte (GCM)
    private const val GCM_TAG_LENGTH = 128          // 128-bit tag

    // ============================================================
    // CACHE MEMORY
    // ============================================================
    @Volatile private var cachedToken: String = ""
    @Volatile private var cachedGroupId: String = ""
    @Volatile private var cachedPat: String = ""
    @Volatile private var isInitialized: Boolean = false
    @Volatile private var activePassphrase: String? = null  // HANYA di memory, tidak pernah ditulis ke disk

    // ============================================================
    // DATA CLASS
    // ============================================================
    data class Credentials(
        val fonnteToken: String = "",
        val fonnteGroupId: String = "",
        val githubPat: String = ""
    )

    // ============================================================
    // INIT — Dipanggil saat app buka
    // Cek apakah passphrase sudah di-set sebelumnya
    // ============================================================
    fun init(context: Context) {
        isInitialized = true
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hasPassphrase = prefs.getBoolean(KEY_HAS_PASSPHRASE, false)
        Log.d(TAG, "Init: hasPassphrase=$hasPassphrase")
        // Cache tetap kosong sampai user input passphrase
    }

    // ============================================================
    // CEK APAKAH PASSPHRASE SUDAH PERNAH DI-SET
    // ============================================================
    fun hasPassphrase(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_HAS_PASSPHRASE, false)
    }

    // ============================================================
    // SET PASSPHRASE BARU (pertama kali)
    // Return true kalau berhasil
    // ============================================================
    fun setPassphrase(context: Context, passphrase: String): Boolean {
        if (passphrase.length < 12) {
            Log.w(TAG, "Passphrase terlalu pendek: ${passphrase.length}")
            return false
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hash = hashPassphrase(passphrase)
        prefs.edit()
            .putString(KEY_PASSPHRASE_HASH, hash)
            .putBoolean(KEY_HAS_PASSPHRASE, true)
            .apply()

        activePassphrase = passphrase
        Log.d(TAG, "Passphrase baru di-set")
        return true
    }

    // ============================================================
    // VERIFIKASI PASSPHRASE
    // ============================================================
    fun verifyPassphrase(context: Context, passphrase: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedHash = prefs.getString(KEY_PASSPHRASE_HASH, null) ?: return false
        val inputHash = hashPassphrase(passphrase)
        val match = storedHash == inputHash
        if (match) {
            activePassphrase = passphrase
        }
        return match
    }

    // ============================================================
    // UNLOCK — Verifikasi + Load credentials dari file
    // Dipanggil setelah verifyPassphrase sukses
    // ============================================================
    fun unlock(context: Context, passphrase: String): Boolean {
        if (!verifyPassphrase(context, passphrase)) return false
        return loadFromFile(context, passphrase)
    }

    // ============================================================
    // LOCK — Hapus cache dari memory
    // ============================================================
    fun lock() {
        cachedToken = ""
        cachedGroupId = ""
        cachedPat = ""
        activePassphrase = null
    }

    // ============================================================
    // GETTERS — Baca dari cache memory
    // ============================================================
    fun getToken(): String = cachedToken
    fun getGroupId(): String = cachedGroupId
    fun getPat(): String = cachedPat
    fun isUnlocked(): Boolean = activePassphrase != null

    // ============================================================
    // SAVE — Simpan credentials (encrypt + tulis file)
    // ============================================================
    suspend fun saveCredentials(
        context: Context,
        credentials: Credentials
    ): Boolean = withContext(Dispatchers.IO) {
        val passphrase = activePassphrase
        if (passphrase == null) {
            Log.w(TAG, "saveCredentials: passphrase belum di-set")
            return@withContext false
        }

        return@withContext try {
            val json = JSONObject().apply {
                put("token", credentials.fonnteToken)
                put("groupId", credentials.fonnteGroupId)
                put("pat", credentials.githubPat)
            }.toString()

            val encrypted = encrypt(json, passphrase)
            val file = File(context.filesDir, CREDENTIALS_FILE)
            FileOutputStream(file).use { it.write(encrypted.toByteArray(Charsets.UTF_8)) }

            // Update cache
            cachedToken = credentials.fonnteToken
            cachedGroupId = credentials.fonnteGroupId
            cachedPat = credentials.githubPat

            Log.d(TAG, "Credentials tersimpan lokal")
            true
        } catch (e: Exception) {
            Log.e(TAG, "saveCredentials error: ${e.message}", e)
            false
        }
    }
// ============================================================
// LOAD FROM FILE — Baca file terenkripsi & decrypt
// ============================================================
private suspend fun loadFromFile(context: Context, passphrase: String): Boolean =
    withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(context.filesDir, CREDENTIALS_FILE)
            if (!file.exists()) {
                Log.d(TAG, "File credentials belum ada")
                // Tidak ada file = credentials kosong, tapi passphrase valid
                cachedToken = ""
                cachedGroupId = ""
                cachedPat = ""
                return@withContext true
            }

            val encrypted = file.readText(Charsets.UTF_8).trim()
            if (encrypted.isEmpty()) {
                cachedToken = ""
                cachedGroupId = ""
                cachedPat = ""
                return@withContext true
            }

            val json = decrypt(encrypted, passphrase)
            if (json == null) {
                Log.w(TAG, "Gagal decrypt credentials (passphrase salah?)")
                return@withContext false
            }

            val obj = JSONObject(json)
            cachedToken = obj.optString("token", "")
            cachedGroupId = obj.optString("groupId", "")
            cachedPat = obj.optString("pat", "")

            Log.d(TAG, "Credentials loaded dari file")
            true
        } catch (e: Exception) {
            Log.e(TAG, "loadFromFile error: ${e.message}", e)
            false
        }
    }

// ============================================================
// GET CREDENTIALS — Ambil dari cache
// ============================================================
fun getCredentials(): Credentials {
    return Credentials(
        fonnteToken = cachedToken,
        fonnteGroupId = cachedGroupId,
        githubPat = cachedPat
    )
}

// ============================================================
// CLEAR — Hapus semua credentials (lokal + cache)
// ============================================================
suspend fun clearCredentials(context: Context): Boolean = withContext(Dispatchers.IO) {
    return@withContext try {
        val file = File(context.filesDir, CREDENTIALS_FILE)
        if (file.exists()) file.delete()

        cachedToken = ""
        cachedGroupId = ""
        cachedPat = ""

        Log.d(TAG, "Credentials dibersihkan")
        true
    } catch (e: Exception) {
        Log.e(TAG, "clearCredentials error: ${e.message}", e)
        false
    }
}

// ============================================================
// ENCRYPT — AES-256-GCM
// Output: Base64(salt[16] + iv[12] + ciphertext+tag)
// ============================================================
private fun encrypt(plainText: String, passphrase: String): String {
    val salt = ByteArray(SALT_LENGTH)
    SecureRandom().nextBytes(salt)

    val key = deriveKey(passphrase, salt)

    val iv = ByteArray(IV_LENGTH)
    SecureRandom().nextBytes(iv)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
    cipher.init(Cipher.ENCRYPT_MODE, key, spec)

    val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

    // Gabung: salt + iv + cipherText
    val combined = ByteArray(salt.size + iv.size + cipherText.size)
    System.arraycopy(salt, 0, combined, 0, salt.size)
    System.arraycopy(iv, 0, combined, salt.size, iv.size)
    System.arraycopy(cipherText, 0, combined, salt.size + iv.size, cipherText.size)

    return Base64.encodeToString(combined, Base64.NO_WRAP)
}

// ============================================================
// DECRYPT — AES-256-GCM
// Input: Base64(salt[16] + iv[12] + ciphertext+tag)
// ============================================================
private fun decrypt(encryptedBase64: String, passphrase: String): String? {
    return try {
        val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)

        if (combined.size < SALT_LENGTH + IV_LENGTH + 16) {
            Log.w(TAG, "Data terenkripsi terlalu pendek")
            return null
        }

        val salt = combined.copyOfRange(0, SALT_LENGTH)
        val iv = combined.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
        val cipherText = combined.copyOfRange(SALT_LENGTH + IV_LENGTH, combined.size)

        val key = deriveKey(passphrase, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val plainBytes = cipher.doFinal(cipherText)
        String(plainBytes, Charsets.UTF_8)
    } catch (e: Exception) {
        Log.e(TAG, "decrypt error: ${e.message}")
        null
    }
}

// ============================================================
// DERIVE KEY — PBKDF2WithHmacSHA256
// ============================================================
private fun deriveKey(passphrase: String, salt: ByteArray): SecretKey {
    val spec = PBEKeySpec(
        passphrase.toCharArray(),
        salt,
        PBKDF2_ITERATIONS,
        PBKDF2_KEY_LENGTH
    )
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val secret = factory.generateSecret(spec)
    return SecretKeySpec(secret.encoded, "AES")
}

// ============================================================
// HASH PASSPHRASE — SHA-256 untuk verifikasi
// ============================================================
private fun hashPassphrase(passphrase: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(passphrase.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}
    // ============================================================
    // GIST API — GitHub Gist untuk sync antar device
    // ============================================================
    private const val GIST_FILENAME = "masjid-io-credentials.enc"
    private const val GIST_DESCRIPTION = "Masjid.io Encrypted Credentials (DO NOT DELETE)"
    private const val GITHUB_API = "https://api.github.com/gists"

    // ============================================================
    // DATA CLASS — Hasil sync
    // ============================================================
    data class SyncResult(
        val success: Boolean,
        val message: String,
        val hasLocalChange: Boolean = false,
        val hasRemoteChange: Boolean = false,
        val remoteCredentials: Credentials? = null
    )

    // ============================================================
    // GET / SET GIST ID (disimpan di SharedPreferences)
    // ============================================================
    fun getGistId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_GIST_ID, null)?.takeIf { it.isNotBlank() }
    }

    private fun setGistId(context: Context, gistId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_GIST_ID, gistId).apply()
    }

    // ============================================================
    // CREATE GIST — Auto-create Gist pertama kali
    // Return: Gist ID kalau sukses
    // ============================================================
    suspend fun createGist(context: Context, pat: String): String? =
        withContext(Dispatchers.IO) {
            if (pat.isBlank()) {
                Log.w(TAG, "createGist: PAT kosong")
                return@withContext null
            }

            return@withContext try {
                val payload = JSONObject().apply {
                    put("description", GIST_DESCRIPTION)
                    put("public", false)
                    put("files", JSONObject().apply {
                        put(GIST_FILENAME, JSONObject().apply {
                            put("content", "INITIALIZED")  // placeholder, nanti di-update
                        })
                    })
                }

                val response = httpRequest(
                    method = "POST",
                    url = GITHUB_API,
                    pat = pat,
                    body = payload.toString()
                )

                if (response.code == 201) {
                    val obj = JSONObject(response.body)
                    val gistId = obj.optString("id", "")
                    if (gistId.isNotBlank()) {
                        setGistId(context, gistId)
                        Log.d(TAG, "Gist dibuat: $gistId")
                        gistId
                    } else null
                } else {
                    Log.w(TAG, "createGist gagal: ${response.code} - ${response.body}")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "createGist error: ${e.message}", e)
                null
            }
        }

    // ============================================================
    // FETCH GIST — Ambil isi terenkripsi dari Gist
    // Return: String base64 ciphertext
    // ============================================================
    suspend fun fetchGist(context: Context, pat: String): String? =
        withContext(Dispatchers.IO) {
            val gistId = getGistId(context) ?: return@withContext null
            if (pat.isBlank()) return@withContext null

            return@withContext try {
                val response = httpRequest(
                    method = "GET",
                    url = "$GITHUB_API/$gistId",
                    pat = pat,
                    body = null
                )

                if (response.code == 200) {
                    val obj = JSONObject(response.body)
                    val files = obj.optJSONObject("files")
                    val fileObj = files?.optJSONObject(GIST_FILENAME)
                    val content = fileObj?.optString("content", "")?.trim() ?: ""
                    if (content.isBlank() || content == "INITIALIZED") null else content
                } else {
                    Log.w(TAG, "fetchGist gagal: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchGist error: ${e.message}", e)
                null
            }
        }

    // ============================================================
    // UPDATE GIST — Upload credentials terenkripsi ke Gist
    // ============================================================
    suspend fun updateGist(context: Context, pat: String, encryptedContent: String): Boolean =
        withContext(Dispatchers.IO) {
            val gistId = getGistId(context) ?: return@withContext false
            if (pat.isBlank()) return@withContext false

            return@withContext try {
                val payload = JSONObject().apply {
                    put("files", JSONObject().apply {
                        put(GIST_FILENAME, JSONObject().apply {
                            put("content", encryptedContent)
                        })
                    })
                }

                val response = httpRequest(
                    method = "PATCH",
                    url = "$GITHUB_API/$gistId",
                    pat = pat,
                    body = payload.toString()
                )

                val ok = response.code in 200..299
                Log.d(TAG, "updateGist: code=${response.code} ok=$ok")
                ok
            } catch (e: Exception) {
                Log.e(TAG, "updateGist error: ${e.message}", e)
                false
            }
        }

    // ============================================================
    // SYNC TO CLOUD — Enkripsi + upload ke Gist
    // ============================================================
    suspend fun syncToCloud(context: Context): SyncResult = withContext(Dispatchers.IO) {
        val passphrase = activePassphrase
            ?: return@withContext SyncResult(false, "Passphrase belum di-set")
        val pat = cachedPat
            ?: return@withContext SyncResult(false, "PAT belum diisi")

        if (pat.isBlank()) {
            return@withContext SyncResult(false, "PAT kosong")
        }

        try {
            // 1. Pastikan Gist sudah ada
            var gistId = getGistId(context)
            if (gistId == null) {
                gistId = createGist(context, pat)
                if (gistId == null) {
                    return@withContext SyncResult(false, "Gagal bikin Gist")
                }
            }

            // 2. Enkripsi credentials
            val json = JSONObject().apply {
                put("token", cachedToken)
                put("groupId", cachedGroupId)
                put("pat", cachedPat)
            }.toString()

            val encrypted = encrypt(json, passphrase)

            // 3. Upload
            val ok = updateGist(context, pat, encrypted)
            if (ok) {
                SyncResult(true, "Sync ke cloud berhasil")
            } else {
                SyncResult(false, "Upload ke Gist gagal")
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncToCloud error: ${e.message}", e)
            SyncResult(false, "Error: ${e.message}")
        }
    }

    // ============================================================
    // SYNC FROM CLOUD — Fetch dari Gist + decrypt + bandingkan
    // ============================================================
    suspend fun syncFromCloud(context: Context): SyncResult = withContext(Dispatchers.IO) {
        val passphrase = activePassphrase
            ?: return@withContext SyncResult(false, "Passphrase belum di-set")
        val pat = cachedPat ?: ""

        if (pat.isBlank()) {
            return@withContext SyncResult(false, "PAT belum diisi")
        }

        try {
            val encrypted = fetchGist(context, pat)
                ?: return@withContext SyncResult(false, "Gist kosong / belum ada")

            val json = decrypt(encrypted, passphrase)
                ?: return@withContext SyncResult(false, "Gagal decrypt (passphrase beda?)")

            val obj = JSONObject(json)
            val remote = Credentials(
                fonnteToken = obj.optString("token", ""),
                fonnteGroupId = obj.optString("groupId", ""),
                githubPat = obj.optString("pat", "")
            )

            // Bandingkan dengan lokal
            val local = Credentials(cachedToken, cachedGroupId, cachedPat)
            val beda = remote.fonnteToken != local.fonnteToken ||
                    remote.fonnteGroupId != local.fonnteGroupId ||
                    remote.githubPat != local.githubPat

            if (beda) {
                SyncResult(
                    success = true,
                    message = "Ada perubahan di cloud",
                    hasRemoteChange = true,
                    remoteCredentials = remote
                )
            } else {
                SyncResult(true, "Sudah sama dengan cloud")
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncFromCloud error: ${e.message}", e)
            SyncResult(false, "Error: ${e.message}")
        }
    }

    // ============================================================
    // APPLY REMOTE — Terapkan credentials dari cloud ke cache + file
    // ============================================================
    suspend fun applyRemoteCredentials(
        context: Context,
        remote: Credentials
    ): Boolean = withContext(Dispatchers.IO) {
        saveCredentials(context, remote)
    }

    // ============================================================
    // HTTP HELPER — Request ke GitHub API
    // ============================================================
    private data class HttpResult(val code: Int, val body: String)

    private fun httpRequest(
        method: String,
        url: String,
        pat: String,
        body: String?
    ): HttpResult {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            setRequestProperty("Authorization", "token $pat")
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            setRequestProperty("User-Agent", "Masjid.io-App")
            connectTimeout = 15_000
            readTimeout = 30_000

            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
        }

        try {
            if (body != null) {
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseBody = stream?.let {
                BufferedReader(InputStreamReader(it, Charsets.UTF_8)).use { r ->
                    r.readText()
                }
            } ?: ""

            return HttpResult(code, responseBody)
        } finally {
            conn.disconnect()
        }
    }
}
