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
import com.example.ui.focus.PrayerFocusOverlay
import com.example.ui.focus.QRISFocusOverlay
import com.example.ui.home.HomeScreen
import com.example.ui.components.PinDialog
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.MasjidTheme
import com.example.ui.theme.MosqueDeepBg
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class AppScreen {
    HOME,
    FOCUS_MODE,
    SETTINGS,
    QRIS_PREVIEW
}

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settingsRepository = SettingsRepository(this)
        soundManager = SoundManager(this)

        setContent {
            val settings by settingsRepository.settingsFlow.collectAsState()

            // ==== KIOSK MODE: Enable / Disable saat setting berubah ====
            LaunchedEffect(settings.kioskModeEnabled) {
                if (settings.kioskModeEnabled) {
                    KioskManager.enableKiosk(this@MainActivity)
                } else {
                    KioskManager.disableKiosk(this@MainActivity)
                }
            }

            // ==== WATCHDOG: Start saat kiosk aktif, stop saat nonaktif ====
            DisposableEffect(settings.kioskModeEnabled) {
                val serviceIntent = Intent(this@MainActivity, WatchdogService::class.java)
                if (settings.kioskModeEnabled) {
                    try {
                        startService(serviceIntent)
                    } catch (_: Exception) { }
                } else {
                    try {
                        stopService(serviceIntent)
                    } catch (_: Exception) { }
                }
                onDispose {
                    try {
                        stopService(serviceIntent)
                    } catch (_: Exception) { }
                }
            }

            // Request Gallery / Media Permissions on launch
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { /* Results handled gracefully */ }

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

            // Keep Screen On dynamically
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

            // Weather state (real-time from Open-Meteo)
            var currentTemperature by remember { mutableStateOf(30) }
            var currentWeatherCondition by remember { mutableStateOf("Cerah") }

            LaunchedEffect(settings.latitude, settings.longitude) {
                while (true) {
                    val weather = WeatherService.fetchWeather(settings.latitude, settings.longitude)
                    currentTemperature = weather.temperature
                    currentWeatherCondition = weather.condition
                    delay(30 * 60 * 1000L)
                }
            }

            // Kiosk Back Press Handling
            DisposableEffect(settings.kioskModeEnabled, currentScreen) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        when (currentScreen) {
                            AppScreen.SETTINGS, AppScreen.QRIS_PREVIEW -> {
                                currentScreen = AppScreen.HOME
                            }
                            AppScreen.FOCUS_MODE -> {
                                showPinDialog = true
                            }
                            AppScreen.HOME -> {
                                if (settings.kioskModeEnabled) {
                                    showPinDialog = true
                                } else {
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

            // Real-Time Clock & Prayer Schedule State
            var currentTimeString by remember { mutableStateOf("12:00:00") }
            var hijriDateString by remember { mutableStateOf("17 Rajab 1447 H") }
            var gregorianDateString by remember { mutableStateOf("Jum'at, 24 September 2026") }
            var prayerSchedule by remember { mutableStateOf(PrayerSchedule()) }

            val today = remember { LocalDate.now() }
            val upcomingEvent = remember {
                val hDate = IslamicCalendar.getHijriDate(today)
                IslamicCalendar.getUpcomingEvent(hDate)
            }

            // 1-second continuous ticker
            LaunchedEffect(settings.latitude, settings.longitude, settings.isManualTimeEnabled, settings.manualTimeOffsetSeconds) {
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                var lastTriggeredPrayerMinute: String? = null

                while (true) {
                    val currentDateTime = if (settings.isManualTimeEnabled) {
                        java.time.LocalDateTime.now().plusSeconds(settings.manualTimeOffsetSeconds)
                    } else {
                        java.time.LocalDateTime.now()
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
                                    adzanStyle = settings.adzanFile,
                                    adzanVolume = settings.adzanVolume
                                )
                                currentScreen = AppScreen.FOCUS_MODE
                                break
                            }
                        }
                    }

                    delay(1000)
                }
            }

            MasjidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MosqueDeepBg
                ) {
                    Crossfade(targetState = currentScreen, label = "screen_fade") { screen ->
                        when (screen) {
                            AppScreen.HOME -> {
                                HomeScreen(
                                    settings = settings,
                                    schedule = prayerSchedule,
                                    currentTimeString = currentTimeString,
                                    hijriDateString = hijriDateString,
                                    gregorianDateString = gregorianDateString,
                                    upcomingEvent = upcomingEvent,
                                    temperature = currentTemperature,
                                    weatherCondition = currentWeatherCondition,
                                    onSettingsClick = {
                                        showPinDialog = true
                                    }
                                )
                            }
                            AppScreen.FOCUS_MODE -> {
                                PrayerFocusOverlay(
                                    prayerId = focusPrayerId,
                                    prayerTimeFormatted = focusPrayerTime,
                                    totalDurationMinutes = settings.prayerFocusDurationMinutes,
                                    iqamahWaitMinutes = settings.iqamahWaitMinutes,
                                    qobliyahWaitMinutes = settings.qobliyahWaitMinutes,
                                    onDismiss = {
                                        currentScreen = AppScreen.HOME
                                    }
                                )
                            }
                            AppScreen.SETTINGS -> {
                                SettingsScreen(
                                    currentSettings = settings,
                                    soundManager = soundManager,
                                    onSaveSettings = { updated ->
                                        settingsRepository.updateSettings(updated)
                                        currentScreen = AppScreen.HOME
                                    },
                                    onBack = {
                                        currentScreen = AppScreen.HOME
                                    },
                                    onTestQrisFocus = {
                                        currentScreen = AppScreen.QRIS_PREVIEW
                                    }
                                )
                            }
                            AppScreen.QRIS_PREVIEW -> {
                                QRISFocusOverlay(
                                    settings = settings,
                                    onDismiss = {
                                        currentScreen = AppScreen.SETTINGS
                                    }
                                )
                            }
                        }
                    }

                    // PIN Dialog
                    if (showPinDialog) {
                        PinDialog(
                            correctPin = settings.pinCode,
                            onSuccess = {
                                showPinDialog = false
                                if (currentScreen == AppScreen.FOCUS_MODE) {
                                    currentScreen = AppScreen.HOME
                                } else {
                                    currentScreen = AppScreen.SETTINGS
                                }
                            },
                            onDismiss = {
                                showPinDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Tandai Activity di foreground untuk Watchdog
        KioskManager.isMainActivityForeground = true
    }

    override fun onPause() {
        super.onPause()
        // Tandai Activity tidak di foreground
        KioskManager.isMainActivityForeground = false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.stopAll()
    }
}
