package com.example.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.AppSettings
import com.example.ui.components.ChangePinDialog
import com.example.ui.components.NeonFocusBorder
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.MosqueDeepBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    REMOTE_CONTROL("iO Control", Icons.Default.PhoneAndroid),
    ABOUT("Tentang Aplikasi", Icons.Default.Info),
    DEVELOPER("Opsi Developer", Icons.Default.Code)
}

object SettingsNavState {
    var lastCategory: SettingsCategory = SettingsCategory.LOCATION
    // Flag unlock Opsi Developer (persist selama app berjalan)
    var isDeveloperUnlocked: Boolean = false
}

@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    soundManager: SoundManager,
    isRemoteServerRunning: Boolean = false,
    onAutoSaveSettings: (AppSettings) -> Unit = {},
    onSaveSettings: (AppSettings) -> Unit,
    onBack: () -> Unit,
    onTestQrisFocus: () -> Unit,
    onOpenIoControl: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(SettingsNavState.lastCategory) }
    var previewCategory by remember { mutableStateOf(SettingsNavState.lastCategory) }
    var draftSettings by remember { mutableStateOf(currentSettings) }
    var showDeveloperPinDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showRiwayatUpdate by remember { mutableStateOf(false) }
    var isInitialLoad by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val paneFocusRequester = remember { FocusRequester() }

    LaunchedEffect(selectedCategory) {
        SettingsNavState.lastCategory = selectedCategory
    }

    // AUTO-SAVE debounce 500ms
    LaunchedEffect(draftSettings) {
        if (isInitialLoad) {
            isInitialLoad = false
            return@LaunchedEffect
        }
        delay(500)
        onAutoSaveSettings(draftSettings)
    }

    if (showRiwayatUpdate) {
        RiwayatUpdateScreen(
            onBack = { showRiwayatUpdate = false }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(MosqueDeepBg)) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B1720))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopBarIconButton(onClick = onBack) {
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
            SaveButton(
                onClick = { onSaveSettings(draftSettings) }
            )
        }

        // BODY
        Row(modifier = Modifier.fillMaxSize()) {
            // SIDEBAR
            Column(
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight()
                    .background(Color(0xFF09141D))
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsCategory.values().forEach { cat ->
                    SidebarItem(
                        category = cat,
                        isSelected = selectedCategory == cat,
                        onClick = {
                            if (cat == SettingsCategory.DEVELOPER) {
                                if (SettingsNavState.isDeveloperUnlocked) {
                                    // Sudah unlock — langsung tampil
                                    selectedCategory = cat
                                    previewCategory = cat
                                } else {
                                    // Belum unlock — tampil PIN dialog
                                    showDeveloperPinDialog = true
                                }
                            } else {
                                selectedCategory = cat
                                previewCategory = cat
                            }
                        },
                        onFocusChange = { isFocused ->
                            if (isFocused) {
                                scope.launch {
                                    delay(100)
                                    // Preview hanya untuk kategori NON-DEVELOPER
                                    // atau kalau developer sudah unlock
                                    if (cat != SettingsCategory.DEVELOPER ||
                                        SettingsNavState.isDeveloperUnlocked
                                    ) {
                                        previewCategory = cat
                                    }
                                }
                            }
                        },
                        rightFocusRequester = paneFocusRequester
                    )
                }
            }
            
            // PANE KANAN
            Box(
                modifier = Modifier
                    .weight(0.68f)
                    .fillMaxHeight()
                    .padding(24.dp)
                    .focusRequester(paneFocusRequester)
            ) {
                key(previewCategory) {
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        visible = true
                    }
                    val alpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "pane_fade"
                    )
                    Box(modifier = Modifier.fillMaxSize().alpha(alpha)) {

                        // ============ CEK DEVELOPER BELUM UNLOCK ============
                        if (previewCategory == SettingsCategory.DEVELOPER &&
                            !SettingsNavState.isDeveloperUnlocked
                        ) {
                            // Tampilkan placeholder kosong (LOCKED)
                            DeveloperLockedPane()
                        } else {
                            when (previewCategory) {
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
                                    onUpdate = { draftSettings = it },
                                    onOpenIoControl = onOpenIoControl
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
            }
        }
    }

    if (showDeveloperPinDialog) {
        DeveloperPinDialog(
            correctPin = "140399",
            onSuccess = {
                showDeveloperPinDialog = false
                SettingsNavState.isDeveloperUnlocked = true   // ← Set unlock
                selectedCategory = SettingsCategory.DEVELOPER
                previewCategory = SettingsCategory.DEVELOPER
            },
            onDismiss = { showDeveloperPinDialog = false }
        )
    }

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
// DEVELOPER LOCKED PANE — placeholder sebelum PIN benar
// ============================================================
@Composable
private fun DeveloperLockedPane() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Terkunci",
                tint = IslamicGold.copy(alpha = 0.5f),
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = "🔒 AKSES TERKUNCI",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(
                text = "Menu ini memerlukan PIN Developer.\n" +
                        "Klik \"Opsi Developer\" di sidebar kiri untuk memasukkan PIN.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// ============================================================
// SIDEBAR ITEM
// ============================================================
@Composable
private fun SidebarItem(
    category: SettingsCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    rightFocusRequester: FocusRequester
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = when {
        isSelected -> Color(0x44FFD700)
        isFocused -> Color(0x33FFD700)
        else -> Color(0x22000000)
    }

    LaunchedEffect(isFocused) {
        onFocusChange(isFocused)
    }

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .focusProperties {
                    right = rightFocusRequester
                }
                .focusable(interactionSource = interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
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
}

// ============================================================
// TOP BAR ICON BUTTON
// ============================================================
@Composable
private fun TopBarIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1f,
        animationSpec = tween(150),
        label = "icon_scale"
    )

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 12.dp,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF142735))
                .focusable(interactionSource = interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

// ============================================================
// SAVE BUTTON
// ============================================================
@Composable
private fun SaveButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 12.dp
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(IslamicGold)
                .focusable(interactionSource = interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
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
}
