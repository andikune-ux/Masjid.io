package dev.andikune.masjidio.data.content

data class AyatItem(
    val surahName: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabic: String,
    val latin: String,
    val translation: String,
    val tafsirShort: String = ""
)

object AyatStore {

    val ayatHarian: List<AyatItem> = listOf(
        AyatItem(
            surahName = "Al-Baqarah",
            surahNumber = 2,
            ayahNumber = 255,
            arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
            latin = "Allahu la ilaha illa huwal-hayyul-qayyum, la ta'khudzuhu sinatuw wa la naum",
            translation = "Allah, tidak ada Tuhan selain Dia, Yang Maha Hidup, Yang terus-menerus mengurus (makhluk-Nya). Dia tidak mengantuk dan tidak tidur.",
            tafsirShort = "Ayat Kursi — ayat paling agung dalam Al-Quran"
        ),
        AyatItem(
            surahName = "Ar-Ra'd",
            surahNumber = 13,
            ayahNumber = 28,
            arabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            latin = "Ala bidzikrillahi tathma'innul-qulub",
            translation = "Ingatlah, hanya dengan mengingat Allah hati menjadi tenang.",
            tafsirShort = "Hati yang tenang hanya didapat dengan dzikir"
        ),
        AyatItem(
            surahName = "Al-Insyirah",
            surahNumber = 94,
            ayahNumber = 6,
            arabic = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            latin = "Inna ma'al-'usri yusra",
            translation = "Sesungguhnya bersama kesulitan ada kemudahan.",
            tafsirShort = "Setiap kesulitan pasti ada jalan keluar"
        ),
        AyatItem(
            surahName = "Al-Baqarah",
            surahNumber = 2,
            ayahNumber = 286,
            arabic = "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا",
            latin = "La yukallifullahu nafsan illa wus'aha",
            translation = "Allah tidak membebani seseorang melainkan sesuai dengan kesanggupannya.",
            tafsirShort = "Beban hidup sesuai kemampuan hamba"
        ),
        AyatItem(
            surahName = "Ali 'Imran",
            surahNumber = 3,
            ayahNumber = 159,
            arabic = "فَاعْفُ عَنْهُمْ وَاسْتَغْفِرْ لَهُمْ وَشَاوِرْهُمْ فِي الْأَمْرِ ۖ فَإِذَا عَزَمْتَ فَتَوَكَّلْ عَلَى اللَّهِ",
            latin = "Fa'fu 'anhum wastaghfir lahum wa syawirhum fil-amr, fa idza 'azamta fatawakkal 'alallah",
            translation = "Maafkanlah mereka, mohonkanlah ampun bagi mereka, dan bermusyawarahlah dengan mereka dalam urusan itu. Kemudian apabila engkau telah membulatkan tekad, bertawakallah kepada Allah.",
            tafsirShort = "Musyawarah dan tawakal kunci keberhasilan"
        ),
        AyatItem(
            surahName = "Al-Baqarah",
            surahNumber = 2,
            ayahNumber = 152,
            arabic = "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
            latin = "Fadzkuruni adzkurkum wasykuru li wa la takfurun",
            translation = "Maka ingatlah kepada-Ku, Aku pun akan ingat kepadamu. Bersyukurlah kepada-Ku, dan janganlah kamu mengingkari (nikmat)-Ku.",
            tafsirShort = "Dzikir berbalas dengan dzikir Allah"
        ),
        AyatItem(
            surahName = "At-Talaq",
            surahNumber = 65,
            ayahNumber = 2,
            arabic = "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا",
            latin = "Wa man yattaqillaha yaj'al lahu makhraja",
            translation = "Dan barangsiapa bertakwa kepada Allah, niscaya Dia akan menjadikan jalan keluar baginya.",
            tafsirShort = "Takwa mendatangkan solusi"
        ),
        AyatItem(
            surahName = "At-Talaq",
            surahNumber = 65,
            ayahNumber = 3,
            arabic = "وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ",
            latin = "Wa yarzuqhu min haitsu la yahtasib",
            translation = "Dan memberinya rezeki dari arah yang tidak disangka-sangkanya.",
            tafsirShort = "Rezeki tak terduga bagi yang bertakwa"
        ),
        AyatItem(
            surahName = "Al-Isra",
            surahNumber = 17,
            ayahNumber = 80,
            arabic = "رَّبِّ أَدْخِلْنِي مُدْخَلَ صِدْقٍ وَأَخْرِجْنِي مُخْرَجَ صِدْقٍ",
            latin = "Rabbi adkhilni mudkhala shidqin wa akhrijni mukhraja shidqin",
            translation = "Ya Tuhanku, masukkanlah aku secara masuk yang benar, dan keluarkanlah (pula) aku secara keluar yang benar.",
            tafsirShort = "Doa mohon keberkahan setiap langkah"
        ),
        AyatItem(
            surahName = "Ibrahim",
            surahNumber = 14,
            ayahNumber = 7,
            arabic = "لَئِن شَكَرْتُمْ لَأَزِيدَنَّكُمْ",
            latin = "La'in syakartum la'azidannakum",
            translation = "Sesungguhnya jika kamu bersyukur, pasti Kami akan menambah (nikmat) kepadamu.",
            tafsirShort = "Syukur mendatangkan tambahan nikmat"
        )
    )

    fun getAyat(index: Int): AyatItem {
        return ayatHarian.getOrElse(index % ayatHarian.size) {
            ayatHarian.first()
        }
    }

    fun getRandomAyat(): AyatItem {
        return ayatHarian.random()
    }

    val totalAyat: Int get() = ayatHarian.size
}
