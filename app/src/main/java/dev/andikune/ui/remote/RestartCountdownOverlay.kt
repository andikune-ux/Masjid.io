package dev.andikune.masjidio.ui.remote

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * RestartCountdownOverlay — Overlay hitung mundur 5 detik sebelum restart.
 *
 * Dipakai setelah transfer iO Control selesai (settings + media).
 *
 * @param countdownStart Angka mulai hitung mundur (default 5)
 * @param message Pesan tambahan
 * @param onComplete Callback dipanggil saat countdown habis
 */
@Composable
fun RestartCountdownOverlay(
    countdownStart: Int = 5,
    message: String = "Pengaturan baru sedang diterapkan",
    onComplete: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(countdownStart) }

    // Animasi pulse untuk angka
    val infiniteTransition = rememberInfiniteTransition(label = "countdown_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Logic hitung mundur
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown -= 1
        }
        delay(200L)
        onComplete()
    }

    // Warna berubah sesuai countdown
    val countdownColor = when {
        countdown > 3 -> Color(0xFF4CAF50)   // hijau
        countdown > 1 -> Color(0xFFFFA726)   // kuning/oranye
        else -> Color(0xFFFF5252)            // merah
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE041020))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ikon Check
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                    .border(3.dp, Color(0xFF4CAF50), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Judul
            Text(
                text = "✅ SEMUA DATA TERKIRIM",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 15.sp,
                color = Color(0xFFB0BEC5),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Divider emas
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .background(Color(0xFFFFD700))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ikon Refresh + teks
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RESTART DALAM",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700),
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Angka countdown BESAR
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                countdownColor.copy(alpha = 0.3f),
                                countdownColor.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(4.dp, countdownColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$countdown",
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = countdownColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "DETIK",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = countdownColor,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Info kecil di bawah
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF132F4C))
                    .border(1.dp, Color(0xFF2196F3).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💡 Aplikasi akan memuat ulang pengaturan & media baru.\nMohon jangan matikan TV.",
                    fontSize = 12.sp,
                    color = Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
