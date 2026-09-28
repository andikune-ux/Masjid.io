package com.example.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.AppSettings
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
    ABOUT("Tentang Aplikasi", Icons.Default.Info),
    DEVELOPER("Opsi Developer", Icons.Default.Code)
}

@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    soundManager: SoundManager,
    onSaveSettings: (AppSettings) -> Unit,
    onBack: () -> Unit,
    onTestQrisFocus: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(SettingsCategory.LOCATION) }
    var draftSettings by remember { mutableStateOf(currentSettings) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showDeveloperPinDialog by remember { mutableStateOf(false) }

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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF142735))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
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

        // BODY: Sidebar + Content
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
                    SettingsCategory.LOCATION -> LocationSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.TIME_SETTINGS -> TimeSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.COUNTDOWN -> CountdownSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.IDENTITY -> IdentitySettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.OFFICERS -> WeeklyOfficersSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.QRIS_DONATION -> QrisSettingsPane(draftSettings, { draftSettings = it }, onTestQrisFocus)
                    SettingsCategory.VIDEO_MEDIA -> VideoSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.APPEARANCE -> CustomBackgroundPane(draftSettings) { draftSettings = it }
                    SettingsCategory.WISDOM_CARDS -> WisdomSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.RUNNING_TEXT -> RunningTextSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.AUDIO -> AudioSettingsPane(draftSettings, soundManager) { draftSettings = it }
                    SettingsCategory.RAMADHAN -> RamadhanSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.SECURITY -> SecuritySettingsPane(
                        settings = draftSettings,
                        onUpdate = { draftSettings = it },
                        onChangePinClick = { showChangePinDialog = true }
                    )
                    SettingsCategory.POWER -> PowerSettingsPane(draftSettings) { draftSettings = it }
                    SettingsCategory.ABOUT -> AboutSettingsPane()
                    SettingsCategory.DEVELOPER -> DeveloperSettingsPane(
                        settings = draftSettings,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // DIALOG PIN DEVELOPER
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

    // DIALOG UBAH PIN
    if (showChangePinDialog) {
        PinDialog(
            correctPin = draftSettings.pinCode,
            onSuccess = {
                showChangePinDialog = false
                Toast.makeText(
                    LocalContext.current,
                    "PIN berhasil diubah",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { showChangePinDialog = false }
        )
    }
}

@Composable
private fun SidebarItem(
    category: SettingsCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = when {
        isFocused -> IslamicGoldLight
        isSelected -> IslamicGold
        else -> Color(0x33FFFFFF)
    }
    val bgColor = when {
        isSelected -> Color(0x44FFD700)
        isFocused -> Color(0x22FFD700)
        else -> Color(0x22000000)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(if (isFocused) 3.dp else 1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.label,
            tint = if (isSelected) IslamicGold else TextPrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = category.label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) IslamicGoldLight else TextPrimary
        )
    }
}

@Composable
fun LocationSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("LOKASI & WAKTU SHOLAT", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
        Text("Atur lokasi masjid untuk perhitungan jadwal sholat.", fontSize = 13.sp, color = TextSecondary)
    }
}

@Composable
fun IdentitySettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("IDENTITAS MASJID", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
    }
}

@Composable
fun RunningTextSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("RUNNING TEXT", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
    }
}

@Composable
fun OfficersSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    WeeklyOfficersSettingsPane(settings, onUpdate)
}

@Composable
fun AudioSettingsPane(
    settings: AppSettings,
    soundManager: SoundManager,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AUDIO & ADZAN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
    }
}
