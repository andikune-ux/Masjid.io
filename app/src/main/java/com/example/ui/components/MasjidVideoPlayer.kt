package dev.andikune.masjidio.ui.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.TextSecondary

@Composable
fun MasjidVideoPlayer(
    videoUriString: String?,
    isFullscreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(20.dp)
    val borderWidth = if (isFullscreen) 0.dp else 2.5.dp

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            volume = 0f
            playWhenReady = true
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
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            player = exoPlayer
                        }
                        addView(playerView)
                    }
                },
                modifier = Modifier.fillMaxSize()
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
