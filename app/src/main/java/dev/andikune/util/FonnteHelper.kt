package dev.andikune.masjidio.util

/**
 * FonnteHelper — V1.04.429
 *
 * Wrapper tipis untuk CredentialManager.
 *
 * SEBELUMNYA: Token & Group ID di-hardcode di file ini
 *             (BAHAYA karena repo PUBLIC).
 *
 * SEKARANG:   Token & Group ID dibaca dari CredentialManager
 *             (terenkripsi, user yang input via Opsi Developer).
 *
 * Cara kerja:
 *   - CredentialManager menyimpan token di memory cache setelah
 *     user input passphrase di Opsi Developer.
 *   - FonnteHelper.getToken() / getGroupId() tinggal baca cache.
 *   - Kalau belum di-setup, return string kosong.
 */
object FonnteHelper {

    /**
     * Ambil token Fonnte dari CredentialManager.
     * Return string kosong kalau belum di-setup.
     */
    fun getToken(): String {
        return try {
            CredentialManager.getToken()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Ambil Group ID dari CredentialManager.
     * Return string kosong kalau belum di-setup.
     */
    fun getGroupId(): String {
        return try {
            CredentialManager.getGroupId()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Cek apakah kredensial Fonnte sudah dikonfigurasi.
     * Return true kalau Token & Group ID tidak kosong.
     */
    fun isConfigured(): Boolean {
        return getToken().isNotBlank() && getGroupId().isNotBlank()
    }

    /**
     * Cek apakah CredentialManager sedang unlocked.
     * (User sudah input passphrase di Opsi Developer)
     */
    fun isUnlocked(): Boolean {
        return try {
            CredentialManager.isUnlocked()
        } catch (e: Exception) {
            false
        }
    }
}
