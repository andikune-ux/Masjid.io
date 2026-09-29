package com.example.ui.settings

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.AppSettings
import com.example.ui.components.ChangePinDialog
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.MosqueDeepBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class SettingsCategory(
    val label: String,
    val icon: ImageVector
) {
    LOCATION("Lokasi & Waktu Sholat", Icons.Default.LocationOn),
    TIME_SETTINGS("Pengaturan Waktu", Icons.Default.Schedule),
    COUNTDOWN("Durasi & Hitungan Mundur", Icons.Default.Timer),
    IDENTITY("Identitas Masjid", Icons.Default.Mosque),
    OFFICERS("Jadwal Petugas & Foto", Icons.Default.Person),
    QRIS_DONATION("Donasi QRIS & Rekening", Icons.Default.QrCode),
    VIDEO_MEDIA("Video Kegiatan Masjid", Icons.Default.VideoLibrary),
    APPEARANCE("Tampilan & Background", Icons.Default.Palette),
    WISDOM_CARDS("Kartu Nasihat & Mutiara", Icons.Default.Book),
    RUNNING_TEXT("Running Text", Icons.Default.TextFields),
    AUDIO("Audio & Adzan", Icons.Default.Notifications),
    RAMADHAN("Mode Ramadhan", Icons.Default.NightsStay),
    SECURITY("Keamanan", Icons.Default.Security),
    POWER("Daya & Booting", Icons.Default.Power),
    SLIDESHOW("Slide Fullscreen", Icons.Default.Slideshow),
    CCTV("CCTV Masjid", Icons.Default.Videocam),
    REMOTE_CONTROL("Remote Control", Icons.Default.PhoneAndroid),
    ABOUT("Tentang Aplikasi", Icons.Default.Info),
    DEVELOPER("Opsi Developer", Icons.Default.Code)
}

@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    soundManager: SoundManager,
    isRemoteServerRunning: Boolean = false,
    onSaveSettings: (AppSettings) -> Unit,
    onBack: () -> Unit,
    onTestQrisFocus: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(SettingsCategory.LOCATION) }
    var draftSettings by remember { mutableStateOf(currentSettings) }
    var showDeveloperPinDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showRiwayatUpdate by remember { mutableStateOf(false) }

    if (showRiwayatUpdate) {
        RiwayatUpdateScreen(
            onBack = { showRiwayatUpdate = false }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(MosqueDeepBg)) {
        // ============ TOP BAR ============
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B1720))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopBarIconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = IslamicGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "PENGATURAN MASJID.IO",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(IslamicGold)
                    .clickable { onSaveSettings(draftSettings) }
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF09141D),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMPAN PENGATURAN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF09141D)
                    )
                }
            }
        }

        // ============ BODY ============
        Row(modifier = Modifier.fillMaxSize()) {
            // SIDEBAR
            Column(
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight()
                    .background(Color(0xFF09141D))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsCategory.values().forEach { cat ->
                    SidebarItem(
                        category = cat,
                        isSelected = selectedCategory == cat,
                        onClick = {
                            if (cat == SettingsCategory.DEVELOPER) {
                                showDeveloperPinDialog = true
                            } else {
                                selectedCategory = cat
                            }
                        }
                    )
                }
            }

            // CONTENT
            Crossfade(
                targetState = selectedCategory,
                modifier = Modifier.weight(0.68f).fillMaxHeight().padding(24.dp),
                label = "settings_pane"
            ) { category ->
                when (category) {
                    SettingsCategory.LOCATION -> LocationSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.TIME_SETTINGS -> TimeSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.COUNTDOWN -> CountdownSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.IDENTITY -> IdentitySettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.OFFICERS -> WeeklyOfficersSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.QRIS_DONATION -> QrisSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it },
                        onTestQrisFocus = onTestQrisFocus
                    )
                    SettingsCategory.VIDEO_MEDIA -> VideoSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.APPEARANCE -> CustomBackgroundPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.WISDOM_CARDS -> WisdomSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.RUNNING_TEXT -> RunningTextSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.AUDIO -> AudioSettingsPane(
                        settings = draftSettings,
                        soundManager = soundManager,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.RAMADHAN -> RamadhanSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.SECURITY -> SecuritySettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it },
                        onChangePinClick = { showChangePinDialog = true }
                    )
                    SettingsCategory.POWER -> PowerSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.SLIDESHOW -> SlideSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.CCTV -> CctvSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.REMOTE_CONTROL -> RemoteSettingsPane(
                        settings = draftSettings,
                        isServerRunning = isRemoteServerRunning,
                        onUpdate = { draftSettings = it }
                    )
                    SettingsCategory.ABOUT -> AboutSettingsPane(
                        onOpenRiwayatUpdate = { showRiwayatUpdate = true }
                    )
                    SettingsCategory.DEVELOPER -> DeveloperSettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // ============ DIALOG PIN DEVELOPER ============
    if (showDeveloperPinDialog) {
        DeveloperPinDialog(
            correctPin = "140399",
            onSuccess = {
                showDeveloperPinDialog = false
                selectedCategory = SettingsCategory.DEVELOPER
            },
            onDismiss = { showDeveloperPinDialog = false }
        )
    }

    // ============ DIALOG UBAH PIN ============
    if (showChangePinDialog) {
        ChangePinDialog(
            currentPin = draftSettings.pinCode,
            onPinChanged = { newPin ->
                draftSettings = draftSettings.copy(pinCode = newPin)
                showChangePinDialog = false
            },
            onDismiss = { showChangePinDialog = false }
        )
    }
}

// ============================================================
// SIDEBAR ITEM
// ============================================================

@Composable
private fun SidebarItem(
    category: SettingsCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 1.5.dp,
        animationSpec = tween(200),
        label = "sidebar_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "sidebar_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 12.dp else 0.dp,
        animationSpec = tween(200),
        label = "sidebar_shadow"
    )

    val borderColor = when {
        isFocused -> Color(0xFFFFE44D)
        isSelected -> IslamicGold
        else -> Color(0x33FFFFFF)
    }

    val bgColor = when {
        isSelected -> Color(0x44FFD700)
        isFocused -> Color(0x33FFD700)
        else -> Color(0x22000000)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x88FFD700),
                spotColor = Color(0x88FFD700)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.label,
            tint = when {
                isSelected -> IslamicGold
                isFocused -> Color(0xFFFFE44D)
                else -> TextPrimary
            },
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = category.label,
            fontSize = 14.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
            color = when {
                isSelected -> IslamicGoldLight
                isFocused -> Color(0xFFFFE44D)
                else -> TextPrimary
            }
        )
    }
}

// ============================================================
// TOMBOL ICON
// ============================================================

@Composable
private fun TopBarIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 0.dp,
        animationSpec = tween(200),
        label = "icon_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 1000f),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF142735))
            .border(
                width = borderWidth,
                color = if (isFocused) Color(0xFFFFE44D) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
