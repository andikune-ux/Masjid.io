package com.example.ui.settings

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.UpdateHistory
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import com.example.util.ApkDownloader
import com.example.util.UpdateManager
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AboutSettingsPane(
    onOpenRiwayatUpdate: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isChecking by remember { mutableStateOf(false) }
    var checkResult by remember { mutableStateOf<String?>(null) }
    var isUpdateAvailable by remember { mutableStateOf(false) }
    var latestVersion by remember { mutableStateOf("") }
    var downloadUrl by remember { mutableStateOf<String?>(null) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadedApkPath by remember { mutableStateOf<String?>(null) }

    var apkList by remember { mutableStateOf<List<File>>(emptyList()) }
    var apkListRefreshKey by remember { mutableStateOf(0) }

    val currentVersion = try {
        BuildConfig.VERSION_NAME
    } catch (e: Exception) {
        "Unknown"
    }

    LaunchedEffect(apkListRefreshKey) {
        downloadedApkPath = ApkDownloader.getDownloadedApkPath(context)
        apkList = ApkDownloader.getDownloadedApkList(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "TENTANG APLIKASI",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Informasi, update, dan kontak",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22000000), RoundedCornerShape(14.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "MASJID.IO",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Display Jadwal Sholat Android TV",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IslamicGreen.copy(alpha = 0.25f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Versi $currentVersion",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreen
                    )
                }
            }
        }

        Text(
            text = "UPDATE APLIKASI",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        ActionButton(
            icon = Icons.Default.Refresh,
            title = if (isChecking) "MEMERIKSA..." else "PERIKSA UPDATE",
            description = "Cek versi terbaru di server GitHub",
            backgroundColor = IslamicGold,
            textColor = Color(0xFF09141D),
            enabled = !isChecking && !isDownloading,
            onClick = {
                if (isChecking || isDownloading) return@ActionButton
                isChecking = true
                checkResult = null

                scope.launch {
                    val info = UpdateManager.checkForUpdate()
                    isChecking = false
                    isUpdateAvailable = info.available
                    latestVersion = info.latestVersion
                    downloadUrl = info.downloadUrl
                    checkResult = if (info.available) {
                        "✅ Update tersedia: ${info.latestVersion}"
                    } else {
                        "✅ Sudah versi terbaru ($currentVersion)"
                    }
                }
            }
        )

        if (checkResult != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22000000), RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (isUpdateAvailable) IslamicGreen else IslamicGold.copy(alpha = 0.5f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = checkResult ?: "",
                    fontSize = 14.sp,
                    color = if (isUpdateAvailable) IslamicGreen else TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isUpdateAvailable) {
            ActionButton(
                icon = Icons.Default.Download,
                title = if (isDownloading) "MENGUNDUH..." else "DOWNLOAD UPDATE $latestVersion",
                description = "Download APK terbaru dari GitHub",
                backgroundColor = IslamicGreen,
                textColor = Color.White,
                enabled = !isDownloading && downloadUrl != null,
                onClick = {
                    if (isDownloading) return@ActionButton
                    val url = downloadUrl
                    if (url.isNullOrBlank()) {
                        Toast.makeText(context, "URL download belum tersedia", Toast.LENGTH_LONG).show()
                        return@ActionButton
                    }
                    isDownloading = true
                    downloadProgress = 0f
                    downloadedApkPath = null

                    scope.launch {
                        ApkDownloader.downloadApk(
                            context = context,
                            downloadUrl = url,
                            fileName = "masjid-io-$latestVersion.apk"
                        ).collect { state ->
                            if (state.errorMessage != null) {
                                isDownloading = false
                                Toast.makeText(
                                    context,
                                    "Download gagal: ${state.errorMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                downloadProgress = state.progress
                                if (state.isFinished && state.savedFilePath != null) {
                                    isDownloading = false
                                    downloadedApkPath = state.savedFilePath
                                    apkListRefreshKey++
                                    Toast.makeText(
                                        context,
                                        "Download selesai! Siap install.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
            )

            if (isDownloading || downloadProgress > 0f) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress Download", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            "${(downloadProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(downloadProgress)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(IslamicGold)
                        )
                    }
                }
            }

            if (downloadedApkPath != null && !isDownloading) {
                ActionButton(
                    icon = Icons.Default.CheckCircle,
                    title = "INSTALL UPDATE $latestVersion",
                    description = "Install APK yang sudah di-download",
                    backgroundColor = IslamicGold,
                    textColor = Color(0xFF09141D),
                    enabled = true,
                    onClick = {
                        val path = downloadedApkPath
                        if (path.isNullOrBlank()) {
                            Toast.makeText(context, "Path APK tidak ditemukan", Toast.LENGTH_SHORT).show()
                            return@ActionButton
                        }
                        if (!ApkDownloader.canInstallApk(context)) {
                            Toast.makeText(context, "Beri izin 'Install unknown apps' dulu", Toast.LENGTH_LONG).show()
                            ApkDownloader.openInstallPermissionSettings(context)
                            return@ActionButton
                        }
                        val ok = ApkDownloader.installApk(context, path)
                        if (!ok) {
                            Toast.makeText(context, "Gagal membuka installer APK", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "FILE UPDATE TERSIMPAN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22000000), RoundedCornerShape(12.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${apkList.size} file tersimpan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "/sdcard/masjid.io/pembaharuan aplikasi/",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (apkList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada file update tersimpan",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    apkList.forEach { file ->
                        ApkFileItem(
                            file = file,
                            onInstall = {
                                if (!ApkDownloader.canInstallApk(context)) {
                                    Toast.makeText(
                                        context,
                                        "Beri izin 'Install unknown apps' dulu",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    ApkDownloader.openInstallPermissionSettings(context)
                                } else {
                                    val ok = ApkDownloader.installApk(context, file.absolutePath)
                                    if (!ok) {
                                        Toast.makeText(
                                            context,
                                            "Gagal membuka installer APK",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            onDelete = {
                                val deleted = ApkDownloader.deleteApk(file.absolutePath)
                                if (deleted) {
                                    Toast.makeText(
                                        context,
                                        "Terhapus: ${file.name}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    apkListRefreshKey++
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Gagal menghapus file",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "RIWAYAT UPDATE",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        ActionButton(
            icon = Icons.Default.History,
            title = "LIHAT RIWAYAT UPDATE",
            description = "${UpdateHistory.entries.size} versi tercatat (tidak bisa dihapus)",
            backgroundColor = Color(0xFF142735),
            textColor = TextPrimary,
            enabled = true,
            onClick = onOpenRiwayatUpdate
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "IKUTI KAMI",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SocialButton(
                label = "WhatsApp",
                emoji = "💬",
                color = Color(0xFF25D366),
                modifier = Modifier.weight(1f),
                onClick = {
                    openUrl(context, "https://chat.whatsapp.com/ErJpG34fdzwL9FOmoh4fNN?s=cl&p=a&mlu=4&ilr=4")
                }
            )
            SocialButton(
                label = "TikTok",
                emoji = "🎵",
                color = Color(0xFFFF0050),
                modifier = Modifier.weight(1f),
                onClick = {
                    openUrl(context, "https://www.tiktok.com/@nayyra.une?_r=1&_t=ZS-9A6wlkFNFht")
                }
            )
            SocialButton(
                label = "Instagram",
                emoji = "📷",
                color = Color(0xFFE1306C),
                modifier = Modifier.weight(1f),
                onClick = {
                    openUrl(context, "https://www.instagram.com/nayyra.une?stkn=MTJ3ZHg3NXM0amtxbQ==")
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22000000), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "🛠️ Developer",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "MASJID.IO",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "GitHub: github.com/andikune-ux/Masjid.io",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "© 2026 MASJID.IO. All rights reserved.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ============================================================
// APK FILE ITEM — Kiri: INSTALL | Kanan: HAPUS (terpisah)
// ============================================================
@Composable
private fun ApkFileItem(
    file: File,
    onInstall: () -> Unit,
    onDelete: () -> Unit
) {
    var isFocusedInstall by remember { mutableStateOf(false) }
    var isFocusedDelete by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val sizeText = ApkDownloader.formatSize(file.length())
    val dateText = dateFormat.format(Date(file.lastModified()))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F2636))
            .border(
                width = if (isFocusedInstall) 2.dp else 1.dp,
                color = if (isFocusedInstall) IslamicGreen else IslamicGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isFocusedInstall) IslamicGreen.copy(alpha = 0.15f)
                    else Color.Transparent
                )
                .onFocusChanged { isFocusedInstall = it.isFocused }
                .focusable()
                .clickable { onInstall() }
                .padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isFocusedInstall) Icons.Default.CheckCircle else Icons.Default.Download,
                contentDescription = "Install",
                tint = IslamicGreen,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$sizeText · $dateText",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                if (isFocusedInstall) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "TAP UNTUK INSTALL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isFocusedDelete) UrgentRed.copy(alpha = 0.3f)
                    else UrgentRed.copy(alpha = 0.15f)
                )
                .border(
                    if (isFocusedDelete) 2.dp else 1.dp,
                    if (isFocusedDelete) UrgentRed else UrgentRed.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
                .onFocusChanged { isFocusedDelete = it.isFocused }
                .focusable()
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus",
                tint = UrgentRed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    title: String,
    description: String,
    backgroundColor: Color,
    textColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.4f))
            .border(
                if (isFocused && enabled) 3.dp else 0.dp,
                if (isFocused && enabled) IslamicGoldLight else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled)
            .clickable(enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun SocialButton(
    label: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFocused) color.copy(alpha = 0.35f) else color.copy(alpha = 0.15f))
            .border(
                if (isFocused) 3.dp else 1.5.dp,
                if (isFocused) color else color.copy(alpha = 0.6f),
                RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 32.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
    }
}
