package dev.andikune.masjidio.data.content

data class HaditsItem(
    val title: String,
    val arabic: String,
    val translation: String,
    val source: String
)

object HaditsStore {

    val haditsList: List<HaditsItem> = listOf(
        HaditsItem(
            title = "Niat",
            arabic = "إِنَّمَا الْأَعْمَالُ بِالنِّيَّاتِ",
            translation = "Sesungguhnya setiap amalan tergantung pada niatnya.",
            source = "HR. Bukhari & Muslim"
        ),
        HaditsItem(
            title = "Sebaik-baik Manusia",
            arabic = "خَيْرُ النَّاسِ أَنْفَعُهُمْ لِلنَّاسِ",
            translation = "Sebaik-baik manusia adalah yang paling bermanfaat bagi manusia lain.",
            source = "HR. Thabrani"
        ),
        HaditsItem(
            title = "Sholat Berjamaah",
            arabic = "صَلَاةُ الْجَمَاعَةِ تَفْضُلُ صَلَاةَ الْفَذِّ بِسَبْعٍ وَعِشْرِينَ دَرَجَةً",
            translation = "Sholat berjamaah lebih utama 27 derajat dibanding sholat sendirian.",
            source = "HR. Bukhari & Muslim"
        ),
        HaditsItem(
            title = "Senyum",
            arabic = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            translation = "Senyummu kepada saudaramu adalah sedekah.",
            source = "HR. Tirmidzi"
        ),
        HaditsItem(
            title = "Kebersihan",
            arabic = "الطُّهُورُ شَطْرُ الْإِيمَانِ",
            translation = "Kebersihan adalah sebagian dari iman.",
            source = "HR. Muslim"
        ),
        HaditsItem(
            title = "Menuntut Ilmu",
            arabic = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            translation = "Menuntut ilmu itu wajib bagi setiap muslim.",
            source = "HR. Ibnu Majah"
        ),
        HaditsItem(
            title = "Larangan Marah",
            arabic = "لَا تَغْضَبْ",
            translation = "Jangan marah.",
            source = "HR. Bukhari"
        ),
        HaditsItem(
            title = "Berkata Baik",
            arabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الْآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            translation = "Barangsiapa beriman kepada Allah dan hari akhir, hendaklah ia berkata baik atau diam.",
            source = "HR. Bukhari & Muslim"
        ),
        HaditsItem(
            title = "Sedekah Tidak Mengurangi Harta",
            arabic = "مَا نَقَصَتْ صَدَقَةٌ مِنْ مَالٍ",
            translation = "Sedekah tidak akan mengurangi harta.",
            source = "HR. Muslim"
        ),
        HaditsItem(
            title = "Malu",
            arabic = "الْحَيَاءُ شُعْبَةٌ مِنَ الْإِيمَانِ",
            translation = "Rasa malu adalah bagian dari iman.",
            source = "HR. Bukhari & Muslim"
        )
    )

    fun getHadits(index: Int): HaditsItem {
        return haditsList.getOrElse(index % haditsList.size) {
            haditsList.first()
        }
    }

    fun getRandomHadits(): HaditsItem {
        return haditsList.random()
    }

    val totalHadits: Int get() = haditsList.size
}
