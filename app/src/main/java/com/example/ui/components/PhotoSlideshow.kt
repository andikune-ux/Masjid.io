package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * PhotoSlideshow — Slideshow foto kegiatan masjid.
 *
 * V1.04.420: Tambah parameter contentScale
 *   - contentScale: cara foto menyesuaikan frame (PAS/POTONG/ZOOM/FULL/FIT)
 */
@Composable
fun PhotoSlideshow(
    photoUris: List<String>,
    intervalSeconds: Int = 10,
    isFullscreen: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier
) {
    val shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(20.dp)
    val borderWidth = if (isFullscreen) 0.dp else 2.5.dp

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFF000000))
            .border(
                width = borderWidth,
                color = if (isFullscreen) Color.Transparent else IslamicGold,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (photoUris.isNotEmpty()) {
            var currentIndex by remember { mutableIntStateOf(0) }
            val safeInterval = intervalSeconds.coerceIn(3, 120)

            LaunchedEffect(photoUris.size, safeInterval) {
                while (true) {
                    delay(safeInterval * 1000L)
                    currentIndex = (currentIndex + 1) % photoUris.size
                }
            }

            if (currentIndex >= photoUris.size) {
                currentIndex = 0
            }

            Crossfade(
                targetState = currentIndex,
                animationSpec = tween(durationMillis = 800),
                label = "photo_slideshow"
            ) { index ->
                val uri = photoUris.getOrElse(index) { photoUris.first() }
                AsyncImage(
                    model = uri,
                    contentDescription = "Foto Kegiatan Masjid",
                    contentScale = contentScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                )
            }

            if (photoUris.size > 1 && !isFullscreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xAA000000))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(photoUris.size.coerceAtMost(10)) { i ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == currentIndex) 8.dp else 6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (i == currentIndex) IslamicGold
                                        else Color(0x88FFFFFF)
                                    )
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Foto Kegiatan",
                    tint = IslamicGold.copy(alpha = 0.6f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Foto Kegiatan Masjid",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Tambahkan foto di Pengaturan",
                    fontSize = 11.sp,
                    color = Color(0x88FFFFFF)
                )
            }
        }
    }
}
