package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.content.ContentRotationStore
import com.example.data.content.RotationType
import com.example.data.local.DynamicSkyTheme
import com.example.data.local.IslamicEvent
import com.example.data.model.AppSettings
import com.example.data.model.BackgroundMode
import com.example.data.model.PrayerId
import com.example.data.model.PrayerSchedule
import com.example.ui.cctv.CctvWidget
import com.example.ui.components.ArabesquePattern
import com.example.ui.components.AspectType
import com.example.ui.components.ClockAndDate
import com.example.ui.components.MakkahDynamicBackground
import com.example.ui.components.MasjidVideoPlayer
import com.example.ui.components.MosqueHeader
import com.example.ui.components.OfficerCarousel
import com.example.ui.components.PhotoSlideshow
import com.example.ui.components.PrayerCardsRow
import com.example.ui.components.PrayerProgressBar
import com.example.ui.components.ResponsiveRoot
import com.example.ui.components.RunningTextMarquee
import com.example.ui.components.ScreenInfo
import com.example.ui.components.TopBar
import com.example.ui.components.WeatherAmbientOverlay
import com.example.ui.components.WisdomCardCarousel
import com.example.ui.components.scaledDp
import com.example.ui.components.scaledSp
import com.example.ui.focus.QRISFocusOverlay
import com.example.ui.slides.SlideManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
    ResponsiveRoot(
        autoScaleEnabled = settings.tvAutoScaleEnabled,
        safeAreaPercent = settings.tvSafeAreaPercent
    ) { screenInfo ->
        HomeScreenContent(
            settings = settings,
            schedule = schedule,
            currentTimeString = currentTimeString,
            hijriDateString = hijriDateString,
            gregorianDateString = gregorianDateString,
            upcomingEvent = upcomingEvent,
            temperature = temperature,
            weatherCondition = weatherCondition,
            onSettingsClick = onSettingsClick,
            screenInfo = screenInfo,
            modifier = modifier
        )
    }
}

@Composable
private fun HomeScreenContent(
    settings: AppSettings,
    schedule: PrayerSchedule,
    currentTimeString: String,
    hijriDateString: String,
    gregorianDateString: String,
    upcomingEvent: IslamicEvent,
    temperature: Int,
    weatherCondition: String,
    onSettingsClick: () -> Unit,
    screenInfo: ScreenInfo,
    modifier: Modifier = Modifier
) {
    var showQrisModal by remember { mutableStateOf(false) }
    var showSlideOverlay by remember { mutableStateOf(false) }
    var userDismissedVideoFullscreen by remember { mutableStateOf(false) }
    var userDismissedSlide by remember { mutableStateOf(false) }

    val layoutPad: Float = when {
        settings.tvLayoutPreset == "ULTRAWIDE" -> 0.05f
        settings.tvLayoutPreset == "AUTO" && screenInfo.aspectType == AspectType.ULTRAWIDE -> 0.05f
        else -> 0f
    }
    val contentMaxWidth: Float = when {
        settings.tvLayoutPreset == "ULTRAWIDE" -> 0.88f
        settings.tvLayoutPreset == "AUTO" && screenInfo.aspectType == AspectType.ULTRAWIDE -> 0.88f
        else -> 1f
    }

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
            schedule.secondsToNext > 300
        } else true

        if (canShow && !userDismissedSlide) {
            showSlideOverlay = true
        } else {
            showSlideOverlay = false
        }
    }

    LaunchedEffect(settings.slideEnabled) {
        if (!settings.slideEnabled) userDismissedSlide = false
    }

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
        when (settings.backgroundMode) {
            BackgroundMode.MAKKAH_DYNAMIC -> {
                MakkahDynamicBackground(
                    weatherCondition = weatherCondition,
                    modifier = Modifier.fillMaxSize()
                )
            }
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

        if (settings.backgroundMode != BackgroundMode.MAKKAH_DYNAMIC) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0x22000000), Color(0x66040B10), Color(0xAA02070A))
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0x66000000),
                                Color(0xAA000000)
                            )
                        )
                    )
            )
        }

        ArabesquePattern(lineColor = Color(0x12FFD700))

        if (settings.animationsEnabled && settings.backgroundMode != BackgroundMode.MAKKAH_DYNAMIC) {
            WeatherAmbientOverlay(
                weatherCondition = weatherCondition,
                showBirds = settings.showBirdsAnimation
            )
        }

        if (settings.cctvEnabled && settings.cctvUrl.isNotBlank()) {
            CctvWidget(
                settings = settings,
                modifier = Modifier.fillMaxSize()
            )
        }

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
                        .padding(top = screenInfo.scaledDp(16))
                        .clip(RoundedCornerShape(screenInfo.scaledDp(12)))
                        .background(Color(0xCC000000))
                        .padding(
                            horizontal = screenInfo.scaledDp(20),
                            vertical = screenInfo.scaledDp(8)
                        )
                        .clickable { userDismissedVideoFullscreen = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(screenInfo.scaledDp(18))
                    )
                    Spacer(modifier = Modifier.width(screenInfo.scaledDp(10)))
                    val nextName = schedule.nextPrayer?.id?.displayName ?: "Sholat"
                    val mm = schedule.secondsToNext / 60
                    Text(
                        text = "$nextName dalam $mm menit • Sentuh untuk tampilan penuh",
                        fontSize = screenInfo.scaledSp(13),
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicGoldLight
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(screenInfo.safePaddingDp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = screenInfo.widthDp.dp * layoutPad)
                        .then(
                            if (contentMaxWidth < 1f) {
                                Modifier.width(screenInfo.widthDp.dp * contentMaxWidth)
                            } else Modifier
                        )
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TopBar(
                        locationName = "${settings.city}, ${settings.province}",
                        dayDateString = gregorianDateString,
                        currentTimeString = currentTimeString,
                        temperature = temperature,
                        weatherCondition = weatherCondition,
                        onSettingsClick = onSettingsClick,
                        modifier = Modifier.weight(0.07f)
                    )

                    if (isSplitMode) {
                        Row(
                            modifier = Modifier
                                .weight(0.24f)
                                .fillMaxWidth()
                                .padding(horizontal = screenInfo.scaledDp(24)),
                            horizontalArrangement = Arrangement.spacedBy(screenInfo.scaledDp(16)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(0.38f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MosqueHeader(mosqueName = settings.mosqueName)
                                Spacer(modifier = Modifier.height(screenInfo.scaledDp(4)))
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

                    PrayerCardsRow(
                        prayerItems = schedule.items,
                        settings = settings,
                        modifier = Modifier
                            .weight(0.28f)
                            .fillMaxWidth(if (isSplitMode) 0.62f else 1f)
                    )

                    PrayerProgressBar(
                        nextPrayerName = schedule.nextPrayer?.id?.displayName ?: "Sholat",
                        secondsRemaining = schedule.secondsToNext,
                        progress = schedule.progressToNext,
                        modifier = Modifier.weight(0.05f)
                    )

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

                    OfficerCarousel(
                        officers = settings.officers,
                        weeklyOfficers = settings.weeklyOfficers,
                        officerPhotoUri = settings.officerPhotoUri,
                        nextPrayerId = schedule.nextPrayer?.id ?: PrayerId.MAGHRIB,
                        modifier = Modifier.weight(0.24f)
                    )

                    RunningTextMarquee(
                        text = settings.runningText,
                        speed = settings.runningTextSpeed,
                        fontSize = settings.runningTextFontSize,
                        modifier = Modifier.weight(0.06f)
                    )
                }
            }
        }

        if (showQrisModal && !showSlideOverlay) {
            QRISFocusOverlay(
                settings = settings,
                onDismiss = { showQrisModal = false }
            )
        }

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
                    if (!content.arabic.isNullOrBlank()) {
                        Text(
                            text = content.arabic,
                            fontSize = 14.sp,
                            color = IslamicGoldLight,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = content.latin ?: content.translation ?: "",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

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
