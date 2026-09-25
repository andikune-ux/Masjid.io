package com.example.data.local

data class WisdomCardItem(
    val title: String,
    val arabic: String,
    val translation: String,
    val source: String,
    val category: String // "Hadits", "Al-Qur'an", "Adab Masjid", "Doa Harian"
)

object IslamicWisdomStore {
    val items: List<WisdomCardItem> = listOf(
        WisdomCardItem(
            title = "Keutamaan Sholat Berjamaah",
            arabic = "صَلَاةُ الْجَمَاعَةِ أَفْضَلُ مِنْ صَلَاةِ الْفَذِّ بِسَبْعٍ وَعِشْرِينَ دَرَجَةً",
            translation = "\"Sholat berjamaah lebih utama dua puluh tujuh derajat dibanding sholat sendirian.\"",
            source = "HR. Bukhari & Muslim",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Adab & Ketenangan Menuju Sholat",
            arabic = "إِذَا أُقِيمَتِ الصَّلَاةُ فَلَا تَأْتُوهَا تَسْعَوْنَ، وَأْتُوهَا تَمْشُونَ وَعَلَيْكُمُ السَّكِينَةُ",
            translation = "\"Jika sholat telah diiqamahkan, janganlah kalian mendatanginya dengan berlari tergesa-gesa, tetapi datangilah dengan berjalan tenang.\"",
            source = "HR. Bukhari no. 636",
            category = "Adab Masjid"
        ),
        WisdomCardItem(
            title = "Pahala Langkah Menuju Masjid",
            arabic = "مَنْ غَدَا إِلَى الْمَسْجِدِ أَوْ رَاحَ أَعَدَّ اللَّهُ لَهُ فِي الْجَنَّةِ نُزُلًا",
            translation = "\"Barangsiapa yang pergi ke masjid pada pagi atau petang hari, Allah menyediakan baginya tempat tinggal di surga setiap kali ia pergi.\"",
            source = "HR. Bukhari & Muslim",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Merapatkan & Meluruskan Shaf",
            arabic = "سَوُّوا صُفُوفَكُمْ، فَإِنَّ تَسْوِيَةَ الصَّفِّ مِنْ تَمَامِ الصَّلَاةِ",
            translation = "\"Luruskan shaf-shaf kalian, karena sesungguhnya lurusnya shaf merupakan bagian dari kesempurnaan sholat.\"",
            source = "HR. Bukhari & Muslim",
            category = "Adab Masjid"
        ),
        WisdomCardItem(
            title = "Peringatan Menjaga Kekhusyukan",
            arabic = "وَأَقِيمُوا الصَّلَاةَ وَآتُوا الزَّكَاةَ وَارْكَعُوا مَعَ الرَّاكِعِينَ",
            translation = "\"Dan dirikanlah sholat, tunaikanlah zakat, dan ruku'lah bersama orang-orang yang ruku'. Harap menonaktifkan nada dering ponsel demi kenyamanan ibadah.\"",
            source = "QS. Al-Baqarah: 43",
            category = "Al-Qur'an"
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
            translation = "\"Ya Allah, sesungguhnya aku memohon kepada-Mu dari karunia-Mu.\"",
            source = "HR. Muslim no. 713",
            category = "Doa Harian"
        ),
        WisdomCardItem(
            title = "Keutamaan Dua Rakaat Qobliyah Subuh",
            arabic = "رَكْعَتَا الْفَجْرِ خَيْرٌ مِنَ الدُّنْيَا وَمَا فِيهَا",
            translation = "\"Dua rakaat sunnah fajar (Qobliyah Subuh) lebih baik daripada dunia beserta seluruh isinya.\"",
            source = "HR. Muslim no. 725",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Perintah Sholat Tepat Waktu",
            arabic = "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَوْقُوتًا",
            translation = "\"Sesungguhnya sholat itu adalah fardhu yang ditentukan waktunya atas orang-orang yang beriman.\"",
            source = "QS. An-Nisa: 103",
            category = "Al-Qur'an"
        ),
        WisdomCardItem(
            title = "Sedekah Memadamkan Murka Allah",
            arabic = "إِنَّ الصَّدَقَةَ لَتُطْفِئُ غَضَبَ الرَّبِّ وَتَدْفَعُ مِيتَةَ السُّوءِ",
            translation = "\"Sesungguhnya sedekah itu memadamkan murka Allah dan menolak kematian yang buruk.\"",
            source = "HR. Tirmidzi no. 664",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Kemuliaan Membaca Al-Qur'an",
            arabic = "اقْرَؤُوا الْقُرْآنَ فَإِنَّهُ يَأْتِي يَوْمَ الْقِيَامَةِ شَفِيعًا لِأَصْحَابِهِ",
            translation = "\"Bacalah Al-Qur'an, karena sesungguhnya ia akan datang pada hari kiamat sebagai pemberi syafaat bagi para pembacanya.\"",
            source = "HR. Muslim no. 804",
            category = "Hadits"
        ),
        WisdomCardItem(
            title = "Adab Bicara di Rumah Allah",
            arabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الْآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            translation = "\"Barangsiapa yang beriman kepada Allah dan hari akhir, hendaklah ia berkata yang baik atau diam.\"",
            source = "HR. Bukhari & Muslim",
            category = "Adab Masjid"
        )
    )

    val wisdomCards: List<WisdomCardItem> get() = items
}
