package dev.andikune.masjidio.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.andikune.masjidio.data.content.ContentRotationStore
import dev.andikune.masjidio.data.content.RotationType
import dev.andikune.masjidio.data.local.DynamicSkyTheme
import dev.andikune.masjidio.data.local.IslamicEvent
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.data.model.BackgroundMode
import dev.andikune.masjidio.data.model.PrayerId
import dev.andikune.masjidio.data.model.PrayerItem
import dev.andikune.masjidio.data.model.PrayerSchedule
import dev.andikune.masjidio.ui.components.ArabesquePattern
import dev.andikune.masjidio.ui.components.AspectType
import dev.andikune.masjidio.ui.components.ClockAndDate
import dev.andikune.masjidio.ui.components.MakkahDynamicBackground
import dev.andikune.masjidio.ui.components.MasjidVideoPlayer
import dev.andikune.masjidio.ui.components.MosqueHeader
import dev.andikune.masjidio.ui.components.OfficerCarousel
import dev.andikune.masjidio.ui.components.PhotoSlideshow
import dev.andikune.masjidio.ui.components.PrayerCardsRow
import dev.andikune.masjidio.ui.components.PrayerProgressBar
import dev.andikune.masjidio.ui.components.ResponsiveRoot
import dev.andikune.masjidio.ui.components.RunningTextMarquee
import dev.andikune.masjidio.ui.components.ScreenInfo
import dev.andikune.masjidio.ui.components.TopBar
import dev.andikune.masjidio.ui.components.WeatherAmbientOverlay
import dev.andikune.masjidio.ui.components.WisdomCardCarousel
import dev.andikune.masjidio.ui.focus.QRISFocusOverlay
import dev.andikune.masjidio.ui.slides.SlideManager
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
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
    var currentMode by remember { mutableStateOf("VIDEO") }
    var videoLoopedOnce by remember { mutableStateOf(false) }

    val hasVideo = settings.videoEnabled && !settings.videoUri.isNullOrBlank()
    val hasPhotos = settings.photoSlideshowEnabled && settings.photoSlideshowUris.isNotEmpty()
    val hasAnyMedia = hasVideo || hasPhotos

    val effectiveMode = if (!hasAnyMedia) "NORMAL" else currentMode

    LaunchedEffect(
        settings.autoSwitchEnabled,
        effectiveMode,
        hasAnyMedia,
        settings.videoModeIntervalMinutes,
        settings.normalModeDurationMinutes,
        settings.waitVideoFinishBeforeSwitch
    ) {
        if (!settings.autoSwitchEnabled) return@LaunchedEffect
        if (!hasAnyMedia) return@LaunchedEffect

        val durationMin = if (effectiveMode == "VIDEO") {
            settings.videoModeIntervalMinutes
        } else {
            settings.normalModeDurationMinutes
        }
        val durationMs = durationMin.coerceAtLeast(1) * 60_000L

        delay(durationMs)

        if (effectiveMode == "VIDEO" && settings.waitVideoFinishBeforeSwitch && hasVideo) {
            videoLoopedOnce = false
            while (!videoLoopedOnce) {
                delay(500L)
            }
        }

        currentMode = if (effectiveMode == "VIDEO") "NORMAL" else "VIDEO"
        videoLoopedOnce = false
    }

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
                        contentDescription = "Background Kustom",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = 0.5f
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DynamicSkyTheme.getSkyBrush(LocalTime.now()))
                    )
                }
            }
            BackgroundMode.KABAH -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF050C15), Color(0xFF1A0D20), Color(0xFF050C15))
                            )
                        )
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DynamicSkyTheme.getSkyBrush(LocalTime.now()))
                )
            }
        }

        ArabesquePattern(lineColor = Color(0x12FFD700))

        if (settings.animationsEnabled && settings.backgroundMode != BackgroundMode.MAKKAH_DYNAMIC) {
            WeatherAmbientOverlay(
                weatherCondition = weatherCondition,
                showBirds = settings.showBirdsAnimation
            )
        }

        if (effectiveMode == "VIDEO" && hasAnyMedia) {
            VideoModeLayout(
                settings = settings,
                schedule = schedule,
                currentTimeString = currentTimeString,
                hijriDateString = hijriDateString,
                gregorianDateString = gregorianDateString,
                hasVideo = hasVideo,
                hasPhotos = hasPhotos,
                screenInfo = screenInfo,
                onSettingsClick = onSettingsClick,
                onVideoLooped = { videoLoopedOnce = true }
            )
        } else {
            NormalModeLayout(
                settings = settings,
                schedule = schedule,
                currentTimeString = currentTimeString,
                hijriDateString = hijriDateString,
                gregorianDateString = gregorianDateString,
                upcomingEvent = upcomingEvent,
                temperature = temperature,
                weatherCondition = weatherCondition,
                screenInfo = screenInfo,
                onSettingsClick = onSettingsClick
            )
        }
    }
}
// ============================================================
// MODE VIDEO (Opsi H) — V1.04.423 FINAL
// ============================================================
@Composable
private fun VideoModeLayout(
    settings: AppSettings,
    schedule: PrayerSchedule,
    currentTimeString: String,
    hijriDateString: String,
    gregorianDateString: String,
    hasVideo: Boolean,
    hasPhotos: Boolean,
    screenInfo: ScreenInfo,
    onSettingsClick: () -> Unit,
    onVideoLooped: () -> Unit
) {
    val isFullMode = settings.videoFrameScale == "FULL"

    val contentScale: ContentScale = when (settings.videoFrameScale) {
        "POTONG" -> ContentScale.Crop
        "PAS" -> ContentScale.Fit
        "ZOOM" -> ContentScale.Crop
        "FULL" -> ContentScale.Crop
        "FIT" -> ContentScale.FillBounds
        else -> ContentScale.Crop
    }

    val zoomFactor: Float = when (settings.videoFrameScale) {
        "ZOOM" -> 1.15f
        else -> 1.0f
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // PANEL KIRI 24%
                if (!isFullMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.24f)
                            .background(Color(0xDD071219))
                            .border(
                                width = 1.dp,
                                color = IslamicGold.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .padding(8.dp)
                    ) {
                        VideoModeLeftPanel(
                            settings = settings,
                            schedule = schedule,
                            currentTimeString = currentTimeString,
                            hijriDateString = hijriDateString,
                            gregorianDateString = gregorianDateString
                        )
                    }
                }

                // PANEL KANAN 76% — Video Stage Full-Bleed
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(if (isFullMode) 1f else 0.76f)
                        .background(Color.Black)
                ) {
                    if (hasVideo) {
                        MasjidVideoPlayer(
                            videoUriString = settings.videoUri,
                            isFullscreen = true,
                            contentScale = contentScale,
                            zoomFactor = zoomFactor,
                            onVideoLooped = onVideoLooped,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (hasPhotos) {
                        PhotoSlideshow(
                            photoUris = settings.photoSlideshowUris,
                            intervalSeconds = settings.photoSlideshowIntervalSeconds,
                            isFullscreen = true,
                            contentScale = contentScale,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Wisdom card overlay full width panel kanan
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        WisdomCardCarousel(
                            upcomingEvent = null,
                            intervalSeconds = settings.wisdomCardIntervalSeconds,
                            animationType = settings.wisdomCardAnimation,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Tombol ⚙ overlay sudut kanan atas layar
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                SettingsOverlayButton(onClick = onSettingsClick)
            }
        }

        // Running Text fixed di paling bawah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            RunningTextMarquee(
                text = settings.runningText,
                speed = settings.runningTextSpeed,
                fontSize = settings.runningTextFontSize,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================
// PANEL KIRI VIDEO MODE
// ============================================================
@Composable
private fun VideoModeLeftPanel(
    settings: AppSettings,
    schedule: PrayerSchedule,
    currentTimeString: String,
    hijriDateString: String,
    gregorianDateString: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // LOGO + NAMA MASJID
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC091620))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🕌", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = settings.mosqueName.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.3.sp,
                    lineHeight = 13.sp
                )
            }
        }

        // KOTAK JAM + TANGGAL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC091620))
                .border(1.5.dp, IslamicGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = currentTimeString,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(1.dp)
                        .background(IslamicGold.copy(alpha = 0.7f))
                )
                Text(
                    text = hijriDateString,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 12.sp
                )
                Text(
                    text = gregorianDateString,
                    fontSize = 9.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 11.sp
                )
            }
        }

        // LIST SHOLAT VERTIKAL pakai weight(1f)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            schedule.items.forEach { item ->
                PrayerRowItem(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        // KOTAK PROGRESS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC091620))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MENUJU ${schedule.nextPrayer?.id?.displayName?.uppercase() ?: "SHOLAT"}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight,
                        letterSpacing = 0.3.sp,
                        maxLines = 1
                    )
                }
                val h = schedule.secondsToNext / 3600
                val m = (schedule.secondsToNext % 3600) / 60
                val s = schedule.secondsToNext % 60
                Text(
                    text = String.format("%02d:%02d:%02d", h, m, s),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x33FFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(schedule.progressToNext.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(IslamicGold, IslamicGoldLight)
                                )
                            )
                    )
                }
            }
        }
    }
}

// ============================================================
// ITEM SHOLAT VERTIKAL — Icon berbeda per waktu
// ============================================================
@Composable
private fun PrayerRowItem(
    item: PrayerItem,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        item.isNext -> Color(0xCC2C220E)
        item.isActive -> Color(0xCC0F3B2A)
        item.isPassed -> Color(0x880D1B26)
        else -> Color(0xCC0F2332)
    }
    val borderColor = when {
        item.isNext -> IslamicGold
        item.isActive -> IslamicGreen
        item.isPassed -> Color(0x33446075)
        else -> Color(0x44264A66)
    }
    val borderWidth = when {
        item.isNext -> 1.5.dp
        item.isActive -> 1.5.dp
        else -> 1.dp
    }
    val textColor = when {
        item.isNext -> IslamicGoldLight
        item.isActive -> IslamicGreen
        item.isPassed -> TextSecondary.copy(alpha = 0.5f)
        else -> TextPrimary
    }

    val prayerIcon: ImageVector = when (item.id) {
        PrayerId.SUBUH -> Icons.Default.WbTwilight
        PrayerId.SYURUQ -> Icons.Default.WbSunny
        PrayerId.DZUHUR -> Icons.Default.LightMode
        PrayerId.ASHAR -> Icons.Default.WbCloudy
        PrayerId.MAGHRIB -> Icons.Default.NightsStay
        PrayerId.ISYA -> Icons.Default.Nightlight
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(bgColor)
            .border(borderWidth, borderColor, RoundedCornerShape(5.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = prayerIcon,
            contentDescription = null,
            tint = if (item.isNext || item.isActive) textColor else TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = item.id.displayName,
            fontSize = 10.sp,
            fontWeight = if (item.isNext || item.isActive) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = item.timeFormatted,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}
// ============================================================
// TOMBOL SETTINGS OVERLAY — Sudut kanan atas layar
// ============================================================
@Composable
private fun SettingsOverlayButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xAA091620))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = IslamicGold.copy(alpha = if (isFocused) 1f else 0.5f),
                shape = CircleShape
            )
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Pengaturan",
            tint = if (isFocused) IslamicGoldLight else IslamicGold,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ============================================================
// MODE NORMAL — Layout lengkap
// ============================================================
@Composable
private fun NormalModeLayout(
    settings: AppSettings,
    schedule: PrayerSchedule,
    currentTimeString: String,
    hijriDateString: String,
    gregorianDateString: String,
    upcomingEvent: IslamicEvent,
    temperature: Int,
    weatherCondition: String,
    screenInfo: ScreenInfo,
    onSettingsClick: () -> Unit
) {
    var showQrisModal by remember { mutableStateOf(false) }
    var showSlideOverlay by remember { mutableStateOf(false) }
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

    // COOLDOWN SLIDE — setelah dismiss via OK/BACK, tunggu 5 menit
    LaunchedEffect(userDismissedSlide) {
        if (userDismissedSlide) {
            delay(5 * 60 * 1000L)
            userDismissedSlide = false
        }
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

            PrayerCardsRow(
                prayerItems = schedule.items,
                settings = settings,
                modifier = Modifier
                    .weight(0.28f)
                    .fillMaxWidth()
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
// ============================================================
// KONTEN ROTASI
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
