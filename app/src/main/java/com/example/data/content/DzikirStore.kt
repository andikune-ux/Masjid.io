package dev.andikune.masjidio.data.content

data class DzikirItem(
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val repeat: String = ""
)

object DzikirStore {

    val dzikirSetelahSholat: List<DzikirItem> = listOf(
        DzikirItem(
            title = "Istighfar 3x",
            arabic = "أَسْتَغْفِرُ اللَّهَ",
            latin = "Astaghfirullah",
            translation = "Aku memohon ampun kepada Allah",
            repeat = "3x"
        ),
        DzikirItem(
            title = "Doa Keselamatan",
            arabic = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            latin = "Allahumma antas-salam wa minkas-salam, tabarakta ya dzal-jalali wal-ikram",
            translation = "Ya Allah, Engkau Maha Pemberi Keselamatan, dan dari-Mu segala keselamatan. Maha Suci Engkau, wahai Dzat yang memiliki keagungan dan kemuliaan",
            repeat = "1x"
        ),
        DzikirItem(
            title = "Ayat Kursi",
            arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ",
            latin = "Allahu la ilaha illa huwal-hayyul-qayyum, la ta'khudzuhu sinatuw wa la naum, lahu ma fis-samawati wa ma fil-ardh",
            translation = "Allah, tidak ada Tuhan selain Dia, Yang Maha Hidup, Yang terus-menerus mengurus (makhluk-Nya). Dia tidak mengantuk dan tidak tidur. Milik-Nya apa yang ada di langit dan di bumi",
            repeat = "1x"
        ),
        DzikirItem(
            title = "Tasbih",
            arabic = "سُبْحَانَ اللَّهِ",
            latin = "Subhanallah",
            translation = "Maha Suci Allah",
            repeat = "33x"
        ),
        DzikirItem(
            title = "Tahmid",
            arabic = "الْحَمْدُ لِلَّهِ",
            latin = "Alhamdulillah",
            translation = "Segala puji bagi Allah",
            repeat = "33x"
        ),
        DzikirItem(
            title = "Takbir",
            arabic = "اللَّهُ أَكْبَرُ",
            latin = "Allahu Akbar",
            translation = "Allah Maha Besar",
            repeat = "33x"
        ),
        DzikirItem(
            title = "Tahlil Penutup",
            arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            latin = "La ilaha illallahu wahdahu la syarika lah, lahul-mulku wa lahul-hamdu wa huwa 'ala kulli syai'in qadir",
            translation = "Tidak ada Tuhan selain Allah, Yang Maha Esa, tidak ada sekutu bagi-Nya. Milik-Nya kerajaan dan segala pujian, dan Dia Maha Kuasa atas segala sesuatu",
            repeat = "1x"
        ),
        DzikirItem(
            title = "Doa Penutup",
            arabic = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
            latin = "Allahumma a'inni 'ala dzikrika wa syukrika wa husni 'ibadatik",
            translation = "Ya Allah, tolonglah aku untuk selalu berdzikir kepada-Mu, bersyukur kepada-Mu, dan beribadah dengan baik kepada-Mu",
            repeat = "1x"
        )
    )

    /**
     * Ambil dzikir berdasarkan index (untuk rotasi).
     */
    fun getDzikir(index: Int): DzikirItem {
        return dzikirSetelahSholat.getOrElse(index % dzikirSetelahSholat.size) {
            dzikirSetelahSholat.first()
        }
    }

    /**
     * Total dzikir yang tersedia.
     */
    val totalDzikir: Int get() = dzikirSetelahSholat.size
}
