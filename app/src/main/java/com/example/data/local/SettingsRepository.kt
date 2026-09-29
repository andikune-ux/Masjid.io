package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppSettings
import com.example.data.model.WeeklyOfficer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * SettingsRepository — Simpan/load semua pengaturan aplikasi.
 * Menggunakan SharedPreferences + StateFlow untuk reactive update.
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("masjid_io_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    val settingsFlow: StateFlow<AppSettings> = _settings.asStateFlow()

    // ============================================================
    // PUBLIC — Update Settings
    // ============================================================
    fun updateSettings(newSettings: AppSettings) {
        saveSettings(newSettings)
        _settings.value = newSettings
    }

    // ============================================================
    // PRIVATE — Load Settings dari SharedPreferences
    // ============================================================
    private fun loadSettings(): AppSettings {
        return AppSettings(
            // Identitas
            mosqueName = prefs.getString("mosqueName", "Masjid Al-Ikhlas") ?: "Masjid Al-Ikhlas",
            mosqueAddress = prefs.getString("mosqueAddress", "") ?: "",
            mosqueTakmir = prefs.getString("mosqueTakmir", "") ?: "",
            logoUri = prefs.getString("logoUri", null),

            // Lokasi
            isGpsEnabled = prefs.getBoolean("isGpsEnabled", true),
            country = prefs.getString("country", "Indonesia") ?: "Indonesia",
            province = prefs.getString("province", "DKI Jakarta") ?: "DKI Jakarta",
            city = prefs.getString("city", "Jakarta Pusat") ?: "Jakarta Pusat",
            district = prefs.getString("district", "") ?: "",
            latitude = prefs.getFloat("latitude", -6.1754f).toDouble(),
            longitude = prefs.getFloat("longitude", 106.8272f).toDouble(),
            calculationMethod = prefs.getString("calculationMethod", "Kemenag") ?: "Kemenag",

            // Running Text
            runningText = prefs.getString("runningText", "Selamat datang di Masjid") ?: "",
            runningTextSpeed = prefs.getFloat("runningTextSpeed", 1.0f),
            runningTextFontSize = prefs.getFloat("runningTextFontSize", 20f),

            // Petugas
            weeklyOfficers = emptyList(),
            officerPhotoUri = prefs.getString("officerPhotoUri", null),

            // Audio
            audioMode = prefs.getString("audioMode", "BEEP_ONLY") ?: "BEEP_ONLY",
            beepVolume = prefs.getFloat("beepVolume", 1.0f),
            beepCount = prefs.getInt("beepCount", 5),
            beepDuration = prefs.getInt("beepDuration", 1500),
            beepGap = prefs.getInt("beepGap", 2000),
            adzanFile = prefs.getString("adzanFile", "Makkah") ?: "Makkah",
            adzanVolume = prefs.getFloat("adzanVolume", 0.85f),

            // Waktu Fokus
            adzanWaitMinutes = prefs.getInt("adzanWaitMinutes", 3),
            iqamahWaitMinutes = prefs.getInt("iqamahWaitMinutes", 10),
            qobliyahWaitMinutes = prefs.getInt("qobliyahWaitMinutes", 5),
            prayerFocusDurationMinutes = prefs.getInt("prayerFocusDurationMinutes", 30),
            focusModeDurationMinutes = prefs.getInt("focusModeDurationMinutes", 30),

            // Tampilan
            backgroundMode = prefs.getString("backgroundMode", "NATURE") ?: "NATURE",
            customBackgroundUri = prefs.getString("customBackgroundUri", null),
            animationsEnabled = prefs.getBoolean("animationsEnabled", true),
            showBirdsAnimation = prefs.getBoolean("showBirdsAnimation", true),

            // QRIS & Bank
            qrisPhotoUri = prefs.getString("qrisPhotoUri", null),
            qrisIntervalMinutes = prefs.getInt("qrisIntervalMinutes", 5),
            qrisDisplayDurationSeconds = prefs.getInt("qrisDisplayDurationSeconds", 15),
            bankName = prefs.getString("bankName", "BSI") ?: "BSI",
            bankAccountNumber = prefs.getString("bankAccountNumber", "") ?: "",
            bankAccountHolder = prefs.getString("bankAccountHolder", "") ?: "",

            // Nasihat
            wisdomCardAnimation = prefs.getBoolean("wisdomCardAnimation", true),
            wisdomCardIntervalSeconds = prefs.getInt("wisdomCardIntervalSeconds", 30),

            // Video
            videoEnabled = prefs.getBoolean("videoEnabled", true),
            videoUri = prefs.getString("videoUri", null),
            videoSmartFullscreen = prefs.getBoolean("videoSmartFullscreen", false),

            // Foto
            photoSlideshowEnabled = prefs.getBoolean("photoSlideshowEnabled", false),
            photoIntervalSeconds = prefs.getInt("photoIntervalSeconds", 10),

            // Ramadhan
            ramadhanModeEnabled = prefs.getBoolean("ramadhanModeEnabled", false),
            showImsakIftarCountdown = prefs.getBoolean("showImsakIftarCountdown", true),

            // Keamanan & Sistem
            pinCode = prefs.getString("pinCode", "140399") ?: "140399",
            kioskModeEnabled = prefs.getBoolean("kioskModeEnabled", true),
            autoStartOnBoot = prefs.getBoolean("autoStartOnBoot", true),
            isManualTimeEnabled = prefs.getBoolean("isManualTimeEnabled", false),
            manualTimeOffsetSeconds = prefs.getInt("manualTimeOffsetSeconds", 0),
            keepScreenOn = prefs.getBoolean("keepScreenOn", true)
        )
    }

    // ============================================================
    // PRIVATE — Save Settings ke SharedPreferences
    // ============================================================
    private fun saveSettings(s: AppSettings) {
        prefs.edit().apply {
            // Identitas
            putString("mosqueName", s.mosqueName)
            putString("mosqueAddress", s.mosqueAddress)
            putString("mosqueTakmir", s.mosqueTakmir)
            putString("logoUri", s.logoUri)

            // Lokasi
            putBoolean("isGpsEnabled", s.isGpsEnabled)
            putString("country", s.country)
            putString("province", s.province)
            putString("city", s.city)
            putString("district", s.district)
            putFloat("latitude", s.latitude.toFloat())
            putFloat("longitude", s.longitude.toFloat())
            putString("calculationMethod", s.calculationMethod)

            // Running Text
            putString("runningText", s.runningText)
            putFloat("runningTextSpeed", s.runningTextSpeed)
            putFloat("runningTextFontSize", s.runningTextFontSize)

            // Petugas
            putString("officerPhotoUri", s.officerPhotoUri)

            // Audio
            putString("audioMode", s.audioMode)
            putFloat("beepVolume", s.beepVolume)
            putInt("beepCount", s.beepCount)
            putInt("beepDuration", s.beepDuration)
            putInt("beepGap", s.beepGap)
            putString("adzanFile", s.adzanFile)
            putFloat("adzanVolume", s.adzanVolume)

            // Waktu Fokus
            putInt("adzanWaitMinutes", s.adzanWaitMinutes)
            putInt("iqamahWaitMinutes", s.iqamahWaitMinutes)
            putInt("qobliyahWaitMinutes", s.qobliyahWaitMinutes)
            putInt("prayerFocusDurationMinutes", s.prayerFocusDurationMinutes)
            putInt("focusModeDurationMinutes", s.focusModeDurationMinutes)

            // Tampilan
            putString("backgroundMode", s.backgroundMode)
            putString("customBackgroundUri", s.customBackgroundUri)
            putBoolean("animationsEnabled", s.animationsEnabled)
            putBoolean("showBirdsAnimation", s.showBirdsAnimation)

            // QRIS & Bank
            putString("qrisPhotoUri", s.qrisPhotoUri)
            putInt("qrisIntervalMinutes", s.qrisIntervalMinutes)
            putInt("qrisDisplayDurationSeconds", s.qrisDisplayDurationSeconds)
            putString("bankName", s.bankName)
            putString("bankAccountNumber", s.bankAccountNumber)
            putString("bankAccountHolder", s.bankAccountHolder)

            // Nasihat
            putBoolean("wisdomCardAnimation", s.wisdomCardAnimation)
            putInt("wisdomCardIntervalSeconds", s.wisdomCardIntervalSeconds)

            // Video
            putBoolean("videoEnabled", s.videoEnabled)
            putString("videoUri", s.videoUri)
            putBoolean("videoSmartFullscreen", s.videoSmartFullscreen)

            // Foto
            putBoolean("photoSlideshowEnabled", s.photoSlideshowEnabled)
            putInt("photoIntervalSeconds", s.photoIntervalSeconds)

            // Ramadhan
            putBoolean("ramadhanModeEnabled", s.ramadhanModeEnabled)
            putBoolean("showImsakIftarCountdown", s.showImsakIftarCountdown)

            // Keamanan & Sistem
            putString("pinCode", s.pinCode)
            putBoolean("kioskModeEnabled", s.kioskModeEnabled)
            putBoolean("autoStartOnBoot", s.autoStartOnBoot)
            putBoolean("isManualTimeEnabled", s.isManualTimeEnabled)
            putInt("manualTimeOffsetSeconds", s.manualTimeOffsetSeconds)
            putBoolean("keepScreenOn", s.keepScreenOn)
        }.apply()
    }

    // ============================================================
    // BARU — Export Ringkasan untuk Backup Aman
    // ============================================================
    fun exportSummary(): String {
        val s = _settings.value
        return buildString {
            // Identitas
            appendLine("IDENTITAS MASJID")
            appendLine("- Nama Masjid : ${s.mosqueName}")
            appendLine("- Alamat      : ${s.mosqueAddress}")
            appendLine("- Takmir      : ${s.mosqueTakmir}")
            appendLine()

            // Lokasi
            appendLine("LOKASI")
            appendLine("- Kota        : ${s.city}")
            appendLine("- Provinsi    : ${s.province}")
            appendLine("- Latitude    : ${s.latitude}")
            appendLine("- Longitude   : ${s.longitude}")
            appendLine("- Metode      : ${s.calculationMethod}")
            appendLine()

            // Tampilan
            appendLine("TAMPILAN")
            appendLine("- Background Mode : ${s.backgroundMode}")
            appendLine("- Keep Screen On  : ${s.keepScreenOn}")
            appendLine("- Kiosk Mode      : ${s.kioskModeEnabled}")
            appendLine("- Animasi         : ${s.animationsEnabled}")
            appendLine("- Burung Terbang  : ${s.showBirdsAnimation}")
            appendLine()

            // Audio
            appendLine("AUDIO")
            appendLine("- Mode Audio    : ${s.audioMode}")
            appendLine("- Volume Beep   : ${(s.beepVolume * 100).toInt()}%")
            appendLine("- Jumlah Beep   : ${s.beepCount}x")
            appendLine("- Durasi Beep   : ${s.beepDuration}ms")
            appendLine("- Jeda Beep     : ${s.beepGap}ms")
            appendLine("- File Adzan    : ${s.adzanFile}")
            appendLine("- Volume Adzan  : ${(s.adzanVolume * 100).toInt()}%")
            appendLine()

            // Video & Foto
            appendLine("VIDEO & FOTO")
            appendLine("- Video Enabled     : ${s.videoEnabled}")
            appendLine("- Video Smart Full  : ${s.videoSmartFullscreen}")
            appendLine("- Photo Slideshow   : ${s.photoSlideshowEnabled}")
            appendLine("- Interval Foto     : ${s.photoIntervalSeconds} detik")
            appendLine()

            // Running Text
            appendLine("RUNNING TEXT")
            appendLine("- Isi Running Text : ${s.runningText}")
            appendLine("- Kecepatan        : ${s.runningTextSpeed}x")
            appendLine("- Ukuran Font      : ${s.runningTextFontSize}")
            appendLine()

            // Mode Fokus
            appendLine("MODE FOKUS")
            appendLine("- Durasi Mode Fokus  : ${s.focusModeDurationMinutes} menit")
            appendLine("- Jeda Iqamah        : ${s.iqamahWaitMinutes} menit")
            appendLine("- Countdown Qobliyah : ${s.qobliyahWaitMinutes} menit")
            appendLine()

            // QRIS
            appendLine("DONASI QRIS")
            appendLine("- Bank         : ${s.bankName}")
            appendLine("- No Rekening  : ${s.bankAccountNumber}")
            appendLine("- Atas Nama    : ${s.bankAccountHolder}")
            appendLine("- QRIS Foto    : ${if (s.qrisPhotoUri.isNullOrEmpty()) "(kosong)" else "ADA"}")
            appendLine()

            // Keamanan
            appendLine("KEAMANAN")
            appendLine("- PIN           : ${s.pinCode}")
            appendLine("- Kiosk Mode    : ${s.kioskModeEnabled}")
            appendLine("- Auto Start    : ${s.autoStartOnBoot}")
            appendLine("- Manual Time   : ${s.isManualTimeEnabled}")
            appendLine()

            // WhatsApp Fonnte
            appendLine("WHATSAPP FONNTE")
            appendLine("- WA Report Enabled : (lihat di Opsi Developer)")
            appendLine("- Token Fonnte      : (lihat di Opsi Developer)")
            appendLine("- Group ID          : (lihat di Opsi Developer)")
        }
    }
}
