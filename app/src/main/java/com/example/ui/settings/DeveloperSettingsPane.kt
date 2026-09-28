package com.example.ui.settings

import android.widget.Toast
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
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
import com.example.BuildConfig
import com.example.data.model.AppSettings
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import com.example.util.BackupManager
import com.example.util.CrashReporter

@Composable
fun DeveloperSettingsPane(
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showCrashHistory by remember { mutableStateOf(false) }
    var lastBackupPath by remember { mutableStateOf<String?>(null) }
    var lastBackupStatus by remember { mutableStateOf<String?>(null) }
    var needStoragePermission by remember {
        mutableStateOf(BackupManager.needsStoragePermission())
    }

    // Kalau user buka Riwayat Crash → tampilkan layar itu
    if (showCrashHistory) {
        RiwayatCrashScreen(
            onBack = { showCrashHistory = false },
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Text(
            text = "OPSI DEVELOPER",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Menu khusus owner. Jangan dibagikan ke orang lain.",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // INFO VERSI
        InfoBox(
            icon = Icons.Default.Info,
            title = "Versi Aplikasi",
            value = try { BuildConfig.VERSION_NAME } catch (e: Exception) { "Unknown" },
            description = "Versi build saat ini"
        )

        // ============ PERMISSION STORAGE ============
        if (needStoragePermission) {
            PermissionWarningCard(
                onGrantClick = {
                    BackupManager.openPermissionSettings(context)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ============ BACKUP AMAN ============
        DeveloperButton(
            icon = Icons.Default.Save,
            title = "BACKUP AMAN",
            description = "Export semua info aplikasi ke file .TXT",
            onClick = {
                try {
                    // Cek permission dulu
                    if (BackupManager.needsStoragePermission()) {
                        needStoragePermission = true
                        lastBackupStatus = "⚠️ Izin storage diperlukan"
                        Toast.makeText(
                            context,
                            "Beri izin storage dulu, lalu coba lagi",
                            Toast.LENGTH_LONG
                        ).show()
                        return@DeveloperButton
                    }

                    val content = BackupManager.generateBackupContent(settings)
                    val result = BackupManager.saveBackupToFile(context, content)

                    if (result.success) {
                        lastBackupPath = result.filePath
                        lastBackupStatus = "✅ Backup berhasil"
                        Toast.makeText(
                            context,
                            "Backup tersimpan di:\n${result.filePath}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (result.needPermission) {
                            needStoragePermission = true
                        }
                        lastBackupPath = null
                        lastBackupStatus = "❌ Gagal: ${result.errorMessage}"
                        Toast.makeText(
                            context,
                            "Backup gagal: ${result.errorMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    lastBackupStatus = "❌ Error: ${e.message}"
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        )

        // STATUS BACKUP
        if (lastBackupStatus != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33000000), RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        if (lastBackupStatus?.startsWith("✅") == true) IslamicGreen
                        else IslamicGold.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = lastBackupStatus ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (lastBackupStatus?.startsWith("✅") == true) IslamicGreen else UrgentRed
                )
                if (lastBackupPath != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lokasi: $lastBackupPath",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ============ RIWAYAT CRASH ============
        DeveloperButton(
            icon = Icons.Default.BugReport,
            title = "RIWAYAT CRASH",
            description = "Lihat daftar error yang pernah terjadi (${CrashReporter.getCrashHistory(context).size} tercatat)",
            onClick = { showCrashHistory = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ============ INFO FOLDER ============
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22000000), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "📁 Lokasi Backup",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "/sdcard/masjid.io/backup aman/",
                fontSize = 12.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "File: Backup Aman-masjid.io-DD-MM-YYYY.TXT",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================

@Composable
private fun InfoBox(
    icon: ImageVector,
    title: String,
    value: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33000000), RoundedCornerShape(10.dp))
            .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = IslamicGold,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, color = TextSecondary)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
            Text(description, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun PermissionWarningCard(
    onGrantClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UrgentRed.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .border(
                if (isFocused) 3.dp else 2.dp,
                UrgentRed,
                RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = UrgentRed,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "⚠️ IZIN STORAGE DIPERLUKAN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = UrgentRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Untuk menyimpan Backup Aman ke /sdcard/masjid.io/, aplikasi butuh izin 'Akses semua file'.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(UrgentRed)
                .clickable { onGrantClick() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BERI IZIN SEKARANG",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Setelah memberi izin, kembali ke aplikasi dan tekan BACKUP AMAN lagi.",
            fontSize = 11.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun DeveloperButton(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    disabled: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = when {
        disabled -> Color(0x33FFFFFF)
        isFocused -> IslamicGoldLight
        else -> IslamicGold.copy(alpha = 0.5f)
    }
    val contentColor = if (disabled) TextSecondary.copy(alpha = 0.5f) else TextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isFocused && !disabled) Color(0x33FFD700) else Color(0x22000000),
                RoundedCornerShape(10.dp)
            )
            .border(if (isFocused) 3.dp else 1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable(enabled = !disabled) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (disabled) TextSecondary.copy(alpha = 0.5f) else IslamicGold,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (disabled) TextSecondary.copy(alpha = 0.5f) else IslamicGoldLight
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}
