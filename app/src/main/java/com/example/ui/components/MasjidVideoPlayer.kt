package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.TextSecondary

@Composable
fun MasjidVideoPlayer(
    videoUriString: String?,
    isFullscreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.Black)
            .border(if (isFullscreen) 0.dp else 1.5.dp, if (isFullscreen) Color.Transparent else IslamicGold, shape),
        contentAlignment = Alignment.Center
    ) {
        if (!videoUriString.isNullOrBlank()) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        try {
                            val uri = Uri.parse(videoUriString)
                            setVideoURI(uri)
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                mp.setVolume(0f, 0f) // Mute so it doesn't disturb mosque silence
                                start()
                            }
                            setOnErrorListener { _, _, _ ->
                                true // Handled gracefully
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                update = { videoView ->
                    try {
                        val currentUri = Uri.parse(videoUriString)
                        videoView.setVideoURI(currentUri)
                        videoView.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder when no video is selected
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
