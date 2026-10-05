package com.example

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.audio.SoundManager
import com.example.data.local.IslamicCalendar
import com.example.data.local.PrayerTimesCalculator
import com.example.data.local.SettingsRepository
import com.example.data.local.WeatherService
import com.example.data.model.PinLockMode
import com.example.data.model.PrayerId
import com.example.data.model.PrayerSchedule
import com.example.kiosk.KioskManager
import com.example.kiosk.WatchdogService
import com.example.ui.components.PinDialog
import com.example.ui.components.UpdateDialog
import com.example.ui.focus.AdzanSequenceOverlay
import com.example.ui.focus.PrayerFocusOverlay
import com.example.ui.focus.QRISFocusOverlay
import com.example.ui.home.HomeScreen
import com.example.ui.ramadhan.RamadhanOverlay
import com.example.ui.remote.IoControlScreen
import com.example.ui.remote.RemoteServer
import com.example.ui.remote.RestartCountdownOverlay
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.MasjidTheme
import com.example.ui.theme.MosqueDeepBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ApkDownloader
import com.example.util.BackupManager
import com.example.util.CrashAutoShowHelper
import com.example.util.CrashLogDialog
import com.example.util.CrashReporter
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
    ADZAN_SEQUENCE,
    FOCUS_MODE,
    SETTINGS,
    QRIS_PREVIEW,
    RAMADHAN,
    IO_CONTROL
}

private const val UPDATE_PREFS = "update_prefs"
private const val KEY_SKIPPED_VERSION = "skipped_version"
private const val PIN_TIMEOUT_MS = 5 * 60 * 1000L   // 5 menit

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var soundManager: SoundManager

    @Volatile
    private var needsHardRestart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CrashReporter.init(this)

        settingsRepository = SettingsRepository(this)
        soundManager = SoundManager(this)

        val hasPendingCrash = CrashAutoShowHelper.hasPendingCrash(this)
        val pendingCrashLog = if (hasPendingCrash) {
            CrashAutoShowHelper.getLastCrashLog(this)
        } else ""

        val deviceRole = if (isTV()) "TV" else "HP"
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        val doSoftRestart: () -> Unit = {
            runOnUiThread {
                try {
                    if (needsHardRestart) {
                        android.os.Process.killProcess(android.os.Process.myPid())
                    } else {
                        recreate()
                    }
                } catch (e: Exception) {
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
        }
        setContent {
    val context = LocalContext.current
    val settings by settingsRepository.settingsFlow.collectAsState()
    val scope = rememberCoroutineScope()

    var showCrashDialog by remember { mutableStateOf(hasPendingCrash) }

    var showStoragePermissionDialog by remember {
        mutableStateOf(BackupManager.needsStoragePermission())
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val needsPermission = BackupManager.needsStoragePermission()
                showStoragePermissionDialog = needsPermission
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    var showRestartCountdown by remember { mutableStateOf(false) }

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

    // ============================================================
    // V1.04.422 BARU — PIN LOCK STATE
    // ============================================================
    var sessionPinVerified by remember { mutableStateOf(false) }
    var lastPinVerifiedTime by remember { mutableLongStateOf(0L) }

    // Helper: cek apakah perlu minta PIN saat ini
    fun shouldRequestPin(): Boolean {
        return when (settings.pinLockMode) {
            PinLockMode.IMMEDIATE -> true
            PinLockMode.UNTIL_EXIT -> !sessionPinVerified
            PinLockMode.TIMEOUT_5MIN -> {
                if (!sessionPinVerified) return true
                val elapsed = System.currentTimeMillis() - lastPinVerifiedTime
                elapsed >= PIN_TIMEOUT_MS
            }
        }
    }

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

    LaunchedEffect(settings.kioskModeEnabled) {
        if (settings.kioskModeEnabled) {
            KioskManager.enableKiosk(this@MainActivity)
        } else {
            KioskManager.disableKiosk(this@MainActivity)
        }
    }

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
                        val portBerubah = newSettings.remoteServerPort != current.remoteServerPort
                        if (portBerubah) {
                            needsHardRestart = true
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Gagal apply received settings: ${e.message}")
                }
            },
            onFinalize = {
                runOnUiThread {
                    android.util.Log.d("MainActivity", "onFinalize dipanggil")
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
                    "Remote Server gagal: ${remoteServer.lastError}"
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

    val startDownload: () -> Unit = {
        val info = updateInfo
        if (info?.downloadUrl.isNullOrBlank()) {
            Toast.makeText(this@MainActivity, "URL download tidak tersedia", Toast.LENGTH_LONG).show()
        } else if (BackupManager.needsStoragePermission()) {
            Toast.makeText(this@MainActivity, "Izin akses file diperlukan untuk download update", Toast.LENGTH_LONG).show()
            showStoragePermissionDialog = true
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
                                ApkDownloader.deleteOldApks(this@MainActivity, keepCount = 2)
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
DisposableEffect(
    settings.kioskModeEnabled,
    currentScreen,
    showRestartCountdown,
    showStoragePermissionDialog
) {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (showUpdateDialog && updateInfo?.isForceUpdate == true) return
            if (isDownloading || isInstalling) return
            if (showRestartCountdown) return
            if (showStoragePermissionDialog) return

            when (currentScreen) {
                AppScreen.ADZAN_SEQUENCE -> {
                    currentScreen = AppScreen.FOCUS_MODE
                }
                AppScreen.FOCUS_MODE -> {
                    if (settings.focusModeAllowExitWithRemote) {
                        currentScreen = AppScreen.HOME
                    }
                }
                AppScreen.SETTINGS, AppScreen.QRIS_PREVIEW,
                AppScreen.RAMADHAN, AppScreen.IO_CONTROL ->
                    currentScreen = AppScreen.HOME
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
                    currentScreen = AppScreen.ADZAN_SEQUENCE
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

// ============ THEME + UI ============
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
                        onSettingsClick = {
                            // ============================================================
                            // V1.04.422 — PIN LOCK LOGIC
                            // Cek berdasarkan mode sebelum buka Settings
                            // ============================================================
                            if (shouldRequestPin()) {
                                showPinDialog = true
                            } else {
                                currentScreen = AppScreen.SETTINGS
                            }
                        }
                    )

                    AppScreen.ADZAN_SEQUENCE -> AdzanSequenceOverlay(
                        prayerId = focusPrayerId,
                        adzanDurationSeconds = settings.adzanDisplayDurationSeconds,
                        silentPhoneDurationSeconds = settings.silentPhoneDisplayDurationSeconds,
                        qobliyahDurationSeconds = settings.qobliyahNiatDisplayDurationSeconds,
                        iqamahWaitMinutes = settings.iqamahWaitMinutes,
                        onComplete = {
                            android.util.Log.d("MainActivity", "Adzan sequence selesai → Mode Fokus")
                            currentScreen = AppScreen.FOCUS_MODE
                        },
                        onSkip = {
                            android.util.Log.d("MainActivity", "Adzan sequence di-skip → Mode Fokus")
                            currentScreen = AppScreen.FOCUS_MODE
                        }
                    )

                    AppScreen.FOCUS_MODE -> PrayerFocusOverlay(
                        prayerId = focusPrayerId,
                        prayerTimeFormatted = focusPrayerTime,
                        totalDurationMinutes = settings.prayerFocusDurationMinutes,
                        iqamahWaitMinutes = settings.iqamahWaitMinutes,
                        qobliyahWaitMinutes = settings.qobliyahWaitMinutes,
                        settings = settings,
                        onDismiss = {
                            android.util.Log.d("MainActivity", "Mode Fokus selesai → Home")
                            currentScreen = AppScreen.HOME
                        }
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
                        appVersion = com.example.BuildConfig.VERSION_NAME,
                        serverPort = settings.remoteServerPort,
                        onBack = { currentScreen = AppScreen.SETTINGS }
                    )
                }
            }

            // ============ PIN DIALOG ============
            // V1.04.422: onSuccess set waktu & flag verified
            if (showPinDialog) {
                PinDialog(
                    correctPin = settings.pinCode,
                    onSuccess = {
                        showPinDialog = false
                        sessionPinVerified = true
                        lastPinVerifiedTime = System.currentTimeMillis()
                        if (currentScreen == AppScreen.FOCUS_MODE) {
                            currentScreen = AppScreen.HOME
                        } else {
                            currentScreen = AppScreen.SETTINGS
                        }
                    },
                    onDismiss = { showPinDialog = false }
                )
            }
                                    // ============ STORAGE PERMISSION DIALOG ============
                        if (showStoragePermissionDialog) {
                            StoragePermissionDialog(
                                onGrantClick = {
                                    BackupManager.openPermissionSettings(this@MainActivity)
                                },
                                onSkipClick = {
                                    showStoragePermissionDialog = false
                                }
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

                        // ============ RESTART COUNTDOWN OVERLAY ============
                        if (showRestartCountdown) {
                            RestartCountdownOverlay(
                                countdownStart = 5,
                                message = "Pengaturan & media baru sedang diterapkan",
                                onComplete = {
                                    android.util.Log.d("MainActivity", "Countdown selesai — restart sekarang")
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

    // ============================================================
    // LIFECYCLE
    // ============================================================
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

// ============================================================
// STORAGE PERMISSION DIALOG
// Muncul otomatis saat pertama kali app dibuka
// ============================================================
@Composable
private fun StoragePermissionDialog(
    onGrantClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    var isFocusedGrant by remember { mutableStateOf(false) }
    var isFocusedSkip by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { /* tidak bisa dismiss dengan tap luar */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .width(560.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0B1720))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ICON
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(IslamicGold.copy(alpha = 0.15f))
                    .border(2.dp, IslamicGold, RoundedCornerShape(36.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(40.dp)
                )
            }

            // JUDUL
            Text(
                text = "IZIN AKSES FILE DIPERLUKAN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight,
                textAlign = TextAlign.Center
            )

            // DESKRIPSI
            Text(
                text = "MASJID.IO perlu izin \"Akses semua file\" supaya bisa:",
                fontSize = 13.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            // LIST MANFAAT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33000000))
                    .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BenefitRow(icon = "📥", text = "Download update APK otomatis")
                BenefitRow(icon = "💾", text = "Simpan Backup Aman ke /sdcard/masjid.io/")
                BenefitRow(icon = "📁", text = "Baca file template .iO dari penyimpanan")
                BenefitRow(icon = "📷", text = "Akses foto & video dari galeri")
            }

            // CARA AKTIVASI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x22FFD700))
                    .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "📋 Cara Aktivasi:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Text(
                        text = "1. Tap tombol BERI IZIN di bawah\n" +
                                "2. Cari MASJID.IO di daftar aplikasi\n" +
                                "3. Aktifkan toggle izinnya\n" +
                                "4. Tekan tombol Kembali (back)",
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // TOMBOL BERI IZIN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFocusedGrant) IslamicGoldLight else IslamicGold)
                    .border(
                        width = if (isFocusedGrant) 3.dp else 0.dp,
                        color = if (isFocusedGrant) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onGrantClick() }
                    .focusable()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color(0xFF09141D),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "BERI IZIN SEKARANG",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF09141D)
                )
            }

            // TOMBOL NANTI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isFocusedSkip) Color(0x44FFFFFF) else Color.Transparent)
                    .border(
                        width = if (isFocusedSkip) 2.dp else 1.dp,
                        color = if (isFocusedSkip) IslamicGoldLight else TextSecondary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSkipClick() }
                    .focusable()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NANTI SAJA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }

            // WARNING KECIL
            Text(
                text = "⚠️ Tanpa izin ini, update & backup tidak akan berfungsi",
                fontSize = 10.sp,
                color = Color(0xFFFF8A80),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                lineHeight = 14.sp
            )
        }
    }
}

// ============================================================
// HELPER: BENEFIT ROW (untuk StoragePermissionDialog)
// ============================================================
@Composable
private fun BenefitRow(
    icon: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
