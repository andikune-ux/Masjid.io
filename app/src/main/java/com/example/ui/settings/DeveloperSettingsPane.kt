package dev.andikune.masjidio.ui.settings

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andikune.masjidio.BuildConfig
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import dev.andikune.masjidio.util.BackupManager
import dev.andikune.masjidio.util.BuildHistoryFetcher
import dev.andikune.masjidio.util.CrashReporter
import dev.andikune.masjidio.util.FonnteSender
import kotlinx.coroutines.launch

@Composable
fun DeveloperSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showCrashHistory by remember { mutableStateOf(false) }
    var lastBackupPath by remember { mutableStateOf<String?>(null) }
    var lastBackupStatus by remember { mutableStateOf<String?>(null) }
    var needStoragePermission by remember {
        mutableStateOf(BackupManager.needsStoragePermission())
    }

    // State build history
    var cachedBuildHistoryText by remember { mutableStateOf<String?>(null) }
    var isFetchingBuild by remember { mutableStateOf(false) }
    var lastFetchStatus by remember { mutableStateOf<String?>(null) }

    // State backup (BARU)
    var isBackupRunning by remember { mutableStateOf(false) }
    var backupProgress by remember { mutableStateOf("") }

    // Fonnte state
    var tokenInput by remember { mutableStateOf(settings.fonnteToken) }
    var groupIdInput by remember { mutableStateOf(settings.fonnteGroupId) }
    var isTestingFonnte by remember { mutableStateOf(false) }
    var showToken by remember { mutableStateOf(false) }

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

        InfoBox(
            icon = Icons.Default.Info,
            title = "Versi Aplikasi",
            value = try {
                BuildConfig.VERSION_NAME
            } catch (e: Exception) {
                "Unknown"
            },
            description = "Versi build saat ini"
        )

        // ============================================================
        // REFRESH BUILD HISTORY
        // ============================================================
        Text(
            text = "RIWAYAT BUILD",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Fetch 10 build terakhir dari GitHub Actions. Hasilnya akan ikut ke Backup Aman.",
            fontSize = 12.sp,
            color = TextSecondary
        )

        DeveloperButton(
            icon = Icons.Default.Refresh,
            title = if (isFetchingBuild) "MENGAMBIL DATA..." else "REFRESH BUILD HISTORY",
            description = lastFetchStatus ?: "Ambil data build terbaru dari GitHub",
            disabled = isFetchingBuild,
            onClick = {
                if (isFetchingBuild) return@DeveloperButton
                isFetchingBuild = true
                lastFetchStatus = null
                scope.launch {
                    val builds = BuildHistoryFetcher.fetchRecentBuilds()
                    isFetchingBuild = false
                    if (builds.isEmpty()) {
                        cachedBuildHistoryText = null
                        lastFetchStatus = "⚠️ Gagal fetch (tidak ada koneksi / rate limit)"
                        Toast.makeText(
                            context,
                            "Gagal fetch build history. Cek koneksi internet.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        cachedBuildHistoryText = BuildHistoryFetcher.formatBuildsAsText(builds)
                        val failedCount = builds.count { it.conclusion == "failure" }
                        lastFetchStatus = "✅ ${builds.size} build di-fetch • $failedCount gagal"
                        Toast.makeText(
                            context,
                            "Berhasil! ${builds.size} build (${failedCount} gagal)",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )

        if (lastFetchStatus != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33000000), RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        if (lastFetchStatus?.startsWith("✅") == true) IslamicGreen else UrgentRed,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = lastFetchStatus ?: "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (lastFetchStatus?.startsWith("✅") == true) IslamicGreen else UrgentRed
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ============================================================
        // PERMISSION STORAGE WARNING
        // ============================================================
        if (needStoragePermission) {
            PermissionWarningCard(
                onGrantClick = { BackupManager.openPermissionSettings(context) }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ============================================================
        // BACKUP AMAN (dengan fetch source code)
        // ============================================================
        DeveloperButton(
            icon = Icons.Default.Save,
            title = if (isBackupRunning) {
                backupProgress.ifEmpty { "BACKUP SEDANG PROSES..." }
            } else {
                "BACKUP AMAN"
            },
            description = "Export semua info aplikasi + FULL SOURCE CODE ke file .TXT",
            disabled = isBackupRunning,
            onClick = {
                if (isBackupRunning) return@DeveloperButton

                try {
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

                    isBackupRunning = true
                    lastBackupStatus = null
                    lastBackupPath = null
                    backupProgress = "Menyiapkan backup..."

                    scope.launch {
                        // ==== STEP 1: Fetch build history kalau belum ada ====
                        if (cachedBuildHistoryText == null) {
                            backupProgress = "Mengambil build history..."
                            val builds = BuildHistoryFetcher.fetchRecentBuilds()
                            cachedBuildHistoryText = if (builds.isNotEmpty()) {
                                BuildHistoryFetcher.formatBuildsAsText(builds)
                            } else null
                        }

                        // ==== STEP 2: Fetch source code dari GitHub ====
                        backupProgress = "Mengambil source code dari GitHub..."
                        val sourceCodeText = try {
                            BackupManager.fetchSourceCodeText()
                        } catch (e: Exception) {
                            android.util.Log.e("Backup", "Fetch source gagal: ${e.message}")
                            ""
                        }

                        // ==== STEP 3: Tulis backup ====
                        backupProgress = "Menyimpan file backup..."
                        doBackup(
                            context = context,
                            settings = settings,
                            buildHistoryText = cachedBuildHistoryText,
                            sourceCodeText = sourceCodeText.ifEmpty { null },
                            onSuccess = { path ->
                                lastBackupPath = path
                                lastBackupStatus = "✅ Backup berhasil"
                            },
                            onError = { msg ->
                                lastBackupPath = null
                                lastBackupStatus = "❌ Gagal: $msg"
                            },
                            onNeedPermission = {
                                needStoragePermission = true
                            }
                        )

                        isBackupRunning = false
                        backupProgress = ""
                    }
                } catch (e: Exception) {
                    isBackupRunning = false
                    backupProgress = ""
                    lastBackupStatus = "❌ Error: ${e.message}"
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        )

        if (lastBackupStatus != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33000000), RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        if (lastBackupStatus?.startsWith("✅") == true)
                            IslamicGreen
                        else IslamicGold.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = lastBackupStatus ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (lastBackupStatus?.startsWith("✅") == true)
                        IslamicGreen else UrgentRed
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
        
        // ============================================================
        // RIWAYAT CRASH
        // ============================================================
        DeveloperButton(
            icon = Icons.Default.BugReport,
            title = "RIWAYAT CRASH",
            description = "Lihat daftar error yang pernah terjadi (${CrashReporter.getCrashHistory(context).size} tercatat)",
            onClick = { showCrashHistory = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ============================================================
        // WHATSAPP FONNTE
        // ============================================================
        Text(
            text = "WHATSAPP REPORT (FONNTE)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Kirim notifikasi otomatis ke grup WA admin saat aplikasi crash.",
            fontSize = 12.sp,
            color = TextSecondary
        )

        FonnteToggle(
            label = "Aktifkan Kirim WA",
            description = if (settings.whatsappReportEnabled)
                "Setiap crash akan dikirim ke grup WA admin"
            else "WA report tidak aktif",
            isChecked = settings.whatsappReportEnabled,
            onToggle = {
                onUpdate(settings.copy(whatsappReportEnabled = it))
                CrashReporter.updateFonnteConfig(
                    token = settings.fonnteToken,
                    groupId = settings.fonnteGroupId,
                    enabled = it
                )
            }
        )

        FonnteInputField(
            label = "Token Fonnte",
            value = tokenInput,
            placeholder = "Contoh: M@N!4Yr-Vs#CPtaopCkE",
            isPassword = true,
            showPassword = showToken,
            onTogglePassword = { showToken = !showToken },
            onValueChange = { tokenInput = it }
        )

        FonnteInputField(
            label = "ID Grup WA",
            value = groupIdInput,
            placeholder = "Contoh: 123456789-123456@g.us",
            isPassword = false,
            showPassword = true,
            onTogglePassword = {},
            onValueChange = { groupIdInput = it }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                FonnteButton(
                    icon = Icons.Default.CheckCircle,
                    label = "SIMPAN",
                    backgroundColor = IslamicGold,
                    textColor = Color(0xFF09141D),
                    enabled = !isTestingFonnte,
                    onClick = {
                        onUpdate(
                            settings.copy(
                                fonnteToken = tokenInput.trim(),
                                fonnteGroupId = groupIdInput.trim()
                            )
                        )
                        CrashReporter.updateFonnteConfig(
                            token = tokenInput.trim(),
                            groupId = groupIdInput.trim(),
                            enabled = settings.whatsappReportEnabled
                        )
                        Toast.makeText(context, "Konfigurasi Fonnte tersimpan", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                FonnteButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    label = if (isTestingFonnte) "MENGIRIM..." else "TEST",
                    backgroundColor = IslamicGreen,
                    textColor = Color.White,
                    enabled = !isTestingFonnte && tokenInput.isNotBlank() && groupIdInput.isNotBlank(),
                    onClick = {
                        if (isTestingFonnte) return@FonnteButton
                        isTestingFonnte = true
                        scope.launch {
                            val result = FonnteSender.sendTestMessage(
                                token = tokenInput.trim(),
                                groupId = groupIdInput.trim()
                            )
                            isTestingFonnte = false
                            Toast.makeText(
                                context,
                                if (result.success) "Test berhasil! Cek grup WA Anda"
                                else "Test gagal: ${result.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "ℹ️ Cara Dapat Token Fonnte",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Daftar di fonnte.com\n" +
                            "2. Scan QR pakai WA Anda\n" +
                            "3. Copy Token dari dashboard\n" +
                            "4. Buat grup WA admin & dapatkan ID grup\n" +
                            "5. Paste di kolom atas → SIMPAN",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22000000), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "📍 Lokasi Backup",
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
// FUNGSI HELPER: DO BACKUP
// ============================================================
private fun doBackup(
    context: android.content.Context,
    settings: AppSettings,
    buildHistoryText: String?,
    sourceCodeText: String? = null,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    onNeedPermission: () -> Unit
) {
    try {
        val content = BackupManager.generateBackupContent(
            settings = settings,
            buildHistoryText = buildHistoryText,
            sourceCodeText = sourceCodeText
        )
        val result = BackupManager.saveBackupToFile(context, content)

        if (result.success) {
            onSuccess(result.filePath ?: "")
            Toast.makeText(
                context,
                "Backup tersimpan:\n${result.filePath}",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (result.needPermission) onNeedPermission()
            onError(result.errorMessage ?: "Unknown error")
            Toast.makeText(
                context,
                "Backup gagal: ${result.errorMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        onError(e.message ?: "Error")
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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
            .border(
                if (isFocused) 3.dp else 1.5.dp,
                borderColor,
                RoundedCornerShape(10.dp)
            )
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

@Composable
private fun FonnteToggle(
    label: String,
    description: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
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
            .clickable { onToggle(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isChecked) IslamicGreen else Color(0x55FFFFFF))
                .padding(3.dp),
            contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun FonnteInputField(
    label: String,
    value: String,
    placeholder: String,
    isPassword: Boolean,
    showPassword: Boolean,
    onTogglePassword: () -> Unit,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            if (isPassword) {
                Text(
                    text = if (showPassword) "SEMBUNYIKAN" else "LIHAT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold,
                    modifier = Modifier
                        .clickable { onTogglePassword() }
                        .padding(4.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x22000000))
                .border(
                    if (isFocused) 3.dp else 1.dp,
                    if (isFocused) IslamicGoldLight else Color(0x44FFFFFF),
                    RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
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
                visualTransformation = if (isPassword && !showPassword)
                    PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
            )
        }
    }
}

@Composable
private fun FonnteButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.4f))
            .border(
                if (isFocused && enabled) 3.dp else 0.dp,
                if (isFocused && enabled) IslamicGoldLight else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled)
            .clickable(enabled) { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
