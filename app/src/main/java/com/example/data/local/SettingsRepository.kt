package dev.andikune.masjidio.data.local

import android.content.Context
import android.content.SharedPreferences
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.data.model.AudioMode
import dev.andikune.masjidio.data.model.BackgroundMode
import dev.andikune.masjidio.data.model.CctvPosition
import dev.andikune.masjidio.data.model.DailyOfficerItem
import dev.andikune.masjidio.data.model.LaporanKeuangan
import dev.andikune.masjidio.data.model.OfficerSchedule
import dev.andikune.masjidio.util.FonnteHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("masjid_io_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    val settingsFlow: StateFlow<AppSettings> = _settings.asStateFlow()

    fun updateSettings(newSettings: AppSettings) {
        saveSettings(newSettings)
        _settings.value = newSettings
    }

    // ============================================================
    // LOAD SETTINGS + MIGRASI PORT
    // ============================================================
    private fun loadSettings(): AppSettings {
        // Migrasi port lama (8080) → baru (14039)
        val savedPort = prefs.getInt("remoteServerPort", -1)
        val migratedPort = if (savedPort == -1 || savedPort == 8080) 14039 else savedPort
        if (savedPort != migratedPort) {
            prefs.edit().putInt("remoteServerPort", migratedPort).apply()
        }

        return AppSettings(
            mosqueName = prefs.getString("mosqueName", "MASJID AL-IKHLAS") ?: "MASJID AL-IKHLAS",
            mosqueAddress = prefs.getString("mosqueAddress", "Jl. Raya Madinah No. 7, Gambir, Jakarta Pusat") ?: "",
            mosqueTakmir = prefs.getString("mosqueTakmir", "H. Muhammad Syarif, S.E.") ?: "",
            isGpsEnabled = prefs.getBoolean("isGpsEnabled", false),
            country = prefs.getString("country", "Indonesia") ?: "Indonesia",
            province = prefs.getString("province", "DKI Jakarta") ?: "DKI Jakarta",
            city = prefs.getString("city", "Jakarta Pusat") ?: "Jakarta Pusat",
            district = prefs.getString("district", "Gambir") ?: "Gambir",
            latitude = prefs.getString("latitude", "-6.1754")?.toDoubleOrNull() ?: -6.1754,
            longitude = prefs.getString("longitude", "106.8272")?.toDoubleOrNull() ?: 106.8272,
            calculationMethod = prefs.getString("calculationMethod", "Kementerian Agama RI (Kemenag)") ?: "",
            languageCode = prefs.getString("languageCode", "id") ?: "id",
            runningText = prefs.getString("runningText", "") ?: "",
            runningTextSpeed = prefs.getInt("runningTextSpeed", 5),
            runningTextFontSize = prefs.getInt("runningTextFontSize", 18),
            officers = loadOfficerSchedule(),
            weeklyOfficers = loadWeeklyOfficers(),
            officerPhotoUri = prefs.getString("officerPhotoUri", null),
            audioMode = runCatching {
                AudioMode.valueOf(prefs.getString("audioMode", "BEEP_ONLY") ?: "BEEP_ONLY")
            }.getOrDefault(AudioMode.BEEP_ONLY),
            beepVolume = prefs.getInt("beepVolume", 100),
            beepCount = prefs.getInt("beepCount", 5),
            beepDurationMs = prefs.getInt("beepDurationMs", 1500),
            beepIntervalMs = prefs.getInt("beepIntervalMs", 2000),
            adzanFile = prefs.getString("adzanFile", "Makkah") ?: "Makkah",
            adzanVolume = prefs.getInt("adzanVolume", 85),
            adzanWaitMinutes = prefs.getInt("adzanWaitMinutes", 5),
            iqamahWaitMinutes = prefs.getInt("iqamahWaitMinutes", 10),
            qobliyahWaitMinutes = prefs.getInt("qobliyahWaitMinutes", 5),
            prayerFocusDurationMinutes = prefs.getInt("prayerFocusDurationMinutes", 30),
            focusModeDurationMinutes = prefs.getInt("focusModeDurationMinutes", 30),
            backgroundMode = runCatching {
                BackgroundMode.valueOf(prefs.getString("backgroundMode", "MAKKAH_DYNAMIC") ?: "MAKKAH_DYNAMIC")
            }.getOrDefault(BackgroundMode.MAKKAH_DYNAMIC),
            customBackgroundUri = prefs.getString("customBackgroundUri", null),
            animationsEnabled = prefs.getBoolean("animationsEnabled", true),
            showBirdsAnimation = prefs.getBoolean("showBirdsAnimation", true),
            // ============================================================
            // RESPONSIVE LAYOUT (V1.30.3)
            // ============================================================
            tvAutoScaleEnabled = prefs.getBoolean("tvAutoScaleEnabled", true),
            tvSafeAreaPercent = prefs.getFloat("tvSafeAreaPercent", 3f),
            tvLayoutPreset = prefs.getString("tvLayoutPreset", "AUTO") ?: "AUTO",
            // ============================================================
            qrisPhotoUri = prefs.getString("qrisPhotoUri", null),
            qrisImageUri = prefs.getString("qrisImageUri", "") ?: "",
            qrisIntervalMinutes = prefs.getInt("qrisIntervalMinutes", 15),
            qrisDisplayDurationSeconds = prefs.getInt("qrisDisplayDurationSeconds", 30),
            bankName = prefs.getString("bankName", "Bank Syariah Indonesia (BSI)") ?: "",
            bankAccountNumber = prefs.getString("bankAccountNumber", "7123-4567-890") ?: "",
            bankAccountHolder = prefs.getString("bankAccountHolder", "DKM MASJID AL-IKHLAS") ?: "",
            wisdomCardAnimation = prefs.getString("wisdomCardAnimation", "Fade") ?: "Fade",
            wisdomCardIntervalSeconds = prefs.getInt("wisdomCardIntervalSeconds", 12),
            videoEnabled = prefs.getBoolean("videoEnabled", false),
            videoUri = prefs.getString("videoUri", null),
            videoSmartFullscreen = prefs.getBoolean("videoSmartFullscreen", true),
            photoSlideshowEnabled = prefs.getBoolean("photoSlideshowEnabled", false),
            photoSlideshowUris = loadStringList("photoSlideshowUris"),
            photoSlideshowIntervalSeconds = prefs.getInt("photoSlideshowIntervalSeconds", 10),
            slideEnabled = prefs.getBoolean("slideEnabled", false),
            slideIntervalSeconds = prefs.getInt("slideIntervalSeconds", 15),
            qrisSlideEnabled = prefs.getBoolean("qrisSlideEnabled", true),
            laporanSlideEnabled = prefs.getBoolean("laporanSlideEnabled", true),
            kajianSlideEnabled = prefs.getBoolean("kajianSlideEnabled", true),
            slideShowOnlyWhenIdle = prefs.getBoolean("slideShowOnlyWhenIdle", true),
            laporanKeuangan = loadLaporanKeuangan(),
            prayerCardPhotoEnabled = prefs.getBoolean("prayerCardPhotoEnabled", false),
            prayerCardPhotoUri = prefs.getString("prayerCardPhotoUri", null),
            prayerCardPhotoAlpha = prefs.getFloat("prayerCardPhotoAlpha", 0.35f),
            ramadhanModeEnabled = prefs.getBoolean("ramadhanModeEnabled", false),
            showImsakIftarCountdown = prefs.getBoolean("showImsakIftarCountdown", true),
            showTarawihSchedule = prefs.getBoolean("showTarawihSchedule", true),
            showKultumSchedule = prefs.getBoolean("showKultumSchedule", true),
            showMenuSahurIftar = prefs.getBoolean("showMenuSahurIftar", false),
            showImsakBesar = prefs.getBoolean("showImsakBesar", true),
            showIftarBesar = prefs.getBoolean("showIftarBesar", true),
            ramadhanImsakOffsetMinutes = prefs.getInt("ramadhanImsakOffsetMinutes", 10),
            tarawihTime = prefs.getString("tarawihTime", "19:30") ?: "19:30",
            tarawihImam = prefs.getString("tarawihImam", "") ?: "",
            kultumTitle = prefs.getString("kultumTitle", "") ?: "",
            kultumUstadz = prefs.getString("kultumUstadz", "") ?: "",
            kultumTime = prefs.getString("kultumTime", "17:30") ?: "17:30",
            menuSahurText = prefs.getString("menuSahurText", "") ?: "",
            menuIftarText = prefs.getString("menuIftarText", "") ?: "",
            dzikirEnabled = prefs.getBoolean("dzikirEnabled", true),
            dzikirDurationSeconds = prefs.getInt("dzikirDurationSeconds", 120),
            dzikirAutoShowAfterPrayer = prefs.getBoolean("dzikirAutoShowAfterPrayer", true),
            contentRotationEnabled = prefs.getBoolean("contentRotationEnabled", false),
            contentRotationShowAyat = prefs.getBoolean("contentRotationShowAyat", true),
            contentRotationShowHadits = prefs.getBoolean("contentRotationShowHadits", true),
            contentRotationShowAsmaulHusna = prefs.getBoolean("contentRotationShowAsmaulHusna", true),
            contentRotationIntervalSeconds = prefs.getInt("contentRotationIntervalSeconds", 20),
            cctvEnabled = prefs.getBoolean("cctvEnabled", false),
            cctvUrl = prefs.getString("cctvUrl", "") ?: "",
            cctvPosition = runCatching {
                CctvPosition.valueOf(prefs.getString("cctvPosition", "TOP_RIGHT") ?: "TOP_RIGHT")
            }.getOrDefault(CctvPosition.TOP_RIGHT),
            cctvSizePercent = prefs.getInt("cctvSizePercent", 20),
            remoteControlEnabled = prefs.getBoolean("remoteControlEnabled", false),
            remoteServerPort = migratedPort,
            remoteAuthToken = prefs.getString("remoteAuthToken", "masjid-io") ?: "masjid-io",
            pinCode = prefs.getString("pinCode", "1234") ?: "1234",
            kioskModeEnabled = prefs.getBoolean("kioskModeEnabled", true),
            autoStartOnBoot = prefs.getBoolean("autoStartOnBoot", true),
            autoRestartIfCrash = prefs.getBoolean("autoRestartIfCrash", true),
            disableBackButton = prefs.getBoolean("disableBackButton", true),
            lockTaskMode = prefs.getBoolean("lockTaskMode", true),
            isManualTimeEnabled = prefs.getBoolean("isManualTimeEnabled", false),
            manualTimeOffsetSeconds = prefs.getLong("manualTimeOffsetSeconds", 0L),
            keepScreenOn = prefs.getBoolean("keepScreenOn", true),
            autoOnOff = prefs.getBoolean("autoOnOff", false),
            autoOffMinutesAfterIsya = prefs.getInt("autoOffMinutesAfterIsya", 30),
            autoOnMinutesBeforeSubuh = prefs.getInt("autoOnMinutesBeforeSubuh", 15),
            autoOffDialogEnabled = prefs.getBoolean("autoOffDialogEnabled", true),
            idleScreenOff = prefs.getBoolean("idleScreenOff", true),
            idleTimeoutMinutes = prefs.getInt("idleTimeoutMinutes", 30),
            autoBrightness = prefs.getBoolean("autoBrightness", true),
            saveBatteryMode = prefs.getBoolean("saveBatteryMode", false),
            fonnteToken = prefs.getString("fonnteToken", "")?.takeIf { it.isNotBlank() } ?: FonnteHelper.getToken(),
            fonnteGroupId = prefs.getString("fonnteGroupId", "")?.takeIf { it.isNotBlank() } ?: FonnteHelper.getGroupId(),
            whatsappReportEnabled = prefs.getBoolean("whatsappReportEnabled", true)
        )
    }

    // ============================================================
    // SAVE SETTINGS
    // ============================================================
    private fun saveSettings(s: AppSettings) {
        prefs.edit().apply {
            putString("mosqueName", s.mosqueName)
            putString("mosqueAddress", s.mosqueAddress)
            putString("mosqueTakmir", s.mosqueTakmir)
            putBoolean("isGpsEnabled", s.isGpsEnabled)
            putString("country", s.country)
            putString("province", s.province)
            putString("city", s.city)
            putString("district", s.district)
            putString("latitude", s.latitude.toString())
            putString("longitude", s.longitude.toString())
            putString("calculationMethod", s.calculationMethod)
            putString("languageCode", s.languageCode)
            putString("runningText", s.runningText)
            putInt("runningTextSpeed", s.runningTextSpeed)
            putInt("runningTextFontSize", s.runningTextFontSize)
            saveOfficerSchedule(s.officers)
            saveWeeklyOfficers(s.weeklyOfficers)
            putString("officerPhotoUri", s.officerPhotoUri)
            putString("audioMode", s.audioMode.name)
            putInt("beepVolume", s.beepVolume)
            putInt("beepCount", s.beepCount)
            putInt("beepDurationMs", s.beepDurationMs)
            putInt("beepIntervalMs", s.beepIntervalMs)
            putString("adzanFile", s.adzanFile)
            putInt("adzanVolume", s.adzanVolume)
            putInt("adzanWaitMinutes", s.adzanWaitMinutes)
            putInt("iqamahWaitMinutes", s.iqamahWaitMinutes)
            putInt("qobliyahWaitMinutes", s.qobliyahWaitMinutes)
            putInt("prayerFocusDurationMinutes", s.prayerFocusDurationMinutes)
            putInt("focusModeDurationMinutes", s.focusModeDurationMinutes)
            putString("backgroundMode", s.backgroundMode.name)
            putString("customBackgroundUri", s.customBackgroundUri)
            putBoolean("animationsEnabled", s.animationsEnabled)
            putBoolean("showBirdsAnimation", s.showBirdsAnimation)
            // ============================================================
            // RESPONSIVE LAYOUT (V1.30.3)
            // ============================================================
            putBoolean("tvAutoScaleEnabled", s.tvAutoScaleEnabled)
            putFloat("tvSafeAreaPercent", s.tvSafeAreaPercent)
            putString("tvLayoutPreset", s.tvLayoutPreset)
            // ============================================================
            putString("qrisPhotoUri", s.qrisPhotoUri)
            putString("qrisImageUri", s.qrisImageUri)
            putInt("qrisIntervalMinutes", s.qrisIntervalMinutes)
            putInt("qrisDisplayDurationSeconds", s.qrisDisplayDurationSeconds)
            putString("bankName", s.bankName)
            putString("bankAccountNumber", s.bankAccountNumber)
            putString("bankAccountHolder", s.bankAccountHolder)
            putString("wisdomCardAnimation", s.wisdomCardAnimation)
            putInt("wisdomCardIntervalSeconds", s.wisdomCardIntervalSeconds)
            putBoolean("videoEnabled", s.videoEnabled)
            putString("videoUri", s.videoUri)
            putBoolean("videoSmartFullscreen", s.videoSmartFullscreen)
            putBoolean("photoSlideshowEnabled", s.photoSlideshowEnabled)
            saveStringList("photoSlideshowUris", s.photoSlideshowUris)
            putInt("photoSlideshowIntervalSeconds", s.photoSlideshowIntervalSeconds)
            putBoolean("slideEnabled", s.slideEnabled)
            putInt("slideIntervalSeconds", s.slideIntervalSeconds)
            putBoolean("qrisSlideEnabled", s.qrisSlideEnabled)
            putBoolean("laporanSlideEnabled", s.laporanSlideEnabled)
            putBoolean("kajianSlideEnabled", s.kajianSlideEnabled)
            putBoolean("slideShowOnlyWhenIdle", s.slideShowOnlyWhenIdle)
            saveLaporanKeuangan(s.laporanKeuangan)
            putBoolean("prayerCardPhotoEnabled", s.prayerCardPhotoEnabled)
            putString("prayerCardPhotoUri", s.prayerCardPhotoUri)
            putFloat("prayerCardPhotoAlpha", s.prayerCardPhotoAlpha)
            putBoolean("ramadhanModeEnabled", s.ramadhanModeEnabled)
            putBoolean("showImsakIftarCountdown", s.showImsakIftarCountdown)
            putBoolean("showTarawihSchedule", s.showTarawihSchedule)
            putBoolean("showKultumSchedule", s.showKultumSchedule)
            putBoolean("showMenuSahurIftar", s.showMenuSahurIftar)
            putBoolean("showImsakBesar", s.showImsakBesar)
            putBoolean("showIftarBesar", s.showIftarBesar)
            putInt("ramadhanImsakOffsetMinutes", s.ramadhanImsakOffsetMinutes)
            putString("tarawihTime", s.tarawihTime)
            putString("tarawihImam", s.tarawihImam)
            putString("kultumTitle", s.kultumTitle)
            putString("kultumUstadz", s.kultumUstadz)
            putString("kultumTime", s.kultumTime)
            putString("menuSahurText", s.menuSahurText)
            putString("menuIftarText", s.menuIftarText)
            putBoolean("dzikirEnabled", s.dzikirEnabled)
            putInt("dzikirDurationSeconds", s.dzikirDurationSeconds)
            putBoolean("dzikirAutoShowAfterPrayer", s.dzikirAutoShowAfterPrayer)
            putBoolean("contentRotationEnabled", s.contentRotationEnabled)
            putBoolean("contentRotationShowAyat", s.contentRotationShowAyat)
            putBoolean("contentRotationShowHadits", s.contentRotationShowHadits)
            putBoolean("contentRotationShowAsmaulHusna", s.contentRotationShowAsmaulHusna)
            putInt("contentRotationIntervalSeconds", s.contentRotationIntervalSeconds)
            putBoolean("cctvEnabled", s.cctvEnabled)
            putString("cctvUrl", s.cctvUrl)
            putString("cctvPosition", s.cctvPosition.name)
            putInt("cctvSizePercent", s.cctvSizePercent)
            putBoolean("remoteControlEnabled", s.remoteControlEnabled)
            putInt("remoteServerPort", s.remoteServerPort)
            putString("remoteAuthToken", s.remoteAuthToken)
            putString("pinCode", s.pinCode)
            putBoolean("kioskModeEnabled", s.kioskModeEnabled)
            putBoolean("autoStartOnBoot", s.autoStartOnBoot)
            putBoolean("autoRestartIfCrash", s.autoRestartIfCrash)
            putBoolean("disableBackButton", s.disableBackButton)
            putBoolean("lockTaskMode", s.lockTaskMode)
            putBoolean("isManualTimeEnabled", s.isManualTimeEnabled)
            putLong("manualTimeOffsetSeconds", s.manualTimeOffsetSeconds)
            putBoolean("keepScreenOn", s.keepScreenOn)
            putBoolean("autoOnOff", s.autoOnOff)
            putInt("autoOffMinutesAfterIsya", s.autoOffMinutesAfterIsya)
            putInt("autoOnMinutesBeforeSubuh", s.autoOnMinutesBeforeSubuh)
            putBoolean("autoOffDialogEnabled", s.autoOffDialogEnabled)
            putBoolean("idleScreenOff", s.idleScreenOff)
            putInt("idleTimeoutMinutes", s.idleTimeoutMinutes)
            putBoolean("autoBrightness", s.autoBrightness)
            putBoolean("saveBatteryMode", s.saveBatteryMode)
            putString("fonnteToken", s.fonnteToken)
            putString("fonnteGroupId", s.fonnteGroupId)
            putBoolean("whatsappReportEnabled", s.whatsappReportEnabled)
        }.apply()
    }

    private fun loadStringList(key: String): List<String> {
        val raw = prefs.getString(key, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i -> arr.getString(i) }
        }.getOrDefault(emptyList())
    }

    private fun saveStringList(key: String, list: List<String>) {
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        prefs.edit().putString(key, arr.toString()).apply()
    }

    private fun loadWeeklyOfficers(): List<DailyOfficerItem> {
        val raw = prefs.getString("weeklyOfficers", null) ?: return AppSettings.createDefaultWeeklySchedule()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                DailyOfficerItem(
                    dayName = o.optString("dayName", "Senin"),
                    imamSubuh = o.optString("imamSubuh", ""),
                    muadzinSubuh = o.optString("muadzinSubuh", ""),
                    fotoImamSubuh = o.optString("fotoImamSubuh", "").ifEmpty { null },
                    fotoMuadzinSubuh = o.optString("fotoMuadzinSubuh", "").ifEmpty { null },
                    imamDzuhur = o.optString("imamDzuhur", ""),
                    muadzinDzuhur = o.optString("muadzinDzuhur", ""),
                    fotoImamDzuhur = o.optString("fotoImamDzuhur", "").ifEmpty { null },
                    fotoMuadzinDzuhur = o.optString("fotoMuadzinDzuhur", "").ifEmpty { null },
                    imamAshar = o.optString("imamAshar", ""),
                    muadzinAshar = o.optString("muadzinAshar", ""),
                    fotoImamAshar = o.optString("fotoImamAshar", "").ifEmpty { null },
                    fotoMuadzinAshar = o.optString("fotoMuadzinAshar", "").ifEmpty { null },
                    imamMaghrib = o.optString("imamMaghrib", ""),
                    muadzinMaghrib = o.optString("muadzinMaghrib", ""),
                    fotoImamMaghrib = o.optString("fotoImamMaghrib", "").ifEmpty { null },
                    fotoMuadzinMaghrib = o.optString("fotoMuadzinMaghrib", "").ifEmpty { null },
                    imamIsya = o.optString("imamIsya", ""),
                    muadzinIsya = o.optString("muadzinIsya", ""),
                    fotoImamIsya = o.optString("fotoImamIsya", "").ifEmpty { null },
                    fotoMuadzinIsya = o.optString("fotoMuadzinIsya", "").ifEmpty { null },
                    khatibJumat = o.optString("khatibJumat", ""),
                    temaJumat = o.optString("temaJumat", ""),
                    fotoKhatibJumat = o.optString("fotoKhatibJumat", "").ifEmpty { null },
                    ustadzKajian = o.optString("ustadzKajian", ""),
                    temaKajian = o.optString("temaKajian", ""),
                    fotoUstadzKajian = o.optString("fotoUstadzKajian", "").ifEmpty { null }
                )
            }
        }.getOrDefault(AppSettings.createDefaultWeeklySchedule())
    }

    private fun saveWeeklyOfficers(list: List<DailyOfficerItem>) {
        val arr = JSONArray()
        list.forEach { o ->
            val obj = JSONObject().apply {
                put("dayName", o.dayName)
                put("imamSubuh", o.imamSubuh); put("muadzinSubuh", o.muadzinSubuh)
                put("fotoImamSubuh", o.fotoImamSubuh ?: ""); put("fotoMuadzinSubuh", o.fotoMuadzinSubuh ?: "")
                put("imamDzuhur", o.imamDzuhur); put("muadzinDzuhur", o.muadzinDzuhur)
                put("fotoImamDzuhur", o.fotoImamDzuhur ?: ""); put("fotoMuadzinDzuhur", o.fotoMuadzinDzuhur ?: "")
                put("imamAshar", o.imamAshar); put("muadzinAshar", o.muadzinAshar)
                put("fotoImamAshar", o.fotoImamAshar ?: ""); put("fotoMuadzinAshar", o.fotoMuadzinAshar ?: "")
                put("imamMaghrib", o.imamMaghrib); put("muadzinMaghrib", o.muadzinMaghrib)
                put("fotoImamMaghrib", o.fotoImamMaghrib ?: ""); put("fotoMuadzinMaghrib", o.fotoMuadzinMaghrib ?: "")
                put("imamIsya", o.imamIsya); put("muadzinIsya", o.muadzinIsya)
                put("fotoImamIsya", o.fotoImamIsya ?: ""); put("fotoMuadzinIsya", o.fotoMuadzinIsya ?: "")
                put("khatibJumat", o.khatibJumat); put("temaJumat", o.temaJumat)
                put("fotoKhatibJumat", o.fotoKhatibJumat ?: "")
                put("ustadzKajian", o.ustadzKajian); put("temaKajian", o.temaKajian)
                put("fotoUstadzKajian", o.fotoUstadzKajian ?: "")
            }
            arr.put(obj)
        }
        prefs.edit().putString("weeklyOfficers", arr.toString()).apply()
    }

    private fun loadOfficerSchedule(): OfficerSchedule {
        val raw = prefs.getString("officers", null) ?: return OfficerSchedule()
        return runCatching {
            val o = JSONObject(raw)
            OfficerSchedule(
                imamSubuh = o.optString("imamSubuh", ""),
                muadzinSubuh = o.optString("muadzinSubuh", ""),
                imamDzuhur = o.optString("imamDzuhur", ""),
                muadzinDzuhur = o.optString("muadzinDzuhur", ""),
                imamAshar = o.optString("imamAshar", ""),
                muadzinAshar = o.optString("muadzinAshar", ""),
                imamMaghrib = o.optString("imamMaghrib", ""),
                muadzinMaghrib = o.optString("muadzinMaghrib", ""),
                imamIsya = o.optString("imamIsya", ""),
                muadzinIsya = o.optString("muadzinIsya", ""),
                khatibJumat = o.optString("khatibJumat", ""),
                temaJumat = o.optString("temaJumat", ""),
                ustadzKajian = o.optString("ustadzKajian", ""),
                jadwalKajian = o.optString("jadwalKajian", ""),
                temaKajian = o.optString("temaKajian", "")
            )
        }.getOrDefault(OfficerSchedule())
    }

    private fun saveOfficerSchedule(s: OfficerSchedule) {
        val obj = JSONObject().apply {
            put("imamSubuh", s.imamSubuh); put("muadzinSubuh", s.muadzinSubuh)
            put("imamDzuhur", s.imamDzuhur); put("muadzinDzuhur", s.muadzinDzuhur)
            put("imamAshar", s.imamAshar); put("muadzinAshar", s.muadzinAshar)
            put("imamMaghrib", s.imamMaghrib); put("muadzinMaghrib", s.muadzinMaghrib)
            put("imamIsya", s.imamIsya); put("muadzinIsya", s.muadzinIsya)
            put("khatibJumat", s.khatibJumat); put("temaJumat", s.temaJumat)
            put("ustadzKajian", s.ustadzKajian); put("jadwalKajian", s.jadwalKajian)
            put("temaKajian", s.temaKajian)
        }
        prefs.edit().putString("officers", obj.toString()).apply()
    }

    private fun loadLaporanKeuangan(): LaporanKeuangan {
        val raw = prefs.getString("laporanKeuangan", null) ?: return LaporanKeuangan()
        return runCatching {
            val o = JSONObject(raw)
            LaporanKeuangan(
                saldoSebelumnya = o.optLong("saldoSebelumnya", 41_000),
                pemasukanJumat = o.optLong("pemasukanJumat", 5_100_000),
                pemasukanUmum = o.optLong("pemasukanUmum", 236_000),
                pengeluaranDakwah = o.optLong("pengeluaranDakwah", 2_900_000),
                pengeluaranSosial = o.optLong("pengeluaranSosial", 1_800_000),
                pengeluaranOperasional = o.optLong("pengeluaranOperasional", 656_000),
                periodeMulai = o.optString("periodeMulai", ""),
                periodeSelesai = o.optString("periodeSelesai", "")
            )
        }.getOrDefault(LaporanKeuangan())
    }

    private fun saveLaporanKeuangan(l: LaporanKeuangan) {
        val obj = JSONObject().apply {
            put("saldoSebelumnya", l.saldoSebelumnya)
            put("pemasukanJumat", l.pemasukanJumat)
            put("pemasukanUmum", l.pemasukanUmum)
            put("pengeluaranDakwah", l.pengeluaranDakwah)
            put("pengeluaranSosial", l.pengeluaranSosial)
            put("pengeluaranOperasional", l.pengeluaranOperasional)
            put("periodeMulai", l.periodeMulai)
            put("periodeSelesai", l.periodeSelesai)
        }
        prefs.edit().putString("laporanKeuangan", obj.toString()).apply()
    }

    fun exportSummary(): String {
        val s = _settings.value
        return buildString {
            appendLine("IDENTITAS MASJID")
            appendLine("- Nama Masjid : ${s.mosqueName}")
            appendLine("- Alamat      : ${s.mosqueAddress}")
            appendLine("- Takmir      : ${s.mosqueTakmir}")
            appendLine()
            appendLine("LOKASI")
            appendLine("- Kota        : ${s.city}")
            appendLine("- Provinsi    : ${s.province}")
            appendLine("- Latitude    : ${s.latitude}")
            appendLine("- Longitude   : ${s.longitude}")
            appendLine("- Metode      : ${s.calculationMethod}")
            appendLine()
            appendLine("TAMPILAN")
            appendLine("- Background Mode : ${s.backgroundMode.name}")
            appendLine("- Keep Screen On  : ${s.keepScreenOn}")
            appendLine("- Kiosk Mode      : ${s.kioskModeEnabled}")
            appendLine("- Animasi         : ${s.animationsEnabled}")
            appendLine("- Burung Terbang  : ${s.showBirdsAnimation}")
            appendLine("- Auto Scale TV   : ${s.tvAutoScaleEnabled}")
            appendLine("- Safe Area       : ${s.tvSafeAreaPercent}%")
            appendLine("- Layout Preset   : ${s.tvLayoutPreset}")
            appendLine()
            appendLine("AUDIO")
            appendLine("- Mode Audio    : ${s.audioMode.name}")
            appendLine("- Volume Beep   : ${s.beepVolume}%")
            appendLine("- Jumlah Beep   : ${s.beepCount}x")
            appendLine("- Durasi Beep   : ${s.beepDurationMs}ms")
            appendLine("- Jeda Beep     : ${s.beepIntervalMs}ms")
            appendLine("- File Adzan    : ${s.adzanFile}")
            appendLine("- Volume Adzan  : ${s.adzanVolume}%")
            appendLine()
            appendLine("VIDEO & FOTO")
            appendLine("- Video Enabled     : ${s.videoEnabled}")
            appendLine("- Video Smart Full  : ${s.videoSmartFullscreen}")
            appendLine("- Photo Slideshow   : ${s.photoSlideshowEnabled}")
            appendLine("- Interval Foto     : ${s.photoSlideshowIntervalSeconds} detik")
            appendLine("- Jumlah Foto       : ${s.photoSlideshowUris.size} foto")
            appendLine()
            appendLine("RUNNING TEXT")
            appendLine("- Isi Running Text : ${s.runningText}")
            appendLine("- Kecepatan        : ${s.runningTextSpeed} (1-10)")
            appendLine("- Ukuran Font      : ${s.runningTextFontSize}")
            appendLine()
            appendLine("MODE FOKUS")
            appendLine("- Durasi Mode Fokus  : ${s.focusModeDurationMinutes} menit")
            appendLine("- Jeda Iqamah        : ${s.iqamahWaitMinutes} menit")
            appendLine("- Countdown Qobliyah : ${s.qobliyahWaitMinutes} menit")
            appendLine()
            appendLine("iO CONTROL")
            appendLine("- Remote Control    : ${s.remoteControlEnabled}")
            appendLine("- Port Server       : ${s.remoteServerPort}")
            appendLine()
            appendLine("WHATSAPP FONNTE")
            appendLine("- WA Report Enabled : ${s.whatsappReportEnabled}")
            appendLine("- Token Fonnte      : ${if (s.fonnteToken.isEmpty()) "(kosong)" else "(terisi)"}")
            appendLine("- Group ID          : ${if (s.fonnteGroupId.isEmpty()) "(kosong)" else s.fonnteGroupId}")
        }
    }
}
