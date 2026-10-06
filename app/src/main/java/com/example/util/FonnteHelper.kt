package dev.andikune.masjidio.util

/**
 * FonnteHelper — Token & Group ID Fonnte yang tertanam di aplikasi.
 *
 * Nilai dibagi beberapa bagian agar tidak langsung terbaca di source.
 * Kalau token perlu diganti, cukup ubah konstanta di bawah.
 *
 * KALAU TOKEN DI-ABUSE:
 *   1. Login ke dashboard Fonnte
 *   2. Regenerate token
 *   3. Update TOKEN_PART_1/2/3 di file ini
 *   4. Commit + Build ulang
 */
object FonnteHelper {

    // Token dibagi 3 bagian
    private const val TOKEN_PART_1 = "JbdS"
    private const val TOKEN_PART_2 = "VhrV7mcrG"
    private const val TOKEN_PART_3 = "HWCjH7D"

    // Group ID dibagi 3 bagian
    private const val GROUP_PART_1 = "1203634321"
    private const val GROUP_PART_2 = "39302728"
    private const val GROUP_SUFFIX = "@g.us"

    /** Ambil token Fonnte lengkap. */
    fun getToken(): String = TOKEN_PART_1 + TOKEN_PART_2 + TOKEN_PART_3

    /** Ambil Group ID lengkap. */
    fun getGroupId(): String = GROUP_PART_1 + GROUP_PART_2 + GROUP_SUFFIX
}
