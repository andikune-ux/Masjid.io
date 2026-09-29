package com.example.ui.slides

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.LaporanKeuangan
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed

/**
 * Slide Laporan Keuangan Masjid
 * Menampilkan:
 * - Saldo sebelumnya, pemasukan, pengeluaran, saldo akhir
 * - Detail penerimaan (infaq Jumat, infaq umum)
 * - Detail pengeluaran (dakwah, sosial, operasional)
 * - Periode laporan
 */
@Composable
fun LaporanSlide(
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    val laporan = settings.laporanKeuangan

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1A0D2A),
                        Color(0xFF2E1B3A),
                        Color(0xFF1A0D2A)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ============ HEADER ============
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = settings.mosqueName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "LAPORAN KEUANGAN MASJID",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = IslamicGoldLight,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Periode ${laporan.periodeMulai} - ${laporan.periodeSelesai}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            // ============ ROW UTAMA: 2 KOLOM ============
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ===== KOLOM KIRI: RINGKASAN SALDO =====
                Column(
                    modifier = Modifier.weight(0.32f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Saldo Sebelumnya
                    SaldoBox(
                        icon = Icons.Default.Savings,
                        label = "Saldo Sebelumnya",
                        value = formatRupiah(laporan.saldoSebelumnya),
                        color = IslamicGold,
                        backgroundColor = Color(0x33FFD700)
                    )

                    // Total Pemasukan
                    SaldoBox(
                        icon = Icons.Default.TrendingUp,
                        label = "Total Pemasukan",
                        value = formatRupiah(laporan.totalPemasukan),
                        color = IslamicGreen,
                        backgroundColor = Color(0x33A5D6A7)
                    )

                    // Total Pengeluaran
                    SaldoBox(
                        icon = Icons.Default.TrendingDown,
                        label = "Total Pengeluaran",
                        value = formatRupiah(laporan.totalPengeluaran),
                        color = UrgentRed,
                        backgroundColor = Color(0x33EF5350)
                    )

                    // Saldo Akhir
                    SaldoBox(
                        icon = Icons.Default.AccountBalance,
                        label = "Saldo Terkini",
                        value = formatRupiah(laporan.saldoAkhir),
                        color = IslamicGoldLight,
                        backgroundColor = Color(0x55FFD700),
                        isHighlight = true
                    )
                }

                // ===== KOLOM KANAN: DETAIL =====
                Column(
                    modifier = Modifier.weight(0.68f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Detail Penerimaan
                    DetailCard(
                        title = "DETAIL PENERIMAAN",
                        titleColor = IslamicGreen,
                        icon = Icons.Default.ArrowDownward,
                        items = listOf(
                            "Infaq Jum'at" to formatRupiah(laporan.pemasukanJumat),
                            "Infaq Umum" to formatRupiah(laporan.pemasukanUmum)
                        )
                    )

                    // Detail Pengeluaran
                    DetailCard(
                        title = "DETAIL PENYALURAN",
                        titleColor = UrgentRed,
                        icon = Icons.Default.ArrowUpward,
                        items = listOf(
                            "Dakwah" to formatRupiah(laporan.pengeluaranDakwah),
                            "Sosial" to formatRupiah(laporan.pengeluaranSosial),
                            "Operasional Masjid" to formatRupiah(laporan.pengeluaranOperasional)
                        )
                    )

                    // Footer: QRIS kecil
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mari Berinfaq Bersama",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldLight
                            )
                            Text(
                                text = "Scan QRIS untuk berinfaq. Laporan transparan untuk umat.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// KOMPONEN: SALDO BOX
// ============================================================

@Composable
private fun SaldoBox(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    backgroundColor: Color,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isHighlight) 2.dp else 1.dp,
                color = color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(if (isHighlight) 16.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(if (isHighlight) 28.dp else 22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = if (isHighlight) 20.sp else 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ============================================================
// KOMPONEN: DETAIL CARD
// ============================================================

@Composable
private fun DetailCard(
    title: String,
    titleColor: Color,
    icon: ImageVector,
    items: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33000000))
            .border(1.dp, titleColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = titleColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                letterSpacing = 1.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { (label, value) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x22000000))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ============================================================
// HELPER: FORMAT RUPIAH
// ============================================================

private fun formatRupiah(amount: Long): String {
    return when {
        amount >= 1_000_000 -> {
            val juta = amount / 1_000_000.0
            if (juta == juta.toLong().toDouble()) "${juta.toLong()} jt"
            else String.format("%.1f jt", juta)
        }
        amount >= 1_000 -> {
            val ribu = amount / 1_000.0
            if (ribu == ribu.toLong().toDouble()) "${ribu.toLong()} rb"
            else String.format("%.1f rb", ribu)
        }
        else -> "$amount"
    }
}
