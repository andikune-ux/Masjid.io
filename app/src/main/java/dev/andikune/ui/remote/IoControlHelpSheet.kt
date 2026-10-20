package dev.andikune.masjidio.ui.remote

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andikune.masjidio.ui.components.NeonFocusBorder
import dev.andikune.masjidio.ui.theme.TextSecondary

/**
 * Sheet panduan iO Control.
 * Menampilkan 4 seksi: cara pakai, syarat jaringan, troubleshooting, prioritas.
 */
@Composable
fun IoControlHelpSheet(
    onClose: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF071A2E), Color(0xFF0A1929))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF10283D).copy(alpha = 0.95f), RoundedCornerShape(18.dp))
                .border(1.dp, Color(0xFF64B5F6).copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ============ HEADER ============
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Panduan iO Control",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBFE8FF)
                    )
                }

                NeonFocusBorder(
                    focused = isFocused,
                    pressed = isPressed,
                    borderWidth = 4.dp,
                    cornerRadius = 12.dp
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E3A5F))
                            .focusable(interactionSource = interactionSource)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onClose() }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Tutup",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBFE8FF)
                        )
                    }
                }
            }

            // ============ KONTEN (SCROLL) ============
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HelpSection(
                    title = "1. Cara Memakai",
                    icon = Icons.Default.PlayArrow,
                    color = Color(0xFF4CAF50),
                    content = listOf(
                        "Pastikan HP dan TV terhubung ke WiFi atau hotspot yang sama.",
                        "Buka menu Settings → iO Control.",
                        "Tekan tombol BUKA iO CONTROL pada TV atau aplikasi pengirim.",
                        "Pilih perangkat yang muncul di daftar, lalu tekan KONEKSI.",
                        "Setelah terhubung, pilih KIRIM untuk mengirim pengaturan atau TERIMA untuk menerima dari perangkat lain.",
                        "Saat transfer selesai, aplikasi akan otomatis restart agar perubahan aktif."
                    )
                )

                HelpSection(
                    title = "2. Syarat Jaringan",
                    icon = Icons.Default.Info,
                    color = Color(0xFF64B5F6),
                    content = listOf(
                        "WiFi/Hotspot harus sama antara perangkat pengirim dan penerima.",
                        "Jangan pakai koneksi berbeda jaringan atau 4G/5G yang terpisah.",
                        "Aktifkan WiFi, jangan menggunakan mode pesawat.",
                        "Jika salah satu perangkat menutup akses jaringan, scan ulang daftar perangkat."
                    )
                )

                HelpSection(
                    title = "3. Masalah Umum & Solusinya",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFFFA726),
                    content = listOf(
                        "Tidak menemukan perangkat: cek WiFi sama, matikan VPN, lalu tekan SCAN ULANG.",
                        "Tidak bisa terhubung: pastikan server remote aktif di perangkat lain dan port tidak dipakai aplikasi lain.",
                        "Gagal menerima: pastikan perangkat pengirim sudah menekan tombol KIRIM dan data belum terputus.",
                        "Gagal mengirim: pastikan target masih online dan tidak sedang dipakai aplikasi lain yang menggunakan port yang sama.",
                        "Server terdeteksi sedang dipakai aplikasi lain: tutup aplikasi lain lalu restart TV/HP, atau pilih port yang tidak bentrok.",
                        "Transfer berhenti di tengah jalan: ulangi proses dan pastikan signal WiFi stabil.",
                        "Aplikasi tidak otomatis restart setelah transfer: buka ulang aplikasi secara manual, lalu cek pengaturan yang masuk."
                    )
                )

                HelpSection(
                    title = "4. Aturan Prioritas Koneksi",
                    icon = Icons.Default.Info,
                    color = Color(0xFFBB86FC),
                    content = listOf(
                        "Saat server terdeteksi dipakai aplikasi lain, aplikasi akan menolak koneksi agar tidak bentrok.",
                        "Aplikasi Masjid.io akan diprioritaskan dibanding server milik aplikasi lain di port yang sama.",
                        "Jika port bentrok, lakukan force close aplikasi lain atau restart perangkat.",
                        "Setelah port bebas, tombol KIRIM dan TERIMA bisa dipakai kembali normal."
                    )
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: SECTION CARD
// ============================================================
@Composable
private fun HelpSection(
    title: String,
    icon: ImageVector,
    color: Color,
    content: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF112A42), RoundedCornerShape(14.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEAF7FF)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        content.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    fontSize = 13.sp,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = item,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
