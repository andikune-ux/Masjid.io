package com.example.data.local

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class HijriDate(
    val day: Int,
    val month: Int,
    val monthName: String,
    val year: Int
)

data class IslamicEvent(
    val title: String,
    val hijriDay: Int,
    val hijriMonth: Int,
    val description: String,
    val daysRemaining: Long,
    val isUrgent: Boolean // < 7 days
)

object IslamicCalendar {
    private val HIJRI_MONTHS = listOf(
        "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
        "Jumadil Awwal", "Jumadil Akhir", "Rajab", "Sya'ban",
        "Ramadhan", "Syawwal", "Dzulqa'dah", "Dzulhijjah"
    )

    private val INDO_DAYS = listOf(
        "Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Ahad"
    )

    private val INDO_MONTHS = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    // Standard epoch conversion Kuweit / Umm al-Qura algorithm
    fun getHijriDate(date: LocalDate = LocalDate.now()): HijriDate {
        val jd = gregorianToJulianDay(date.year, date.monthValue, date.dayOfMonth)
        val l = (jd - 1948440 + 10632).toInt()
        val n = ((l - 1) / 10631).toInt()
        val lPrime = l - 10631 * n + 354
        val j = (((10985 - lPrime) / 5316)).toInt() * ((50 * lPrime) / 17719).toInt() +
                (lPrime / 5670).toInt() * ((43 * lPrime) / 15238).toInt()
        val lDoublePrime = lPrime - (((30 - j) / 15)).toInt() * ((17719 * j) / 50).toInt() -
                (j / 16).toInt() * ((15238 * j) / 43).toInt() + 29
        val m = ((24 * lDoublePrime) / 709).toInt()
        val day = lDoublePrime - ((709 * m) / 24).toInt()
        val year = 30 * n + j - 30

        val validMonth = ((m - 1).coerceIn(0, 11))
        val validDay = day.coerceIn(1, 30)

        return HijriDate(
            day = validDay,
            month = validMonth + 1,
            monthName = HIJRI_MONTHS[validMonth],
            year = year
        )
    }

    private fun gregorianToJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = (y / 100).toInt()
        val b = 2 - a + (a / 4).toInt()
        return (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + day + b - 1524.5
    }

    fun getUpcomingEvent(currentHijri: HijriDate): IslamicEvent {
        // Defined events:
        // 1 Muharram: Tahun Baru Hijriah
        // 10 Muharram: Asyura
        // 12 Rabiul Awal: Maulid Nabi Muhammad SAW
        // 27 Rajab: Isra Mi'raj
        // 15 Sya'ban: Nisfu Sya'ban
        // 1 Ramadhan: Awal Ramadhan
        // 17 Ramadhan: Nuzulul Qur'an
        // 1 Syawal: Hari Raya Idul Fitri
        // 9 Dzulhijjah: Hari Arafah
        // 10 Dzulhijjah: Hari Raya Idul Adha

        val eventList = listOf(
            Triple("Tahun Baru 1 Muharram", 1, 1),
            Triple("Hari Asyura (10 Muharram)", 10, 1),
            Triple("Maulid Nabi Muhammad SAW", 12, 3),
            Triple("Isra Mi'raj Nabi Muhammad SAW", 27, 7),
            Triple("Malam Nisfu Sya'ban", 15, 8),
            Triple("Awal Ramadhan 1447 H", 1, 9),
            Triple("Nuzulul Qur'an (17 Ramadhan)", 17, 9),
            Triple("Hari Raya Idul Fitri 1 Syawal", 1, 10),
            Triple("Hari Arafah (9 Dzulhijjah)", 9, 12),
            Triple("Hari Raya Idul Adha 10 Dzulhijjah", 10, 12)
        )

        // Approximate days remaining in Islamic calendar year
        val currentDaysInYear = (currentHijri.month - 1) * 29.5 + currentHijri.day

        var selectedEvent: Triple<String, Int, Int>? = null
        var minDiff = 999.0

        for (ev in eventList) {
            val evDaysInYear = (ev.third - 1) * 29.5 + ev.second
            var diff = evDaysInYear - currentDaysInYear
            if (diff < 0) {
                diff += 354.0 // next hijri year
            }
            if (diff < minDiff) {
                minDiff = diff
                selectedEvent = ev
            }
        }

        val finalEvent = selectedEvent ?: eventList[2]
        val days = minDiff.toLong().coerceAtLeast(1)

        return IslamicEvent(
            title = finalEvent.first,
            hijriDay = finalEvent.second,
            hijriMonth = finalEvent.third,
            description = "Peringatan Hari Besar Islam",
            daysRemaining = days,
            isUrgent = days < 7
        )
    }

    fun formatIndonesianDate(date: LocalDate = LocalDate.now()): String {
        val dayName = INDO_DAYS[(date.dayOfWeek.value - 1) % 7]
        val monthName = INDO_MONTHS[date.monthValue - 1]
        return "$dayName, ${date.dayOfMonth} $monthName ${date.year}"
    }

    fun formatHijriDateString(hijriDate: HijriDate): String {
        return "${hijriDate.day} ${hijriDate.monthName} ${hijriDate.year} H"
    }
}
