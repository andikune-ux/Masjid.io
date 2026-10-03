package com.example.util

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * UpdateManager — Cek versi terbaru dari GitHub Releases.
 *
 * FITUR:
 *   - Auto-detect versi terbaru (tag V{x}.{y}.{z})
 *   - Parse marker FORCE_UPDATE=true dari release body → wajib update
 *   - Ambil APK download URL dari assets
 */
object UpdateManager {

    private const val TAG = "UpdateManager"
    private const val GITHUB_API =
        "https://api.github.com/repos/andikune-ux/Masjid.io/releases/latest"

    data class UpdateInfo(
        val available: Boolean,
        val currentVersion: String,
        val latestVersion: String,
        val downloadUrl: String?,
        val releaseNotes: String?,
        val releaseDate: String?,
        val isForceUpdate: Boolean
    )

    suspend fun checkForUpdate(): UpdateInfo = withContext(Dispatchers.IO) {
        val currentVersion = try {
            BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            "V0.0.0"
        }

        try {
            val connection = URL(GITHUB_API).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.setRequestProperty("User-Agent", "MasjidIO-App")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode != 200) {
                Log.w(TAG, "GitHub API response: $responseCode")
                return@withContext noUpdate(currentVersion)
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)

            val latestVersion = json.optString("tag_name", "")
            val releaseNotes = json.optString("body", "")
            val releaseDate = json.optString("published_at", "")
            val downloadUrl = findApkAssetUrl(json)

            if (latestVersion.isBlank()) {
                return@withContext noUpdate(currentVersion)
            }

            // Cek apakah ada update baru
            val isNewer = compareVersions(latestVersion, currentVersion) > 0

            // Parse FORCE_UPDATE marker dari release body
            // Format: baris pertama "FORCE_UPDATE=true"
            val isForce = isNewer && parseForceUpdate(releaseNotes)

            Log.d(
                TAG,
                "Versi: current=$currentVersion, latest=$latestVersion, " +
                        "isNewer=$isNewer, isForce=$isForce"
            )

            UpdateInfo(
                available = isNewer,
                currentVersion = currentVersion,
                latestVersion = latestVersion,
                downloadUrl = downloadUrl,
                releaseNotes = releaseNotes,
                releaseDate = releaseDate,
                isForceUpdate = isForce
            )
        } catch (e: Exception) {
            Log.e(TAG, "Cek update gagal: ${e.message}", e)
            noUpdate(currentVersion)
        }
    }

    // ============================================================
    // PARSE FORCE_UPDATE MARKER
    // ============================================================
    /**
     * Cek apakah release body mengandung marker FORCE_UPDATE=true.
     *
     * Format yang dikenali:
     *   - "FORCE_UPDATE=true" (di mana saja di body)
     *   - "FORCE_UPDATE=TRUE"
     *
     * Contoh release body:
     *   FORCE_UPDATE=true
     *   ## Update MASJID.IO V1.30.5
     *   ...
     */
    private fun parseForceUpdate(releaseNotes: String?): Boolean {
        if (releaseNotes.isNullOrBlank()) return false
        // Cari baris yang mengandung FORCE_UPDATE=true (case-insensitive)
        return releaseNotes.lineSequence().any { line ->
            val trimmed = line.trim().uppercase()
            trimmed == "FORCE_UPDATE=TRUE" || trimmed.startsWith("FORCE_UPDATE=TRUE")
        }
    }

    // ============================================================
    // HELPER — Cari URL APK dari assets
    // ============================================================
    private fun findApkAssetUrl(json: JSONObject): String? {
        return try {
            val assets = json.optJSONArray("assets") ?: return null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.optString("name", "")
                if (name.endsWith(".apk", ignoreCase = true)) {
                    return if (asset.has("browser_download_url")) asset.getString("browser_download_url") else null
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    // ============================================================
    // HELPER — Bandingkan versi
    // ============================================================
    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = parseVersion(v1)
        val parts2 = parseVersion(v2)
        val maxLen = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLen) {
            val p1 = parts1.getOrElse(i) { 0 }
            val p2 = parts2.getOrElse(i) { 0 }
            if (p1 != p2) return p1 - p2
        }
        return 0
    }

    private fun parseVersion(version: String): List<Int> {
        return version
            .removePrefix("V")
            .removePrefix("v")
            .split(".")
            .mapNotNull { it.trim().toIntOrNull() }
    }

    private fun noUpdate(currentVersion: String) = UpdateInfo(
        available = false,
        currentVersion = currentVersion,
        latestVersion = currentVersion,
        downloadUrl = null,
        releaseNotes = null,
        releaseDate = null,
        isForceUpdate = false
    )
}
