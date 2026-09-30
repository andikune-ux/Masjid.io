package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.audio.SoundManager
import com.example.data.local.IslamicCalendar
import com.example.data.local.PrayerTimesCalculator
import com.example.data.local.SettingsRepository
import com.example.data.local.WeatherService
import com.example.data.model.PrayerId
import com.example.data.model.PrayerSchedule
import com.example.kiosk.KioskManager
import com.example.kiosk.WatchdogService
import com.example.ui.components.PinDialog
import com.example.ui.components.UpdateDialog
import com.example.ui.focus.PrayerFocusOverlay
import com.example.ui.focus.QRISFocusOverlay
import com.example.ui.home.HomeScreen
import com.example.ui.ramadhan.RamadhanOverlay
import com.example.ui.remote.IoControlScreen
import com.example.ui.remote.RemoteServer
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.MasjidTheme
import com.example.ui.theme.MosqueDeepBg
import com.example.util.CrashAutoShowHelper
import com.example.util.CrashLogDialog
import com.example.util.CrashReporter
import com.example.util.FonnteSender
import com.example.util.SettingsTransferHelper
import com.example.util.UpdateManager
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

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CrashReporter.init(this)

        settingsRepository = SettingsRepository(this)
        soundManager = SoundManager(this)

        // ============ CEK PENDING CRASH ============
        val hasPendingCrash = CrashAutoShowHelper.hasPendingCrash(this)
        val pendingCrashLog = if (hasPendingCrash) {
            CrashAutoShowHelper.getLastCrashLog(this)
        } else ""

        // ============ INFO DEVICE UNTUK iO CONTROL ============
        val deviceRole = if (isTV()) "TV" else "HP"
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        setContent {
            val settings by settingsRepository.settingsFlow.collectAsState()

            // ============ DIALOG CRASH AUTO-SHOW ============
            var showCrashDialog by remember { mutableStateOf(hasPendingCrash) }

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

            // ============ REMOTE SERVER ============
            val remoteServer = remember {
                RemoteServer(
                    context = this@MainActivity,
                    settingsRepository = settingsRepository,
                    onRestart = {
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
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Gagal apply received settings: ${e.message}")
                        }
                    }
                )
            }
            var isRemoteServerRunning by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            // ============ FIX: CEK REAL STATUS SERVER ============
            LaunchedEffect(settings.remoteControlEnabled, settings.remoteServerPort, settings.remoteAuthToken) {
                if (settings.remoteControlEnabled) {
                    remoteServer.stop()
                    delay(300)
                    remoteServer.start(scope)

                    // Tunggu server benar-benar start
                    delay(800)

                    // CEK REAL: apakah server benar-benar running?
                    val realStatus = remoteServer.isRunning()
                    isRemoteServerRunning = realStatus

                    if (!realStatus) {
                        // ============ SERVER GAGAL → REPORT KE WA ============
                        val errorMsg = remoteServer.lastError ?: "Unknown error"
                        val port = settings.remoteServerPort
                        android.util.Log.e(
                            "MainActivity",
                            "❌ Remote Server gagal start di port $port: $errorMsg"
                        )

                        // Kirim WA ke grup admin
                        if (settings.whatsappReportEnabled &&
                            settings.fonnteToken.isNotBlank() &&
                            settings.fonnteGroupId.isNotBlank()
                        ) {
                            val reportMsg = """
🚨 SERVER REMOTE GAGAL START 🚨

Device: $deviceName
Role: $deviceRole
Port: $port
Error: $errorMsg

Kemungkinan penyebab:
• Port $port sedang dipakai app lain
• Port diblokir sistem
• Ada bug di aplikasi

Solusi:
1. Restart HP
2. Atau force stop app lain yang pakai port $port
3. Atau hubungi developer

Waktu: ${java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}
                            """.trimIndent()

                            scope.launch {
                                try {
                                    FonnteSender.sendMessage(
                                        token = settings.fonnteToken,
                                        target = settings.fonnteGroupId,
                                        message = reportMsg
                                    )
                                    android.util.Log.d("MainActivity", "✅ Report WA terkirim")
                                } catch (e: Exception) {
                                    android.util.Log.e("MainActivity", "❌ Gagal kirim report WA: ${e.message}")
                                }
                            }
                        }
                    } else {
                        android.util.Log.d("MainActivity", "✅ Remote Server running at port ${remoteServer.actualPort}")
                    }
                } else {
                    remoteServer.stop()
                    isRemoteServerRunning = false
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    remoteServer.stop()
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

// ============ UPDATE STATE ============
var showUpdateDialog by remember { mutableStateOf(false) }
var updateInfo by remember { mutableStateOf<UpdateManager.UpdateInfo?>(null) }

LaunchedEffect(Unit) {
    try {
        delay(3000)
        val info = UpdateManager.checkForUpdate()
        if (info.available) {
            updateInfo = info
            showUpdateDialog = true
        }
    } catch (_: Exception) { }
}

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
DisposableEffect(settings.kioskModeEnabled, currentScreen) {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
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
                    // ============ DIALOG CRASH (PALING ATAS) ============
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
                                    appVersion = com.example.BuildConfig.VERSION_NAME,
                                    serverPort = settings.remoteServerPort,
                                    onBack = { currentScreen = AppScreen.SETTINGS }
                                )
                            }
                        }

                        // PIN DIALOG
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

                        // UPDATE DIALOG
                        if (showUpdateDialog && updateInfo != null) {
                            val info = updateInfo!!
                            UpdateDialog(
                                currentVersion = info.currentVersion,
                                latestVersion = info.latestVersion,
                                releaseNotes = info.releaseNotes,
                                forceUpdate = !BuildConfig.DEBUG,
                                downloadProgress = null,
                                isDownloading = false,
                                isInstalling = false,
                                onUpdateClick = {
                                    showUpdateDialog = false
                                    currentScreen = AppScreen.SETTINGS
                                },
                                onLaterClick = { showUpdateDialog = false },
                                onSkipClick = { showUpdateDialog = false }
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
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
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

