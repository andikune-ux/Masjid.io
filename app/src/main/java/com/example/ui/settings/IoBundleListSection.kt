package dev.andikune.masjidio.ui.settings

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import dev.andikune.masjidio.util.IoBundleHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================================================
// V1.30.7 BARU — DAFTAR FILE TEMPLATE .iO
// ============================================================

@Composable
fun IoBundleListSection(
    currentSettings: AppSettings,
    onApplySettings: (AppSettings) -> Unit,
    onRestart: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var bundleList by remember { mutableStateOf<List<IoBundleHelper.BundleInfo>>(emptyList()) }
    var refreshKey by remember { mutableStateOf(0) }
    var selectedBundleForInfo by remember { mutableStateOf<IoBundleHelper.BundleInfo?>(null) }
    var selectedBundleForRestart by remember { mutableStateOf<IoBundleHelper.BundleInfo?>(null) }
    var selectedBundleForDelete by remember { mutableStateOf<IoBundleHelper.BundleInfo?>(null) }
    var isRestoring by remember { mutableStateOf(false) }

    // Load daftar bundle
    LaunchedEffect(refreshKey) {
        bundleList = IoBundleHelper.listBundles(context)
    }

    // ============================================================
    // KONTEN UTAMA
    // ============================================================
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (bundleList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x22000000))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IslamicGold.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Belum ada file template .iO",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "File akan muncul di sini setelah menerima transfer dari HP",
                        fontSize = 11.sp,
                        color = TextSecondary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }
            }
        } else {
            Text(
                text = "Total: ${bundleList.size} file template tersimpan",
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            bundleList.forEach { bundle ->
                BundleListItem(
                    bundle = bundle,
                    onUse = { selectedBundleForRestart = bundle },
                    onInfo = { selectedBundleForInfo = bundle },
                    onDelete = { selectedBundleForDelete = bundle }
                )
            }
        }
    }
    // ============================================================
    // DIALOG: INFO LOG
    // ============================================================
    if (selectedBundleForInfo != null) {
        BundleInfoDialog(
            bundle = selectedBundleForInfo!!,
            onDismiss = { selectedBundleForInfo = null }
        )
    }

    // ============================================================
    // DIALOG: KONFIRMASI HAPUS
    // ============================================================
    if (selectedBundleForDelete != null) {
        BundleDeleteDialog(
            bundle = selectedBundleForDelete!!,
            onConfirm = {
                val b = selectedBundleForDelete!!
                val ok = IoBundleHelper.deleteBundle(b.file)
                if (ok) {
                    Toast.makeText(context, "File dihapus: ${b.fileName}", Toast.LENGTH_SHORT).show()
                    refreshKey++
                } else {
                    Toast.makeText(context, "Gagal hapus file", Toast.LENGTH_SHORT).show()
                }
                selectedBundleForDelete = null
            },
            onDismiss = { selectedBundleForDelete = null }
        )
    }

    // ============================================================
    // DIALOG: KONFIRMASI RESTART SETELAH APPLY TEMPLATE
    // ============================================================
    if (selectedBundleForRestart != null) {
        BundleRestartDialog(
            bundle = selectedBundleForRestart!!,
            isRestoring = isRestoring,
            onConfirm = {
                val b = selectedBundleForRestart!!
                isRestoring = true

                // Restore bundle
                val result = IoBundleHelper.restoreBundleWithLocalPaths(
                    context = context,
                    bundleFile = b.file,
                    currentSettings = currentSettings
                )

                isRestoring = false

                if (result.success && result.settings != null) {
                    // Apply settings
                    onApplySettings(result.settings)
                    Toast.makeText(
                        context,
                        "✅ Template diterapkan! ${result.restoredMediaCount} file media dipulihkan",
                        Toast.LENGTH_LONG
                    ).show()

                    selectedBundleForRestart = null

                    // Panggil restart kalau ada callback
                    if (onRestart != null) {
                        scope.launch {
                            delay(500)
                            onRestart.invoke()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Template aktif. Restart app untuk melihat perubahan.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "❌ Gagal apply: ${result.errorMessage ?: "Unknown"}",
                        Toast.LENGTH_LONG
                    ).show()
                    selectedBundleForRestart = null
                }
            },
            onDismiss = {
                if (!isRestoring) selectedBundleForRestart = null
            }
        )
    }
}

// ============================================================
// KARTU ITEM BUNDLE
// ============================================================
@Composable
private fun BundleListItem(
    bundle: IoBundleHelper.BundleInfo,
    onUse: () -> Unit,
    onInfo: () -> Unit,
    onDelete: () -> Unit
) {
    val hasFailures = (bundle.metadata?.mediaFailed ?: 0) > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF091620))
            .border(
                width = 1.5.dp,
                color = if (hasFailures) UrgentRed.copy(alpha = 0.5f)
                        else IslamicGold.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ===== INFO FILE =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (hasFailures) UrgentRed.copy(alpha = 0.15f)
                        else IslamicGreen.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasFailures) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (hasFailures) UrgentRed else IslamicGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bundle.fileName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "📊 ${bundle.summaryLine}",
                    fontSize = 11.sp,
                    color = if (hasFailures) UrgentRed else IslamicGoldLight,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📁 ${bundle.fileSizeText}",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        // ===== 3 TOMBOL: GUNAKAN / INFO / HAPUS =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BundleActionButton(
                icon = Icons.Default.PlayArrow,
                label = "GUNAKAN",
                backgroundColor = IslamicGreen,
                textColor = Color(0xFF09141D),
                modifier = Modifier.weight(1.2f),
                onClick = onUse
            )
            BundleActionButton(
                icon = Icons.Default.Info,
                label = "INFO",
                backgroundColor = Color(0xFF1E3A5F),
                textColor = IslamicGoldLight,
                modifier = Modifier.weight(1f),
                onClick = onInfo
            )
            BundleActionButton(
                icon = Icons.Default.Delete,
                label = "HAPUS",
                backgroundColor = Color(0x33FF5252),
                textColor = UrgentRed,
                modifier = Modifier.weight(1f),
                onClick = onDelete
            )
        }
    }
}

// ============================================================
// TOMBOL AKSI BUNDLE (compact)
// ============================================================
@Composable
private fun BundleActionButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 0.dp,
        animationSpec = tween(150),
        label = "btn_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_scale"
    )

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = if (isFocused) Color(0xFFFFE44D) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
// ============================================================
// DIALOG: INFO / LOG KEGAGALAN
// ============================================================
@Composable
private fun BundleInfoDialog(
    bundle: IoBundleHelper.BundleInfo,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val meta = bundle.metadata

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    // Build log text
    val logText = remember(bundle) {
        buildString {
            appendLine("=".repeat(60))
            appendLine("INFO FILE TEMPLATE .iO")
            appendLine("=".repeat(60))
            appendLine("Nama file   : ${bundle.fileName}")
            appendLine("Ukuran      : ${bundle.fileSizeText}")
            appendLine("Lokasi      : ${bundle.filePath}")
            appendLine()
            if (meta != null) {
                appendLine("PENGIRIM:")
                appendLine("  Device      : ${meta.senderDevice}")
                appendLine("  Role        : ${meta.senderRole}")
                appendLine("  Versi app   : ${meta.senderVersion}")
                appendLine("  Diterima    : ${meta.receivedAt}")
                appendLine()
                appendLine("RINGKASAN TRANSFER:")
                appendLine("  Total file  : ${meta.mediaTotal}")
                appendLine("  ✅ Sukses   : ${meta.mediaSuccess}")
                appendLine("  ❌ Gagal    : ${meta.mediaFailed}")
                appendLine("  📷 Foto     : ${meta.photoCount}")
                appendLine("  🎬 Video    : ${meta.videoCount}")
                appendLine()
                if (meta.failedFiles.isNotEmpty()) {
                    appendLine("=".repeat(60))
                    appendLine("DETAIL KEGAGALAN (${meta.failedFiles.size} file)")
                    appendLine("=".repeat(60))
                    appendLine()
                    meta.failedFiles.forEachIndexed { idx, f ->
                        appendLine("[${idx + 1}] ${f.displayName}")
                        appendLine("    Field      : ${f.fieldKey}")
                        appendLine("    Alasan     : ${f.reason}")
                        appendLine("    Exception  : ${f.exceptionClass}")
                        appendLine()
                        appendLine("    Stack Trace:")
                        appendLine(f.stackTrace)
                        appendLine("-".repeat(60))
                        appendLine()
                    }
                } else {
                    appendLine("✅ Tidak ada file yang gagal — semua terkirim dengan baik.")
                    appendLine()
                }
            } else {
                appendLine("⚠️ Metadata tidak tersedia di file ini.")
                appendLine()
            }
            appendLine("=".repeat(60))
        }
    }

    val hasFailures = (meta?.mediaFailed ?: 0) > 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0A1929))
                .border(
                    width = 2.dp,
                    color = if (hasFailures) UrgentRed.copy(alpha = 0.7f) else IslamicGreen.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== HEADER =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (hasFailures) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (hasFailures) UrgentRed else IslamicGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (hasFailures) "LOG KEGAGALAN TRANSFER" else "INFO FILE TEMPLATE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasFailures) UrgentRed else IslamicGreen
                        )
                        Text(
                            text = bundle.fileName,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF132F4C))
                        .clickable { onDismiss() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "TUTUP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // ===== LOG CONTENT =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF000000))
                    .border(
                        1.dp,
                        if (hasFailures) UrgentRed.copy(alpha = 0.4f) else IslamicGreen.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = logText,
                    fontSize = 11.sp,
                    color = Color(0xFF80E080),
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }

            // ===== TOMBOL SALIN LOG =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (copied) IslamicGreen.copy(alpha = 0.3f)
                        else Color(0xFF2196F3).copy(alpha = 0.2f)
                    )
                    .border(
                        1.5.dp,
                        if (copied) IslamicGreen else Color(0xFF2196F3),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        clipboard.setText(AnnotatedString(logText))
                        copied = true
                    }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (copied) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (copied) IslamicGreen else Color(0xFF64B5F6),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (copied) "✓ TERSALIN KE CLIPBOARD" else "📋 SALIN LOG KE CLIPBOARD",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (copied) IslamicGreen else Color(0xFF64B5F6)
                )
            }
        }
    }
}

// ============================================================
// DIALOG: KONFIRMASI HAPUS
// ============================================================
@Composable
private fun BundleDeleteDialog(
    bundle: IoBundleHelper.BundleInfo,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0A1929))
                .border(2.dp, UrgentRed, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = UrgentRed,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "HAPUS FILE INI?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = UrgentRed
            )

            Text(
                text = bundle.fileName,
                fontSize = 12.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
            )

            Text(
                text = "File template akan dihapus permanen.\nTidak bisa dikembalikan.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF132F4C))
                        .clickable { onDismiss() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BATAL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(UrgentRed)
                        .clickable { onConfirm() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "HAPUS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ============================================================
// DIALOG: KONFIRMASI RESTART SETELAH APPLY TEMPLATE
// ============================================================
@Composable
private fun BundleRestartDialog(
    bundle: IoBundleHelper.BundleInfo,
    isRestoring: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val meta = bundle.metadata
    val hasFailures = (meta?.mediaFailed ?: 0) > 0

    Dialog(
        onDismissRequest = { if (!isRestoring) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = !isRestoring)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0A1929))
                .border(2.dp, IslamicGold, RoundedCornerShape(18.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "TERAPKAN TEMPLATE?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Text(
                text = bundle.fileName,
                fontSize = 12.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // ===== RINGKASAN =====
            if (meta != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33000000))
                        .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    InfoRow("Pengirim", meta.senderDevice)
                    InfoRow("Diterima", meta.receivedAt)
                    InfoRow("Sukses", "${meta.mediaSuccess}", IslamicGreen)
                    InfoRow("Gagal", "${meta.mediaFailed}", if (hasFailures) UrgentRed else TextSecondary)
                    InfoRow("📷 Foto", "${meta.photoCount}")
                    InfoRow("🎬 Video", "${meta.videoCount}")
                }
            }

            Text(
                text = if (isRestoring)
                    "Sedang menerapkan template..."
                else
                    "Semua pengaturan + media dari file ini akan diterapkan.\n" +
                            "Aplikasi akan restart dalam 5 detik setelah apply.",
                fontSize = 12.sp,
                color = if (isRestoring) IslamicGoldLight else TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            if (!isRestoring) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF132F4C))
                            .clickable { onDismiss() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BATAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IslamicGold)
                            .clickable { onConfirm() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color(0xFF09141D),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TERAPKAN & RESTART",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF09141D)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// HELPER: BARIS INFO
// ============================================================
@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = FontFamily.Monospace
        )
    }
}
