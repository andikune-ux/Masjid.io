package dev.andikune.masjidio.data.local

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * SunMoonCalculator — Hitung posisi matahari, bulan, dan fase bulan.
 *
 * Digunakan oleh MakkahDynamicBackground untuk menampilkan langit dinamis
 * yang mengikuti waktu real-time perangkat.
 *
 * Tidak perlu akurasi astronomi tinggi — cukup untuk efek visual yang masuk akal.
 */
object SunMoonCalculator {

    // ============================================================
    // DATA CLASS
    // ============================================================
    data class CelestialBody(
        val azimuth: Float,
        val elevation: Float,
        val isVisible: Boolean,
        val sizeScale: Float = 1f
    )

    data class MoonPhase(
        val ageDays: Float,
        val illumination: Float,
        val phaseAngle: Float,
        val phaseName: String
    )

    // ============================================================
    // KONSTANTA
    // ============================================================
    private val KNOWN_NEW_MOON: LocalDateTime = LocalDateTime.of(2000, 1, 6, 18, 14)
    private const val SYNODIC_MONTH_DAYS = 29.530588853

    // ============================================================
    // POSISI MATAHARI
    // ============================================================
    fun getSunPosition(
        dateTime: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): CelestialBody {
        val hour = dateTime.hour + dateTime.minute / 60f + dateTime.second / 3600f

        val maxElevation = (90f - abs(latitude.toFloat() - 23.44f)).coerceIn(15f, 90f)
        val dayProgress = (hour - 6f) / 12f

        val elevation: Float
        val azimuth: Float
        val isVisible: Boolean

        if (dayProgress in 0f..1f) {
            elevation = (sin(dayProgress * PI).toFloat() * maxElevation).coerceAtLeast(0f)
            azimuth = 90f + dayProgress * 180f
            isVisible = true
        } else if (hour < 6f) {
            elevation = -30f
            azimuth = 90f
            isVisible = false
        } else {
            elevation = -30f
            azimuth = 270f
            isVisible = false
        }

        val sizeScale = if (isVisible) {
            1f + (1f - elevation / maxElevation).coerceIn(0f, 1f) * 0.25f
        } else 1f

        return CelestialBody(
            azimuth = azimuth.coerceIn(0f, 360f),
            elevation = elevation,
            isVisible = isVisible,
            sizeScale = sizeScale
        )
    }

    // ============================================================
    // POSISI BULAN
    // ============================================================
    fun getMoonPosition(
        dateTime: LocalDateTime,
        latitude: Double,
        longitude: Double
    ): CelestialBody {
        val hour = dateTime.hour + dateTime.minute / 60f + dateTime.second / 3600f
        val nightHour = if (hour < 6f) hour + 24f else hour
        val isNight = nightHour in 18f..30f
        val maxElevation = 70f

        val elevation: Float
        val azimuth: Float

        if (isNight) {
            val nightProgress = (nightHour - 18f) / 12f
            elevation = (sin(nightProgress * PI).toFloat() * maxElevation).coerceAtLeast(0f)
            azimuth = 90f + nightProgress * 180f
        } else {
            elevation = -30f
            azimuth = if (hour < 12f) 90f else 270f
        }

        return CelestialBody(
            azimuth = azimuth.coerceIn(0f, 360f),
            elevation = elevation,
            isVisible = elevation > 0f,
            sizeScale = 1f
        )
    }

    // ============================================================
    // FASE BULAN
    // ============================================================
    fun getMoonPhase(dateTime: LocalDateTime): MoonPhase {
        val secondsSince = java.time.Duration.between(KNOWN_NEW_MOON, dateTime).seconds.toDouble()
        val daysSince = secondsSince / 86400.0
        val age = ((daysSince % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS
        val phaseAngle = age / SYNODIC_MONTH_DAYS * 360.0
        val illumination = ((1.0 - cos(Math.toRadians(phaseAngle))) / 2.0).toFloat()

        val name = when {
            phaseAngle < 22.5 || phaseAngle > 337.5 -> "Bulan Baru"
            phaseAngle < 67.5 -> "Sabit Muda"
            phaseAngle < 112.5 -> "Kuartal Pertama"
            phaseAngle < 157.5 -> "Cembung Awal"
            phaseAngle < 202.5 -> "Purnama"
            phaseAngle < 247.5 -> "Cembung Akhir"
            phaseAngle < 292.5 -> "Kuartal Akhir"
            else -> "Sabit Tua"
        }

        return MoonPhase(
            ageDays = age.toFloat(),
            illumination = illumination,
            phaseAngle = phaseAngle.toFloat(),
            phaseName = name
        )
    }

    // ============================================================
    // WARNA LANGIT
    // ============================================================
    fun getSkyGradient(dateTime: LocalDateTime): List<Color> {
        val hour = dateTime.hour + dateTime.minute / 60f

        return when {
            hour in 4f..6f -> listOf(
                Color(0xFF0A1128),
                Color(0xFF1C2541),
                Color(0xFF4A3B5B),
                Color(0xFFE89B5B)
            )
            hour in 6f..9f -> listOf(
                Color(0xFF3B7DBF),
                Color(0xFF6BA3D6),
                Color(0xFFA8C8E8),
                Color(0xFFE0D5B0)
            )
            hour in 9f..15f -> listOf(
                Color(0xFF1E5F9E),
                Color(0xFF4A8FC7),
                Color(0xFF87B8DD),
                Color(0xFFD6E7F5)
            )
            hour in 15f..17.5f -> listOf(
                Color(0xFF2C5F8F),
                Color(0xFF6E96B8),
                Color(0xFFC9A878),
                Color(0xFFE8B575)
            )
            hour in 17.5f..19f -> listOf(
                Color(0xFF2A1F3D),
                Color(0xFF5B3A4D),
                Color(0xFFB87B5B),
                Color(0xFFE89B5B)
            )
            hour in 19f..20.5f -> listOf(
                Color(0xFF0D0B1F),
                Color(0xFF1A1740),
                Color(0xFF2A2555),
                Color(0xFF3D3568)
            )
            else -> listOf(
                Color(0xFF050510),
                Color(0xFF0A0A25),
                Color(0xFF131340),
                Color(0xFF1E1E50)
            )
        }
    }

    fun isNightTime(dateTime: LocalDateTime): Boolean {
        val hour = dateTime.hour + dateTime.minute / 60f
        return hour < 6f || hour >= 18.5f
    }

    fun isGoldenHour(dateTime: LocalDateTime): Boolean {
        val hour = dateTime.hour + dateTime.minute / 60f
        return hour in 5.5f..7f || hour in 17f..18.5f
    }

    // ============================================================
    // SUNRISE & SUNSET
    // ============================================================
    fun getSunriseSunset(
        latitude: Double,
        dateTime: LocalDateTime
    ): Pair<Float, Float> {
        val dayOfYear = dateTime.dayOfYear
        val declination = 23.44 * sin(Math.toRadians(360.0 / 365.0 * (dayOfYear - 81)))
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)

        val cosH = -tan(latRad) * tan(decRad)
        val clamped = cosH.coerceIn(-1.0, 1.0)
        val hourAngle = Math.toDegrees(acos(clamped)) / 15.0

        val sunrise = (12.0 - hourAngle).toFloat()
        val sunset = (12.0 + hourAngle).toFloat()

        return Pair(sunrise, sunset)
    }
}
