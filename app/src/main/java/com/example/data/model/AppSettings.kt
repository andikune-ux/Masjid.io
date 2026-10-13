package dev.andikune.masjidio.data.model

enum class PrayerId(
    val arabicName: String,
    val displayName: String,
    val iconResName: String
) {
    SUBUH("الفجر", "Subuh", "ic_subuh"),
    SYURUQ("الشروق", "Syuruq", "ic_syuruq"),
    DZUHUR("الظهر", "Dzuhur", "ic_dzuhur"),
    ASHAR("العصر", "Ashar", "ic_ashar"),
    MAGHRIB("المغرب", "Maghrib", "ic_maghrib"),
    ISYA("العشاء", "Isya", "ic_isya")
}

data class PrayerItem(
    val id: PrayerId,
    val timeFormatted: String,
    val targetTimeMillis: Long,
    val isNext: Boolean = false,
    val isPassed: Boolean = false,
    val isActive: Boolean = false
)

data class PrayerSchedule(
    val imsak: String = "04:22",
    val subuh: String = "04:32",
    val syuruq: String = "05:48",
    val dhuha: String = "06:15",
    val dzuhur: String = "11:52",
    val ashar: String = "15:08",
    val maghrib: String = "17:52",
    val isya: String = "19:02",
    val items: List<PrayerItem> = emptyList(),
    val nextPrayer: PrayerItem? = null,
    val previousPrayer: PrayerItem? = null,
    val progressToNext: Float = 0f,
    val secondsToNext: Long = 0L
)

enum class AudioMode { BEEP_ONLY, FULL_ADZAN, SILENT }

enum class BackgroundMode {
    NATURE,
    DEFAULT_NATURE,
    KABAH,
    EMERALD_GEOMETRIC,
    MAKKAH_DYNAMIC,
    CUSTOM,
    CUSTOM_GALLERY
}

enum class CctvPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

/**
 * Mode PIN Lock setelah keluar dari Settings.
 *
 *   IMMEDIATE      = Setiap kali tekan tombol Settings -> langsung minta PIN
 *   TIMEOUT_5MIN   = PIN diminta kembali 5 menit setelah kembali ke menu utama
 *   UNTIL_EXIT     = PIN hanya diminta saat pertama buka, tidak minta lagi
 *                    sampai aplikasi di-keluarkan (DEFAULT — behavior lama)
 */
enum class PinLockMode {
    IMMEDIATE,
    TIMEOUT_5MIN,
    UNTIL_EXIT
}

/**
 * V1.04.429 — Mode Kecerahan Layar
 *
 *   MANUAL   = Brightness tetap dari slider (manualBrightnessPercent)
 *   AUTO     = Ikut jadwal sholat:
 *              siang = dayBrightnessPercent (Subuh-Maghrib)
 *              malam = nightBrightnessPercent (Maghrib-Subuh)
 *   SCHEDULE = Ikut jadwal ON/OFF (jam OFF = layar gelap total)
 *              (DEFAULT — pakai fitur Jadwal On/Off Otomatis)
 */
enum class BrightnessMode {
    MANUAL,
    AUTO,
    SCHEDULE
}

data class OfficerSchedule(
    val imamSubuh: String = "Ust. H. Ahmad Fauzi",
    val muadzinSubuh: String = "Ust. Ridwan Kamil",
    val imamDzuhur: String = "Ust. M. Ridho, M.Ag",
    val muadzinDzuhur: String = "Ust. Bilal Sanjaya",
    val imamAshar: String = "Ust. Dr. H. Lukman",
    val muadzinAshar: String = "Ust. Ilham Pratama",
    val imamMaghrib: String = "Ust. Ahmad Syarifuddin",
    val muadzinMaghrib: String = "Ust. Ridwan Ar-Rasyid",
    val imamIsya: String = "Ust. KH. Abdullah Gymnast",
    val muadzinIsya: String = "Ust. Farhan Azis",
    val khatibJumat: String = "Prof. Dr. KH. Zainuddin MZ",
    val temaJumat: String = "Menjaga Ukhuwah & Istiqomah di Era Modern",
    val ustadzKajian: String = "Ust. Hanan Attaki, Lc",
    val jadwalKajian: String = "Ba'da Maghrib",
    val temaKajian: String = "Tafsir Ayat-Ayat Rahmat & Tazkiyatun Nafs"
)

data class DailyOfficerItem(
    val dayName: String = "Senin",
    val imamSubuh: String = "Ust. H. Ahmad Fauzi",
    val muadzinSubuh: String = "Ust. Ridwan Kamil",
    val fotoImamSubuh: String? = null,
    val fotoMuadzinSubuh: String? = null,
    val imamDzuhur: String = "Ust. M. Ridho, M.Ag",
    val muadzinDzuhur: String = "Ust. Bilal Sanjaya",
    val fotoImamDzuhur: String? = null,
    val fotoMuadzinDzuhur: String? = null,
    val imamAshar: String = "Ust. Dr. H. Lukman",
    val muadzinAshar: String = "Ust. Ilham Pratama",
    val fotoImamAshar: String? = null,
    val fotoMuadzinAshar: String? = null,
    val imamMaghrib: String = "Ust. Ahmad Syarifuddin",
    val muadzinMaghrib: String = "Ust. Ridwan Ar-Rasyid",
    val fotoImamMaghrib: String? = null,
    val fotoMuadzinMaghrib: String? = null,
    val imamIsya: String = "Ust. KH. Abdullah Gymnast",
    val muadzinIsya: String = "Ust. Farhan Azis",
    val fotoImamIsya: String? = null,
    val fotoMuadzinIsya: String? = null,
    val khatibJumat: String = "Prof. Dr. KH. Zainuddin MZ",
    val temaJumat: String = "Menjaga Ukhuwah & Istiqomah di Era Modern",
    val fotoKhatibJumat: String? = null,
    val ustadzKajian: String = "Ust. Hanan Attaki, Lc",
    val temaKajian: String = "Tafsir Ayat-Ayat Rahmat",
    val fotoUstadzKajian: String? = null
)

data class LaporanKeuangan(
    val saldoSebelumnya: Long = 41_000,
    val pemasukanJumat: Long = 5_100_000,
    val pemasukanUmum: Long = 236_000,
    val pengeluaranDakwah: Long = 2_900_000,
    val pengeluaranSosial: Long = 1_800_000,
    val pengeluaranOperasional: Long = 656_000,
    val periodeMulai: String = "11 September 2026",
    val periodeSelesai: String = "17 September 2026"
) {
    val totalPemasukan: Long get() = pemasukanJumat + pemasukanUmum
    val totalPengeluaran: Long get() = pengeluaranDakwah + pengeluaranSosial + pengeluaranOperasional
    val saldoAkhir: Long get() = saldoSebelumnya + totalPemasukan - totalPengeluaran
}
