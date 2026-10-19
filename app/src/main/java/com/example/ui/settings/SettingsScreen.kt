package dev.andikune.masjidio.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
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
import androidx.compose.material.icons.filled.Tv
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
import dev.andikune.masjidio.audio.SoundManager
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.components.AutoFocusPane
import dev.andikune.masjidio.ui.components.ChangePinDialog
import dev.andikune.masjidio.ui.components.NeonFocusBorder
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.MosqueDeepBg
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SettingsCategory(val label: String, val icon: ImageVector) {
    LOCATION("Lokasi & Waktu Sholat", Icons.Default.LocationOn),
    TIME_SETTINGS("Pengaturan Waktu", Icons.Default.Schedule),
    COUNTDOWN("Durasi & Hitungan Mundur", Icons.Default.Timer),
    IDENTITY("Identitas Masjid", Icons.Default.Mosque),
    OFFICERS("Jadwal Petugas & Foto", Icons.Default.Person),
    QRIS_DONATION("Donasi QRIS & Rekening", Icons.Default.QrCode),
    VIDEO_MEDIA("Galeri Kegiatan Masjid", Icons.Default.VideoLibrary),
    APPEARANCE("Tampilan & Background", Icons.Default.Palette),
    TV_DISPLAY("Tampilan TV", Icons.Default.Tv),
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

    LaunchedEffect(selectedCategory) { SettingsNavState.lastCategory = selectedCategory }

    LaunchedEffect(draftSettings) {
        if (isInitialLoad) { isInitialLoad = false; return@LaunchedEffect }
        delay(500)
        onAutoSaveSettings(draftSettings)
    }

    if (showRiwayatUpdate) {
        RiwayatUpdateScreen(onBack = { showRiwayatUpdate = false })
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(MosqueDeepBg)) {
        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF0B1720)).padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopBarIconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = IslamicGold, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("PENGATURAN MASJID.IO", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
            }
            SaveButton(onClick = { onSaveSettings(draftSettings) })
        }

        // BODY
        Row(modifier = Modifier.fillMaxSize()) {
            // SIDEBAR (FIX: up = Cancel)
            Column(
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight()
                    .background(Color(0xFF09141D))
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .focusProperties { up = FocusRequester.Cancel },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsCategory.values().forEach { cat ->
                    SidebarItem(
                        category = cat,
                        isSelected = selectedCategory == cat,
                        onClick = {
                            if (cat == SettingsCategory.DEVELOPER) {
                                if (SettingsNavState.isDeveloperUnlocked) {
                                    selectedCategory = cat; previewCategory = cat
                                    scope.launch { delay(200); runCatching { paneFocusRequester.requestFocus() } }
                                } else { showDeveloperPinDialog = true }
                            } else {
                                selectedCategory = cat; previewCategory = cat
                                scope.launch { delay(200); runCatching { paneFocusRequester.requestFocus() } }
                            }
                        },
                        onFocusChange = { },
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
                    .focusProperties { up = FocusRequester.Cancel }
                    .focusRequester(paneFocusRequester)
            ) {
                key(previewCategory) {
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { visible = true }
                    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = tween(200), label = "pane_fade")

                    AutoFocusPane(paneKey = previewCategory, modifier = Modifier.fillMaxSize().alpha(alpha)) {
                        if (previewCategory == SettingsCategory.DEVELOPER && !SettingsNavState.isDeveloperUnlocked) {
                            DeveloperLockedPane()
                        } else {
                            when (previewCategory) {
                                SettingsCategory.LOCATION -> LocationSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.TIME_SETTINGS -> TimeSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.COUNTDOWN -> CountdownSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.IDENTITY -> IdentitySettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.OFFICERS -> WeeklyOfficersSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.QRIS_DONATION -> QrisSettingsPane(draftSettings, { draftSettings = it }, onTestQrisFocus)
                                SettingsCategory.VIDEO_MEDIA -> VideoSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.APPEARANCE -> CustomBackgroundPane(draftSettings) { draftSettings = it }
                                SettingsCategory.TV_DISPLAY -> TvDisplaySettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.WISDOM_CARDS -> WisdomSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.RUNNING_TEXT -> RunningTextSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.AUDIO -> AudioSettingsPane(draftSettings, soundManager) { draftSettings = it }
                                SettingsCategory.RAMADHAN -> RamadhanSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.SECURITY -> SecuritySettingsPane(draftSettings, { draftSettings = it }, { showChangePinDialog = true })
                                SettingsCategory.POWER -> PowerSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.SLIDESHOW -> SlideSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.CCTV -> CctvSettingsPane(draftSettings) { draftSettings = it }
                                SettingsCategory.REMOTE_CONTROL -> RemoteSettingsPane(draftSettings, isRemoteServerRunning, { draftSettings = it }, onOpenIoControl)
                                SettingsCategory.ABOUT -> AboutSettingsPane(onOpenRiwayatUpdate = { showRiwayatUpdate = true })
                                SettingsCategory.DEVELOPER -> DeveloperSettingsPane(draftSettings, { draftSettings = it }, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeveloperPinDialog) {
        DeveloperPinDialog(correctPin = "140399", onSuccess = {
            showDeveloperPinDialog = false; SettingsNavState.isDeveloperUnlocked = true
            selectedCategory = SettingsCategory.DEVELOPER; previewCategory = SettingsCategory.DEVELOPER
        }, onDismiss = { showDeveloperPinDialog = false })
    }

    if (showChangePinDialog) {
        ChangePinDialog(currentPin = draftSettings.pinCode, onPinChanged = { newPin ->
            draftSettings = draftSettings.copy(pinCode = newPin); showChangePinDialog = false
        }, onDismiss = { showChangePinDialog = false })
    }
}

@Composable
private fun DeveloperLockedPane() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Lock, "Terkunci", tint = IslamicGold.copy(alpha = 0.5f), modifier = Modifier.size(72.dp))
            Text("🔒 AKSES TERKUNCI", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
            Text("Menu ini memerlukan PIN Developer.\nKlik \"Opsi Developer\" di sidebar kiri untuk memasukkan PIN.", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun SidebarItem(category: SettingsCategory, isSelected: Boolean, onClick: () -> Unit, onFocusChange: (Boolean) -> Unit, rightFocusRequester: FocusRequester) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = when { isSelected -> Color(0x44FFD700); isFocused -> Color(0x33FFD700); else -> Color(0x22000000) }

    NeonFocusBorder(focused = isFocused, pressed = isPressed, borderWidth = 5.dp, cornerRadius = 12.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bgColor)
                .focusProperties { right = rightFocusRequester }
                .focusable(interactionSource = interactionSource)
                .clickable(interactionSource = interactionSource, indication = null) { onClick() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(category.icon, category.label, tint = if (isSelected) IslamicGold else if (isFocused) Color(0xFFFFE44D) else TextPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(category.label, fontSize = 14.sp, fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) IslamicGoldLight else if (isFocused) Color(0xFFFFE44D) else TextPrimary)
        }
    }
}

@Composable
private fun TopBarIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(focused = isFocused, pressed = isPressed, borderWidth = 5.dp, cornerRadius = 12.dp, modifier = Modifier.size(48.dp)) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF142735)).focusable(interactionSource = interactionSource).clickable(interactionSource = interactionSource, indication = null) { onClick() }, contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(focused = isFocused, pressed = isPressed, borderWidth = 5.dp, cornerRadius = 12.dp) {
        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(IslamicGold).focusable(interactionSource = interactionSource).clickable(interactionSource = interactionSource, indication = null) { onClick() }.padding(horizontal = 24.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, null, tint = Color(0xFF09141D), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SIMPAN PENGATURAN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF09141D))
            }
        }
    }
}
