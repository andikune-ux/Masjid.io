package dev.andikune.masjidio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.andikune.masjidio.audio.SoundManager
import dev.andikune.masjidio.data.local.IslamicCalendar
import dev.andikune.masjidio.data.local.PrayerTimesCalculator
import dev.andikune.masjidio.data.local.SettingsRepository
import dev.andikune.masjidio.data.local.WeatherService
import dev.andikune.masjidio.data.model.PrayerId
import dev.andikune.masjidio.data.model.PrayerSchedule
import dev.andikune.masjidio.kiosk.KioskManager
import dev.andikune.masjidio.kiosk.WatchdogService
import dev.andikune.masjidio.ui.components.PinDialog
import dev.andikune.masjidio.ui.components.UpdateDialog
import dev.andikune.masjidio.ui.focus.PrayerFocusOverlay
import dev.andikune.masjidio.ui.focus.QRISFocusOverlay
import dev.andikune.masjidio.ui.home.HomeScreen
import dev.andikune.masjidio.ui.ramadhan.RamadhanOverlay
import dev.andikune.masjidio.ui.remote.IoControlScreen
import dev.andikune.masjidio.ui.remote.RemoteServer
import dev.andikune.masjidio.ui.remote.RestartCountdownOverlay
import dev.andikune.masjidio.ui.settings.SettingsScreen
import dev.andikune.masjidio.ui.theme.MasjidTheme
import dev.andikune.masjidio.ui.theme.MosqueDeepBg
import dev.andikune.masjidio.util.ApkDownloader
import dev.andikune.masjidio.util.CrashAutoShowHelper
import dev.andikune.masjidio.util.CrashLogDialog
import dev.andikune.masjidio.util.CrashReporter
import dev.andikune.masjidio.util.FonnteSender
import dev.andikune.masjidio.util.SettingsTransferHelper
import dev.andikune.masjidio.util.UpdateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class AppScreen {
    HOME,
    FOCUS_MODE,
    SETTINGS,
    QRIS_PREVIEW,
    RAMADHAN,
    IO_CONTROL
}

private const val UPDATE_PREFS = "update_prefs"
private const val KEY_SKIPPED_VERSION = "skipped_version"

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var soundManager: SoundManager

    // Flag untuk menentukan mode restart (V1.30.5)
    @Volatile
    private var needsHardRestart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()

        CrashReporter.init(this)

        settingsRepository = SettingsRepository(this)
        soundManager = SoundManager(this)

        val hasPendingCrash = CrashAutoShowHelper.hasPendingCrash(this)
        val pendingCrashLog = if (hasPendingCrash) {
            CrashAutoShowHelper.getLastCrashLog(this)
        } else ""

        val deviceRole = if (isTV()) "TV" else "HP"
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        // ============================================================
        // RESTART HANDLER — Opsi C
        // - Normal: recreate() → activity re-init, tidak keluar
        // - Port berubah: killProcess → Watchdog auto-reopen
        // ============================================================
        val doSoftRestart: () -> Unit = {
            runOnUiThread {
                try {
                    // Cek apakah port Remote Server berubah
                    // Kalau iya → hard restart (killProcess)
                    // Kalau tidak → soft restart (recreate)
                    if (needsHardRestart) {
                        // Hard restart: kill process, WatchdogService akan reopen
                        android.os.Process.killProcess(android.os.Process.myPid())
                    } else {
                        // Soft restart: recreate activity — cepat, tidak keluar
                        recreate()
                    }
                } catch (e: Exception) {
                    // Fallback: kill process
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
        }

        setContent {
            val context = LocalContext.current
            val settings by settingsRepository.settingsFlow.collectAsState()
            val scope = rememberCoroutineScope()

            var showCrashDialog by remember { mutableStateOf(hasPendingCrash) }

            // ============ RESTART COUNTDOWN STATE (V1.30.5) ============
            var showRestartCountdown by remember { mutableStateOf(false) }

            // ============ UPDATE STATE ============
            var showUpdateDialog by remember { mutableStateOf(false) }
            var updateInfo by remember { mutableStateOf<UpdateManager.UpdateInfo?>(null) }
            var downloadProgress by remember { mutableFloatStateOf(0f) }
            var isDownloading by remember { mutableStateOf(false) }
            var isInstalling by remember { mutableStateOf(false) }

            val updatePrefs = remember {
                context.getSharedPreferences(UPDATE_PREFS, Context.MODE_PRIVATE)
            }
            fun getSkippedVersion(): String? =
                updatePrefs.getString(KEY_SKIPPED_VERSION, null)

            fun setSkippedVersion(version: String) {
                updatePrefs.edit().putString(KEY_SKIPPED_VERSION, version).apply()
            }

            // ============ SYNC FONNTE ============
            LaunchedEffect(
                settings.fonnteToken,
                settings.fonnteGroupId,
                settings.whatsappReportEnabled
            ) {
                CrashReporter.updateFonnteConfig(
                    token = settings.fonnteToken,
                    groupId = settings.fonnteGroupId,
                    enabled = settings.whatsappReportEnabled
                )
            }

            // ============ KIOSK MODE ============
            LaunchedEffect(settings.kioskModeEnabled) {
                if (settings.kioskModeEnabled) {
                    KioskManager.enableKiosk(this@MainActivity)
                } else {
                    KioskManager.disableKiosk(this@MainActivity)
                }
            }

            // ============ WATCHDOG SERVICE ============
            DisposableEffect(settings.kioskModeEnabled) {
                val serviceIntent = Intent(this@MainActivity, WatchdogService::class.java)
                if (settings.kioskModeEnabled) {
                    try { startService(serviceIntent) } catch (_: Exception) {}
                } else {
                    try { stopService(serviceIntent) } catch (_: Exception) {}
                }
                onDispose {
                    try { stopService(serviceIntent) } catch (_: Exception) {}
                }
            }

            // ============ REMOTE SERVER — V1.30.5 ============
            val remoteServer = remember {
                RemoteServer(
                    context = this@MainActivity,
                    settingsRepository = settingsRepository,
                    onRestart = {
                        // Fallback: dipanggil dari /api/restart (bukan dari iO Control)
                        runOnUiThread {
                            val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                            finish()
                        }
                    },
                    onSettingsReceived = { jsonBody ->
                        try {
                            val current = settingsRepository.settingsFlow.value
                            val newSettings = SettingsTransferHelper.deserializeSettings(jsonBody, current)
                            if (newSettings != null) {
                                settingsRepository.updateSettings(newSettings)
                                // Cek apakah port berubah
                                val portBerubah = newSettings.remoteServerPort != current.remoteServerPort
                                if (portBerubah) {
                                    needsHardRestart = true
                                }
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Gagal apply received settings: ${e.message}")
                        }
                    },
                    // ============================================================
                    // V1.30.5 BARU: onFinalize — dipanggil setelah semua settings + media terkirim
                    // Trigger overlay countdown 5 detik
                    // ============================================================
                    onFinalize = {
                        runOnUiThread {
                            android.util.Log.d("MainActivity", "🎬 onFinalize dipanggil — tampilkan countdown")
                            showRestartCountdown = true
                        }
                    }
                )
            }
            var isRemoteServerRunning by remember { mutableStateOf(false) }

            LaunchedEffect(settings.remoteControlEnabled, settings.remoteServerPort, settings.remoteAuthToken) {
                if (settings.remoteControlEnabled) {
                    remoteServer.stop()
                    delay(300)
                    remoteServer.start(scope)
                    delay(2500)
                    val realStatus = remoteServer.isRunning()
                    isRemoteServerRunning = realStatus
                    if (!realStatus) {
                        android.util.Log.e(
                            "MainActivity",
                            "❌ Remote Server gagal: ${remoteServer.lastError}"
                        )
                    }
                } else {
                    remoteServer.stop()
                    isRemoteServerRunning = false
                }
            }

            DisposableEffect(Unit) {
                onDispose { remoteServer.stop() }
            }

            // ============ CEK UPDATE ============
            LaunchedEffect(Unit) {
                try {
                    delay(3000)
                    val info = UpdateManager.checkForUpdate()
                    if (info.available) {
                        val skippedVersion = getSkippedVersion()
                        val isSkipped = skippedVersion == info.latestVersion

                        if (info.isForceUpdate || !isSkipped) {
                            updateInfo = info
                            showUpdateDialog = true
                        }
                    }
                } catch (_: Exception) { }
            }

            // ============ HANDLER TOMBOL UPDATE ============
            val startDownload: () -> Unit = {
                val info = updateInfo
                if (info?.downloadUrl.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        "URL download tidak tersedia",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    isDownloading = true
                    downloadProgress = 0f

                    scope.launch {
                        ApkDownloader.downloadApk(
                            context = this@MainActivity,
                            downloadUrl = info!!.downloadUrl!!,
                            fileName = "masjid-io-${info.latestVersion}.apk"
                        ).collect { state ->
                            when {
                                state.errorMessage != null -> {
                                    isDownloading = false
                                    isInstalling = false
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Download gagal: ${state.errorMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                state.isFinished && state.savedFilePath != null -> {
                                    isDownloading = false
                                    isInstalling = true

                                    val ok = ApkDownloader.installApk(
                                        this@MainActivity,
                                        state.savedFilePath
                                    )
                                    if (!ok) {
                                        isInstalling = false
                                        ApkDownloader.openInstallPermissionSettings(this@MainActivity)
                                    } else {
                                        ApkDownloader.deleteOldApks(
                                            this@MainActivity,
                                            keepCount = 2
                                        )
                                    }
                                }
                                else -> {
                                    downloadProgress = state.progress
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ PERMISSIONS ============
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    )
                } else {
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    )
                }
            }

            // ============ KEEP SCREEN ON ============
            DisposableEffect(settings.keepScreenOn) {
                if (settings.keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                onDispose {}
            }

            var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
            var showPinDialog by remember { mutableStateOf(false) }
            var focusPrayerId by remember { mutableStateOf(PrayerId.MAGHRIB) }
            var focusPrayerTime by remember { mutableStateOf("17:52") }

            // ============ WEATHER ============
            var currentTemperature by remember { mutableStateOf(30) }
            var currentWeatherCondition by remember { mutableStateOf("Cerah") }

            LaunchedEffect(settings.latitude, settings.longitude) {
                while (true) {
                    try {
                        val weather = WeatherService.fetchWeather(settings.latitude, settings.longitude)
                        currentTemperature = weather.temperature
                        currentWeatherCondition = weather.condition
                    } catch (_: Exception) { }
                    delay(30 * 60 * 1000L)
                }
            }

            // ============ BACK PRESS ============
            DisposableEffect(settings.kioskModeEnabled, currentScreen, showRestartCountdown) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (showUpdateDialog && updateInfo?.isForceUpdate == true) return
                        if (isDownloading || isInstalling) return
                        // V1.30.5: saat countdown restart aktif, tahan back
                        if (showRestartCountdown) return

                        when (currentScreen) {
                            AppScreen.SETTINGS, AppScreen.QRIS_PREVIEW,
                            AppScreen.RAMADHAN, AppScreen.IO_CONTROL ->
                                currentScreen = AppScreen.HOME
                            AppScreen.FOCUS_MODE -> showPinDialog = true
                            AppScreen.HOME -> {
                                if (settings.kioskModeEnabled) showPinDialog = true
                                else {
                                    isEnabled = false
                                    onBackPressedDispatcher.onBackPressed()
                                }
                            }
                        }
                    }
                }
                onBackPressedDispatcher.addCallback(callback)
                onDispose { callback.remove() }
            }

            // ============ CLOCK & SCHEDULE ============
            var currentTimeString by remember { mutableStateOf("12:00:00") }
            var hijriDateString by remember { mutableStateOf("17 Rajab 1447 H") }
            var gregorianDateString by remember { mutableStateOf("Jum'at, 24 September 2026") }
            var prayerSchedule by remember { mutableStateOf(PrayerSchedule()) }

            // ============ RAMADHAN STATE ============
            var secondsToImsak by remember { mutableLongStateOf(0L) }
            var secondsToMaghrib by remember { mutableLongStateOf(0L) }
            var userDismissedRamadhan by remember { mutableStateOf(false) }

            val today = remember { LocalDate.now() }
            val upcomingEvent = remember {
                val hDate = IslamicCalendar.getHijriDate(today)
                IslamicCalendar.getUpcomingEvent(hDate)
            }

            LaunchedEffect(
                settings.latitude,
                settings.longitude,
                settings.isManualTimeEnabled,
                settings.manualTimeOffsetSeconds
            ) {
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                var lastTriggeredPrayerMinute: String? = null

                while (true) {
                    val currentDateTime = if (settings.isManualTimeEnabled) {
                        LocalDateTime.now().plusSeconds(settings.manualTimeOffsetSeconds)
                    } else {
                        LocalDateTime.now()
                    }

                    val now = currentDateTime.toLocalTime()
                    currentTimeString = now.format(timeFormatter)

                    val currentDate = currentDateTime.toLocalDate()
                    val hDate = IslamicCalendar.getHijriDate(currentDate)
                    hijriDateString = IslamicCalendar.formatHijriDateString(hDate)
                    gregorianDateString = IslamicCalendar.formatIndonesianDate(currentDate)

                    val schedule = PrayerTimesCalculator.calculate(
                        date = currentDate,
                        latitude = settings.latitude,
                        longitude = settings.longitude
                    )
                    prayerSchedule = schedule

                    secondsToImsak = calculateSecondsTo(schedule.imsak, currentDateTime)
                    secondsToMaghrib = calculateSecondsTo(schedule.maghrib, currentDateTime)

                    val currentMinuteStr = String.format("%02d:%02d", now.hour, now.minute)
                    if (now.second == 0 && currentMinuteStr != lastTriggeredPrayerMinute) {
                        for (item in schedule.items) {
                            if (item.timeFormatted == currentMinuteStr && item.id != PrayerId.SYURUQ) {
                                lastTriggeredPrayerMinute = currentMinuteStr
                                focusPrayerId = item.id
                                focusPrayerTime = item.timeFormatted
                                soundManager.playPrayerAlert(
                                    mode = settings.audioMode,
                                    beepVolume = settings.beepVolume,
                                    beepCount = settings.beepCount,
                                    beepDurationMs = settings.beepDurationMs,
                                    beepIntervalMs = settings.beepIntervalMs,
                                    adzanStyle = settings.adzanFile,
                                    adzanVolume = settings.adzanVolume
                                )
                                userDismissedRamadhan = false
                                currentScreen = AppScreen.FOCUS_MODE
                                break
                            }
                        }
                    }
                    delay(1000)
                }
            }

            // ============ RAMADHAN OVERLAY TRIGGER ============
            LaunchedEffect(
                settings.ramadhanModeEnabled,
                secondsToImsak,
                secondsToMaghrib,
                currentScreen,
                userDismissedRamadhan
            ) {
                if (!settings.ramadhanModeEnabled) return@LaunchedEffect
                if (currentScreen != AppScreen.HOME) return@LaunchedEffect
                if (userDismissedRamadhan) return@LaunchedEffect

                val nearImsak = secondsToImsak in 1..3600
                val nearMaghrib = secondsToMaghrib in 1..3600

                if (nearImsak || nearMaghrib) {
                    currentScreen = AppScreen.RAMADHAN
                }
            }

            LaunchedEffect(currentScreen, secondsToImsak, secondsToMaghrib) {
                if (currentScreen == AppScreen.RAMADHAN) {
                    if (secondsToImsak <= 0 && secondsToMaghrib <= 0) {
                        currentScreen = AppScreen.HOME
                    }
                }
            }

            MasjidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MosqueDeepBg
                ) {
                    if (showCrashDialog) {
                        CrashLogDialog(
                            log = pendingCrashLog,
                            onDismiss = {
                                CrashAutoShowHelper.markAsSeen(this@MainActivity)
                                showCrashDialog = false
                            }
                        )
                    } else {
                        Crossfade(targetState = currentScreen, label = "screen_fade") { screen ->
                            when (screen) {
                                AppScreen.HOME -> HomeScreen(
                                    settings = settings,
                                    schedule = prayerSchedule,
                                    currentTimeString = currentTimeString,
                                    hijriDateString = hijriDateString,
                                    gregorianDateString = gregorianDateString,
                                    upcomingEvent = upcomingEvent,
                                    temperature = currentTemperature,
                                    weatherCondition = currentWeatherCondition,
                                    onSettingsClick = { showPinDialog = true }
                                )
                                AppScreen.FOCUS_MODE -> PrayerFocusOverlay(
                                    prayerId = focusPrayerId,
                                    prayerTimeFormatted = focusPrayerTime,
                                    totalDurationMinutes = settings.prayerFocusDurationMinutes,
                                    iqamahWaitMinutes = settings.iqamahWaitMinutes,
                                    qobliyahWaitMinutes = settings.qobliyahWaitMinutes,
                                    settings = settings,
                                    onDismiss = { currentScreen = AppScreen.HOME }
                                )
                                AppScreen.SETTINGS -> SettingsScreen(
                                    currentSettings = settings,
                                    soundManager = soundManager,
                                    isRemoteServerRunning = isRemoteServerRunning,
                                    onAutoSaveSettings = { updated ->
                                        settingsRepository.updateSettings(updated)
                                    },
                                    onSaveSettings = { updated ->
                                        settingsRepository.updateSettings(updated)
                                        currentScreen = AppScreen.HOME
                                    },
                                    onBack = { currentScreen = AppScreen.HOME },
                                    onTestQrisFocus = { currentScreen = AppScreen.QRIS_PREVIEW },
                                    onOpenIoControl = { currentScreen = AppScreen.IO_CONTROL }
                                )
                                AppScreen.QRIS_PREVIEW -> QRISFocusOverlay(
                                    settings = settings,
                                    onDismiss = { currentScreen = AppScreen.SETTINGS }
                                )
                                AppScreen.RAMADHAN -> RamadhanOverlay(
                                    settings = settings,
                                    currentTimeString = currentTimeString,
                                    imsakTime = prayerSchedule.imsak,
                                    maghribTime = prayerSchedule.maghrib,
                                    secondsToImsak = secondsToImsak,
                                    secondsToMaghrib = secondsToMaghrib,
                                    onDismiss = {
                                        currentScreen = AppScreen.HOME
                                        userDismissedRamadhan = true
                                    }
                                )
                                AppScreen.IO_CONTROL -> IoControlScreen(
                                    settingsRepository = settingsRepository,
                                    deviceName = deviceName,
                                    deviceRole = deviceRole,
                                    appVersion = dev.andikune.masjidio.BuildConfig.VERSION_NAME,
                                    serverPort = settings.remoteServerPort,
                                    onBack = { currentScreen = AppScreen.SETTINGS }
                                )
                            }
                        }

                        // ============ PIN DIALOG ============
                        if (showPinDialog) {
                            PinDialog(
                                correctPin = settings.pinCode,
                                onSuccess = {
                                    showPinDialog = false
                                    if (currentScreen == AppScreen.FOCUS_MODE) currentScreen = AppScreen.HOME
                                    else currentScreen = AppScreen.SETTINGS
                                },
                                onDismiss = { showPinDialog = false }
                            )
                        }

                        // ============ UPDATE DIALOG ============
                        if (showUpdateDialog && updateInfo != null) {
                            val info = updateInfo!!

                            UpdateDialog(
                                currentVersion = info.currentVersion,
                                latestVersion = info.latestVersion,
                                releaseNotes = info.releaseNotes,
                                forceUpdate = info.isForceUpdate,
                                downloadProgress = if (isDownloading) downloadProgress else null,
                                isDownloading = isDownloading,
                                isInstalling = isInstalling,
                                onUpdateClick = { startDownload() },
                                onLaterClick = {
                                    showUpdateDialog = false
                                },
                                onSkipClick = {
                                    setSkippedVersion(info.latestVersion)
                                    showUpdateDialog = false
                                },
                                onTidakClick = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Aplikasi wajib diupdate. Menutup aplikasi...",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    scope.launch {
                                        delay(1500)
                                        this@MainActivity.finishAffinity()
                                        android.os.Process.killProcess(android.os.Process.myPid())
                                    }
                                }
                            )
                        }

                        // ============================================================
                        // V1.30.5 BARU — RESTART COUNTDOWN OVERLAY
                        // Muncul setelah terima sinyal finalize dari HP
                        // ============================================================
                        if (showRestartCountdown) {
                            RestartCountdownOverlay(
                                countdownStart = 5,
                                message = "Pengaturan & media baru sedang diterapkan",
                                onComplete = {
                                    android.util.Log.d("MainActivity", "⏰ Countdown selesai — restart sekarang")
                                    showRestartCountdown = false
                                    doSoftRestart()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        KioskManager.isMainActivityForeground = true
    }

    override fun onPause() {
        super.onPause()
        KioskManager.isMainActivityForeground = false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun hideSystemBars() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.stopAll()
    }

    // ============================================================
    // HELPER: DETEKSI TV / HP
    // ============================================================
    private fun isTV(): Boolean {
        val uiMode = resources.configuration.uiMode
        val uiModeType = uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK
        return uiModeType == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }

    // ============================================================
    // HELPER: HITUNG DETIK KE WAKTU TERTENTU
    // ============================================================
    private fun calculateSecondsTo(timeString: String, now: LocalDateTime): Long {
        return try {
            val parts = timeString.split(":")
            if (parts.size < 2) return 0L
            val targetHour = parts[0].trim().toInt()
            val targetMinute = parts[1].trim().toInt()

            var target = now.toLocalDate().atTime(targetHour, targetMinute)
            if (target.isBefore(now)) {
                target = target.plusDays(1)
            }
            Duration.between(now, target).seconds
        } catch (e: Exception) {
            0L
        }
    }
}
