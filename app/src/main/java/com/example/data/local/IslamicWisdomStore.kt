package com.example.data.local

import com.example.data.model.AppSettings

data class WisdomCardItem(
    val title: String,
    val arabic: String,
    val translation: String,
    val source: String,
    val category: String // "Asmaul Husna", "Hadits", "Al-Qur'an", "Adab Masjid", "Doa Harian", "Sholat Sunnah"
)

object IslamicWisdomStore {
    val items: List<WisdomCardItem> = listOf(
        // === ASMAUL HUSNA ===
        WisdomCardItem(
            title = "Asmaul Husna: Ar-Rahman (الرَّحْمٰنُ)",
            arabic = "الرَّحْمٰنُ عَلَى الْعَرْشِ اسْتَوَىٰ",
            translation = "Yang Maha Pengasih • Kasih sayang Allah meliputi seluruh makhluk-Nya di alam semesta tanpa terkecuali.",
            source = "QS. Thaha: 5",
            category = "Asmaul Husna"
        ),
        WisdomCardItem(
            title = "Asmaul Husna: Ar-Rahim (الرَّحِيمُ)",
            arabic = "وَكَانَ بِالْمُؤْمِنِينَ رَحِيمًا",
            translation = "Yang Maha Penyayang • Kasih sayang khusus dan abadi yang Allah limpahkan kepada hamba-hamba-Nya yang beriman.",
            source = "QS. Al-Ahzab: 43",
            category = "Asmaul Husna"
        ),
        WisdomCardItem(
            title = "Asmaul Husna: Al-Malik (الْمَلِكُ)",
            arabic = "فَتَعَالَى اللَّهُ الْمَلِكُ الْحَقُّ",
            translation = "Yang Maha Merajai • Penguasa mutlak langit dan bumi, pemilik kerajaan sejati yang tidak pernah binasa.",
            source = "QS. Thaha: 114",
            category = "Asmaul Husna"
        ),
        WisdomCardItem(
            title = "Asmaul Husna: Al-Quddus (الْقُدُّوسُ)",
            arabic = "الْمَلِكِ الْقُدُّوسِ الْعَزِيزِ الْحَكِيمِ",
            translation = "Yang Maha Suci • Bersih dari segala cacat, kekurangan, dan keserupaan dengan makhluk-Nya.",
            source = "QS. Al-Jumu'ah: 1",
            category = "Asmaul Husna"
        ),
        WisdomCardItem(
            title = "Asmaul Husna: As-Salam (السَّلَامُ)",
            arabic = "السَّلَامُ الْمُؤْمِنُ الْمُهَيْمِنُ",
            translation = "Yang Maha Memberi Kesejahteraan • Sumber kedamaian sejati, keselamatan di dunia dan akhirat.",
            source = "QS. Al-Hasyr: 23",
            category = "Asmaul Husna"
        ),
        WisdomCardItem(
            title = "Asmaul Husna: Al-Ghaffar (الْغَفَّارُ)",
            arabic = "وَإِنِّي لَغَفَّارٌ لِّمَن تَابَ وَآمَنَ وَعَمِلَ صَالِحًا",
            translation = "Yang Maha Pengampun • Senantiasa menutupi dosa dan memaafkan hamba-Nya yang bertaubat dengan tulus.",
            source = "QS. Thaha: 82",
            category = "Asmaul Husna"
        ),

        // === PENGINGAT SHOLAT SUNNAH (RAWATIB) ===
        WisdomCardItem(
            title = "Keutamaan Sunnah Qobliyah Subuh",
            arabic = "رَكْعَتَا الْفَجْرِ خَيْرٌ مِنَ الدُّنْيَا وَمَا فِيهَا",
            translation = "\"Dua rakaat sunnah sebelum sholat Subuh (Qobliyah) itu lebih baik daripada dunia dan seisinya.\"",
            source = "HR. Muslim no. 725",
            category = "Sholat Sunnah"
        ),
        WisdomCardItem(
            title = "Keutamaan 12 Rakaat Rawatib",
            arabic = "مَنْ صَلَّى اثْنَتَيْ عَشْرَةَ رَكْعَةً فِي يَوْمٍ وَلَيْلَةٍ بُنِيَ لَهُ بِهِنَّ بَيْتٌ فِي الْجَنَّةِ",
            translation = "\"Barangsiapa sholat 12 rakaat sunnah sehari semalam (rawatib mu'akkad), niscaya dibangunkan baginya rumah di surga.\"",
            source = "HR. Muslim no. 728",
            category = "Sholat Sunnah"
        ),
        WisdomCardItem(
            title = "Sunnah Rawatib Dzuhur & Ba'diyah",
            arabic = "أَرْبَعًا قَبْلَ الظُّهْرِ وَرَكْعَتَيْنِ بَعْدَهَا",
            translation = "\"Empat rakaat sebelum Dzuhur dan dua rakaat sesudahnya; dua rakaat setelah Maghrib, dua rakaat setelah Isya, dan dua rakaat sebelum Subuh.\"",
            source = "HR. Tirmidzi & An-Nasa'i",
            category = "Sholat Sunnah"
        ),

        // === HADITS SHAHIH HARIAN ===
        WisdomCardItem(
            title = "Keutamaan Sholat Berjamaah di Masjid",
            arabic = "صَلَاةُ الْجَمَاعَةِ أَفْضَلُ مِنْ صَلَاةِ الْفَذِّ بِسَبْعٍ وَعِشْرِينَ دَرَجَةً",
            translation = "\"Sholat berjamaah itu lebih utama dua puluh tujuh derajat dibandingkan dengan sholat sendirian.\"",
            source = "HR. Bukhari no. 645 & Muslim no. 650",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Pahala Langkah Kaki ke Masjid",
            arabic = "مَنْ غَدَا إِلَى الْمَسْجِدِ أَوْ رَاحَ أَعَدَّ اللَّهُ لَهُ فِي الْجَنَّةِ نُزُلًا كُلَّمَا غَدَا أَوْ رَاحَ",
            translation = "\"Barangsiapa pergi ke masjid di pagi atau petang hari, Allah menyiapkan baginya tempat singgah di surga setiap kali ia melangkah.\"",
            source = "HR. Bukhari no. 662 & Muslim no. 669",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Sedekah yang Tidak Mengurangi Harta",
            arabic = "مَا نَقَصَتْ صَدَقَةٌ مِنْ مَالٍ وَمَا زَادَ اللَّهُ عَبْدًا بِعَفْوٍ إِلَّا عِزًّا",
            translation = "\"Sedekah tidaklah mengurangi harta, dan tidaklah Allah menambah bagi seorang hamba yang suka memaafkan melainkan kemuliaan.\"",
            source = "HR. Muslim no. 2588",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Kewajiban Menjaga Lisan & Perilaku",
            arabic = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
            translation = "\"Seorang muslim sejati adalah orang yang mana muslim lainnya merasa selamat dari lisan dan tangannya.\"",
            source = "HR. Bukhari no. 10 & Muslim no. 40",
            category = "Hadits"
        ),

        // === AYAT AL-QUR'AN HARIAN ===
        WisdomCardItem(
            title = "Menegakkan Sholat & Ketenangan Jiwa",
            arabic = "إِنَّ الصَّلَاةَ تَنْهَىٰ عَنِ الْفَحْشَاءِ وَالْمُنكَرِ ۗ وَلَذِكْرُ اللَّهِ أَكْبَرُ",
            translation = "\"Sesungguhnya sholat itu mencegah dari (perbuatan-perbuatan) keji dan mungkar. Dan sesungguhnya mengingat Allah (sholat) adalah lebih besar (keutamaannya).\"",
            source = "QS. Al-'Ankabut: 45",
            category = "Al-Qur'an"
        ),
        WisdomCardItem(
            title = "Janji Kemudahan Bersama Kesulitan",
            arabic = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            translation = "\"Maka sesungguhnya beserta kesulitan ada kemudahan, sesungguhnya beserta kesulitan ada kemudahan.\"",
            source = "QS. Al-Insyirah: 5-6",
            category = "Al-Qur'an"
        ),
        WisdomCardItem(
            title = "Memperbanyak Dzikir & Mengingat Allah",
            arabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            translation = "\"Ingatlah, hanya dengan mengingat Allah hati menjadi tenteram.\"",
            source = "QS. Ar-Ra'd: 28",
            category = "Al-Qur'an"
        ),
        WisdomCardItem(
            title = "Berinfaq di Jalan Kebaikan",
            arabic = "مَّثَلُ الَّذِينَ يُنفِقُونَ أَمْوَالَهُمْ فِي سَبِيلِ اللَّهِ كَمَثَلِ حَبَّةٍ أَنبَتَتْ سَبْعَ سَنَابِلَ",
            translation = "\"Perumpamaan orang yang menafkahkan hartanya di jalan Allah seperti sebutir benih yang menumbuhkan tujuh bulir, pada tiap-tiap bulir seratus biji.\"",
            source = "QS. Al-Baqarah: 261",
            category = "Al-Qur'an"
        ),

        // === DOA HARIAN LENGKAP ===
        WisdomCardItem(
            title = "Doa Setelah Selesai Adzan",
            arabic = "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ وَالصَّلَاةِ الْقَائِمَةِ آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ",
            translation = "\"Ya Allah, Tuhan pemilik seruan yang sempurna ini dan sholat yang akan didirikan, berikanlah kepada Nabi Muhammad wasilah dan keutamaan, serta tempatkanlah beliau pada kedudukan terpuji yang Engkau janjikan.\"",
            source = "HR. Bukhari no. 614",
            category = "Doa Harian"
        ),
        WisdomCardItem(
            title = "Doa Masuk Masjid",
            arabic = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            translation = "\"Ya Allah, bukakanlah untukku pintu-pintu rahmat-Mu.\"",
            source = "HR. Muslim no. 713",
            category = "Doa Harian"
        ),
        WisdomCardItem(
            title = "Doa Keluar Masjid",
            arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
            translation = "\"Ya Allah, sesungguhnya aku memohon karunia dari sisi-Mu.\"",
            source = "HR. Muslim no. 713",
            category = "Doa Harian"
        ),
        WisdomCardItem(
            title = "Doa Kebaikan Dunia & Akhirat (Sapu Jagat)",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            translation = "\"Ya Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat, dan lindungilah kami dari siksa api neraka.\"",
            source = "QS. Al-Baqarah: 201",
            category = "Doa Harian"
        ),
        WisdomCardItem(
            title = "Adab: Lurus & Rapatkan Shaf",
            arabic = "سَوُّوا صُفُوفَكُمْ، فَإِنَّ تَسْوِيَةَ الصَّفِّ مِنْ تَمَامِ الصَّلَاةِ",
            translation = "\"Luruskan shaf-shaf kalian, karena sesungguhnya lurusnya shaf merupakan bagian dari kesempurnaan sholat. Harap matikan nada dering ponsel.\"",
            source = "HR. Bukhari & Muslim",
            category = "Adab Masjid"
        )
    )

    fun getFilteredItems(settings: AppSettings): List<WisdomCardItem> {
        val filtered = items.filter { item ->
            when (item.category) {
                "Asmaul Husna" -> settings.showAsmaulHusna
                "Hadits" -> settings.showHaditsHarian
                "Al-Qur'an" -> settings.showAyatQuran
                "Doa Harian" -> settings.showDoaHarian
                "Sholat Sunnah" -> settings.showSunnahReminder
                else -> true
            }
        }
        return if (filtered.isEmpty()) items else filtered
    }
}
