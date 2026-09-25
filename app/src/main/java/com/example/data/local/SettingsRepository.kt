package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
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
    val settingsFlow: StateFlow<AppSettings> get() = settings

    fun updateSettings(newSettings: AppSettings) {
        saveSettings(newSettings)
        _settings.value = newSettings
    }

    private fun loadSettings(): AppSettings {
        val jsonStr = prefs.getString("settings_json", null) ?: return AppSettings()
        return try {
            val json = JSONObject(jsonStr)

            // Weekly officers schedule (List of DailyOfficerItem)
            val weeklyArray = json.optJSONArray("weeklyOfficers")
            val weeklyList = mutableListOf<DailyOfficerItem>()
            val defaultList = AppSettings.createDefaultWeeklySchedule()

            if (weeklyArray != null && weeklyArray.length() > 0) {
                for (i in 0 until weeklyArray.length()) {
                    val dJson = weeklyArray.getJSONObject(i)
                    weeklyList.add(
                        DailyOfficerItem(
                            dayName = dJson.optString("dayName", defaultList.getOrNull(i)?.dayName ?: "Hari"),
                            imamSubuh = dJson.optString("imamSubuh", "Ust. H. Ahmad Fauzi"),
                            muadzinSubuh = dJson.optString("muadzinSubuh", "Ust. Ridwan Kamil"),
                            imamDzuhur = dJson.optString("imamDzuhur", "Ust. M. Ridho, M.Ag"),
                            muadzinDzuhur = dJson.optString("muadzinDzuhur", "Ust. Bilal Sanjaya"),
                            imamAshar = dJson.optString("imamAshar", "Ust. Dr. H. Lukman"),
                            muadzinAshar = dJson.optString("muadzinAshar", "Ust. Ilham Pratama"),
                            imamMaghrib = dJson.optString("imamMaghrib", "Ust. Ahmad Syarifuddin"),
                            muadzinMaghrib = dJson.optString("muadzinMaghrib", "Ust. Ridwan Ar-Rasyid"),
                            imamIsya = dJson.optString("imamIsya", "Ust. KH. Abdullah Gymnast"),
                            muadzinIsya = dJson.optString("muadzinIsya", "Ust. Farhan Azis"),
                            khatibJumat = dJson.optString("khatibJumat", "Prof. Dr. KH. Zainuddin MZ"),
                            temaJumat = dJson.optString("temaJumat", "Menjaga Ukhuwah & Istiqomah di Era Modern"),
                            ustadzKajian = dJson.optString("ustadzKajian", "Ust. Hanan Attaki, Lc"),
                            temaKajian = dJson.optString("temaKajian", "Tafsir Ayat-Ayat Rahmat & Tazkiyatun Nafs"),
                            fotoImamSubuh = dJson.optString("fotoImamSubuh", "").ifBlank { null },
                            fotoMuadzinSubuh = dJson.optString("fotoMuadzinSubuh", "").ifBlank { null },
                            fotoImamDzuhur = dJson.optString("fotoImamDzuhur", "").ifBlank { null },
                            fotoMuadzinDzuhur = dJson.optString("fotoMuadzinDzuhur", "").ifBlank { null },
                            fotoImamAshar = dJson.optString("fotoImamAshar", "").ifBlank { null },
                            fotoMuadzinAshar = dJson.optString("fotoMuadzinAshar", "").ifBlank { null },
                            fotoImamMaghrib = dJson.optString("fotoImamMaghrib", "").ifBlank { null },
                            fotoMuadzinMaghrib = dJson.optString("fotoMuadzinMaghrib", "").ifBlank { null },
                            fotoImamIsya = dJson.optString("fotoImamIsya", "").ifBlank { null },
                            fotoMuadzinIsya = dJson.optString("fotoMuadzinIsya", "").ifBlank { null },
                            fotoKhatibJumat = dJson.optString("fotoKhatibJumat", "").ifBlank { null },
                            fotoUstadzKajian = dJson.optString("fotoUstadzKajian", "").ifBlank { null }
                        )
                    )
                }
            } else {
                weeklyList.addAll(defaultList)
            }

            val audioModeStr = json.optString("audioMode", AudioMode.BEEP_ONLY.name)
            val audioMode = try { AudioMode.valueOf(audioModeStr) } catch (e: Exception) { AudioMode.BEEP_ONLY }

            val bgModeStr = json.optString("backgroundMode", BackgroundMode.NATURE.name)
            val bgMode = try { BackgroundMode.valueOf(bgModeStr) } catch (e: Exception) { BackgroundMode.NATURE }

            AppSettings(
                mosqueName = json.optString("mosqueName", "MASJID AL-IKHLAS"),
                mosqueAddress = json.optString("mosqueAddress", "Jl. Raya Madinah No. 7, Gambir, Jakarta Pusat"),
                mosqueTakmir = json.optString("mosqueTakmir", "H. Muhammad Syarif, S.E."),
                isGpsEnabled = json.optBoolean("isGpsEnabled", false),
                country = json.optString("country", "Indonesia"),
                province = json.optString("province", "DKI Jakarta"),
                city = json.optString("city", "Jakarta Pusat"),
                district = json.optString("district", "Gambir"),
                latitude = json.optDouble("latitude", -6.1754),
                longitude = json.optDouble("longitude", 106.8272),
                calculationMethod = json.optString("calculationMethod", "Kementerian Agama RI (Kemenag)"),
                runningText = json.optString("runningText", "═══ Selamat datang di Masjid Al-Ikhlas ═══ Luruskan dan rapatkan shaf sholat ═══ Harap nonaktifkan nada dering ponsel ═══ Infaq & Shadaqah dapat melalui Rek BSI: 7123-4567-89 a.n Masjid Al-Ikhlas ═══ Kajian Rutin Sabtu Ba'da Maghrib bersama Ust. Hanan Attaki, Lc ═══"),
                runningTextSpeed = json.optInt("runningTextSpeed", 2),
                runningTextFontSize = json.optInt("runningTextFontSize", 18),
                weeklyOfficers = weeklyList,
                officerPhotoUri = json.optString("officerPhotoUri", "").ifBlank { null },
                audioMode = audioMode,
                beepVolume = json.optInt("beepVolume", 70),
                beepCount = json.optInt("beepCount", 3),
                adzanFile = json.optString("adzanFile", "Makkah"),
                adzanVolume = json.optInt("adzanVolume", 85),
                adzanWaitMinutes = json.optInt("adzanWaitMinutes", 5),
                iqamahWaitMinutes = json.optInt("iqamahWaitMinutes", 10),
                qobliyahWaitMinutes = json.optInt("qobliyahWaitMinutes", 5),
                prayerFocusDurationMinutes = json.optInt("prayerFocusDurationMinutes", 30),
                focusModeDurationMinutes = json.optInt("focusModeDurationMinutes", 30),
                backgroundMode = bgMode,
                customBackgroundUri = json.optString("customBackgroundUri", "").ifBlank { null },
                animationsEnabled = json.optBoolean("animationsEnabled", true),
                showBirdsAnimation = json.optBoolean("showBirdsAnimation", true),
                qrisPhotoUri = json.optString("qrisPhotoUri", "").ifBlank { null },
                qrisIntervalMinutes = json.optInt("qrisIntervalMinutes", 15),
                qrisDisplayDurationSeconds = json.optInt("qrisDisplayDurationSeconds", 30),
                bankName = json.optString("bankName", "Bank Syariah Indonesia (BSI)"),
                bankAccountNumber = json.optString("bankAccountNumber", "7123-4567-890"),
                bankAccountHolder = json.optString("bankAccountHolder", "DKM MASJID AL-IKHLAS"),
                wisdomCardAnimation = json.optString("wisdomCardAnimation", "Fade"),
                wisdomCardIntervalSeconds = json.optInt("wisdomCardIntervalSeconds", 12),
                videoEnabled = json.optBoolean("videoEnabled", false),
                videoUri = json.optString("videoUri", "").ifBlank { null },
                videoSmartFullscreen = json.optBoolean("videoSmartFullscreen", true),
                ramadhanModeEnabled = json.optBoolean("ramadhanModeEnabled", false),
                showImsakIftarCountdown = json.optBoolean("showImsakIftarCountdown", true),
                pinCode = json.optString("pinCode", "1234"),
                kioskModeEnabled = json.optBoolean("kioskModeEnabled", true),
                autoStartOnBoot = json.optBoolean("autoStartOnBoot", true),
                isManualTimeEnabled = json.optBoolean("isManualTimeEnabled", false),
                manualTimeOffsetSeconds = json.optLong("manualTimeOffsetSeconds", 0L),
                keepScreenOn = json.optBoolean("keepScreenOn", true)
            )
        } catch (e: Exception) {
            AppSettings()
        }
    }

    private fun saveSettings(s: AppSettings) {
        val json = JSONObject().apply {
            put("mosqueName", s.mosqueName)
            put("mosqueAddress", s.mosqueAddress)
            put("mosqueTakmir", s.mosqueTakmir)
            put("isGpsEnabled", s.isGpsEnabled)
            put("country", s.country)
            put("province", s.province)
            put("city", s.city)
            put("district", s.district)
            put("latitude", s.latitude)
            put("longitude", s.longitude)
            put("calculationMethod", s.calculationMethod)
            put("runningText", s.runningText)
            put("runningTextSpeed", s.runningTextSpeed)
            put("runningTextFontSize", s.runningTextFontSize)

            // Weekly officers JSON Array
            val weeklyArray = JSONArray()
            s.weeklyOfficers.forEach { dayItem ->
                val dJson = JSONObject().apply {
                    put("dayName", dayItem.dayName)
                    put("imamSubuh", dayItem.imamSubuh)
                    put("muadzinSubuh", dayItem.muadzinSubuh)
                    put("imamDzuhur", dayItem.imamDzuhur)
                    put("muadzinDzuhur", dayItem.muadzinDzuhur)
                    put("imamAshar", dayItem.imamAshar)
                    put("muadzinAshar", dayItem.muadzinAshar)
                    put("imamMaghrib", dayItem.imamMaghrib)
                    put("muadzinMaghrib", dayItem.muadzinMaghrib)
                    put("imamIsya", dayItem.imamIsya)
                    put("muadzinIsya", dayItem.muadzinIsya)
                    put("khatibJumat", dayItem.khatibJumat)
                    put("temaJumat", dayItem.temaJumat)
                    put("ustadzKajian", dayItem.ustadzKajian)
                    put("temaKajian", dayItem.temaKajian)
                }
                weeklyArray.put(dJson)
            }
            put("weeklyOfficers", weeklyArray)

            put("officerPhotoUri", s.officerPhotoUri ?: "")
            put("audioMode", s.audioMode.name)
            put("beepVolume", s.beepVolume)
            put("beepCount", s.beepCount)
            put("adzanFile", s.adzanFile)
            put("adzanVolume", s.adzanVolume)
            put("adzanWaitMinutes", s.adzanWaitMinutes)
            put("iqamahWaitMinutes", s.iqamahWaitMinutes)
            put("qobliyahWaitMinutes", s.qobliyahWaitMinutes)
            put("prayerFocusDurationMinutes", s.prayerFocusDurationMinutes)
            put("focusModeDurationMinutes", s.focusModeDurationMinutes)
            put("backgroundMode", s.backgroundMode.name)
            put("customBackgroundUri", s.customBackgroundUri ?: "")
            put("animationsEnabled", s.animationsEnabled)
            put("showBirdsAnimation", s.showBirdsAnimation)
            put("qrisPhotoUri", s.qrisPhotoUri ?: "")
            put("qrisIntervalMinutes", s.qrisIntervalMinutes)
            put("qrisDisplayDurationSeconds", s.qrisDisplayDurationSeconds)
            put("bankName", s.bankName)
            put("bankAccountNumber", s.bankAccountNumber)
            put("bankAccountHolder", s.bankAccountHolder)
            put("wisdomCardAnimation", s.wisdomCardAnimation)
            put("wisdomCardIntervalSeconds", s.wisdomCardIntervalSeconds)
            put("videoEnabled", s.videoEnabled)
            put("videoUri", s.videoUri ?: "")
            put("videoSmartFullscreen", s.videoSmartFullscreen)
            put("ramadhanModeEnabled", s.ramadhanModeEnabled)
            put("showImsakIftarCountdown", s.showImsakIftarCountdown)
            put("pinCode", s.pinCode)
            put("kioskModeEnabled", s.kioskModeEnabled)
            put("autoStartOnBoot", s.autoStartOnBoot)
            put("isManualTimeEnabled", s.isManualTimeEnabled)
            put("manualTimeOffsetSeconds", s.manualTimeOffsetSeconds)
            put("keepScreenOn", s.keepScreenOn)
        }

        prefs.edit().putString("settings_json", json.toString()).apply()
    }
}
