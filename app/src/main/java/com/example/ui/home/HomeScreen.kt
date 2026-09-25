package com.example.ui.home

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.DynamicSkyTheme
import com.example.data.local.IslamicEvent
import com.example.data.model.AppSettings
import com.example.data.model.BackgroundMode
import com.example.data.model.PrayerId
import com.example.data.model.PrayerSchedule
import com.example.ui.components.*
import com.example.ui.focus.QRISFocusOverlay
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalTime

@Composable
fun HomeScreen(
    settings: AppSettings,
    schedule: PrayerSchedule,
    currentTimeString: String,
    hijriDateString: String,
    gregorianDateString: String,
    upcomingEvent: IslamicEvent,
    temperature: Int = 30,
    weatherCondition: String = "Cerah",
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQrisModal by remember { mutableStateOf(false) }
    var userDismissedVideoFullscreen by remember { mutableStateOf(false) }

    // Automatic periodic QRIS Focus trigger (0-30 min slider)
    if (settings.qrisIntervalMinutes > 0) {
        LaunchedEffect(settings.qrisIntervalMinutes) {
            val intervalMs = settings.qrisIntervalMinutes * 60 * 1000L
            while (true) {
                delay(intervalMs)
                // Only trigger QRIS if next prayer is more than 5 minutes away
                if (schedule.secondsToNext > 300) {
                    showQrisModal = true
                }
            }
        }
    }

    // Determine smart video mode: Fullscreen if enabled, smart fullscreen checked, and next prayer > 30 minutes away
    val isSmartVideoFullscreen = settings.videoEnabled &&
            !settings.videoUri.isNullOrBlank() &&
            settings.videoSmartFullscreen &&
            schedule.secondsToNext > 1800 &&
            !userDismissedVideoFullscreen

    // Sky theme brush: Based on real-time clock!
    // At 15:13, it will be bright afternoon sky, not dark!
    val now = LocalTime.now()
    val realTimeSkyBrush = DynamicSkyTheme.getSkyBrush(now)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF071219))
    ) {
        // --- 1. BACKGROUND LAYER ---
        when (settings.backgroundMode) {
            BackgroundMode.CUSTOM -> {
                if (!settings.customBackgroundUri.isNullOrBlank()) {
                    AsyncImage(
                        model = settings.customBackgroundUri,
                        contentDescription = "Background Kustom Masjid",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = 0.5f
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(realTimeSkyBrush))
                }
            }
            BackgroundMode.KABAH -> {
                Image(
                    painter = painterResource(id = R.drawable.bg_kabah_1790267578617),
                    contentDescription = "Background Ka'bah",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.35f
                )
            }
            BackgroundMode.EMERALD_GEOMETRIC -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF041A12), Color(0xFF08261B), Color(0xFF04120D))
                            )
                        )
                )
            }
            else -> {
                // REAL-TIME DYNAMIC SKY THEME (Resolves 15:13 daylight issue!)
                Box(modifier = Modifier.fillMaxSize().background(realTimeSkyBrush))
            }
        }

        // Tint overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x22000000),
                            Color(0x66040B10),
                            Color(0xAA02070A)
                        )
                    )
                )
        )

        // Geometric Arabesque Pattern
        ArabesquePattern(lineColor = Color(0x12FFD700))

        // Ambient Weather Overlay (Rain / Clouds / Birds)
        if (settings.animationsEnabled) {
            WeatherAmbientOverlay(
                weatherCondition = weatherCondition,
                showBirds = settings.showBirdsAnimation
            )
        }

        // --- SMART FULLSCREEN VIDEO MODE ---
        if (isSmartVideoFullscreen) {
            Box(modifier = Modifier.fillMaxSize()) {
                MasjidVideoPlayer(
                    videoUriString = settings.videoUri,
                    isFullscreen = true,
                    modifier = Modifier.fillMaxSize()
                )

                // Top banner overlay indicating next prayer
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC000000))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clickable { userDismissedVideoFullscreen = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Mosque, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    val nextName = schedule.nextPrayer?.id?.displayName ?: "Sholat"
                    val mm = schedule.secondsToNext / 60
                    Text(
                        text = "$nextName dalam $mm menit • Sentuh / Tekan OK untuk Tampilan Penuh",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicGoldLight
                    )
                }
            }
        } else {
            // --- MAIN UI: STRICT 1-SCREEN PROPORTIONAL LAYOUT ---
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // [A] TOP BAR (tinggi 7%)
                TopBar(
                    locationName = "${settings.city}, ${settings.province}",
                    dayDateString = gregorianDateString,
                    currentTimeString = currentTimeString,
                    temperature = temperature,
                    weatherCondition = weatherCondition,
                    onSettingsClick = onSettingsClick,
                    modifier = Modifier.weight(0.07f)
                )

                // [B] MIDDLE CLOCK & VIDEO SECTION (tinggi 24%)
                // Auto-shifts: If video is in Split Mode, divides space into 58% clock + 42% video
                val isSplitVideo = settings.videoEnabled && !settings.videoUri.isNullOrBlank() && !settings.videoSmartFullscreen

                if (isSplitVideo) {
                    Row(
                        modifier = Modifier
                            .weight(0.24f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: Mosque Name + Compact Clock
                        Column(
                            modifier = Modifier.weight(0.58f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MosqueHeader(mosqueName = settings.mosqueName)
                            Spacer(modifier = Modifier.height(4.dp))
                            ClockAndDate(
                                timeString = currentTimeString,
                                hijriDateString = hijriDateString,
                                gregorianDateString = gregorianDateString
                            )
                        }

                        // Right side: Mosque Activity Video Player (harmonious, no collisions)
                        Box(
                            modifier = Modifier
                                .weight(0.42f)
                                .fillMaxHeight(0.95f)
                        ) {
                            MasjidVideoPlayer(
                                videoUriString = settings.videoUri,
                                isFullscreen = false,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    // Standard centered Clock & Mosque Header (neat and compact)
                    Column(
                        modifier = Modifier
                            .weight(0.24f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MosqueHeader(mosqueName = settings.mosqueName)
                        ClockAndDate(
                            timeString = currentTimeString,
                            hijriDateString = hijriDateString,
                            gregorianDateString = gregorianDateString
                        )
                    }
                }

                // [C] 6 KARTU SHOLAT 1 BARIS HORIZONTAL (tinggi 28%)
                PrayerCardsRow(
                    prayerItems = schedule.items,
                    modifier = Modifier.weight(0.28f)
                )

                // [D] PROGRESS COUNTDOWN MENUJU WAKTU SHOLAT (tinggi 5%)
                PrayerProgressBar(
                    nextPrayerName = schedule.nextPrayer?.id?.displayName ?: "Sholat",
                    secondsRemaining = schedule.secondsToNext,
                    progress = schedule.progressToNext,
                    modifier = Modifier.weight(0.05f)
                )

                // [E] KARTU MUTIARA NASIHAT / EVENT ISLAMI BERGANTI (tinggi 6%)
                WisdomCardCarousel(
                    upcomingEvent = upcomingEvent,
                    intervalSeconds = settings.wisdomCardIntervalSeconds,
                    animationType = settings.wisdomCardAnimation,
                    modifier = Modifier.weight(0.06f)
                )

                // [F] JADWAL PETUGAS HARI INI & FOTO USTADZ (tinggi 24%)
                OfficerCarousel(
                    officers = settings.officers,
                    weeklyOfficers = settings.weeklyOfficers,
                    officerPhotoUri = settings.officerPhotoUri,
                    nextPrayerId = schedule.nextPrayer?.id ?: PrayerId.MAGHRIB,
                    modifier = Modifier.weight(0.24f)
                )

                // [G] RUNNING TEXT MARQUEE (tinggi 6%)
                RunningTextMarquee(
                    text = settings.runningText,
                    speed = settings.runningTextSpeed,
                    fontSize = settings.runningTextFontSize,
                    modifier = Modifier.weight(0.06f)
                )
            }
        }

        // --- QRIS FOCUS MODAL OVERLAY ---
        if (showQrisModal) {
            QRISFocusOverlay(
                settings = settings,
                onDismiss = { showQrisModal = false }
            )
        }
    }
}
