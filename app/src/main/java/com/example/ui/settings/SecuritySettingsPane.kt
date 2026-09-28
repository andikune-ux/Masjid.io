package com.example.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SecuritySettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    onChangePinClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "KEAMANAN & KIOSK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Atur keamanan aplikasi dan mode kiosk",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ==================== PIN AKSES ====================
        Text(
            text = "PIN AKSES",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        InfoButton(
            icon = Icons.Default.Lock,
            title = "UBAH PIN PENGATURAN",
            description = "Ubah PIN untuk masuk ke halaman pengaturan",
            onClick = onChangePinClick
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "🔐 PIN Saat Ini",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PIN: ${"*".repeat(settings.pinCode.length)} (${settings.pinCode.length} digit)",
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ==================== MODE KIOSK ====================
        Text(
            text = "MODE KIOSK",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Aktifkan Mode Kiosk",
            description = "Kunci aplikasi agar jamaah tidak bisa keluar",
            isChecked = settings.kioskModeEnabled,
            onToggle = { onUpdate(settings.copy(kioskModeEnabled = it)) }
        )

        TvToggle(
            label = "Lock Task Mode",
            description = "Kunci penuh (butuh PIN untuk keluar)",
            isChecked = settings.lockTaskMode,
            onToggle = { onUpdate(settings.copy(lockTaskMode = it)) },
            enabled = settings.kioskModeEnabled
        )

        TvToggle(
            label = "Nonaktifkan Tombol Back",
            description = "Tombol Back remote tidak bisa keluar aplikasi",
            isChecked = settings.disableBackButton,
            onToggle = { onUpdate(settings.copy(disableBackButton = it)) },
            enabled = settings.kioskModeEnabled
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ==================== AUTO START ====================
        Text(
            text = "AUTO-START",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Auto-Start saat TV Nyala",
            description = "Aplikasi otomatis terbuka saat TV dinyalakan",
            isChecked = settings.autoStartOnBoot,
            onToggle = { onUpdate(settings.copy(autoStartOnBoot = it)) }
        )

        TvToggle(
            label = "Auto-Restart jika Crash",
            description = "Aplikasi otomatis buka ulang jika force close",
            isChecked = settings.autoRestartIfCrash,
            onToggle = { onUpdate(settings.copy(autoRestartIfCrash = it)) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ==================== INFO LAUNCHER DEFAULT ====================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "ℹ️ Info Launcher Default",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Untuk menjadikan MASJID.IO sebagai launcher default:",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1. Tekan tombol HOME di remote TV\n" +
                           "2. Pilih MASJID.IO\n" +
                           "3. Pilih 'Always' / 'Selalu'",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================

@Composable
private fun InfoButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isFocused) Color(0x33FFD700) else Color(0x22000000))
            .border(
                if (isFocused) 3.dp else 1.5.dp,
                if (isFocused) IslamicGoldLight else IslamicGold.copy(alpha = 0.5f),
                RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = IslamicGold,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
