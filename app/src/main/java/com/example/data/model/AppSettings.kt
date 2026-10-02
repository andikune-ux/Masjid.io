package com.example.data.model

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

data class AppSettings(
    val mosqueName: String = "MASJID AL-IKHLAS",
    val mosqueAddress: String = "Jl. Raya Madinah No. 7, Gambir, Jakarta Pusat",
    val mosqueTakmir: String = "H. Muhammad Syarif, S.E.",
    val isGpsEnabled: Boolean = false,
    val country: String = "Indonesia",
    val province: String = "DKI Jakarta",
    val city: String = "Jakarta Pusat",
    val district: String = "Gambir",
    val latitude: Double = -6.1754,
    val longitude: Double = 106.8272,
    val calculationMethod: String = "Kementerian Agama RI (Kemenag)",
    val languageCode: String = "id",
    val runningText: String = "║ Selamat datang di Masjid Al-Ikhlas ║ Luruskan dan rapatkan shaf sholat ║ Harap nonaktifkan nada dering ponsel ║ Mari jaga kebersihan dan ketertiban masjid ║ Infaq & Shadaqah: Rek BSI 7123-4567-89 a.n Masjid Al-Ikhlas ║",
    val runningTextSpeed: Int = 5,
    val runningTextFontSize: Int = 18,
    val officers: OfficerSchedule = OfficerSchedule(),
    val weeklyOfficers: List<DailyOfficerItem> = createDefaultWeeklySchedule(),
    val officerPhotoUri: String? = null,
    val audioMode: AudioMode = AudioMode.BEEP_ONLY,
    val beepVolume: Int = 100,
    val beepCount: Int = 5,
    val beepDurationMs: Int = 1500,
    val beepIntervalMs: Int = 2000,
    val adzanFile: String = "Makkah",
    val adzanVolume: Int = 85,
    val adzanWaitMinutes: Int = 5,
    val iqamahWaitMinutes: Int = 10,
    val qobliyahWaitMinutes: Int = 5,
    val prayerFocusDurationMinutes: Int = 30,
    val focusModeDurationMinutes: Int = 30,
    val backgroundMode: BackgroundMode = BackgroundMode.MAKKAH_DYNAMIC,
    val customBackgroundUri: String? = null,
    val animationsEnabled: Boolean = true,
    val showBirdsAnimation: Boolean = true,
    // ============================================================
    // RESPONSIVE LAYOUT (V1.30.3) — Auto-scale tampilan TV
    // ============================================================
    val tvAutoScaleEnabled: Boolean = true,
    val tvSafeAreaPercent: Float = 3f,
    val tvLayoutPreset: String = "AUTO",
    // ============================================================
    val qrisPhotoUri: String? = null,
    val qrisImageUri: String = "",
    val qrisIntervalMinutes: Int = 15,
    val qrisDisplayDurationSeconds: Int = 30,
    val bankName: String = "Bank Syariah Indonesia (BSI)",
    val bankAccountNumber: String = "7123-4567-890",
    val bankAccountHolder: String = "DKM MASJID AL-IKHLAS",
    val wisdomCardAnimation: String = "Fade",
    val wisdomCardIntervalSeconds: Int = 12,
    val videoEnabled: Boolean = false,
    val videoUri: String? = null,
    val videoSmartFullscreen: Boolean = true,
    val photoSlideshowEnabled: Boolean = false,
    val photoSlideshowUris: List<String> = emptyList(),
    val photoSlideshowIntervalSeconds: Int = 10,
    val slideEnabled: Boolean = false,
    val slideIntervalSeconds: Int = 15,
    val qrisSlideEnabled: Boolean = true,
    val laporanSlideEnabled: Boolean = true,
    val kajianSlideEnabled: Boolean = true,
    val slideShowOnlyWhenIdle: Boolean = true,
    val laporanKeuangan: LaporanKeuangan = LaporanKeuangan(),
    val prayerCardPhotoEnabled: Boolean = false,
    val prayerCardPhotoUri: String? = null,
    val prayerCardPhotoAlpha: Float = 0.35f,
    val ramadhanModeEnabled: Boolean = false,
    val showImsakIftarCountdown: Boolean = true,
    val showTarawihSchedule: Boolean = true,
    val showKultumSchedule: Boolean = true,
    val showMenuSahurIftar: Boolean = false,
    val showImsakBesar: Boolean = true,
    val showIftarBesar: Boolean = true,
    val ramadhanImsakOffsetMinutes: Int = 10,
    val tarawihTime: String = "19:30",
    val tarawihImam: String = "",
    val kultumTitle: String = "",
    val kultumUstadz: String = "",
    val kultumTime: String = "17:30",
    val menuSahurText: String = "",
    val menuIftarText: String = "",
    val dzikirEnabled: Boolean = true,
    val dzikirDurationSeconds: Int = 120,
    val dzikirAutoShowAfterPrayer: Boolean = true,
    val contentRotationEnabled: Boolean = false,
    val contentRotationShowAyat: Boolean = true,
    val contentRotationShowHadits: Boolean = true,
    val contentRotationShowAsmaulHusna: Boolean = true,
    val contentRotationIntervalSeconds: Int = 20,
    val cctvEnabled: Boolean = false,
    val cctvUrl: String = "",
    val cctvPosition: CctvPosition = CctvPosition.TOP_RIGHT,
    val cctvSizePercent: Int = 20,
    val remoteControlEnabled: Boolean = false,
    val remoteServerPort: Int = 14039,
    val remoteAuthToken: String = "masjid-io",
    val pinCode: String = "1234",
    val kioskModeEnabled: Boolean = true,
    val autoStartOnBoot: Boolean = true,
    val autoRestartIfCrash: Boolean = true,
    val disableBackButton: Boolean = true,
    val lockTaskMode: Boolean = true,
    val isManualTimeEnabled: Boolean = false,
    val manualTimeOffsetSeconds: Long = 0L,
    val keepScreenOn: Boolean = true,
    val autoOnOff: Boolean = false,
    val autoOnTime: String = "04:00",
    val autoOffTime: String = "22:30",
    val idleScreenOff: Boolean = true,
    val idleTimeoutMinutes: Int = 30,
    val autoBrightness: Boolean = true,
    val saveBatteryMode: Boolean = false,
    val fonnteToken: String = "",
    val fonnteGroupId: String = "",
    val whatsappReportEnabled: Boolean = true
) {
    companion object {
        fun createDefaultWeeklySchedule(): List<DailyOfficerItem> {
            val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Ahad")
            return days.map { day -> DailyOfficerItem(dayName = day) }
        }
    }
}
