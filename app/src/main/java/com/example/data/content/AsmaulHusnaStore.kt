package com.example.data.content

data class AsmaulHusnaItem(
    val number: Int,
    val arabic: String,
    val latin: String,
    val translation: String
)

object AsmaulHusnaStore {

    // Format: nomor|arab|latih|arti
    private val rawData = """
1|الرَّحْمَنُ|Ar-Rahman|Yang Maha Pengasih
2|الرَّحِيمُ|Ar-Rahim|Yang Maha Penyayang
3|الْمَلِكُ|Al-Malik|Yang Maha Menguasai
4|الْقُدُّوسُ|Al-Quddus|Yang Maha Suci
5|السَّلَامُ|As-Salam|Yang Maha Memberi Kesejahteraan
6|الْمُؤْمِنُ|Al-Mu'min|Yang Maha Memberi Keamanan
7|الْمُهَيْمِنُ|Al-Muhaimin|Yang Maha Memelihara
8|الْعَزِيزُ|Al-'Aziz|Yang Maha Perkasa
9|الْجَبَّارُ|Al-Jabbar|Yang Memiliki Mutlak Kegagahan
10|الْمُتَكَبِّرُ|Al-Mutakabbir|Yang Maha Megah
11|الْخَالِقُ|Al-Khaliq|Yang Maha Pencipta
12|الْبَارِئُ|Al-Bari'|Yang Maha Mengadakan
13|الْمُصَوِّرُ|Al-Mushawwir|Yang Maha Membentuk Rupa
14|الْغَفَّارُ|Al-Ghaffar|Yang Maha Pengampun
15|الْقَهَّارُ|Al-Qahhar|Yang Maha Memaksa
16|الْوَهَّابُ|Al-Wahhab|Yang Maha Pemberi Karunia
17|الرَّزَّاقُ|Ar-Razzaq|Yang Maha Pemberi Rezeki
18|الْفَتَّاحُ|Al-Fattah|Yang Maha Pembuka Rahmat
19|الْعَلِيمُ|Al-'Alim|Yang Maha Mengetahui
20|الْقَابِضُ|Al-Qabidh|Yang Maha Menyempitkan
21|الْبَاسِطُ|Al-Basith|Yang Maha Melapangkan
22|الْخَافِضُ|Al-Khafidh|Yang Maha Merendahkan
23|الرَّافِعُ|Ar-Rafi'|Yang Maha Meninggikan
24|الْمُعِزُّ|Al-Mu'izz|Yang Maha Memuliakan
25|الْمُذِلُّ|Al-Mudzill|Yang Maha Menghinakan
26|السَّمِيعُ|As-Sami'|Yang Maha Mendengar
27|الْبَصِيرُ|Al-Bashir|Yang Maha Melihat
28|الْحَكَمُ|Al-Hakam|Yang Maha Menetapkan
29|الْعَدْلُ|Al-'Adl|Yang Maha Adil
30|اللَّطِيفُ|Al-Lathif|Yang Maha Lembut
31|الْخَبِيرُ|Al-Khabir|Yang Maha Mengenal
32|الْحَلِيمُ|Al-Halim|Yang Maha Penyantun
33|الْعَظِيمُ|Al-'Azhim|Yang Maha Agung
34|الْغَفُورُ|Al-Ghafur|Yang Maha Pengampun
35|الشَّكُورُ|Asy-Syakur|Yang Maha Pembalas Budi
36|الْعَلِيُّ|Al-'Aliy|Yang Maha Tinggi
37|الْكَبِيرُ|Al-Kabir|Yang Maha Besar
38|الْحَفِيظُ|Al-Hafizh|Yang Maha Memelihara
39|الْمُقِيتُ|Al-Muqit|Yang Maha Pemberi Kecukupan
40|الْحَسِيبُ|Al-Hasib|Yang Maha Membuat Perhitungan
41|الْجَلِيلُ|Al-Jalil|Yang Maha Luhur
42|الْكَرِيمُ|Al-Karim|Yang Maha Pemurah
43|الرَّقِيبُ|Ar-Raqib|Yang Maha Mengawasi
44|الْمُجِيبُ|Al-Mujib|Yang Maha Mengabulkan
45|الْوَاسِعُ|Al-Wasi'|Yang Maha Luas
46|الْحَكِيمُ|Al-Hakim|Yang Maha Bijaksana
47|الْوَدُودُ|Al-Wadud|Yang Maha Mengasihi
48|الْمَجِيدُ|Al-Majid|Yang Maha Mulia
49|الْبَاعِثُ|Al-Ba'its|Yang Maha Membangkitkan
50|الشَّهِيدُ|Asy-Syahid|Yang Maha Menyaksikan
51|الْحَقُّ|Al-Haqq|Yang Maha Benar
52|الْوَكِيلُ|Al-Wakil|Yang Maha Memelihara
53|الْقَوِيُّ|Al-Qawiyy|Yang Maha Kuat
54|الْمَتِينُ|Al-Matin|Yang Maha Kokoh
55|الْوَلِيُّ|Al-Waliy|Yang Maha Melindungi
56|الْحَمِيدُ|Al-Hamid|Yang Maha Terpuji
57|الْمُحْصِي|Al-Muhshi|Yang Maha Menghitung
58|الْمُبْدِئُ|Al-Mubdi'|Yang Maha Memulai
59|الْمُعِيدُ|Al-Mu'id|Yang Maha Mengembalikan Kehidupan
60|الْمُحْيِي|Al-Muhyi|Yang Maha Menghidupkan
61|الْمُمِيتُ|Al-Mumit|Yang Maha Mematikan
62|الْحَيُّ|Al-Hayy|Yang Maha Hidup
63|الْقَيُّومُ|Al-Qayyum|Yang Maha Mandiri
64|الْوَاجِدُ|Al-Wajid|Yang Maha Penemu
65|الْمَاجِدُ|Al-Majid|Yang Maha Mulia
66|الْوَاحِدُ|Al-Wahid|Yang Maha Tunggal
67|الْأَحَدُ|Al-Ahad|Yang Maha Esa
68|الصَّمَدُ|Ash-Shamad|Yang Maha Dibutuhkan
69|الْقَادِرُ|Al-Qadir|Yang Maha Menentukan
70|الْمُقْتَدِرُ|Al-Muqtadir|Yang Maha Berkuasa
71|الْمُقَدِّمُ|Al-Muqaddim|Yang Maha Mendahulukan
72|الْمُؤَخِّرُ|Al-Mu'akhkhir|Yang Maha Mengakhirkan
73|الْأَوَّلُ|Al-Awwal|Yang Maha Awal
74|الْآخِرُ|Al-Akhir|Yang Maha Akhir
75|الظَّاهِرُ|Azh-Zhahir|Yang Maha Nyata
76|الْبَاطِنُ|Al-Bathin|Yang Maha Ghaib
77|الْوَالِي|Al-Wali|Yang Maha Memerintah
78|الْمُتَعَالِي|Al-Muta'ali|Yang Maha Tinggi
79|الْبَرُّ|Al-Barr|Yang Maha Penderma
80|التَّوَّابُ|At-Tawwab|Yang Maha Penerima Taubat
81|الْمُنْتَقِمُ|Al-Muntaqim|Yang Maha Pemberi Balasan
82|الْعَفُوُّ|Al-'Afuww|Yang Maha Pemaaf
83|الرَّءُوفُ|Ar-Ra'uf|Yang Maha Pengasih
84|مَالِكُ الْمُلْكِ|Malikul-Mulk|Yang Maha Penguasa Kerajaan
85|ذُو الْجَلَالِ وَالْإِكْرَامِ|Dzul-Jalali wal-Ikram|Yang Maha Pemilik Kebesaran dan Kemuliaan
86|الْمُقْسِطُ|Al-Muqsith|Yang Maha Pemberi Keadilan
87|الْجَامِعُ|Al-Jami'|Yang Maha Mengumpulkan
88|الْغَنِيُّ|Al-Ghaniy|Yang Maha Kaya
89|الْمُغْنِي|Al-Mughni|Yang Maha Pemberi Kekayaan
90|الْمَانِعُ|Al-Mani'|Yang Maha Mencegah
91|الضَّارُّ|Adh-Dharr|Yang Maha Penimpa Kemudharatan
92|النَّافِعُ|An-Nafi'|Yang Maha Memberi Manfaat
93|النُّورُ|An-Nur|Yang Maha Bercahaya
94|الْهَادِي|Al-Hadi|Yang Maha Pemberi Petunjuk
95|الْبَدِيعُ|Al-Badi'|Yang Maha Pencipta
96|الْبَاقِي|Al-Baqi|Yang Maha Kekal
97|الْوَارِثُ|Al-Warits|Yang Maha Pewaris
98|الرَّشِيدُ|Ar-Rasyid|Yang Maha Pandai
99|الصَّبُورُ|Ash-Shabur|Yang Maha Sabar
    """.trimIndent()

    val items: List<AsmaulHusnaItem> by lazy {
        rawData.lines().mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size == 4) {
                AsmaulHusnaItem(
                    number = parts[0].trim().toIntOrNull() ?: 0,
                    arabic = parts[1].trim(),
                    latin = parts[2].trim(),
                    translation = parts[3].trim()
                )
            } else null
        }
    }

    fun getByNumber(number: Int): AsmaulHusnaItem? {
        return items.firstOrNull { it.number == number }
    }

    fun getByIndex(index: Int): AsmaulHusnaItem {
        return items.getOrElse(index % items.size) { items.first() }
    }

    val total: Int get() = items.size
}
