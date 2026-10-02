package com.example.data

/**
 * UpdateHistory — Riwayat update aplikasi MASJID.IO.
 *
 * Menyimpan semua entri versi + fitur baru.
 * Dipakai oleh:
 *   - RiwayatUpdateScreen (UI)
 *   - BackupManager (generateBackupContent)
 */
object UpdateHistory {

    data class UpdateEntry(
        val version: String,
        val date: String,
        val title: String,
        val features: List<String>
    )

    val entries: List<UpdateEntry> = listOf(
        UpdateEntry(
            version = "V1.30.3",
            date = "02 Oktober 2026",
            title = "Responsive Auto-Scale — Tampilan Otomatis Semua Ukuran TV",
            features = listOf(
                "Fitur BARU: Auto-Scale tampilan untuk semua ukuran TV",
                "Deteksi otomatis resolusi TV (px, dp, aspect ratio, density)",
                "Deteksi tipe layar: Ultrawide 21:9, Standar 16:9, Klasik 4:3",
                "Base design 1920x1080 (FHD) — semua elemen auto-scale",
                "Scale factor clamp 0.6× - 2.5× supaya tidak ekstrem",
                "Safe Area Padding dinamis (0-10%) untuk hindari overscan bezel TV",
                "TV Ultrawide: konten otomatis di-center (max 88% lebar)",
                "TV 4:3 lama: spacing & font otomatis disesuaikan",
                "TV 4K: font & padding auto-membesar proporsional",
                "TV 720p: font & padding auto-mengecil proporsional",
                "Tombol Settings di TopBar tidak terpotong di TV apapun",
                "Kartu sholat, jam, panel imam/muadzin semua responsif",
                "Menu baru di Pengaturan: 'Tampilan TV' (kategori ke-20)",
                "Info resolusi TV real-time tampil di panel pengaturan",
                "Toggle Auto-Scale ON/OFF (default ON)",
                "Slider Safe Area 0-10% (default 3%)",
                "4 Preset layout: AUTO / STANDAR / ULTRAWIDE / 4:3",
                "Tombol Test Safe Area untuk cek area aman dari bezel",
                "File baru: ResponsiveLayoutHelper.kt (helper deteksi & scale)",
                "File baru: TvDisplaySettingsPane.kt (panel pengaturan)",
                "AppSettings: 3 field baru — tvAutoScaleEnabled, tvSafeAreaPercent, tvLayoutPreset",
                "SettingsRepository: load/save 3 field responsive",
                "HomeScreen: dibungkus ResponsiveRoot untuk auto-scale"
            )
        ),
        UpdateEntry(
            version = "V1.30.2",
            date = "02 Oktober 2026",
            title = "Tema Makkah Dinamis & Fix iO Control Transfer",
            features = listOf(
                "Fitur BARU: Tema Makkah Dinamis sebagai tema DEFAULT",
                "Langit Makkah bergerak real-time: matahari melengkung dari timur ke barat",
                "Matahari otomatis berubah posisi sesuai jam: Subuh - Dzuhur - Maghrib",
                "Bulan bergeser otomatis dari kiri bawah ke kanan atas sepanjang malam",
                "Fase bulan REAL: purnama/sabit mengikuti siklus lunar otomatis",
                "Awan bergerak kiri ke kanan dengan 6 lapis paralax",
                "Burung berterbangan V-formation (siang saja, tidak saat hujan)",
                "Bintang berkelip di langit malam (60 bintang dengan twinkle)",
                "Cuaca real-time: cerah, berawan, hujan, hujan petir",
                "Efek hujan: 140 tetes air dengan kecepatan bervariasi",
                "Efek petir: kilat putih muncul 10-15 detik sekali saat badai",
                "Kabut tipis muncul otomatis jam 04:00-06:00 (waktu subuh)",
                "Refleksi marmer di lantai Mataf (silau matahari)",
                "Siluet Masjidil Haram + Ka'bah + pita emas Hizam",
                "Lampu arcade menyala otomatis saat malam",
                "7 gradasi warna langit otomatis (subuh-malam)",
                "Background lama tetap ada: NATURE, KABAH, EMERALD, CUSTOM",
                "Fix CRITICAL: transfer iO Control antar device berhasil (fix error 401)",
                "Fix: /api/io/receive jadi public route",
                "Fix: APK tersimpan bisa di-tap untuk INSTALL",
                "Fix: tombol hapus APK dipisah dari area install",
                "File baru: SunMoonCalculator.kt (posisi matahari/bulan/fase lunar)",
                "File baru: MakkahDynamicBackground.kt (canvas langit dinamis)"
            )
        ),
        UpdateEntry(
            version = "V1.30.1",
            date = "30 September 2026",
            title = "iO Control — Kontrol & Transfer Pengaturan Antar Device via WiFi",
            features = listOf(
                "Fitur iO Control: HP sebagai remote tampilan TV via WiFi/Hotspot sama",
                "Auto-discovery device Masjid.io lain pakai UDP broadcast (port 45678)",
                "UI radar biru dengan animasi sweep 360° + pulse",
                "Tampilkan daftar device yang ditemukan (nama, role, IP, versi)",
                "Auto-detect TV atau HP via Configuration.UI_MODE_TYPE_TELEVISION",
                "Setelah connect: 2 tombol besar KIRIM dan TERIMA",
                "Transfer semua pengaturan: tema, jadwal, ustadz, running text, PIN, dll",
                "Progress bar realtime 0-100% saat transfer",
                "Device penerima otomatis restart setelah 100% transfer",
                "Serialisasi AppSettings via JSON (aman lintas versi)",
                "RemoteSettingsPane: tombol BUKA iO CONTROL (biru, di atas)",
                "RemoteSettingsPane: tombol SALIN URL otomatis (clipboard)",
                "RemoteSettingsPane: auto-refresh IP tiap 5 detik",
                "RemoteSettingsPane: info chip IP / PORT / TOKEN",
                "RemoteSettingsPane: peringatan kalau WiFi OFF",
                "Permission baru: CHANGE_WIFI_MULTICAST_STATE, ACCESS_WIFI_STATE, NEARBY_WIFI_DEVICES",
                "Endpoint baru RemoteServer: POST /api/io/handshake",
                "Endpoint baru RemoteServer: POST /api/io/receive",
                "Fix: SettingsRepository lengkap — load/save semua 100+ field",
                "Fix: SettingsRepository tambah exportSummary()",
                "Fix: UpdateHistory tambah getFullText() + getSummary()",
                "Fix: AppKnowledge tambah BAGIAN 15 (link GitHub)",
                "Fix: AppKnowledge tambah aturan anti-truncation",
                "Fix: build.gradle.kts timezone Asia/Jakarta",
                "Fix: build.yml tag release pakai versi asli via aapt"
            )
        ),
        UpdateEntry(
            version = "V1.29.3",
            date = "29 September 2026",
            title = "Fix Crash Scroll & Fade Animasi Manual",
            features = listOf(
                "Fix crash: 'Vertically scrollable component was measured with infinity maximum height'",
                "Ganti Crossfade ke Box + key + alpha fade manual (300ms)",
                "Pertahankan animasi fade antar pane tanpa nested scroll",
                "Update BackupManager: pertahankan method lama + fetch source code dari GitHub",
                "Update SettingsRepository: tambah exportSummary() + load/save semua field",
                "Update UpdateHistory: tambah getFullText() dan getSummary()",
                "Update AppKnowledge: tambah BAGIAN 15 (link GitHub)",
                "Fix: RunningTextMarquee guard text kosong + durasi minimal 1000ms",
                "Feat: MainActivity auto-show dialog crash log setelah force close",
                "Feat: CrashAutoShowHelper + dialog crash (Salin + Kembali)"
            )
        ),
        UpdateEntry(
            version = "V1.28.4",
            date = "29 September 2026",
            title = "Slide Fullscreen, CCTV, Remote Control & Konten Rotasi",
            features = listOf(
                "Menambahkan Slide Fullscreen (QRIS Infaq)",
                "Menambahkan Slide Laporan Keuangan",
                "Menambahkan Slide Jadwal Kajian",
                "Menambahkan SlideManager (rotasi otomatis)",
                "Menambahkan Konten Rotasi (Ayat/Hadits/Asmaul Husna)",
                "Menambahkan data DzikirStore (8 dzikir)",
                "Menambahkan data AyatStore (10 ayat)",
                "Menambahkan data HaditsStore (10 hadits)",
                "Menambahkan data AsmaulHusnaStore (99 nama Allah)",
                "Menambahkan Mode Ramadhan Overlay",
                "Menambahkan CCTV Widget (RTSP + HTTP support)",
                "Menambahkan ExoPlayer RTSP (untuk CCTV RTSP)",
                "Menambahkan Remote Control (HTTP server)",
                "Menambahkan Remote Dashboard (info akses)",
                "Menambahkan background foto di PrayerCard",
                "Menambahkan Phase 4 Dzikir di PrayerFocusOverlay",
                "Menambahkan SlideSettingsPane",
                "Menambahkan CctvSettingsPane",
                "Menambahkan RemoteSettingsPane",
                "Update SettingsScreen (menu baru: Slide, CCTV, Remote)",
                "Update MainActivity (integrasi RemoteServer)"
            )
        ),
        UpdateEntry(
            version = "V1.28.3",
            date = "29 September 2026",
            title = "Fokus D-pad, Pane Lengkap & Restore Fitur",
            features = listOf(
                "Fokus D-pad lebih tebal (TvFocusHelper)",
                "TvSlider + TvToggle (remote-friendly)",
                "LocationSettingsPane lengkap (GPS + manual + Kemenag)",
                "IdentitySettingsPane lengkap (nama, alamat, logo, takmir)",
                "RunningTextSettingsPane lengkap",
                "Fix warning Locale deprecated",
                "Fix warning BorderStroke deprecated",
                "Restore fitur Location & Identity yang hilang"
            )
        ),
        UpdateEntry(
            version = "V1.28.2",
            date = "28 September 2026",
            title = "Versioning, Keystore & Fitur Inti",
            features = listOf(
                "Implementasi versioning otomatis (format V1.28.2)",
                "Setup keystore permanen (update tanpa uninstall)",
                "Auto GitHub Release setiap build",
                "Fitur Backup Aman (TXT)",
                "Fitur Log Crash + Riwayat Crash",
                "Fitur Periksa Update + Riwayat Update",
                "Fitur ApkDownloader (download + install APK)",
                "Fitur WhatsApp Fonnte (crash → grup admin)",
                "Fitur Foto Slideshow",
                "Fitur Build History Fetcher",
                "Restore 4 pane: Ramadhan, Keamanan, Daya, Tentang"
            )
        ),
        UpdateEntry(
            version = "V1.28.1",
            date = "28 September 2026",
            title = "Perbaikan Tampilan & Petugas",
            features = listOf(
                "Upload foto per sesi petugas",
                "Foto profil kotak sudut tumpul",
                "HomeScreen: video diperbesar, jam diperkecil",
                "Kartu sholat digeser kiri saat video aktif",
                "Suara beep diperbaiki (nyaring, panjang, serius)",
                "Integrasi KioskManager & WatchdogService",
                "Deteksi crash di MainActivity"
            )
        ),
        UpdateEntry(
            version = "V1.28.0",
            date = "27 September 2026",
            title = "Versi Dasar",
            features = listOf(
                "Jadwal sholat 6 waktu",
                "Jam digital + tanggal Hijriah",
                "Kartu 6 waktu sholat sejajar",
                "Panel Imam & Muadzin auto-slide",
                "Running text pengumuman",
                "Mode fokus 30 menit",
                "Audio: Beep / Full Adzan / Silent",
                "Pengaturan lengkap dengan PIN",
                "Jadwal petugas mingguan",
                "Kiosk mode + auto-start",
                "Auto-build APK via GitHub Actions",
                "Launcher default Android TV",
                "Background 3 mode",
                "Mode Ramadhan (dasar)",
                "Donasi QRIS & Rekening",
                "Video kegiatan masjid",
                "Kartu Nasihat & Mutiara"
            )
        )
    )

    fun getEntry(version: String): UpdateEntry? =
        entries.find { it.version.equals(version, ignoreCase = true) }

    fun getLatestVersion(): String =
        entries.firstOrNull()?.version ?: "V1.0.0"

    fun getFullText(): String = buildString {
        appendLine("Total: ${entries.size} versi tercatat")
        appendLine()
        entries.forEach { entry ->
            appendLine("┌─ ${entry.version}  (${entry.date})")
            appendLine("│  ${entry.title}")
            appendLine("│")
            entry.features.forEach { f ->
                appendLine("│  • $f")
            }
            appendLine("└─")
            appendLine()
        }
    }

    fun getSummary(): String = buildString {
        entries.forEach { entry ->
            appendLine("${entry.version} (${entry.date})")
            entry.features.take(5).forEach { f ->
                appendLine("- $f")
            }
            if (entry.features.size > 5) {
                appendLine("- ... (${entry.features.size - 5} lagi)")
            }
            appendLine()
        }
    }
}
