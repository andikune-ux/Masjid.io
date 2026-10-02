package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.example.data.local.SunMoonCalculator
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============================================================
// DATA CLASS — untuk partikel & efek
// ============================================================
private data class CloudPuff(
    val x: Float,
    val y: Float,
    val scale: Float,
    val speed: Float,
    val alpha: Float
)

private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinklePhase: Float
)

private data class RainDrop(
    val x: Float,
    val y: Float,
    val speed: Float,
    val length: Float,
    val alpha: Float
)

private data class Bird(
    val x: Float,
    val y: Float,
    val speed: Float,
    val flapPhase: Float,
    val size: Float
)

/**
 * MAKKAH DYNAMIC BACKGROUND
 *
 * Background tema Makkah dengan langit berubah mengikuti:
 *   - Waktu real-time (matahari melengkung, bulan bergeser)
 *   - Fase bulan real (baru/sabit/purnama)
 *   - Cuaca real-time (cerah/berawan/hujan/petir)
 *   - Efek visual: awan, burung, bintang, hujan, petir, kabut
 *
 * Bangunan Masjidil Haram: siluet statis HD (digambar via canvas).
 */
@Composable
fun MakkahDynamicBackground(
    weatherCondition: String = "Cerah",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // State waktu real-time (update tiap 30 detik)
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(30_000L)
        }
    }

    // Posisi & fase benda langit (Lat/Lon Makkah: -21.42, 39.83)
    val sunPos = remember(now) {
        SunMoonCalculator.getSunPosition(now, -21.42, 39.83)
    }
    val moonPos = remember(now) {
        SunMoonCalculator.getMoonPosition(now, -21.42, 39.83)
    }
    val moonPhase = remember(now) {
        SunMoonCalculator.getMoonPhase(now)
    }
    val skyColors = remember(now) {
        SunMoonCalculator.getSkyGradient(now)
    }
    val isNight = remember(now) {
        SunMoonCalculator.isNightTime(now)
    }
    val isGoldenHour = remember(now) {
        SunMoonCalculator.isGoldenHour(now)
    }

    // Deteksi cuaca
    val isRainy = weatherCondition.contains("Hujan", true) ||
            weatherCondition.contains("Gerimis", true)
    val isStormy = weatherCondition.contains("Petir", true)
    val isCloudy = weatherCondition.contains("Berawan", true) ||
            weatherCondition.contains("Kabut", true)

    // ============================================================
    // ANIMASI
    // ============================================================
    val infiniteTransition = rememberInfiniteTransition(label = "makkah_bg")

    val cloudProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 180_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_movement"
    )

    val birdProgress by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 45_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bird_flight"
    )

    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing_flap"
    )

    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    val rainProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_fall"
    )

    // Petir muncul acak (10-15 detik sekali)
    var lightningFlash by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isStormy) {
        if (!isStormy) {
            lightningFlash = 0f
            return@LaunchedEffect
        }
        while (true) {
            delay((10_000L..15_000L).random())
            lightningFlash = 0.9f
            delay(80)
            lightningFlash = 0.2f
            delay(100)
            lightningFlash = 1f
            delay(150)
            lightningFlash = 0f
        }
    }

    // ============================================================
    // PARTIKEL
    // ============================================================
    val clouds = remember {
        List(6) {
            CloudPuff(
                x = Random.nextFloat() * 1.2f,
                y = 0.08f + Random.nextFloat() * 0.25f,
                scale = 0.6f + Random.nextFloat() * 0.8f,
                speed = 0.6f + Random.nextFloat() * 0.4f,
                alpha = 0.55f + Random.nextFloat() * 0.35f
            )
        }
    }

    val stars = remember {
        List(60) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.55f,
                size = 0.8f + Random.nextFloat() * 2.0f,
                twinklePhase = Random.nextFloat() * 6.28f
            )
        }
    }

    val rainDrops = remember {
        List(140) {
            RainDrop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 0.5f,
                length = 20f + Random.nextFloat() * 35f,
                alpha = 0.4f + Random.nextFloat() * 0.4f
            )
        }
    }

    val birds = remember {
        List(5) { i ->
            Bird(
                x = 0f,
                y = 0.15f + (i - 2) * 0.04f + Random.nextFloat() * 0.02f,
                speed = 0.9f + Random.nextFloat() * 0.2f,
                flapPhase = Random.nextFloat() * 6.28f,
                size = 0.8f + Random.nextFloat() * 0.5f
            )
        }
    }

    // ============================================================
    // CANVAS UTAMA
    // ============================================================
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. LANGIT — gradient 4 warna
        drawRect(
            brush = Brush.verticalGradient(
                colors = skyColors,
                startY = 0f,
                endY = h * 0.78f
            ),
            size = Size(w, h)
        )

        // 2. BINTANG (malam cerah)
        if (isNight && !isRainy && !isStormy) {
            stars.forEach { star ->
                val twinkle = 0.3f + (sin(star.twinklePhase + starTwinkle * 3f) + 1f) * 0.35f
                drawCircle(
                    color = Color.White.copy(alpha = twinkle * starTwinkle),
                    radius = star.size * density,
                    center = Offset(star.x * w, star.y * h)
                )
            }
        }

        // 3. BULAN (malam)
        if (moonPos.isVisible && isNight) {
            drawMoon(
                position = Offset(
                    x = (moonPos.azimuth / 360f) * w,
                    y = h * (0.78f - moonPos.elevation / 90f * 0.65f)
                ),
                phase = moonPhase,
                radius = 26f * density
            )
        }

        // 4. MATAHARI (siang)
        if (sunPos.isVisible) {
            drawSun(
                position = Offset(
                    x = (sunPos.azimuth / 360f) * w,
                    y = h * (0.78f - sunPos.elevation / 90f * 0.65f)
                ),
                radius = 34f * density * sunPos.sizeScale,
                isGoldenHour = isGoldenHour
            )
        }

        // 5. AWAN BERGERAK
        clouds.forEach { cloud ->
            val cx = ((cloud.x + cloudProgress * cloud.speed) % 1.4f - 0.2f) * w
            val cy = cloud.y * h
            drawCloud(
                center = Offset(cx, cy),
                scale = cloud.scale * density,
                alpha = if (isRainy || isCloudy) {
                    (cloud.alpha * 1.2f).coerceAtMost(1f)
                } else cloud.alpha
            )
        }

        // 6. BURUNG TERBANG (siang saja, tidak saat hujan)
        if (!isNight && !isRainy && !isStormy) {
            birds.forEachIndexed { index, bird ->
                val bx = (birdProgress * w) + (index * 40f * density)
                val by = bird.y * h + sin(birdProgress * PI * 4).toFloat() * 20f * density
                drawBird(
                    position = Offset(bx, by),
                    size = bird.size * density,
                    flap = wingFlap,
                    alpha = 0.7f
                )
            }
        }

        // 7. KABUT SUBUH (jam 04:00-06:00)
        val hourNow = now.hour + now.minute / 60f
        if (hourNow in 4f..6f) {
            drawFog(w = w, h = h, density = density)
        }

        // 8. BANGUNAN MASJIDIL HARAM (siluet + arcade)
        drawBuilding(w = w, h = h, isNight = isNight, isGoldenHour = isGoldenHour)

        // 9. REFLEKSI MARMER di lantai Mataf
        if (sunPos.isVisible) {
            drawMarbleReflection(
                w = w,
                h = h,
                sunX = (sunPos.azimuth / 360f) * w,
                alpha = if (isGoldenHour) 0.35f else 0.2f
            )
        }

        // 10. HUJAN (kalau cuaca hujan)
        if (isRainy) {
            drawRain(drops = rainDrops, progress = rainProgress, w = w, h = h)
        }

        // 11. KILAT PETIR (overlay putih)
        if (isStormy && lightningFlash > 0.05f) {
            drawRect(
                color = Color.White.copy(alpha = lightningFlash * 0.5f),
                size = Size(w, h)
            )
        }

        // 12. GELAP MALAM OVERLAY
        if (isNight) {
            drawRect(
                color = Color(0xFF000000).copy(alpha = 0.15f),
                size = Size(w, h)
            )
        }
    }
}

// ============================================================
// BAGIAN 2 — SUN, MOON, CLOUD
// ============================================================
private fun DrawScope.drawSun(position: Offset, radius: Float, isGoldenHour: Boolean) {
    val coronaColor = if (isGoldenHour) Color(0xFFFFA726) else Color(0xFFFFF176)
    drawCircle(coronaColor.copy(alpha = 0.08f), radius * 3.5f, position)
    drawCircle(coronaColor.copy(alpha = 0.15f), radius * 2.2f, position)
    drawCircle(coronaColor.copy(alpha = 0.3f), radius * 1.5f, position)
    val coreColor = if (isGoldenHour) Color(0xFFFFD54F) else Color(0xFFFFF9C4)
    drawCircle(coreColor, radius, position)
    drawCircle(Color.White, radius * 0.5f, position)
}

private fun DrawScope.drawMoon(
    position: Offset,
    phase: SunMoonCalculator.MoonPhase,
    radius: Float
) {
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.1f), radius * 2f, position)
    drawCircle(Color(0xFFCFD8DC).copy(alpha = 0.15f), radius * 1.4f, position)
    drawCircle(Color(0xFFECEFF1), radius, position)

    // Kawah bulan
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.3f), radius * 0.18f,
        Offset(position.x - radius * 0.3f, position.y - radius * 0.2f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.25f), radius * 0.12f,
        Offset(position.x + radius * 0.35f, position.y + radius * 0.15f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.2f), radius * 0.15f,
        Offset(position.x - radius * 0.1f, position.y + radius * 0.4f))

    val phaseAngle = phase.phaseAngle
    val isWaxing = phaseAngle in 0f..180f

    when {
        phase.illumination < 0.1f -> {
            drawCircle(Color(0xFF1A1A1A), radius * 0.95f, position)
        }
        phase.illumination > 0.9f -> { /* purnama: kosong */ }
        else -> {
            val offsetX = if (isWaxing) {
                -radius * (2f * phase.illumination - 1f)
            } else {
                radius * (2f * phase.illumination - 1f)
            }
            val shadowPath = Path().apply {
                addOval(Rect(
                    position.x - radius + offsetX,
                    position.y - radius,
                    position.x + radius + offsetX,
                    position.y + radius
                ))
            }
            drawPath(shadowPath, Color(0xFF0A0A25).copy(alpha = 0.85f))
        }
    }
}

private fun DrawScope.drawCloud(center: Offset, scale: Float, alpha: Float) {
    val r = 40f * scale
    val c = Color.White.copy(alpha = alpha * 0.85f)
    drawCircle(c, r * 0.9f, Offset(center.x - r * 0.8f, center.y + r * 0.1f))
    drawCircle(c, r * 1.1f, Offset(center.x - r * 0.2f, center.y - r * 0.15f))
    drawCircle(c, r * 1.0f, Offset(center.x + r * 0.6f, center.y))
    drawCircle(c, r * 0.75f, Offset(center.x - r * 1.3f, center.y + r * 0.2f))
    drawCircle(c, r * 0.7f, Offset(center.x + r * 1.2f, center.y + r * 0.15f))
    val s = Color(0xFF9E9E9E).copy(alpha = alpha * 0.4f)
    drawCircle(s, r * 0.6f, Offset(center.x - r * 0.5f, center.y + r * 0.5f))
    drawCircle(s, r * 0.7f, Offset(center.x + r * 0.3f, center.y + r * 0.45f))
}

// ============================================================
// BAGIAN 3 — BIRD, FOG, RAIN, MARBLE REFLECTION
// ============================================================
private fun DrawScope.drawBird(position: Offset, size: Float, flap: Float, alpha: Float) {
    val wingSpan = 14f * size
    val wingOffset = flap * 6f * size
    val path = Path().apply {
        moveTo(position.x - wingSpan, position.y - wingOffset)
        quadraticTo(position.x - wingSpan * 0.5f, position.y - 2f, position.x, position.y)
        quadraticTo(position.x + wingSpan * 0.5f, position.y - 2f, position.x + wingSpan, position.y - wingOffset)
    }
    drawPath(path, Color(0xFF1A1A1A).copy(alpha = alpha), style = Stroke(width = 2.2f * size))
}

private fun DrawScope.drawFog(w: Float, h: Float, density: Float) {
    val fogY = h * 0.7f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0f),
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.35f)
            ),
            startY = fogY - 100f * density,
            endY = h
        ),
        topLeft = Offset(0f, fogY - 100f * density),
        size = Size(w, h - fogY + 100f * density)
    )
}

private fun DrawScope.drawRain(drops: List<RainDrop>, progress: Float, w: Float, h: Float) {
    drops.forEach { drop ->
        val currentY = ((drop.y + progress * drop.speed) % 1.2f) * h
        val x = drop.x * w
        drawLine(
            color = Color(0xFF90CAF9).copy(alpha = drop.alpha),
            start = Offset(x, currentY),
            end = Offset(x - 8f, currentY + drop.length),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawMarbleReflection(w: Float, h: Float, sunX: Float, alpha: Float) {
    val floorTop = h * 0.85f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFE082).copy(alpha = 0f),
                Color(0xFFFFE082).copy(alpha = alpha * 0.5f),
                Color(0xFFFFE082).copy(alpha = alpha)
            ),
            startY = floorTop,
            endY = h
        ),
        topLeft = Offset(sunX - w * 0.15f, floorTop),
        size = Size(w * 0.3f, h - floorTop)
    )
}

// ============================================================
// BAGIAN 4 — BUILDING MASJIDIL HARAM
// ============================================================
private fun DrawScope.drawBuilding(w: Float, h: Float, isNight: Boolean, isGoldenHour: Boolean) {
    val buildingTop = h * 0.62f
    val buildingBottom = h

    // Warna base building
    val buildingColor = when {
        isNight -> Color(0xFF0A0A15)
        isGoldenHour -> Color(0xFF3D2E1F)
        else -> Color(0xFF4A4034)
    }

    // Base
    drawRect(
        color = buildingColor,
        topLeft = Offset(0f, buildingTop),
        size = Size(w, buildingBottom - buildingTop)
    )

    // Arcade lengkungan
    val arcadeCount = 12
    val arcadeWidth = w / arcadeCount
    val arcadeTop = buildingTop + (buildingBottom - buildingTop) * 0.15f
    val arcadeBottom = buildingTop + (buildingBottom - buildingTop) * 0.55f
    val arcadeColor = if (isNight) {
        Color(0xFFFFE082).copy(alpha = 0.6f)
    } else {
        Color(0xFFE8DCC8).copy(alpha = 0.3f)
    }

    for (i in 0 until arcadeCount) {
        val x = i * arcadeWidth + arcadeWidth * 0.15f
        val archWidth = arcadeWidth * 0.7f
        drawRoundRect(
            color = arcadeColor,
            topLeft = Offset(x, arcadeTop),
            size = Size(archWidth, arcadeBottom - arcadeTop),
            cornerRadius = CornerRadius(archWidth / 2f, archWidth / 2f)
        )
    }

    // Menara / minaret
    val minaretColor = if (isNight) Color(0xFF1A1A25) else Color(0xFF5A5040)
    val minaretWidth = 28f * density

    // Menara kiri
    drawRect(
        color = minaretColor,
        topLeft = Offset(w * 0.15f, buildingTop - 80f * density),
        size = Size(minaretWidth, 120f * density)
    )
    drawCircle(
        color = minaretColor,
        radius = minaretWidth / 2f,
        center = Offset(w * 0.15f + minaretWidth / 2f, buildingTop - 80f * density)
    )

    // Menara kanan
    drawRect(
        color = minaretColor,
        topLeft = Offset(w * 0.82f, buildingTop - 80f * density),
        size = Size(minaretWidth, 120f * density)
    )
    drawCircle(
        color = minaretColor,
        radius = minaretWidth / 2f,
        center = Offset(w * 0.82f + minaretWidth / 2f, buildingTop - 80f * density)
    )

    // Kubah Ka'bah (hitam pekat dengan pita emas)
    val kaabahWidth = w * 0.35f
    val kaabahHeight = (buildingBottom - buildingTop) * 0.55f
    val kaabahLeft = (w - kaabahWidth) / 2f
    val kaabahTop = buildingTop + (buildingBottom - buildingTop) * 0.05f

    // Ka'bah (kotak hitam)
    drawRect(
        color = Color(0xFF0A0A0A),
        topLeft = Offset(kaabahLeft, kaabahTop),
        size = Size(kaabahWidth, kaabahHeight)
    )

    // Pita emas (Hizam) di tengah Ka'bah
    val hizamY = kaabahTop + kaabahHeight * 0.4f
    val hizamHeight = 6f * density
    drawRect(
        color = Color(0xFFFFD700),
        topLeft = Offset(kaabahLeft, hizamY),
        size = Size(kaabahWidth, hizamHeight)
    )

    // Kilau emas saat siang
    if (!isNight) {
        drawRect(
            color = Color(0xFFFFF176).copy(alpha = 0.3f),
            topLeft = Offset(kaabahLeft, hizamY - 1f),
            size = Size(kaabahWidth, hizamHeight + 2f)
        )
    }

    // Lampu arcade menyala saat malam (twinkle kecil)
    if (isNight) {
        for (i in 0 until arcadeCount) {
            val x = i * arcadeWidth + arcadeWidth * 0.5f
            val y = arcadeTop + (arcadeBottom - arcadeTop) * 0.3f
            drawCircle(
                color = Color(0xFFFFF9C4).copy(alpha = 0.7f),
                radius = 2.5f * density,
                center = Offset(x, y)
            )
        }
    }
}
