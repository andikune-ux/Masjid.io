package dev.andikune.masjidio.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

// ============================================================
// WARNA (mengikuti tema aplikasi)
// ============================================================
private val GOLD = Color(0xFFE6C25A)
private val GOLD_LIGHT = Color(0xFFFFE08A)
private val DARK_BG = Color(0xFF0E1621)
private val RED_WARN_BG = Color(0xFF3A1216)
private val RED_WARN_BORDER = Color(0xFFFF4D4D)
private val RED_WARN_TEXT = Color(0xFFFFC7C7)

// ============================================================
// AUTO-OFF DIALOG
// V1.04.427 — Fix D-pad remote tidak bisa pilih YA/TIDAK
//
// PERUBAHAN:
// 1. Tambah FocusRequester ke tombol YA (default fokus masuk dialog)
// 2. Tombol: focusable + onFocusChanged + border fokus visual
// 3. DialogProperties: usePlatformDefaultWidth=false, back=false, outside=false
// 4. Auto-dismiss dihitung dari autoDismissSeconds (bukan ms)
// 5. Layout sesuai screenshot TV
// ============================================================
@Composable
fun AutoOffDialog(
    offStartTime: String,
    offEndTime: String,
    autoDismissSeconds: Int,
    onConfirmTrue: () -> Unit,
    onConfirmFalse: () -> Unit
) {
    // ⭐ FocusRequester untuk tombol YA (default fokus pertama)
    val yaFocusRequester = remember { FocusRequester() }

    // ⭐ Paksa fokus masuk ke tombol YA saat dialog muncul
    LaunchedEffect(Unit) {
        delay(150L) // tunggu layout selesai
        runCatching { yaFocusRequester.requestFocus() }
    }

    // Auto-dismiss setelah N detik -> dianggap TIDAK
    LaunchedEffect(Unit) {
        if (autoDismissSeconds > 0) {
            delay(autoDismissSeconds * 1000L)
            onConfirmFalse()
        }
    }

    Dialog(
        onDismissRequest = { onConfirmFalse() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(820.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DARK_BG)
                    .border(2.dp, GOLD, RoundedCornerShape(24.dp))
                    .padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Icon bulan (bulan sabit) ──
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(GOLD.copy(alpha = 0.12f))
                        .border(3.dp, GOLD, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.NightsStay,
                        contentDescription = null,
                        tint = GOLD_LIGHT,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(Modifier.height(22.dp))

                // ── Judul ──
                Text(
                    text = "JADWAL OFF AKTIF",
                    color = GOLD_LIGHT,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(10.dp))

                // ── Garis pemisah ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(GOLD.copy(alpha = 0.35f))
                )

                Spacer(Modifier.height(20.dp))

                // ── Badge "Layar redup sejak ..." ──
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = GOLD_LIGHT,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Layar redup sejak $offStartTime",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(28.dp))

                // ── Pertanyaan ──
                Text(
                    text = "Apakah Anda bersedia",
                    color = Color.White,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "MEMATIKAN JADWAL ON/OFF?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(22.dp))

                // ── Warning box merah ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RED_WARN_BG)
                        .border(1.5.dp, RED_WARN_BORDER, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = RED_WARN_BORDER,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Kalau memilih YA, layar akan tetap nyala sampai TV dimatikan manual.",
                        color = RED_WARN_TEXT,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(Modifier.height(30.dp))

                // ── Tombol YA + TIDAK ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AutoOffButton(
                        text = "YA",
                        isConfirm = true,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(yaFocusRequester), // ⭐ default fokus
                        onClick = onConfirmTrue
                    )
                    AutoOffButton(
                        text = "TIDAK",
                        isConfirm = false,
                        modifier = Modifier.weight(1f),
                        onClick = onConfirmFalse
                    )
                }
            }
        }
    }
}

// ============================================================
// TOMBOL DIALOG (focusable + border fokus visual)
// ============================================================
@Composable
private fun AutoOffButton(
    text: String,
    isConfirm: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    val baseColor = if (isConfirm) Color(0xFF1BAA5F) else Color(0xFF1A4A9E)
    val focusColor = if (isConfirm) Color(0xFF3DDC84) else Color(0xFF2A6FE0)
    val bgColor = if (focused) focusColor else baseColor

    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (focused) 3.dp else 0.dp,
                color = if (focused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isConfirm) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
