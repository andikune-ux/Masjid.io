package dev.andikune.masjidio.util

import android.content.Context
import android.util.Log
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.data.model.AudioMode
import dev.andikune.masjidio.data.model.BackgroundMode
import dev.andikune.masjidio.data.model.CctvPosition
import dev.andikune.masjidio.data.model.DailyOfficerItem
import dev.andikune.masjidio.data.model.LaporanKeuangan
import dev.andikune.masjidio.data.model.OfficerSchedule
import dev.andikune.masjidio.data.model.PinLockMode
import org.json.JSONArray
import org.json.JSONObject

object SettingsTransferHelper {

    private const val TAG = "SettingsTransfer"
    private const val FORMAT_VERSION = 1
    private const val APP_ID = "MASJID.IO"

    // ============================================================
    // SERIALIZE
    // ============================================================
    fun serializeSettings(settings: AppSettings): String {
        val root = JSONObject()
        root.put("_format", FORMAT_VERSION)
        root.put("_app", APP_ID)
        root.put("_timestamp", System.currentTimeMillis())

        val s = JSONObject()

        s.put("mosqueName", settings.mosqueName)
        s.put("mosqueAddress", settings.mosqueAddress)
        s.put("mosqueTakmir", settings.mosqueTakmir)

        s.put("isGpsEnabled", settings.isGpsEnabled)
        s.put("country", settings.country)
        s.put("province", settings.province)
        s.put("city", settings.city)
        s.put("district", settings.district)
        s.put("latitude", settings.latitude)
        s.put("longitude", settings.longitude)
        s.put("calculationMethod", settings.calculationMethod)
        s.put("languageCode", settings.languageCode)

        s.put("runningText", settings.runningText)
        s.put("runningTextSpeed", settings.runningTextSpeed)
        s.put("runningTextFontSize", settings.runningTextFontSize)

        s.put("officers", serializeOfficerSchedule(settings.officers))
        s.put("weeklyOfficers", serializeWeeklyOfficers(settings.weeklyOfficers))
        s.put("officerPhotoUri", settings.officerPhotoUri ?: "")

        s.put("audioMode", settings.audioMode.name)
        s.put("beepVolume", settings.beepVolume)
        s.put("beepCount", settings.beepCount)
        s.put("beepDurationMs", settings.beepDurationMs)
        s.put("beepIntervalMs", settings.beepIntervalMs)
        s.put("adzanFile", settings.adzanFile)
        s.put("adzanVolume", settings.adzanVolume)
        s.put("adzanWaitMinutes", settings.adzanWaitMinutes)
        s.put("iqamahWaitMinutes", settings.iqamahWaitMinutes)
        s.put("qobliyahWaitMinutes", settings.qobliyahWaitMinutes)
        s.put("prayerFocusDurationMinutes", settings.prayerFocusDurationMinutes)
        s.put("focusModeDurationMinutes", settings.focusModeDurationMinutes)

        s.put("backgroundMode", settings.backgroundMode.name)
        s.put("customBackgroundUri", settings.customBackgroundUri ?: "")
        s.put("animationsEnabled", settings.animationsEnabled)
        s.put("showBirdsAnimation", settings.showBirdsAnimation)

        s.put("tvAutoScaleEnabled", settings.tvAutoScaleEnabled)
        s.put("tvSafeAreaPercent", settings.tvSafeAreaPercent.toDouble())
        s.put("tvLayoutPreset", settings.tvLayoutPreset)

        s.put("qrisPhotoUri", settings.qrisPhotoUri ?: "")
        s.put("qrisImageUri", settings.qrisImageUri)
        s.put("qrisIntervalMinutes", settings.qrisIntervalMinutes)
        s.put("qrisDisplayDurationSeconds", settings.qrisDisplayDurationSeconds)
        s.put("bankName", settings.bankName)
        s.put("bankAccountNumber", settings.bankAccountNumber)
        s.put("bankAccountHolder", settings.bankAccountHolder)

        s.put("wisdomCardAnimation", settings.wisdomCardAnimation)
        s.put("wisdomCardIntervalSeconds", settings.wisdomCardIntervalSeconds)

        s.put("videoEnabled", settings.videoEnabled)
        s.put("videoUri", settings.videoUri ?: "")
        s.put("videoSmartFullscreen", settings.videoSmartFullscreen)
        s.put("photoSlideshowEnabled", settings.photoSlideshowEnabled)
        s.put("photoSlideshowUris", serializeStringList(settings.photoSlideshowUris))
        s.put("photoSlideshowIntervalSeconds", settings.photoSlideshowIntervalSeconds)

        s.put("slideEnabled", settings.slideEnabled)
        s.put("slideIntervalSeconds", settings.slideIntervalSeconds)
        s.put("qrisSlideEnabled", settings.qrisSlideEnabled)
        s.put("laporanSlideEnabled", settings.laporanSlideEnabled)
        s.put("kajianSlideEnabled", settings.kajianSlideEnabled)
        s.put("slideShowOnlyWhenIdle", settings.slideShowOnlyWhenIdle)
        s.put("laporanKeuangan", serializeLaporanKeuangan(settings.laporanKeuangan))

        s.put("prayerCardPhotoEnabled", settings.prayerCardPhotoEnabled)
        s.put("prayerCardPhotoUri", settings.prayerCardPhotoUri ?: "")
        s.put("prayerCardPhotoAlpha", settings.prayerCardPhotoAlpha.toDouble())

        s.put("ramadhanModeEnabled", settings.ramadhanModeEnabled)
        s.put("showImsakIftarCountdown", settings.showImsakIftarCountdown)
        s.put("showTarawihSchedule", settings.showTarawihSchedule)
        s.put("showKultumSchedule", settings.showKultumSchedule)
        s.put("showMenuSahurIftar", settings.showMenuSahurIftar)
        s.put("showImsakBesar", settings.showImsakBesar)
        s.put("showIftarBesar", settings.showIftarBesar)
        s.put("ramadhanImsakOffsetMinutes", settings.ramadhanImsakOffsetMinutes)
        s.put("tarawihTime", settings.tarawihTime)
        s.put("tarawihImam", settings.tarawihImam)
        s.put("kultumTitle", settings.kultumTitle)
        s.put("kultumUstadz", settings.kultumUstadz)
        s.put("kultumTime", settings.kultumTime)
        s.put("menuSahurText", settings.menuSahurText)
        s.put("menuIftarText", settings.menuIftarText)

        s.put("dzikirEnabled", settings.dzikirEnabled)
        s.put("dzikirDurationSeconds", settings.dzikirDurationSeconds)
        s.put("dzikirAutoShowAfterPrayer", settings.dzikirAutoShowAfterPrayer)

        s.put("contentRotationEnabled", settings.contentRotationEnabled)
        s.put("contentRotationShowAyat", settings.contentRotationShowAyat)
        s.put("contentRotationShowHadits", settings.contentRotationShowHadits)
        s.put("contentRotationShowAsmaulHusna", settings.contentRotationShowAsmaulHusna)
        s.put("contentRotationIntervalSeconds", settings.contentRotationIntervalSeconds)

        s.put("cctvEnabled", settings.cctvEnabled)
        s.put("cctvUrl", settings.cctvUrl)
        s.put("cctvPosition", settings.cctvPosition.name)
        s.put("cctvSizePercent", settings.cctvSizePercent)

        s.put("pinCode", settings.pinCode)
        s.put("kioskModeEnabled", settings.kioskModeEnabled)
        s.put("autoStartOnBoot", settings.autoStartOnBoot)
        s.put("autoRestartIfCrash", settings.autoRestartIfCrash)
        s.put("disableBackButton", settings.disableBackButton)
        s.put("lockTaskMode", settings.lockTaskMode)

        s.put("isManualTimeEnabled", settings.isManualTimeEnabled)
        s.put("manualTimeOffsetSeconds", settings.manualTimeOffsetSeconds)

        s.put("keepScreenOn", settings.keepScreenOn)

        // ============================================================
        // V1.04.423 — JADWAL ON/OFF OTOMATIS
        // Field lama autoOnTime & autoOffTime DIHAPUS dari serialisasi
        // ============================================================
        s.put("autoOnOff", settings.autoOnOff)
        s.put("autoOffMinutesAfterIsya", settings.autoOffMinutesAfterIsya)
        s.put("autoOnMinutesBeforeSubuh", settings.autoOnMinutesBeforeSubuh)
        s.put("autoOffDialogEnabled", settings.autoOffDialogEnabled)

        s.put("idleScreenOff", settings.idleScreenOff)
        s.put("idleTimeoutMinutes", settings.idleTimeoutMinutes)
        s.put("autoBrightness", settings.autoBrightness)
        s.put("saveBatteryMode", settings.saveBatteryMode)

        s.put("fonnteToken", settings.fonnteToken)
        s.put("fonnteGroupId", settings.fonnteGroupId)
        s.put("whatsappReportEnabled", settings.whatsappReportEnabled)

        s.put("pinLockMode", settings.pinLockMode.name)

        root.put("settings", s)
        return root.toString()
    }
    // ============================================================
// DESERIALIZE
// ============================================================
fun deserializeSettings(json: String, default: AppSettings): AppSettings? {
    return try {
        val root = JSONObject(json)
        val format = root.optInt("_format", -1)
        if (format <= 0) {
            Log.e(TAG, "Format JSON tidak valid")
            return null
        }
        val s = root.optJSONObject("settings") ?: return null

        AppSettings(
            mosqueName = s.optString("mosqueName", default.mosqueName),
            mosqueAddress = s.optString("mosqueAddress", default.mosqueAddress),
            mosqueTakmir = s.optString("mosqueTakmir", default.mosqueTakmir),
            isGpsEnabled = s.optBoolean("isGpsEnabled", default.isGpsEnabled),
            country = s.optString("country", default.country),
            province = s.optString("province", default.province),
            city = s.optString("city", default.city),
            district = s.optString("district", default.district),
            latitude = s.optDouble("latitude", default.latitude),
            longitude = s.optDouble("longitude", default.longitude),
            calculationMethod = s.optString("calculationMethod", default.calculationMethod),
            languageCode = s.optString("languageCode", default.languageCode),
            runningText = s.optString("runningText", default.runningText),
            runningTextSpeed = s.optInt("runningTextSpeed", default.runningTextSpeed),
            runningTextFontSize = s.optInt("runningTextFontSize", default.runningTextFontSize),
            officers = deserializeOfficerSchedule(s.optJSONObject("officers"), default.officers),
            weeklyOfficers = deserializeWeeklyOfficers(s.optJSONArray("weeklyOfficers"), default.weeklyOfficers),
            officerPhotoUri = s.optString("officerPhotoUri", default.officerPhotoUri ?: "").ifEmpty { default.officerPhotoUri },
            audioMode = runCatching {
                AudioMode.valueOf(s.optString("audioMode", default.audioMode.name))
            }.getOrDefault(default.audioMode),
            beepVolume = s.optInt("beepVolume", default.beepVolume),
            beepCount = s.optInt("beepCount", default.beepCount),
            beepDurationMs = s.optInt("beepDurationMs", default.beepDurationMs),
            beepIntervalMs = s.optInt("beepIntervalMs", default.beepIntervalMs),
            adzanFile = s.optString("adzanFile", default.adzanFile),
            adzanVolume = s.optInt("adzanVolume", default.adzanVolume),
            adzanWaitMinutes = s.optInt("adzanWaitMinutes", default.adzanWaitMinutes),
            iqamahWaitMinutes = s.optInt("iqamahWaitMinutes", default.iqamahWaitMinutes),
            qobliyahWaitMinutes = s.optInt("qobliyahWaitMinutes", default.qobliyahWaitMinutes),
            prayerFocusDurationMinutes = s.optInt("prayerFocusDurationMinutes", default.prayerFocusDurationMinutes),
            focusModeDurationMinutes = s.optInt("focusModeDurationMinutes", default.focusModeDurationMinutes),
            backgroundMode = runCatching {
                BackgroundMode.valueOf(s.optString("backgroundMode", default.backgroundMode.name))
            }.getOrDefault(default.backgroundMode),
            customBackgroundUri = s.optString("customBackgroundUri", default.customBackgroundUri ?: "").ifEmpty { default.customBackgroundUri },
            animationsEnabled = s.optBoolean("animationsEnabled", default.animationsEnabled),
            showBirdsAnimation = s.optBoolean("showBirdsAnimation", default.showBirdsAnimation),
            tvAutoScaleEnabled = s.optBoolean("tvAutoScaleEnabled", default.tvAutoScaleEnabled),
            tvSafeAreaPercent = s.optDouble("tvSafeAreaPercent", default.tvSafeAreaPercent.toDouble()).toFloat(),
            tvLayoutPreset = s.optString("tvLayoutPreset", default.tvLayoutPreset),
            qrisPhotoUri = s.optString("qrisPhotoUri", default.qrisPhotoUri ?: "").ifEmpty { default.qrisPhotoUri },
            qrisImageUri = s.optString("qrisImageUri", default.qrisImageUri),
            qrisIntervalMinutes = s.optInt("qrisIntervalMinutes", default.qrisIntervalMinutes),
            qrisDisplayDurationSeconds = s.optInt("qrisDisplayDurationSeconds", default.qrisDisplayDurationSeconds),
            bankName = s.optString("bankName", default.bankName),
            bankAccountNumber = s.optString("bankAccountNumber", default.bankAccountNumber),
            bankAccountHolder = s.optString("bankAccountHolder", default.bankAccountHolder),
            wisdomCardAnimation = s.optString("wisdomCardAnimation", default.wisdomCardAnimation),
            wisdomCardIntervalSeconds = s.optInt("wisdomCardIntervalSeconds", default.wisdomCardIntervalSeconds),
            videoEnabled = s.optBoolean("videoEnabled", default.videoEnabled),
            videoUri = s.optString("videoUri", default.videoUri ?: "").ifEmpty { default.videoUri },
            videoSmartFullscreen = s.optBoolean("videoSmartFullscreen", default.videoSmartFullscreen),
            photoSlideshowEnabled = s.optBoolean("photoSlideshowEnabled", default.photoSlideshowEnabled),
            photoSlideshowUris = deserializeStringList(s.optJSONArray("photoSlideshowUris"), default.photoSlideshowUris),
            photoSlideshowIntervalSeconds = s.optInt("photoSlideshowIntervalSeconds", default.photoSlideshowIntervalSeconds),
            slideEnabled = s.optBoolean("slideEnabled", default.slideEnabled),
            slideIntervalSeconds = s.optInt("slideIntervalSeconds", default.slideIntervalSeconds),
            qrisSlideEnabled = s.optBoolean("qrisSlideEnabled", default.qrisSlideEnabled),
            laporanSlideEnabled = s.optBoolean("laporanSlideEnabled", default.laporanSlideEnabled),
            kajianSlideEnabled = s.optBoolean("kajianSlideEnabled", default.kajianSlideEnabled),
            slideShowOnlyWhenIdle = s.optBoolean("slideShowOnlyWhenIdle", default.slideShowOnlyWhenIdle),
            laporanKeuangan = deserializeLaporanKeuangan(s.optJSONObject("laporanKeuangan"), default.laporanKeuangan),
            prayerCardPhotoEnabled = s.optBoolean("prayerCardPhotoEnabled", default.prayerCardPhotoEnabled),
            prayerCardPhotoUri = s.optString("prayerCardPhotoUri", default.prayerCardPhotoUri ?: "").ifEmpty { default.prayerCardPhotoUri },
            prayerCardPhotoAlpha = s.optDouble("prayerCardPhotoAlpha", default.prayerCardPhotoAlpha.toDouble()).toFloat(),
            ramadhanModeEnabled = s.optBoolean("ramadhanModeEnabled", default.ramadhanModeEnabled),
            showImsakIftarCountdown = s.optBoolean("showImsakIftarCountdown", default.showImsakIftarCountdown),
            showTarawihSchedule = s.optBoolean("showTarawihSchedule", default.showTarawihSchedule),
            showKultumSchedule = s.optBoolean("showKultumSchedule", default.showKultumSchedule),
            showMenuSahurIftar = s.optBoolean("showMenuSahurIftar", default.showMenuSahurIftar),
            showImsakBesar = s.optBoolean("showImsakBesar", default.showImsakBesar),
            showIftarBesar = s.optBoolean("showIftarBesar", default.showIftarBesar),
            ramadhanImsakOffsetMinutes = s.optInt("ramadhanImsakOffsetMinutes", default.ramadhanImsakOffsetMinutes),
            tarawihTime = s.optString("tarawihTime", default.tarawihTime),
            tarawihImam = s.optString("tarawihImam", default.tarawihImam),
            kultumTitle = s.optString("kultumTitle", default.kultumTitle),
            kultumUstadz = s.optString("kultumUstadz", default.kultumUstadz),
            kultumTime = s.optString("kultumTime", default.kultumTime),
            menuSahurText = s.optString("menuSahurText", default.menuSahurText),
            menuIftarText = s.optString("menuIftarText", default.menuIftarText),
            dzikirEnabled = s.optBoolean("dzikirEnabled", default.dzikirEnabled),
            dzikirDurationSeconds = s.optInt("dzikirDurationSeconds", default.dzikirDurationSeconds),
            dzikirAutoShowAfterPrayer = s.optBoolean("dzikirAutoShowAfterPrayer", default.dzikirAutoShowAfterPrayer),
            contentRotationEnabled = s.optBoolean("contentRotationEnabled", default.contentRotationEnabled),
            contentRotationShowAyat = s.optBoolean("contentRotationShowAyat", default.contentRotationShowAyat),
            contentRotationShowHadits = s.optBoolean("contentRotationShowHadits", default.contentRotationShowHadits),
            contentRotationShowAsmaulHusna = s.optBoolean("contentRotationShowAsmaulHusna", default.contentRotationShowAsmaulHusna),
            contentRotationIntervalSeconds = s.optInt("contentRotationIntervalSeconds", default.contentRotationIntervalSeconds),
            cctvEnabled = s.optBoolean("cctvEnabled", default.cctvEnabled),
            cctvUrl = s.optString("cctvUrl", default.cctvUrl),
            cctvPosition = runCatching {
                CctvPosition.valueOf(s.optString("cctvPosition", default.cctvPosition.name))
            }.getOrDefault(default.cctvPosition),
            cctvSizePercent = s.optInt("cctvSizePercent", default.cctvSizePercent),
            remoteControlEnabled = default.remoteControlEnabled,
            remoteServerPort = default.remoteServerPort,
            remoteAuthToken = default.remoteAuthToken,
            pinCode = s.optString("pinCode", default.pinCode),
            kioskModeEnabled = s.optBoolean("kioskModeEnabled", default.kioskModeEnabled),
            autoStartOnBoot = s.optBoolean("autoStartOnBoot", default.autoStartOnBoot),
            autoRestartIfCrash = s.optBoolean("autoRestartIfCrash", default.autoRestartIfCrash),
            disableBackButton = s.optBoolean("disableBackButton", default.disableBackButton),
            lockTaskMode = s.optBoolean("lockTaskMode", default.lockTaskMode),
            isManualTimeEnabled = s.optBoolean("isManualTimeEnabled", default.isManualTimeEnabled),
            manualTimeOffsetSeconds = s.optLong("manualTimeOffsetSeconds", default.manualTimeOffsetSeconds),
            keepScreenOn = s.optBoolean("keepScreenOn", default.keepScreenOn),
            // ============================================================
            // V1.04.423 — JADWAL ON/OFF OTOMATIS
            // ============================================================
            autoOnOff = s.optBoolean("autoOnOff", default.autoOnOff),
            autoOffMinutesAfterIsya = s.optInt("autoOffMinutesAfterIsya", default.autoOffMinutesAfterIsya),
            autoOnMinutesBeforeSubuh = s.optInt("autoOnMinutesBeforeSubuh", default.autoOnMinutesBeforeSubuh),
            autoOffDialogEnabled = s.optBoolean("autoOffDialogEnabled", default.autoOffDialogEnabled),
            // ============================================================
            idleScreenOff = s.optBoolean("idleScreenOff", default.idleScreenOff),
            idleTimeoutMinutes = s.optInt("idleTimeoutMinutes", default.idleTimeoutMinutes),
            autoBrightness = s.optBoolean("autoBrightness", default.autoBrightness),
            saveBatteryMode = s.optBoolean("saveBatteryMode", default.saveBatteryMode),
            fonnteToken = s.optString("fonnteToken", default.fonnteToken),
            fonnteGroupId = s.optString("fonnteGroupId", default.fonnteGroupId),
            whatsappReportEnabled = s.optBoolean("whatsappReportEnabled", default.whatsappReportEnabled),
            // ============================================================
            pinLockMode = runCatching {
                PinLockMode.valueOf(s.optString("pinLockMode", default.pinLockMode.name))
            }.getOrDefault(default.pinLockMode)
        )
    } catch (e: Exception) {
        Log.e(TAG, "Deserialize gagal: ${e.message}", e)
        null
    }
}
// ============================================================
// HELPERS — OFFICER SCHEDULE
// ============================================================
private fun serializeOfficerSchedule(o: OfficerSchedule): JSONObject {
    return JSONObject().apply {
        put("imamSubuh", o.imamSubuh)
        put("muadzinSubuh", o.muadzinSubuh)
        put("imamDzuhur", o.imamDzuhur)
        put("muadzinDzuhur", o.muadzinDzuhur)
        put("imamAshar", o.imamAshar)
        put("muadzinAshar", o.muadzinAshar)
        put("imamMaghrib", o.imamMaghrib)
        put("muadzinMaghrib", o.muadzinMaghrib)
        put("imamIsya", o.imamIsya)
        put("muadzinIsya", o.muadzinIsya)
        put("khatibJumat", o.khatibJumat)
        put("temaJumat", o.temaJumat)
        put("ustadzKajian", o.ustadzKajian)
        put("jadwalKajian", o.jadwalKajian)
        put("temaKajian", o.temaKajian)
    }
}

private fun deserializeOfficerSchedule(json: JSONObject?, default: OfficerSchedule): OfficerSchedule {
    if (json == null) return default
    return OfficerSchedule(
        imamSubuh = json.optString("imamSubuh", default.imamSubuh),
        muadzinSubuh = json.optString("muadzinSubuh", default.muadzinSubuh),
        imamDzuhur = json.optString("imamDzuhur", default.imamDzuhur),
        muadzinDzuhur = json.optString("muadzinDzuhur", default.muadzinDzuhur),
        imamAshar = json.optString("imamAshar", default.imamAshar),
        muadzinAshar = json.optString("muadzinAshar", default.muadzinAshar),
        imamMaghrib = json.optString("imamMaghrib", default.imamMaghrib),
        muadzinMaghrib = json.optString("muadzinMaghrib", default.muadzinMaghrib),
        imamIsya = json.optString("imamIsya", default.imamIsya),
        muadzinIsya = json.optString("muadzinIsya", default.muadzinIsya),
        khatibJumat = json.optString("khatibJumat", default.khatibJumat),
        temaJumat = json.optString("temaJumat", default.temaJumat),
        ustadzKajian = json.optString("ustadzKajian", default.ustadzKajian),
        jadwalKajian = json.optString("jadwalKajian", default.jadwalKajian),
        temaKajian = json.optString("temaKajian", default.temaKajian)
    )
}

// ============================================================
// HELPERS — WEEKLY OFFICERS
// ============================================================
private fun serializeWeeklyOfficers(list: List<DailyOfficerItem>): JSONArray {
    val arr = JSONArray()
    list.forEach { o ->
        arr.put(JSONObject().apply {
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
        })
    }
    return arr
}

private fun deserializeWeeklyOfficers(arr: JSONArray?, default: List<DailyOfficerItem>): List<DailyOfficerItem> {
    if (arr == null || arr.length() == 0) return default
    return runCatching {
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
    }.getOrDefault(default)
}

// ============================================================
// HELPERS — STRING LIST
// ============================================================
private fun serializeStringList(list: List<String>): JSONArray {
    val arr = JSONArray()
    list.forEach { arr.put(it) }
    return arr
}

private fun deserializeStringList(arr: JSONArray?, default: List<String>): List<String> {
    if (arr == null) return default
    return runCatching {
        List(arr.length()) { i -> arr.getString(i) }
    }.getOrDefault(default)
}

// ============================================================
// HELPERS — LAPORAN KEUANGAN
// ============================================================
private fun serializeLaporanKeuangan(l: LaporanKeuangan): JSONObject {
    return JSONObject().apply {
        put("saldoSebelumnya", l.saldoSebelumnya)
        put("pemasukanJumat", l.pemasukanJumat)
        put("pemasukanUmum", l.pemasukanUmum)
        put("pengeluaranDakwah", l.pengeluaranDakwah)
        put("pengeluaranSosial", l.pengeluaranSosial)
        put("pengeluaranOperasional", l.pengeluaranOperasional)
        put("periodeMulai", l.periodeMulai)
        put("periodeSelesai", l.periodeSelesai)
    }
}

private fun deserializeLaporanKeuangan(json: JSONObject?, default: LaporanKeuangan): LaporanKeuangan {
    if (json == null) return default
    return LaporanKeuangan(
        saldoSebelumnya = json.optLong("saldoSebelumnya", default.saldoSebelumnya),
        pemasukanJumat = json.optLong("pemasukanJumat", default.pemasukanJumat),
        pemasukanUmum = json.optLong("pemasukanUmum", default.pemasukanUmum),
        pengeluaranDakwah = json.optLong("pengeluaranDakwah", default.pengeluaranDakwah),
        pengeluaranSosial = json.optLong("pengeluaranSosial", default.pengeluaranSosial),
        pengeluaranOperasional = json.optLong("pengeluaranOperasional", default.pengeluaranOperasional),
        periodeMulai = json.optString("periodeMulai", default.periodeMulai),
        periodeSelesai = json.optString("periodeSelesai", default.periodeSelesai)
    )
}
    // ============================================================
    // V1.30.4 — UPLOAD MEDIA FILES (dari HP ke TV)
    // ============================================================
    suspend fun uploadMediaFiles(
        context: Context,
        targetIp: String,
        targetPort: Int,
        settings: AppSettings,
        onProgress: (MediaTransferHelper.TransferProgress) -> Unit = {}
    ): RemoteControlClient.MediaTransferResult {
        val mediaList = MediaTransferHelper.collectMediaFiles(settings)
        Log.d(TAG, "Media files to transfer: ${mediaList.size}")

        if (mediaList.isEmpty()) {
            return RemoteControlClient.MediaTransferResult(
                success = true,
                message = "Tidak ada media untuk dikirim"
            )
        }

        return RemoteControlClient.sendMediaFilesChunked(
            context = context,
            targetIp = targetIp,
            targetPort = targetPort,
            mediaList = mediaList,
            onProgress = onProgress
        )
    }
}
