package com.example.data.local

import com.example.ui.theme.*
import java.time.LocalTime

object DynamicSkyTheme {
    /**
     * Determines the sky colors based on the REAL-TIME hour and minute of the day.
     * Prevents daytime (e.g. 15:13) from being dark!
     */
    fun getSkyColorsForTime(now: LocalTime = LocalTime.now()): List<androidx.compose.ui.graphics.Color> {
        val totalMinutes = now.hour * 60 + now.minute

        return when {
            // Fajar / Subuh (04:00 - 05:30)
            totalMinutes in 240..330 -> listOf(SkySubuh1, SkySubuh2)

            // Pagi / Syuruq & Dhuha (05:31 - 11:30) -> Fresh morning blue & golden amber
            totalMinutes in 331..690 -> listOf(
                androidx.compose.ui.graphics.Color(0xFF0D3B66),
                androidx.compose.ui.graphics.Color(0xFF1E5F74)
            )

            // Siang / Dzuhur (11:31 - 15:00) -> Bright sunny daylight sky
            totalMinutes in 691..900 -> listOf(SkyDzuhur1, SkyDzuhur2)

            // Sore / Ashar (15:01 - 17:45) -> Warm afternoon golden-hour sky (NOT dark!)
            totalMinutes in 901..1065 -> listOf(
                androidx.compose.ui.graphics.Color(0xFF1E3A5F),
                androidx.compose.ui.graphics.Color(0xFF8B5E3C)
            )

            // Senja / Maghrib (17:46 - 19:00) -> Warm sunset orange-crimson dusk
            totalMinutes in 1066..1140 -> listOf(SkyMaghrib1, SkyMaghrib2)

            // Malam / Isya (19:01 - 03:59) -> Deep calm midnight
            else -> listOf(SkyIsya1, SkyIsya2)
        }
    }

    fun getSkyBrush(now: LocalTime = LocalTime.now()): androidx.compose.ui.graphics.Brush {
        return androidx.compose.ui.graphics.Brush.verticalGradient(getSkyColorsForTime(now))
    }

    fun getTimePeriodName(now: LocalTime = LocalTime.now()): String {
        val totalMinutes = now.hour * 60 + now.minute
        return when {
            totalMinutes in 240..330 -> "Waktu Subuh"
            totalMinutes in 331..690 -> "Pagi Hari"
            totalMinutes in 691..900 -> "Siang Hari"
            totalMinutes in 901..1065 -> "Sore Hari"
            totalMinutes in 1066..1140 -> "Waktu Senja"
            else -> "Malam Hari"
        }
    }
}
