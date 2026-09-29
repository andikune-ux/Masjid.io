package com.example.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.content.AsmaulHusnaStore
import com.example.data.content.AyatStore
import com.example.data.content.ContentRotationStore
import com.example.data.content.HaditsStore
import com.example.data.content.RotationType
import com.example.data.local.DynamicSkyTheme
import com.example.data.local.IslamicEvent
import com.example.data.model.AppSettings
import com.example.data.model.BackgroundMode
import com.example.data.model.PrayerId
import com.example.data.model.PrayerSchedule
import com.example.ui.cctv.CctvWidget
import com.example.ui.components.*
import com.example.ui.focus.QRISFocusOverlay
import com.example.ui.slides.SlideManager
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
    var showSlideOverlay by remember { mutableStateOf(false) }
    var userDismissedVideoFullscreen by remember { mutableStateOf(false) }
    var userDismissedSlide by remember { mutableStateOf(false) }

    // ============ SLIDE FULLSCREEN TRIGGER ============
    // Slide muncul otomatis kalau:
    // - slideEnabled = true
    // - Tidak ada waktu sholat dekat (idle > 5 menit) ATAU slideShowOnlyWhenIdle = false
    // - User belum dismiss slide ini
    LaunchedEffect(
        settings.slideEnabled,
        settings.slideShowOnlyWhenIdle,
        schedule.secondsToNext,
        userDismissedSlide
    ) {
        if (!settings.slideEnabled) {
            showSlideOverlay = false
            return@LaunchedEffect
        }

        val canShow = if (settings.slideShowOnlyWhenIdle) {
            schedule.secondsToNext > 300  // lebih dari 5 menit ke sholat berikutnya
        } else true

        if (canShow && !userDismissedSlide) {
            showSlideOverlay = true
        } else {
            showSlideOverlay = false
        }
    }

    // Reset userDismissedSlide kalau slide dimatikan
    LaunchedEffect(settings.slideEnabled) {
        if (!settings.slideEnabled) userDismissedSlide = false
    }

    // Automatic periodic QRIS Focus trigger (kalau tidak dalam mode slide)
    if (settings.qrisIntervalMinutes > 0 && !showSlideOverlay) {
        LaunchedEffect(settings.qrisIntervalMinutes, showSlideOverlay) {
            if (showSlideOverlay) return@LaunchedEffect
            val intervalMs = settings.qrisIntervalMinutes * 60 * 1000L
            while (true) {
                delay(intervalMs)
                if (schedule.secondsToNext > 300) {
                    showQrisModal = true
                }
            }
        }
    }

    val isSmartVideoFullscreen = settings.videoEnabled &&
            !settings.videoUri.isNullOrBlank() &&
            settings.videoSmartFullscreen &&
            schedule.secondsToNext > 1800 &&
            !userDismissedVideoFullscreen

    val isSplitVideo = settings.videoEnabled &&
            !settings.videoUri.isNullOrBlank() &&
            !settings.videoSmartFullscreen

    val isSplitPhoto = !settings.videoEnabled &&
            settings.photoSlideshowEnabled &&
            settings.photoSlideshowUris.isNotEmpty()

    val isSplitMode = isSplitVideo || isSplitPhoto

    val now = LocalTime.now()
    val realTimeSkyBrush = DynamicSkyTheme.getSkyBrush(now)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF071219))
    ) {
        // ============ BACKGROUND LAYER ============
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
                Box(modifier = Modifier.fillMaxSize().background(realTimeSkyBrush))
            }
        }

        // Tint overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x22000000), Color(0x66040B10), Color(0xAA02070A))
                    )
                )
        )

        ArabesquePattern(lineColor = Color(0x12FFD700))

        if (settings.animationsEnabled) {
            WeatherAmbientOverlay(
                weatherCondition = weatherCondition,
                showBirds = settings.showBirdsAnimation
            )
        }
        
        // ============ CCTV WIDGET (selalu tampil kalau aktif) ============
        if (settings.cctvEnabled && settings.cctvUrl.isNotBlank()) {
            CctvWidget(
                settings = settings,
                modifier = Modifier.fillMaxSize()
            )
        }

        // ============ SMART FULLSCREEN VIDEO MODE ============
        if (isSmartVideoFullscreen) {
            Box(modifier = Modifier.fillMaxSize()) {
                MasjidVideoPlayer(
                    videoUriString = settings.videoUri,
                    isFullscreen = true,
                    modifier = Modifier.fillMaxSize()
                )
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
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    val nextName = schedule.nextPrayer?.id?.displayName ?: "Sholat"
                    val mm = schedule.secondsToNext / 60
                    Text(
                        text = "$nextName dalam $mm menit • Sentuh untuk tampilan penuh",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicGoldLight
                    )
                }
            }
        } else {
            // ============ MAIN UI ============
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // [A] TOP BAR
                TopBar(
                    locationName = "${settings.city}, ${settings.province}",
                    dayDateString = gregorianDateString,
                    currentTimeString = currentTimeString,
                    temperature = temperature,
                    weatherCondition = weatherCondition,
                    onSettingsClick = onSettingsClick,
                    modifier = Modifier.weight(0.07f)
                )

                // [B] MIDDLE: JAM + Video/Foto
                if (isSplitMode) {
                    Row(
                        modifier = Modifier
                            .weight(0.24f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(0.38f),
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
                        Box(
                            modifier = Modifier
                                .weight(0.62f)
                                .fillMaxHeight(0.95f)
                        ) {
                            if (isSplitVideo) {
                                MasjidVideoPlayer(
                                    videoUriString = settings.videoUri,
                                    isFullscreen = false,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                PhotoSlideshow(
                                    photoUris = settings.photoSlideshowUris,
                                    intervalSeconds = settings.photoSlideshowIntervalSeconds,
                                    isFullscreen = false,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                } else {
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

                // [C] 6 KARTU SHOLAT (passing settings!)
                PrayerCardsRow(
                    prayerItems = schedule.items,
                    settings = settings,
                    modifier = Modifier
                        .weight(0.28f)
                        .fillMaxWidth(if (isSplitMode) 0.62f else 1f)
                )

                // [D] PROGRESS COUNTDOWN
                PrayerProgressBar(
                    nextPrayerName = schedule.nextPrayer?.id?.displayName ?: "Sholat",
                    secondsRemaining = schedule.secondsToNext,
                    progress = schedule.progressToNext,
                    modifier = Modifier.weight(0.05f)
                )

                // [E] KONTEN ROTASI (Ayat/Hadits/Asmaul Husna) ATAU Wisdom Card
                if (settings.contentRotationEnabled) {
                    ContentRotationCard(
                        settings = settings,
                        intervalSeconds = settings.contentRotationIntervalSeconds,
                        modifier = Modifier.weight(0.06f)
                    )
                } else {
                    WisdomCardCarousel(
                        upcomingEvent = upcomingEvent,
                        intervalSeconds = settings.wisdomCardIntervalSeconds,
                        animationType = settings.wisdomCardAnimation,
                        modifier = Modifier.weight(0.06f)
                    )
                }

                // [F] PANEL IMAM & MUADZIN
                OfficerCarousel(
                    officers = settings.officers,
                    weeklyOfficers = settings.weeklyOfficers,
                    officerPhotoUri = settings.officerPhotoUri,
                    nextPrayerId = schedule.nextPrayer?.id ?: PrayerId.MAGHRIB,
                    modifier = Modifier.weight(0.24f)
                )

                // [G] RUNNING TEXT
                RunningTextMarquee(
                    text = settings.runningText,
                    speed = settings.runningTextSpeed,
                    fontSize = settings.runningTextFontSize,
                    modifier = Modifier.weight(0.06f)
                )
            }
        }

        // ============ QRIS FOCUS MODAL ============
        if (showQrisModal && !showSlideOverlay) {
            QRISFocusOverlay(
                settings = settings,
                onDismiss = { showQrisModal = false }
            )
        }

        // ============ SLIDE MANAGER OVERLAY ============
        if (showSlideOverlay) {
            SlideManager(
                settings = settings,
                nextPrayerSeconds = schedule.secondsToNext,
                onDismiss = {
                    showSlideOverlay = false
                    userDismissedSlide = true
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ============================================================
// KONTEN ROTASI CARD — Ayat / Hadits / Asmaul Husna
// ============================================================

@Composable
private fun ContentRotationCard(
    settings: AppSettings,
    intervalSeconds: Int,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(intervalSeconds) {
        val safeInterval = intervalSeconds.coerceIn(5, 120)
        while (true) {
            delay(safeInterval * 1000L)
            currentIndex = (currentIndex + 1) % 3
        }
    }

    val content = remember(currentIndex, settings) {
        ContentRotationStore.getContent(settings, currentIndex)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (content == null) {
            // Tidak ada konten aktif
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFD700)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aktifkan konten rotasi di pengaturan",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33000000))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon berdasarkan tipe
                Text(
                    text = when (content.type) {
                        RotationType.AYAT -> "📖"
                        RotationType.HADITS -> "📜"
                        RotationType.ASMAUL_HUSNA -> "✨"
                        else -> "💎"
                    },
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Arab (kalau ada)
                    if (!content.arabic.isNullOrBlank()) {
                        Text(
                            text = content.arabic,
                            fontSize = 14.sp,
                            color = IslamicGoldLight,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                    // Latin atau terjemahan
                    Text(
                        text = content.latin ?: content.translation ?: "",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Source
                Text(
                    text = content.source ?: "",
                    fontSize = 11.sp,
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
