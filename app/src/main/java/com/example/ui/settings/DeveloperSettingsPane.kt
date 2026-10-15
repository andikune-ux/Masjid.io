package dev.andikune.masjidio.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import dev.andikune.masjidio.util.CredentialManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@Composable
fun DeveloperSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State backup & crash
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

    // State backup
    var isBackupRunning by remember { mutableStateOf(false) }
    var backupProgress by remember { mutableStateOf("") }

    // ============================================================
    // V1.04.429 — KREDENSIAL & PASSPHRASE
    // ============================================================
    var isUnlocked by remember { mutableStateOf(CredentialManager.isUnlocked()) }
    var hasPassphrase by remember {
        mutableStateOf(CredentialManager.hasPassphrase(context))
    }

    // State dialog passphrase
    var showPassphraseDialog by remember { mutableStateOf(false) }
    var passphraseInput by remember { mutableStateOf("") }
    var passphraseConfirm by remember { mutableStateOf("") }
    var isSetupMode by remember { mutableStateOf(!hasPassphrase) }
    var passphraseError by remember { mutableStateOf<String?>(null) }

    // State form Token/Group/PAT
    var tokenInput by remember { mutableStateOf("") }
    var groupIdInput by remember { mutableStateOf("") }
    var patInput by remember { mutableStateOf("") }
    var isEditingCredentials by remember { mutableStateOf(false) }
    var showTokenReveal by remember { mutableStateOf(false) }

    // State sync
    var isSyncing by remember { mutableStateOf(false) }
    var lastSyncStatus by remember { mutableStateOf<String?>(null) }
    // ============================================================
// AUTO-INIT CREDENTIAL SAAT PANE DIBUKA
// ============================================================
LaunchedEffect(Unit) {
    CredentialManager.init(context)
    delay(500)
    if (!hasPassphrase) {
        showPassphraseDialog = true
        isSetupMode = true
    }
}

// ============================================================
// LOAD CREDENTIALS SAAT UNLOCKED
// (Kosongkan form input — tidak reveal token lama)
// ============================================================
LaunchedEffect(isUnlocked) {
    if (isUnlocked) {
        tokenInput = ""
        groupIdInput = ""
        patInput = ""
        isEditingCredentials = false
    }
}

// ============================================================
// EARLY RETURN — TAMPILKAN RIWAYAT CRASH
// ============================================================
if (showCrashHistory) {
    RiwayatCrashScreen(
        onBack = { showCrashHistory = false },
        modifier = modifier
    )
    return
}
// ============================================================
// DIALOG PASSPHRASE
// ============================================================
if (showPassphraseDialog) {
    PassphraseDialog(
        isSetupMode = isSetupMode,
        passphrase = passphraseInput,
        passphraseConfirm = passphraseConfirm,
        errorMessage = passphraseError,
        onPassphraseChange = {
            passphraseInput = it
            passphraseError = null
        },
        onConfirmChange = {
            passphraseConfirm = it
            passphraseError = null
        },
        onConfirm = {
            if (isSetupMode) {
                // Mode setup: bikin passphrase baru
                if (passphraseInput.length < 12) {
                    passphraseError = "Passphrase minimal 12 karakter"
                    return@PassphraseDialog
                }
                if (passphraseInput != passphraseConfirm) {
                    passphraseError = "Passphrase tidak cocok"
                    return@PassphraseDialog
                }
                val ok = CredentialManager.setPassphrase(context, passphraseInput)
                if (ok) {
                    hasPassphrase = true
                    isUnlocked = true
                    showPassphraseDialog = false
                    passphraseInput = ""
                    passphraseConfirm = ""
                    Toast.makeText(
                        context,
                        "Passphrase berhasil dibuat. Catat dan simpan dengan aman!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    passphraseError = "Gagal membuat passphrase"
                }
            } else {
                // Mode unlock: verifikasi passphrase
                scope.launch {
                    val ok = kotlinx.coroutines.withContext(
                        kotlinx.coroutines.Dispatchers.IO
                    ) {
                        CredentialManager.unlock(context, passphraseInput)
                    }
                    if (ok) {
                        isUnlocked = true
                        showPassphraseDialog = false
                        passphraseInput = ""
                        Toast.makeText(
                            context,
                            "Passphrase benar. Kredensial terbuka.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        passphraseError = "Passphrase salah"
                    }
                }
            }
        },
        onDismiss = {
            showPassphraseDialog = false
            passphraseInput = ""
            passphraseConfirm = ""
            passphraseError = null
        }
    )
}
// ============================================================
// MAIN COLUMN
// ============================================================
Column(
    modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    // ============================================================
    // HEADER
    // ============================================================
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
    // BACKUP AMAN
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
                    if (cachedBuildHistoryText == null) {
                        backupProgress = "Mengambil build history..."
                        val builds = BuildHistoryFetcher.fetchRecentBuilds()
                        cachedBuildHistoryText = if (builds.isNotEmpty()) {
                            BuildHistoryFetcher.formatBuildsAsText(builds)
                        } else null
                    }

                    backupProgress = "Mengambil source code dari GitHub..."
                    val sourceCodeText = try {
                        BackupManager.fetchSourceCodeText()
                    } catch (e: Exception) {
                        android.util.Log.e("Backup", "Fetch source gagal: ${e.message}")
                        ""
                    }

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

Spacer(modifier = Modifier.height(16.dp))

// ============================================================
// V1.04.429 — KREDENSIAL SECRET
// ============================================================
Text(
    text = "KREDENSIAL SECRET",
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold,
    color = IslamicGoldLight
)
Text(
    text = "Token Fonnte, Group ID, dan GitHub PAT disimpan terenkripsi " +
            "dengan passphrase. Hanya bisa dibuka oleh Anda.",
    fontSize = 12.sp,
    color = TextSecondary,
    lineHeight = 16.sp
)

// ============================================================
// JIKA BELUM UNLOCK — Tampilkan tombol buka
// ============================================================
if (!isUnlocked) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33FFD700), RoundedCornerShape(10.dp))
            .border(1.dp, IslamicGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (hasPassphrase)
                    "Kredensial terkunci"
                else
                    "Belum ada passphrase",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(
                text = if (hasPassphrase)
                    "Masukkan passphrase untuk membuka kredensial"
                else
                    "Buat passphrase baru (minimal 12 karakter)",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            FonnteButton(
                icon = Icons.Default.Lock,
                label = if (hasPassphrase) "BUKA KREDENSIAL" else "BUAT PASSPHRASE",
                backgroundColor = IslamicGold,
                textColor = Color(0xFF09141D),
                enabled = true,
                onClick = {
                    passphraseInput = ""
                    passphraseConfirm = ""
                    passphraseError = null
                    isSetupMode = !hasPassphrase
                    showPassphraseDialog = true
                }
            )
        }
    }
} else {
    // ============================================================
// FORM TOKEN FONNTE
// ============================================================
Text(
    text = "Token Fonnte",
    fontSize = 12.sp,
    color = TextSecondary,
    modifier = Modifier.padding(bottom = 6.dp)
)
Box(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0x22000000))
        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(10.dp))
        .padding(14.dp)
) {
    if (isEditingCredentials) {
        // Mode edit: field kosong (tidak reveal token lama)
        if (tokenInput.isEmpty()) {
            Text(
                text = "Masukkan Token Fonnte baru...",
                fontSize = 14.sp,
                color = TextSecondary.copy(alpha = 0.5f)
            )
        }
        BasicTextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = TextPrimary
            ),
            visualTransformation = if (showTokenReveal)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        // Mode tampil: masked read-only
        Text(
            text = if (CredentialManager.getToken().isNotEmpty())
                "●●●●●●●●"
            else
                "(belum diisi)",
            fontSize = 14.sp,
            color = if (CredentialManager.getToken().isNotEmpty())
                TextPrimary
            else
                TextSecondary.copy(alpha = 0.5f)
        )
    }
}

// ============================================================
// FORM GROUP ID
// ============================================================
Spacer(modifier = Modifier.height(10.dp))
Text(
    text = "Group ID WhatsApp",
    fontSize = 12.sp,
    color = TextSecondary,
    modifier = Modifier.padding(bottom = 6.dp)
)
Box(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0x22000000))
        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(10.dp))
        .padding(14.dp)
) {
    if (isEditingCredentials) {
        if (groupIdInput.isEmpty()) {
            Text(
                text = "Masukkan Group ID baru...",
                fontSize = 14.sp,
                color = TextSecondary.copy(alpha = 0.5f)
            )
        }
        BasicTextField(
            value = groupIdInput,
            onValueChange = { groupIdInput = it },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Text(
            text = if (CredentialManager.getGroupId().isNotEmpty())
                "●●●●●●●●"
            else
                "(belum diisi)",
            fontSize = 14.sp,
            color = if (CredentialManager.getGroupId().isNotEmpty())
                TextPrimary
            else
                TextSecondary.copy(alpha = 0.5f)
        )
    }
}
// ============================================================
// FORM GITHUB PAT
// ============================================================
Spacer(modifier = Modifier.height(10.dp))
Text(
    text = "GitHub PAT (untuk sync Gist)",
    fontSize = 12.sp,
    color = TextSecondary,
    modifier = Modifier.padding(bottom = 6.dp)
)
Box(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0x22000000))
        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(10.dp))
        .padding(14.dp)
) {
    if (isEditingCredentials) {
        if (patInput.isEmpty()) {
            Text(
                text = "Masukkan GitHub PAT baru...",
                fontSize = 14.sp,
                color = TextSecondary.copy(alpha = 0.5f)
            )
        }
        BasicTextField(
            value = patInput,
            onValueChange = { patInput = it },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = TextPrimary
            ),
            visualTransformation = if (showTokenReveal)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Text(
            text = if (CredentialManager.getPat().isNotEmpty())
                "●●●●●●●●"
            else
                "(belum diisi)",
            fontSize = 14.sp,
            color = if (CredentialManager.getPat().isNotEmpty())
                TextPrimary
            else
                TextSecondary.copy(alpha = 0.5f)
        )
    }
}

// ============================================================
// TOMBOL AKSI
// ============================================================
Spacer(modifier = Modifier.height(14.dp))
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    if (!isEditingCredentials) {
        // Mode tampil: UBAH + SYNC
        Box(modifier = Modifier.weight(1f)) {
            FonnteButton(
                icon = Icons.Default.Edit,
                label = "UBAH",
                backgroundColor = IslamicGold,
                textColor = Color(0xFF09141D),
                enabled = !isSyncing,
                onClick = {
                    tokenInput = ""
                    groupIdInput = ""
                    patInput = ""
                    isEditingCredentials = true
                }
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            FonnteButton(
                icon = Icons.Default.Cloud,
                label = if (isSyncing) "SYNC..." else "SYNC",
                backgroundColor = IslamicGreen,
                textColor = Color.White,
                enabled = !isSyncing && CredentialManager.getPat().isNotEmpty(),
                onClick = {
                    if (isSyncing) return@FonnteButton
                    isSyncing = true
                    lastSyncStatus = null
                    scope.launch {
                        val result = CredentialManager.syncFromCloud(context)
                        isSyncing = false
                        if (result.success) {
                            if (result.hasRemoteChange && result.remoteCredentials != null) {
                                CredentialManager.applyRemoteCredentials(
                                    context,
                                    result.remoteCredentials
                                )
                                lastSyncStatus = "✅ Berhasil sync dari cloud"
                            } else {
                                lastSyncStatus = "ℹ️ ${result.message}"
                            }
                        } else {
                            lastSyncStatus = "❌ ${result.message}"
                        }
                    }
                }
            )
        }
    } else {
        // Mode edit: SIMPAN + BATAL
        Box(modifier = Modifier.weight(1f)) {
            FonnteButton(
                icon = Icons.Default.CheckCircle,
                label = "SIMPAN",
                backgroundColor = IslamicGold,
                textColor = Color(0xFF09141D),
                enabled = tokenInput.isNotBlank() ||
                        groupIdInput.isNotBlank() ||
                        patInput.isNotBlank(),
                onClick = {
                    scope.launch {
                        val tokenBaru = tokenInput.ifBlank {
                            CredentialManager.getToken()
                        }
                        val groupBaru = groupIdInput.ifBlank {
                            CredentialManager.getGroupId()
                        }
                        val patBaru = patInput.ifBlank {
                            CredentialManager.getPat()
                        }

                        val creds = CredentialManager.Credentials(
                            fonnteToken = tokenBaru,
                            fonnteGroupId = groupBaru,
                            githubPat = patBaru
                        )
                        val ok = CredentialManager.saveCredentials(context, creds)
                        if (ok) {
                            onUpdate(
                                settings.copy(
                                    fonnteToken = tokenBaru,
                                    fonnteGroupId = groupBaru,
                                    gistId = CredentialManager.getGistId(context) ?: ""
                                )
                            )
                            if (patBaru.isNotBlank()) {
                                val syncRes = CredentialManager.syncToCloud(context)
                                if (syncRes.success) {
                                    lastSyncStatus = "✅ Tersimpan + sync cloud"
                                } else {
                                    lastSyncStatus = "⚠️ Tersimpan lokal, sync gagal"
                                }
                            } else {
                                lastSyncStatus = "✅ Tersimpan lokal"
                            }
                            isEditingCredentials = false
                            tokenInput = ""
                            groupIdInput = ""
                            patInput = ""
                        } else {
                            Toast.makeText(
                                context,
                                "Gagal menyimpan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            FonnteButton(
                icon = Icons.Default.Close,
                label = "BATAL",
                backgroundColor = Color(0xFF142735),
                textColor = TextPrimary,
                enabled = true,
                onClick = {
                    isEditingCredentials = false
                    tokenInput = ""
                    groupIdInput = ""
                    patInput = ""
                }
            )
        }
    }
}

// ============================================================
// STATUS SYNC
// ============================================================
if (lastSyncStatus != null) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = lastSyncStatus ?: "",
        fontSize = 12.sp,
        color = if (lastSyncStatus?.startsWith("✅") == true)
            IslamicGreen else UrgentRed
    )
}
            // ============================================================
            // INFO CARA DAPAT TOKEN FONNTE
            // ============================================================
            Spacer(modifier = Modifier.height(12.dp))
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
        }

        // ============================================================
        // TOMBOL SALIN PROMPT AI (Proyek 3)
        // ============================================================
        Spacer(modifier = Modifier.height(16.dp))

        DeveloperButton(
            icon = Icons.Default.ContentCopy,
            title = "SALIN PROMPT AI",
            description = "Salin prompt konteks proyek ke clipboard — paste ke AI lain",
            onClick = {
                val promptText = generateAiPrompt(settings, context)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Masjid.io Prompt", promptText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    context,
                    "✅ Prompt tersalin ke clipboard",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ============================================================
        // LOKASI BACKUP
        // ============================================================
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
// KOMPONEN: INFO BOX
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
            Text(
                value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(description, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

// ============================================================
// KOMPONEN: PERMISSION WARNING CARD
// ============================================================
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
// ============================================================
// KOMPONEN: DEVELOPER BUTTON
// ============================================================
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

// ============================================================
// KOMPONEN: FONNTE TOGGLE
// ============================================================
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
// ============================================================
// KOMPONEN: FONNTE INPUT FIELD
// (Masih dipakai untuk backward compatibility /
//  bisa dihapus kalau tidak dipakai lagi)
// ============================================================
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

// ============================================================
// KOMPONEN: FONNTE BUTTON
// ============================================================
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
// ============================================================
// KOMPONEN: PASSPHRASE DIALOG
// V1.04.429 — Dialog untuk buat / masukkan passphrase
//
// Mode SETUP  : Buat passphrase baru (min 12 karakter)
// Mode UNLOCK : Masukkan passphrase untuk buka kredensial
// ============================================================
@Composable
private fun PassphraseDialog(
    isSetupMode: Boolean,
    passphrase: String,
    passphraseConfirm: String,
    errorMessage: String?,
    onPassphraseChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isFocusedPass by remember { mutableStateOf(false) }
    var isFocusedConfirm by remember { mutableStateOf(false) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .width(480.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0B1720))
                .border(2.dp, IslamicGold, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = if (isSetupMode) "BUAT PASSPHRASE" else "BUKA KREDENSIAL",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Text(
                text = if (isSetupMode)
                    "Passphrase minimal 12 karakter. Catat & simpan dengan aman — kalau lupa, kredensial tidak bisa dibuka."
                else
                    "Masukkan passphrase untuk membuka Token Fonnte, Group ID, dan GitHub PAT.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )

            // ---------- INPUT PASSPHRASE ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x22000000))
                    .border(
                        if (isFocusedPass) 3.dp else 1.dp,
                        if (isFocusedPass) IslamicGoldLight else Color(0x44FFFFFF),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(14.dp)
            ) {
                if (passphrase.isEmpty()) {
                    Text(
                        text = "Passphrase...",
                        fontSize = 14.sp,
                        color = TextSecondary.copy(alpha = 0.5f)
                    )
                }
                BasicTextField(
                    value = passphrase,
                    onValueChange = onPassphraseChange,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = TextPrimary
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocusedPass = it.isFocused }
                )
            }

            // ---------- INPUT KONFIRM (hanya setup) ----------
            if (isSetupMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22000000))
                        .border(
                            if (isFocusedConfirm) 3.dp else 1.dp,
                            if (isFocusedConfirm) IslamicGoldLight else Color(0x44FFFFFF),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(14.dp)
                ) {
                    if (passphraseConfirm.isEmpty()) {
                        Text(
                            text = "Ulangi passphrase...",
                            fontSize = 14.sp,
                            color = TextSecondary.copy(alpha = 0.5f)
                        )
                    }
                    BasicTextField(
                        value = passphraseConfirm,
                        onValueChange = onConfirmChange,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = TextPrimary
                        ),
                        visualTransformation = PasswordVisualTransformation(),
// ============================================================
// KOMPONEN: PASSPHRASE DIALOG
// V1.04.430 — Compact HP
// ============================================================
@Composable
private fun PassphraseDialog(
    isSetupMode: Boolean,
    passphrase: String,
    passphraseConfirm: String,
    errorMessage: String?,
    onPassphraseChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isFocusedPass by remember { mutableStateOf(false) }
    var isFocusedConfirm by remember { mutableStateOf(false) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 480.dp)
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0B1720))
                .border(2.dp, IslamicGold, RoundedCornerShape(16.dp))
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = if (isSetupMode) "BUAT PASSPHRASE" else "BUKA KREDENSIAL",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Text(
                text = if (isSetupMode)
                    "Passphrase minimal 12 karakter. Catat & simpan dengan aman — kalau lupa, kredensial tidak bisa dibuka."
                else
                    "Masukkan passphrase untuk membuka Token Fonnte, Group ID, dan GitHub PAT.",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )

            // ---------- INPUT PASSPHRASE ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x22000000))
                    .border(
                        if (isFocusedPass) 3.dp else 1.dp,
                        if (isFocusedPass) IslamicGoldLight else Color(0x44FFFFFF),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                if (passphrase.isEmpty()) {
                    Text(
                        text = "Passphrase...",
                        fontSize = 13.sp,
                        color = TextSecondary.copy(alpha = 0.5f)
                    )
                }
                BasicTextField(
                    value = passphrase,
                    onValueChange = onPassphraseChange,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = TextPrimary
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocusedPass = it.isFocused }
                )
            }

            // ---------- INPUT KONFIRM (hanya setup) ----------
            if (isSetupMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22000000))
                        .border(
                            if (isFocusedConfirm) 3.dp else 1.dp,
                            if (isFocusedConfirm) IslamicGoldLight else Color(0x44FFFFFF),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp)
                ) {
                    if (passphraseConfirm.isEmpty()) {
                        Text(
                            text = "Ulangi passphrase...",
                            fontSize = 13.sp,
                            color = TextSecondary.copy(alpha = 0.5f)
                        )
                    }
                    BasicTextField(
                        value = passphraseConfirm,
                        onValueChange = onConfirmChange,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            color = TextPrimary
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocusedConfirm = it.isFocused }
                    )
                }
            }

            // ---------- ERROR ----------
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UrgentRed,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // ---------- TOMBOL ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    FonnteButton(
                        icon = Icons.Default.Close,
                        label = "BATAL",
                        backgroundColor = Color(0xFF142735),
                        textColor = TextPrimary,
                        enabled = true,
                        onClick = onDismiss
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FonnteButton(
                        icon = Icons.Default.CheckCircle,
                        label = if (isSetupMode) "SIMPAN" else "BUKA",
                        backgroundColor = IslamicGold,
                        textColor = Color(0xFF09141D),
                        enabled = passphrase.isNotBlank() &&
                                (!isSetupMode || passphraseConfirm.isNotBlank()),
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}
// ============================================================
// FUNGSI: GENERATE AI PROMPT
// V1.04.429 — Bikin teks prompt siap paste ke AI lain
// ============================================================
private fun generateAiPrompt(
    settings: AppSettings,
    context: Context
): String {
    val version = try {
        BuildConfig.VERSION_NAME
    } catch (e: Exception) {
        "V1.04.429"
    }

    return buildString {
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("  PROMPT UNTUK AI BARU — PROYEK MASJID.IO")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine()
        appendLine("Halo AI. Saya pakai aplikasi MASJID.IO (Android TV, Jetpack Compose).")
        appendLine("Tolong baca konteks di bawah ini dulu sebelum saya kasih tugas.")
        appendLine("Setelah paham, konfirmasi ke saya, baru kita mulai kerja.")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("1. IDENTITAS PROYEK")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("• Nama Aplikasi : Masjid.io")
        appendLine("• Platform      : Android TV (Jetpack Compose)")
        appendLine("• Repo          : https://github.com/andikune-ux/Masjid.io (PUBLIC)")
        appendLine("• Namespace     : dev.andikune.masjidio")
        appendLine("• Application ID: dev.andikune.masjidio.tv")
        appendLine("• Versi Saat Ini: $version")
        appendLine("• User          : Andi (HP Android, GitHub Mobile)")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("2. ATURAN FORMAT RESPON (WAJIB)")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("Setiap respon harus ada 6 elemen:")
        appendLine("  1. Visualisasi folder (emoji + indentasi)")
        appendLine("  2. Path lengkap file")
        appendLine("  3. Nama file")
        appendLine("  4. URL Edit (file lama) / URL New (file baru)")
        appendLine("  5. Kode TIMPA FULL (jangan suruh user edit manual)")
        appendLine("  6. Pesan commit")
        appendLine()
        appendLine("KETENTUAN:")
        appendLine("• Pakai code block 3 backtick untuk path / URL / nama file")
        appendLine("• Bahasa Indonesia sederhana")
        appendLine("• File >300 baris -> bagi jadi 3-7 BAGIAN kecil")
        appendLine("• Anti-truncation: judul 'BAGIAN 1 DARI N', jangan potong tengah fungsi")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("3. ATURAN KERJA")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("1. Konfirmasi dulu sebelum eksekusi")
        appendLine("2. Jujur kalau belum tahu, jangan menebak")
        appendLine("3. JANGAN hapus fitur lama tanpa izin")
        appendLine("4. Lihat kode asli dulu sebelum timpa")
        appendLine("5. Kerjakan PER-FILE (bukan per-fitur)")
        appendLine("   -> Beberapa fitur di 1 file sama -> kerjakan bersamaan")
        appendLine("6. JANGAN buka file sama 2x")
        appendLine("7. Konfirmasi sebelum lanjut batch berikutnya")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("4. ATURAN KERAS — JANGAN DILANGGAR")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("X JANGAN ubah debug.keystore (root repo)")
        appendLine("X JANGAN ubah signingConfigs di app/build.gradle.kts")
        appendLine("X JANGAN ubah bagian signing di .github/workflows/build.yml")
        appendLine("X JANGAN install APK dari AI Studio (bentrok signature)")
        appendLine("V SELALU install APK dari GitHub Release")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("5. STRUKTUR FOLDER APLIKASI")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("app/src/main/java/dev/andikune/masjidio/")
        appendLine("|-- MainActivity.kt                    -> Activity utama")
        appendLine("|-- audio/SoundManager.kt              -> Suara beep & adzan")
        appendLine("|-- data/")
        appendLine("|   |-- model/AppSettings.kt           -> 130+ field pengaturan")
        appendLine("|   |-- local/                         -> Repository, PrayerTimes, dll")
        appendLine("|   |-- content/                       -> Dzikir, Ayat, Hadits, Asmaul Husna")
        appendLine("|   |-- AppKnowledge.kt                -> Data statis")
        appendLine("|   \\-- UpdateHistory.kt               -> Riwayat update")
        appendLine("|-- kiosk/                             -> Kiosk, Watchdog, AutoStart")
        appendLine("|-- receiver/BootReceiver.kt           -> Broadcast boot")
        appendLine("|-- util/                              -> Backup, Crash, Fonnte, dll")
        appendLine("\\-- ui/")
        appendLine("    |-- home/HomeScreen.kt             -> Tampilan utama")
        appendLine("    |-- focus/                         -> Adzan, Mode Fokus, QRIS")
        appendLine("    |-- slides/                        -> Slide QRIS, Laporan, Kajian")
        appendLine("    |-- ramadhan/                      -> Overlay Ramadhan")
        appendLine("    |-- cctv/CctvWidget.kt             -> Widget CCTV")
        appendLine("    |-- remote/                        -> iO Control, Remote Server")
        appendLine("    |-- settings/                      -> 20 pane Settings")
        appendLine("    |-- components/                    -> 20+ komponen UI")
        appendLine("    \\-- theme/                         -> Warna & tipografi")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("6. MENU SETTINGS (20 KATEGORI)")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("1.  Lokasi & Waktu Sholat")
        appendLine("2.  Pengaturan Waktu")
        appendLine("3.  Durasi & Hitungan Mundur")
        appendLine("4.  Identitas Masjid")
        appendLine("5.  Jadwal Petugas & Foto")
        appendLine("6.  Donasi QRIS & Rekening")
        appendLine("7.  Video Kegiatan Masjid")
        appendLine("8.  Tampilan & Background")
        appendLine("9.  Tampilan TV")
        appendLine("10. Kartu Nasihat & Mutiara")
        appendLine("11. Running Text")
        appendLine("12. Audio & Adzan")
        appendLine("13. Mode Ramadhan")
        appendLine("14. Keamanan (PIN LOCK 3 opsi)")
        appendLine("15. Daya & Booting (jadwal on/off otomatis)")
        appendLine("16. Slide Fullscreen")
        appendLine("17. CCTV Masjid")
        appendLine("18. iO Control")
        appendLine("19. Tentang Aplikasi")
        appendLine("20. Opsi Developer")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("7. FITUR UTAMA APLIKASI")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("• Jadwal sholat 6 waktu + countdown + progress bar")
        appendLine("• Tema Makkah Dinamis (langit real-time 24 jam) — DEFAULT")
        appendLine("• Responsive Auto-Scale (base 1920x1080, scale 0.6x-2.5x)")
        appendLine("• Navigasi D-Pad TV (AutoFocusPane, NeonFocusBorder)")
        appendLine("• Alur Sholat: Beep -> Adzan -> Himbauan HP -> Niat Qobliyah -> Mode Fokus")
        appendLine("• Mode Fokus 2 fase (Niat Fardhu + Dzikir)")
        appendLine("• Layout Opsi H (panel kiri 24% + kanan 76%)")
        appendLine("• Auto-Switch Mode Video <-> Mode Normal")
        appendLine("• 5 Mode Frame: POTONG / PAS / ZOOM / FULL / FIT")
        appendLine("• Kecerahan layar: MANUAL / AUTO / SCHEDULE")
        appendLine("• Jadwal On/Off otomatis (ON=Subuh-X, OFF=Isya+Y)")
        appendLine("• AutoOffDialog (popup konfirmasi jam OFF)")
        appendLine("• PIN Lock Mode (IMMEDIATE / TIMEOUT_5MIN / UNTIL_EXIT)")
        appendLine("• Slide Fullscreen (QRIS / Laporan / Kajian)")
        appendLine("• CCTV Widget (RTSP + HTTP)")
        appendLine("• iO Control (HP remote TV via WiFi UDP)")
        appendLine("• Streaming Upload transfer media (1 request per file)")
        appendLine("• Foreground Service + WiFiLock")
        appendLine("• File Template .iO (bundle ZIP)")
        appendLine("• Color Coding .iO (HIJAU/KUNING/MERAH/HITAM)")
        appendLine("• Backup Aman (TXT)")
        appendLine("• WhatsApp Fonnte (notif crash ke grup admin)")
        appendLine("• Kiosk Mode + Watchdog + Auto-start")
        appendLine("• Konten Rotasi (Ayat, Hadits, Asmaul Husna)")
        appendLine("• Dzikir setelah sholat (8 dzikir)")
        appendLine("• Auto-show Crash Log")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("8. ATURAN UPDATE WAJIB TIAP BUILD")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("1. Bump versionName -> otomatis dari GITHUB_RUN_NUMBER")
        appendLine("2. Tambah entri baru -> UpdateHistory.kt")
        appendLine("3. Update info baru -> AppKnowledge.kt")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("9. LINK GITHUB")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("Repo     : https://github.com/andikune-ux/Masjid.io")
        appendLine("Tree API : https://api.github.com/repos/andikune-ux/Masjid.io/git/trees/main?recursive=1")
        appendLine("RAW      : https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/")
        appendLine("BLOB     : https://github.com/andikune-ux/Masjid.io/blob/main/")
        appendLine("Edit     : https://github.com/andikune-ux/Masjid.io/edit/main/{path}")
        appendLine("New File : https://github.com/andikune-ux/Masjid.io/new/main/{path}")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("10. FILE PENTING (JANGAN SENTUH)")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("• app/build.gradle.kts              -> versioning")
        appendLine("• version.properties                -> data versi otomatis")
        appendLine("• .github/workflows/build.yml       -> build & release")
        appendLine("• debug.keystore                    -> keystore permanen (JANGAN UBAH)")
        appendLine("• gradle/libs.versions.toml         -> KSP & dependency")
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("11. TUGAS SAYA")
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("(Tulis tugas di sini)")
        appendLine()
        appendLine()
        appendLine("═══════════════════════════════════════════════════════")
        appendLine("  END OF PROMPT")
        appendLine("═══════════════════════════════════════════════════════")
    }
}
