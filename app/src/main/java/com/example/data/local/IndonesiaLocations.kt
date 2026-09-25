package com.example.data.local

data class CityLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffset: Double = 7.0 // WIB = +7, WITA = +8, WIT = +9
)

data class ProvinceLocation(
    val name: String,
    val cities: List<CityLocation>
)

object IndonesiaLocations {
    val provinces: List<ProvinceLocation> = listOf(
        ProvinceLocation("Aceh", listOf(
            CityLocation("Banda Aceh", 5.5483, 95.3238, 7.0),
            CityLocation("Lhokseumawe", 5.1801, 97.1407, 7.0),
            CityLocation("Langsa", 4.4719, 97.9683, 7.0),
            CityLocation("Sabang", 5.8942, 95.3167, 7.0),
            CityLocation("Meulaboh", 4.1449, 96.1265, 7.0)
        )),
        ProvinceLocation("Sumatera Utara", listOf(
            CityLocation("Medan", 3.5952, 98.6722, 7.0),
            CityLocation("Pematang Siantar", 2.9592, 99.0687, 7.0),
            CityLocation("Binjai", 3.6006, 98.4854, 7.0),
            CityLocation("Tebing Tinggi", 3.3285, 99.1625, 7.0),
            CityLocation("Padang Sidempuan", 1.3734, 99.2685, 7.0)
        )),
        ProvinceLocation("Sumatera Barat", listOf(
            CityLocation("Padang", -0.9471, 100.4172, 7.0),
            CityLocation("Bukittinggi", -0.3055, 100.3692, 7.0),
            CityLocation("Payakumbuh", -0.2244, 100.6306, 7.0),
            CityLocation("Pariaman", -0.6264, 100.1200, 7.0),
            CityLocation("Solok", -0.7989, 100.6539, 7.0)
        )),
        ProvinceLocation("Riau", listOf(
            CityLocation("Pekanbaru", 0.5071, 101.4478, 7.0),
            CityLocation("Dumai", 1.6669, 101.4497, 7.0),
            CityLocation("Duri", 1.2505, 101.2167, 7.0),
            CityLocation("Bangkinang", 0.3392, 101.0261, 7.0)
        )),
        ProvinceLocation("Kepulauan Riau", listOf(
            CityLocation("Tanjung Pinang", 0.9167, 104.4500, 7.0),
            CityLocation("Batam", 1.1301, 104.0529, 7.0),
            CityLocation("Bintan", 1.1687, 104.5822, 7.0),
            CityLocation("Karimun", 0.9926, 103.4281, 7.0)
        )),
        ProvinceLocation("Jambi", listOf(
            CityLocation("Jambi", -1.6101, 103.6131, 7.0),
            CityLocation("Sungai Penuh", -2.0622, 101.3939, 7.0),
            CityLocation("Muara Bungo", -1.4925, 102.1158, 7.0)
        )),
        ProvinceLocation("Sumatera Selatan", listOf(
            CityLocation("Palembang", -2.9761, 104.7754, 7.0),
            CityLocation("Prabumulih", -3.4331, 104.2330, 7.0),
            CityLocation("Lubuklinggau", -3.2964, 102.8617, 7.0),
            CityLocation("Pagar Alam", -4.0150, 103.2678, 7.0)
        )),
        ProvinceLocation("Kepulauan Bangka Belitung", listOf(
            CityLocation("Pangkal Pinang", -2.1333, 106.1167, 7.0),
            CityLocation("Tanjung Pandan", -2.7333, 107.6333, 7.0)
        )),
        ProvinceLocation("Bengkulu", listOf(
            CityLocation("Bengkulu", -3.8004, 102.2655, 7.0),
            CityLocation("Curup", -3.4667, 102.5333, 7.0)
        )),
        ProvinceLocation("Lampung", listOf(
            CityLocation("Bandar Lampung", -5.4500, 105.2667, 7.0),
            CityLocation("Metro", -5.1136, 105.3069, 7.0),
            CityLocation("Kalianda", -5.7333, 105.5833, 7.0)
        )),
        ProvinceLocation("DKI Jakarta", listOf(
            CityLocation("Jakarta Pusat", -6.1754, 106.8272, 7.0),
            CityLocation("Jakarta Selatan", -6.2615, 106.8106, 7.0),
            CityLocation("Jakarta Timur", -6.2250, 106.9004, 7.0),
            CityLocation("Jakarta Barat", -6.1683, 106.7588, 7.0),
            CityLocation("Jakarta Utara", -6.1214, 106.7741, 7.0),
            CityLocation("Kepulauan Seribu", -5.6122, 106.5622, 7.0)
        )),
        ProvinceLocation("Jawa Barat", listOf(
            CityLocation("Bandung", -6.9175, 107.6191, 7.0),
            CityLocation("Bogor", -6.5971, 106.8060, 7.0),
            CityLocation("Bekasi", -6.2383, 106.9756, 7.0),
            CityLocation("Depok", -6.4025, 106.7942, 7.0),
            CityLocation("Cirebon", -6.7320, 108.5523, 7.0),
            CityLocation("Sukabumi", -6.9277, 106.9300, 7.0),
            CityLocation("Tasikmalaya", -7.3274, 108.2207, 7.0),
            CityLocation("Karawang", -6.3056, 107.3000, 7.0)
        )),
        ProvinceLocation("Banten", listOf(
            CityLocation("Serang", -6.1104, 106.1639, 7.0),
            CityLocation("Tangerang", -6.1783, 106.6319, 7.0),
            CityLocation("Tangerang Selatan", -6.2888, 106.7179, 7.0),
            CityLocation("Cilegon", -6.0174, 106.0538, 7.0)
        )),
        ProvinceLocation("Jawa Tengah", listOf(
            CityLocation("Semarang", -6.9667, 110.4167, 7.0),
            CityLocation("Surakarta (Solo)", -7.5755, 110.8243, 7.0),
            CityLocation("Magelang", -7.4706, 110.2178, 7.0),
            CityLocation("Pekalongan", -6.8886, 109.6753, 7.0),
            CityLocation("Purwokerto", -7.4244, 109.2306, 7.0),
            CityLocation("Kudus", -6.8048, 110.8405, 7.0),
            CityLocation("Tegal", -6.8694, 109.1402, 7.0)
        )),
        ProvinceLocation("DI Yogyakarta", listOf(
            CityLocation("Yogyakarta", -7.7956, 110.3695, 7.0),
            CityLocation("Sleman", -7.6890, 110.3444, 7.0),
            CityLocation("Bantul", -7.8939, 110.3297, 7.0),
            CityLocation("Gunungkidul", -7.9622, 110.6033, 7.0),
            CityLocation("Kulon Progo", -7.7713, 110.1583, 7.0)
        )),
        ProvinceLocation("Jawa Timur", listOf(
            CityLocation("Surabaya", -7.2575, 112.7521, 7.0),
            CityLocation("Malang", -7.9797, 112.6304, 7.0),
            CityLocation("Kediri", -7.8480, 112.0178, 7.0),
            CityLocation("Madiun", -7.6298, 111.5239, 7.0),
            CityLocation("Jember", -8.1724, 113.7007, 7.0),
            CityLocation("Banyuwangi", -8.2192, 114.3692, 7.0),
            CityLocation("Probolinggo", -7.7543, 113.2159, 7.0)
        )),
        ProvinceLocation("Bali", listOf(
            CityLocation("Denpasar", -8.6705, 115.2126, 8.0),
            CityLocation("Singaraja", -8.1120, 115.0882, 8.0),
            CityLocation("Gianyar", -8.5443, 115.3283, 8.0)
        )),
        ProvinceLocation("Nusa Tenggara Barat", listOf(
            CityLocation("Mataram", -8.5833, 116.1167, 8.0),
            CityLocation("Bima", -8.4608, 118.7256, 8.0),
            CityLocation("Sumbawa Besar", -8.5000, 117.4333, 8.0)
        )),
        ProvinceLocation("Nusa Tenggara Timur", listOf(
            CityLocation("Kupang", -10.1772, 123.6070, 8.0),
            CityLocation("Ende", -8.8432, 121.6623, 8.0),
            CityLocation("Maumere", -8.6200, 122.2111, 8.0)
        )),
        ProvinceLocation("Kalimantan Barat", listOf(
            CityLocation("Pontianak", -0.0263, 109.3425, 7.0),
            CityLocation("Singkawang", 0.9073, 108.9859, 7.0),
            CityLocation("Ketapang", -1.8494, 109.9753, 7.0)
        )),
        ProvinceLocation("Kalimantan Tengah", listOf(
            CityLocation("Palangka Raya", -2.2161, 113.9139, 7.0),
            CityLocation("Sampit", -2.5333, 112.9500, 7.0),
            CityLocation("Pangkalan Bun", -2.6833, 111.6167, 7.0)
        )),
        ProvinceLocation("Kalimantan Selatan", listOf(
            CityLocation("Banjarmasin", -3.3194, 114.5908, 8.0),
            CityLocation("Banjarbaru", -3.4402, 114.8302, 8.0),
            CityLocation("Martapura", -3.4116, 114.8519, 8.0)
        )),
        ProvinceLocation("Kalimantan Timur", listOf(
            CityLocation("Samarinda", -0.5022, 117.1536, 8.0),
            CityLocation("Balikpapan", -1.2379, 116.8289, 8.0),
            CityLocation("Bontang", 0.1333, 117.5000, 8.0),
            CityLocation("IKN Nusantara", -0.9744, 116.7088, 8.0)
        )),
        ProvinceLocation("Kalimantan Utara", listOf(
            CityLocation("Tanjung Selor", 2.8375, 117.3653, 8.0),
            CityLocation("Tarakan", 3.3000, 117.6333, 8.0)
        )),
        ProvinceLocation("Sulawesi Utara", listOf(
            CityLocation("Manado", 1.4748, 124.8421, 8.0),
            CityLocation("Bitung", 1.4451, 125.1824, 8.0),
            CityLocation("Kotamobagu", 0.7306, 124.3139, 8.0)
        )),
        ProvinceLocation("Gorontalo", listOf(
            CityLocation("Gorontalo", 0.5435, 123.0568, 8.0),
            CityLocation("Limboto", 0.6272, 122.9817, 8.0)
        )),
        ProvinceLocation("Sulawesi Tengah", listOf(
            CityLocation("Palu", -0.8917, 119.8707, 8.0),
            CityLocation("Poso", -1.3959, 120.7533, 8.0),
            CityLocation("Luwuk", -0.9516, 122.7875, 8.0)
        )),
        ProvinceLocation("Sulawesi Barat", listOf(
            CityLocation("Mamuju", -2.6778, 118.8875, 8.0),
            CityLocation("Polewali", -3.4324, 119.3435, 8.0)
        )),
        ProvinceLocation("Sulawesi Selatan", listOf(
            CityLocation("Makassar", -5.1477, 119.4327, 8.0),
            CityLocation("Parepare", -4.0131, 119.6253, 8.0),
            CityLocation("Palopo", -2.9944, 120.1969, 8.0),
            CityLocation("Bone", -4.5386, 120.3275, 8.0)
        )),
        ProvinceLocation("Sulawesi Tenggara", listOf(
            CityLocation("Kendari", -3.9985, 122.5126, 8.0),
            CityLocation("Baubau", -5.4636, 122.6012, 8.0)
        )),
        ProvinceLocation("Maluku", listOf(
            CityLocation("Ambon", -3.6547, 128.1906, 9.0),
            CityLocation("Tual", -5.6288, 132.7483, 9.0)
        )),
        ProvinceLocation("Maluku Utara", listOf(
            CityLocation("Ternate", 0.7906, 127.3842, 9.0),
            CityLocation("Tidore", 0.6861, 127.4014, 9.0),
            CityLocation("Sofifi", 0.7303, 127.5744, 9.0)
        )),
        ProvinceLocation("Papua", listOf(
            CityLocation("Jayapura", -2.5916, 140.6690, 9.0),
            CityLocation("Sentani", -2.5647, 140.4853, 9.0)
        )),
        ProvinceLocation("Papua Barat", listOf(
            CityLocation("Manokwari", -0.8615, 134.0620, 9.0),
            CityLocation("Fakfak", -2.9264, 132.2961, 9.0)
        )),
        ProvinceLocation("Papua Selatan", listOf(
            CityLocation("Merauke", -8.4991, 140.4017, 9.0),
            CityLocation("Asmat", -5.4667, 138.4500, 9.0)
        )),
        ProvinceLocation("Papua Tengah", listOf(
            CityLocation("Nabire", -3.3667, 135.4833, 9.0),
            CityLocation("Timika", -4.5444, 136.8837, 9.0)
        )),
        ProvinceLocation("Papua Pegunungan", listOf(
            CityLocation("Wamena (Jayawijaya)", -4.0983, 138.9442, 9.0)
        )),
        ProvinceLocation("Papua Barat Daya", listOf(
            CityLocation("Sorong", -0.8762, 131.2558, 9.0),
            CityLocation("Raja Ampat", -0.2333, 130.5167, 9.0)
        ))
    )
}
