package com.example.ui.components

import android.graphics.Outline
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * MasjidVideoPlayer — Pemutar video kegiatan masjid.
 *
 * V1.04.423 FIX AUTO-SWITCH:
 *   - Video pakai REPEAT_MODE_ALL → tidak pernah trigger STATE_ENDED
 *   - SOLUSI: polling posisi video setiap 500ms
 *   - Deteksi LOOP saat posisi turun drastis (dari >80% ke <20%)
 *   - Panggil onVideoLooped() setiap 1x putaran selesai
 */
@Composable
fun MasjidVideoPlayer(
    videoUriString: String?,
    isFullscreen: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
    zoomFactor: Float = 1.0f,
    onVideoLooped: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(20.dp)
    val borderWidth = if (isFullscreen) 0.dp else 2.5.dp

    val aspectResizeMode: Int = when (contentScale) {
        ContentScale.Fit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        ContentScale.Crop -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        ContentScale.FillBounds -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        ContentScale.Inside -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        ContentScale.None -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        else -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            volume = 0f
            playWhenReady = true
        }
    }

    // ============================================================
    // V1.04.423 — POLLING POSISI VIDEO
    // Deteksi loop: kalau posisi turun drastis dari >80% ke <20%
    // ============================================================
    LaunchedEffect(onVideoLooped, videoUriString) {
        if (onVideoLooped == null) return@LaunchedEffect
        if (videoUriString.isNullOrBlank()) return@LaunchedEffect

        var lastPosition = 0L
        var lastDuration = 0L
        var hasStarted = false

        while (true) {
            delay(500L)

            try {
                val currentPosition = exoPlayer.currentPosition
                val currentDuration = exoPlayer.duration

                // Update durasi sekali video siap
                if (currentDuration > 0) {
                    lastDuration = currentDuration
                }

                // Tandai video sudah mulai (posisi > 0)
                if (!hasStarted && currentPosition > 0) {
                    hasStarted = true
                }

                // Deteksi LOOP:
                // - Durasi valid (>1 detik)
                // - Posisi sebelumnya mendekati akhir (>80% durasi)
                // - Posisi sekarang mendekati awal (<20% durasi)
                if (hasStarted && lastDuration > 1000L) {
                    val wasNearEnd = lastPosition > (lastDuration * 0.80)
                    val nowNearStart = currentPosition < (lastDuration * 0.20)

                    if (wasNearEnd && nowNearStart) {
                        // Video baru saja looping 1x putaran
                        onVideoLooped.invoke()
                    }
                }

                lastPosition = currentPosition
            } catch (e: Exception) {
                // Abaikan error polling
            }
        }
    }

    DisposableEffect(videoUriString) {
        if (!videoUriString.isNullOrBlank()) {
            try {
                val uri = Uri.parse(videoUriString)
                val mediaItem = MediaItem.fromUri(uri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onDispose { }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                exoPlayer.stop()
                exoPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
        if (!videoUriString.isNullOrBlank()) {
            AndroidView(
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        clipToOutline = true
                        outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                val radiusPx = 20 * view.resources.displayMetrics.density
                                outline.setRoundRect(0, 0, view.width, view.height, radiusPx)
                            }
                        }

                        val playerView = PlayerView(ctx).apply {
                            useController = false
                            this.resizeMode = aspectResizeMode
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            player = exoPlayer
                        }
                        addView(playerView)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = zoomFactor,
                        scaleY = zoomFactor
                    )
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Kegiatan",
                    tint = IslamicGold.copy(alpha = 0.6f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Video Kegiatan Masjid",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Pilih video di Pengaturan",
                    fontSize = 11.sp,
                    color = Color(0x88FFFFFF)
                )
            }
        }
    }
}
