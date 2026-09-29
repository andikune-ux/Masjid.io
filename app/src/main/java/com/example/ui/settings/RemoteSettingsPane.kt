package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SettingsEthernet
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.components.TvToggle
import com.example.ui.remote.RemoteDashboard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RemoteSettingsPane(
    settings: AppSettings,
    isServerRunning: Boolean,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var portInput by remember(settings.remoteServerPort) {
        mutableStateOf(settings.remoteServerPort.toString())
    }
    var tokenInput by remember(settings.remoteAuthToken) {
        mutableStateOf(settings.remoteAuthToken)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "REMOTE CONTROL",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Kontrol aplikasi dari HP via browser",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // MASTER TOGGLE
        TvToggle(
            label = "Aktifkan Remote Control",
            description = "HTTP Server berjalan di background untuk kontrol via HP",
            isChecked = settings.remoteControlEnabled,
            onToggle = { onUpdate(settings.copy(remoteControlEnabled = it)) }
        )

        // KONFIGURASI SERVER
        if (settings.remoteControlEnabled) {
            Text(
                text = "KONFIGURASI SERVER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            RemoteInputField(
                icon = Icons.Default.SettingsEthernet,
                label = "Port Server",
                value = portInput,
                placeholder = "8080",
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }.take(5)
                    portInput = filtered
                    filtered.toIntOrNull()?.let { port ->
                        if (port in 1024..65535) {
                            onUpdate(settings.copy(remoteServerPort = port))
                        }
                    }
                }
            )

            Text(
                text = "Range: 1024 - 65535 (default: 8080)",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )

            RemoteInputField(
                icon = Icons.Default.Lock,
                label = "Token Akses",
                value = tokenInput,
                placeholder = "masjid-io",
                onValueChange = { newValue ->
                    tokenInput = newValue
                    onUpdate(settings.copy(remoteAuthToken = newValue))
                }
            )

            Text(
                text = "Token digunakan untuk login saat akses dari HP. Ganti untuk keamanan.",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp),
                lineHeight = 16.sp
            )
        }

        // DASHBOARD INFO
        if (settings.remoteControlEnabled) {
            Text(
                text = "INFORMASI AKSES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            // RemoteDashboard dipanggil TANPA modifier fillMaxSize
            // supaya tidak nested scroll dengan Column di atas
            RemoteDashboard(
                settings = settings,
                isServerRunning = isServerRunning
            )
        }

        // INFO UMUM
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cara Kerja",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Server berjalan otomatis saat aplikasi dibuka\n" +
                            "• Akses via browser HP di jaringan WiFi yang sama\n" +
                            "• Bisa ganti running text, PIN, dan lihat status\n" +
                            "• Server berhenti otomatis saat aplikasi ditutup",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: INPUT FIELD
// ============================================================

@Composable
private fun RemoteInputField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x22000000))
                .border(
                    width = if (isFocused) 4.dp else 1.dp,
                    color = if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = TextSecondary.copy(alpha = 0.5f)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }
        }
    }
}
