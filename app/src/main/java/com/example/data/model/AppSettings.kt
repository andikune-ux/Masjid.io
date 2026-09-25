package com.example.data.model

enum class PrayerId(val arabicName: String, val displayName: String, val iconResName: String) {
    SUBUH("الفجر", "Subuh", "ic_subuh"),
    SYURUQ("الشروق", "Syuruq", "ic_syuruq"),
    DZUHUR("الظهر", "Dzuhur", "ic_dzuhur"),
    ASHAR("العصر", "Ashar", "ic_ashar"),
    MAGHRIB("المغرب", "Maghrib", "ic_maghrib"),
    ISYA("العشاء", "Isya", "ic_isya")
}

data class PrayerItem(
    val id: PrayerId,
    val timeFormatted: String, // "04:32"
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

enum class AudioMode {
    BEEP_ONLY,
    FULL_ADZAN,
    SILENT
}

enum class BackgroundMode {
    NATURE,
    DEFAULT_NATURE,
    KABAH,
    EMERALD_GEOMETRIC,
    CUSTOM,
    CUSTOM_GALLERY
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

data class AppSettings(
    // Mosque Identity
    val mosqueName: String = "MASJID AL-IKHLAS",
    val mosqueAddress: String = "Jl. Raya Madinah No. 7, Gambir, Jakarta Pusat",
    val mosqueTakmir: String = "H. Muhammad Syarif, S.E.",

    // Location
    val isGpsEnabled: Boolean = false,
    val country: String = "Indonesia",
    val province: String = "DKI Jakarta",
    val city: String = "Jakarta Pusat",
    val district: String = "Gambir",
    val latitude: Double = -6.1754,
    val longitude: Double = 106.8272,
    val calculationMethod: String = "Kementerian Agama RI (Kemenag)",

    // Running text
    val runningText: String = "═══ Selamat datang di Masjid Al-Ikhlas ═══ Luruskan dan rapatkan shaf sholat ═══ Harap nonaktifkan nada dering ponsel ═══ Infaq & Shadaqah dapat melalui Rek BSI: 7123-4567-89 a.n Masjid Al-Ikhlas ═══ Kajian Rutin Sabtu Ba'da Maghrib bersama Ust. Hanan Attaki, Lc ═══",
    val runningTextSpeed: Int = 2,
    val runningTextFontSize: Int = 18,

    // Officers & 7 Days Schedule
    val officers: OfficerSchedule = OfficerSchedule(),
    val weeklyOfficers: List<DailyOfficerItem> = createDefaultWeeklySchedule(),
    val officerPhotoUri: String? = null,

    // Audio & Adzan
    val audioMode: AudioMode = AudioMode.BEEP_ONLY,
    val beepVolume: Int = 70,
    val beepCount: Int = 3,
    val adzanFile: String = "Makkah",
    val adzanVolume: Int = 85,
    val adzanWaitMinutes: Int = 5,
    val iqamahWaitMinutes: Int = 10,
    val qobliyahWaitMinutes: Int = 5,
    val prayerFocusDurationMinutes: Int = 30,
    val focusModeDurationMinutes: Int = 30,

    // Display & Background
    val backgroundMode: BackgroundMode = BackgroundMode.NATURE,
    val customBackgroundUri: String? = null,
    val animationsEnabled: Boolean = true,
    val showBirdsAnimation: Boolean = true,

    // QRIS Donation Card
    val qrisPhotoUri: String? = null,
    val qrisImageUri: String = "",
    val qrisIntervalMinutes: Int = 15,
    val qrisDisplayDurationSeconds: Int = 30,
    val bankName: String = "Bank Syariah Indonesia (BSI)",
    val bankAccountNumber: String = "7123-4567-890",
    val bankAccountHolder: String = "DKM MASJID AL-IKHLAS",

    // Wisdom cards
    val wisdomCardAnimation: String = "Fade",
    val wisdomCardIntervalSeconds: Int = 12,

    // Video Facility
    val videoEnabled: Boolean = false,
    val videoUri: String? = null,
    val videoSmartFullscreen: Boolean = true,

    // Ramadhan Mode
    val ramadhanModeEnabled: Boolean = false,
    val showImsakIftarCountdown: Boolean = true,

    // Security & Kiosk
    val pinCode: String = "1234",
    val kioskModeEnabled: Boolean = true,
    val autoStartOnBoot: Boolean = true,

    // Manual Offline Time & Date Setting
    val isManualTimeEnabled: Boolean = false,
    val manualTimeOffsetSeconds: Long = 0L,

    // Power
    val keepScreenOn: Boolean = true
) {
    companion object {
        fun createDefaultWeeklySchedule(): List<DailyOfficerItem> {
            val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Ahad")
            return days.map { day -> DailyOfficerItem(dayName = day) }
        }
    }
}
