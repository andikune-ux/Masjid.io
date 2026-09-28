package com.example.ui.components

import android.graphics.Outline
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.VideoView
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
        if (!videoUriString.isNullOrBlank()) {
            AndroidView(
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        // Clip sudut tumpul pada FrameLayout (parent VideoView)
                        clipToOutline = true
                        outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                val radiusPx = 20 * view.resources.displayMetrics.density
                                outline.setRoundRect(0, 0, view.width, view.height, radiusPx)
                            }
                        }

                        val videoView = VideoView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                        addView(videoView)

                        // Simpan referensi VideoView di tag
                        tag = videoView
                    }
                },
                update = { frameLayout ->
                    val videoView = frameLayout.tag as? VideoView ?: return@AndroidView
                    try {
                        // Hindari reload URI yang sama terus-menerus
                        if (videoView.tag != videoUriString) {
                            videoView.tag = videoUriString
                            val uri = Uri.parse(videoUriString)
                            videoView.setVideoURI(uri)
                            videoView.setOnPreparedListener { mp ->
                                mp.isLooping = true
                                mp.setVolume(0f, 0f)
                                videoView.start()
                            }
                            videoView.setOnErrorListener { _, _, _ -> true }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder kalau tidak ada video
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
