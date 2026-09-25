package com.example.data.local

import com.example.data.model.PrayerId
import com.example.data.model.PrayerItem
import com.example.data.model.PrayerSchedule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import kotlin.math.*

object PrayerTimesCalculator {

    /**
     * Calculates prayer times using Indonesian Ministry of Religious Affairs (Kemenag)
     * astronomical equations.
     */
    fun calculate(
        date: LocalDate = LocalDate.now(),
        latitude: Double = -6.1754,
        longitude: Double = 106.8272,
        timezoneOffset: Double = 7.0
    ): PrayerSchedule {
        val dayOfYear = date.dayOfYear

        // Julian date
        val d = 367.0 * date.year - ((7.0 * (date.year + ((date.monthValue + 9) / 12))) / 4.0).toInt() +
                ((275 * date.monthValue) / 9) + date.dayOfMonth - 730531.5

        // Sun's mean anomaly & longitude
        val m = Math.toRadians((357.529 + 0.98560028 * d) % 360.0)
        val l = Math.toRadians((280.459 + 0.98564736 * d) % 360.0)
        val lambda = l + Math.toRadians(1.915 * sin(m) + 0.020 * sin(2 * m))

        // Obliquity of ecliptic
        val epsilon = Math.toRadians(23.439 - 0.00000036 * d)

        // Right ascension & declination
        val alpha = atan2(cos(epsilon) * sin(lambda), cos(lambda))
        val delta = asin(sin(epsilon) * sin(lambda)) // Declination

        // Equation of Time in hours
        var eot = (l - alpha) / (2.0 * Math.PI) * 24.0
        while (eot < -12.0) eot += 24.0
        while (eot > 12.0) eot -= 24.0

        // Solar noon (transit) in local standard time
        val transit = 12.0 + timezoneOffset - (longitude / 15.0) - eot

        val latRad = Math.toRadians(latitude)

        // Helper to calculate hour angle for given solar altitude angle
        fun hourAngle(altitudeDeg: Double): Double {
            val altRad = Math.toRadians(altitudeDeg)
            val cosH = (sin(altRad) - sin(latRad) * sin(delta)) / (cos(latRad) * cos(delta))
            val clamped = cosH.coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(clamped)) / 15.0
        }

        // Kemenag standard parameters:
        // Subuh: -20 degrees
        // Sunrise/Syuruq: -0.833 degrees
        // Ashar (Shafi'i): shadow multiplier = 1
        // Maghrib/Sunset: -0.833 degrees
        // Isya: -18 degrees
        // Ihtiyat (safety margin): +2 minutes for Dzuhur, Subuh, Ashar, Maghrib, Isya

        val subuhHA = hourAngle(-20.0)
        val sunriseHA = hourAngle(-0.833)

        // Ashar altitude calculation
        val noonAlt = (Math.PI / 2.0) - abs(latRad - delta)
        val noonShadow = 1.0 / tan(noonAlt)
        val asharShadow = 1.0 + noonShadow // Shafi'i: 1 * object height + noon shadow
        val asharAltRad = atan(1.0 / asharShadow)
        val asharHA = hourAngle(Math.toDegrees(asharAltRad))

        val sunsetHA = sunriseHA
        val isyaHA = hourAngle(-18.0)

        // Add 2-3 minutes ihtiyat (kemenag practice)
        val ihtiyatHours = 2.0 / 60.0

        val subuhDecimal = transit - subuhHA + ihtiyatHours
        val syuruqDecimal = transit - sunriseHA
        val dzuhurDecimal = transit + ihtiyatHours
        val asharDecimal = transit + asharHA + ihtiyatHours
        val maghribDecimal = transit + sunsetHA + ihtiyatHours
        val isyaDecimal = transit + isyaHA + ihtiyatHours
        val imsakDecimal = subuhDecimal - (10.0 / 60.0)
        val dhuhaDecimal = syuruqDecimal + (25.0 / 60.0)

        fun formatHours(decimalHours: Double): String {
            var h = decimalHours
            while (h < 0) h += 24.0
            while (h >= 24) h -= 24.0
            val hours = h.toInt()
            val minutes = ((h - hours) * 60).roundToInt()
            val adjustedHours = if (minutes >= 60) (hours + 1) % 24 else hours
            val adjustedMinutes = if (minutes >= 60) minutes - 60 else minutes
            return String.format("%02d:%02d", adjustedHours, adjustedMinutes)
        }

        fun toTargetMillis(date: LocalDate, timeStr: String): Long {
            val parts = timeStr.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 12
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val dt = LocalDateTime.of(date, LocalTime.of(h, m, 0))
            return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        val subuhStr = formatHours(subuhDecimal)
        val syuruqStr = formatHours(syuruqDecimal)
        val dzuhurStr = formatHours(dzuhurDecimal)
        val asharStr = formatHours(asharDecimal)
        val maghribStr = formatHours(maghribDecimal)
        val isyaStr = formatHours(isyaDecimal)
        val imsakStr = formatHours(imsakDecimal)
        val dhuhaStr = formatHours(dhuhaDecimal)

        val nowMillis = System.currentTimeMillis()

        val rawItems = listOf(
            PrayerItem(PrayerId.SUBUH, subuhStr, toTargetMillis(date, subuhStr)),
            PrayerItem(PrayerId.SYURUQ, syuruqStr, toTargetMillis(date, syuruqStr)),
            PrayerItem(PrayerId.DZUHUR, dzuhurStr, toTargetMillis(date, dzuhurStr)),
            PrayerItem(PrayerId.ASHAR, asharStr, toTargetMillis(date, asharStr)),
            PrayerItem(PrayerId.MAGHRIB, maghribStr, toTargetMillis(date, maghribStr)),
            PrayerItem(PrayerId.ISYA, isyaStr, toTargetMillis(date, isyaStr))
        )

        // Find next prayer and previous prayer for real-time progress bar
        var nextIndex = rawItems.indexOfFirst { it.targetTimeMillis > nowMillis }
        val isAllPassedToday = nextIndex == -1
        if (isAllPassedToday) {
            nextIndex = 0 // Next will be tomorrow's Subuh
        }

        val prevIndex = if (nextIndex > 0) nextIndex - 1 else rawItems.lastIndex

        val items = rawItems.mapIndexed { idx, it ->
            val isNext = idx == nextIndex
            val isPassed = it.targetTimeMillis <= nowMillis && !isAllPassedToday
            val isActive = abs(nowMillis - it.targetTimeMillis) <= (15 * 60 * 1000) // Within 15 mins
            it.copy(isNext = isNext, isPassed = isPassed, isActive = isActive)
        }

        val nextItem = items.getOrNull(nextIndex)
        val prevItem = items.getOrNull(prevIndex)

        // Calculate progress percentage between previous and next prayer
        val nextTargetMillis = if (isAllPassedToday) {
            nextItem?.targetTimeMillis?.plus(24 * 3600 * 1000L) ?: nowMillis
        } else {
            nextItem?.targetTimeMillis ?: nowMillis
        }

        val prevTargetMillis = if (nextIndex == 0 && !isAllPassedToday) {
            // Previous was yesterday's Isya
            (items.lastOrNull()?.targetTimeMillis ?: nowMillis) - (24 * 3600 * 1000L)
        } else {
            prevItem?.targetTimeMillis ?: nowMillis
        }

        val totalSpan = (nextTargetMillis - prevTargetMillis).coerceAtLeast(1L)
        val elapsed = (nowMillis - prevTargetMillis).coerceIn(0L, totalSpan)
        val progress = (elapsed.toFloat() / totalSpan.toFloat()).coerceIn(0f, 1f)

        val secondsRemaining = ((nextTargetMillis - nowMillis) / 1000L).coerceAtLeast(0L)

        return PrayerSchedule(
            imsak = imsakStr,
            subuh = subuhStr,
            syuruq = syuruqStr,
            dhuha = dhuhaStr,
            dzuhur = dzuhurStr,
            ashar = asharStr,
            maghrib = maghribStr,
            isya = isyaStr,
            items = items,
            nextPrayer = nextItem,
            previousPrayer = prevItem,
            progressToNext = progress,
            secondsToNext = secondsRemaining
        )
    }
}
