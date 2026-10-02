package com.example.data

/**
 * AppKnowledge — Data statis aplikasi MASJID.IO.
 */
object AppKnowledge {

    const val APP_NAME = "MASJID.IO"
    const val APP_DESCRIPTION = "Display jadwal sholat Android TV untuk masjid"
    const val REPO_URL = "https://github.com/andikune-ux/Masjid.io"

    const val GITHUB_REPO = "https://github.com/andikune-ux/Masjid.io"
    const val GITHUB_TREE_API = "https://api.github.com/repos/andikune-ux/Masjid.io/git/trees/main?recursive=1"
    const val GITHUB_RAW_BASE = "https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/"
    const val GITHUB_BLOB_BASE = "https://github.com/andikune-ux/Masjid.io/blob/main/"

    val APP_STRUCTURE = """
app/src/main/java/com/example/
├── MainActivity.kt                    -> Activity utama
├── audio/
│   └── SoundManager.kt                -> Suara beep & adzan
├── data/
│   ├── model/
│   │   └── AppSettings.kt             -> Model pengaturan (100+ field)
│   ├── local/
│   │   ├── SettingsRepository.kt      -> Simpan/load setting + exportSummary
│   │   ├── PrayerTimesCalculator.kt   -> Hitung jadwal sholat
│   │   ├── IslamicCalendar.kt         -> Kalender Hijriah
│   │   ├── IslamicWisdomStore.kt      -> Data kartu nasihat
│   │   ├── SunMoonCalculator.kt       -> Posisi matahari/bulan real-time (V1.30.2)
│   │   ├── DynamicSkyTheme.kt         -> Warna langit dinamis
│   │   ├── IndonesiaLocations.kt      -> Data lokasi Indonesia
│   │   └── WeatherService.kt          -> Data cuaca
│   ├── content/
│   │   ├── DzikirStore.kt             -> Data dzikir setelah sholat
│   │   ├── AyatStore.kt               -> Data ayat Al-Quran
│   │   ├── HaditsStore.kt             -> Data hadits pilihan
│   │   ├── AsmaulHusnaStore.kt        -> 99 nama Allah
│   │   └── ContentRotationStore.kt    -> Manager rotasi konten
│   ├── AppKnowledge.kt                -> File ini (data statis)
│   └── UpdateHistory.kt               -> Data riwayat update
├── kiosk/
│   ├── KioskManager.kt                -> Mode kiosk (lock task)
│   ├── WatchdogService.kt             -> Auto-restart
│   └── AutoStartService.kt            -> Auto-start saat boot
├── receiver/BootReceiver.kt           -> Terima broadcast boot
├── util/
│   ├── BackupManager.kt               -> Backup Aman (TXT)
│   ├── CrashReporter.kt               -> Log crash + WA Fonnte
│   ├── CrashAutoShowHelper.kt         -> Auto-show dialog crash
│   ├── GithubSourceFetcher.kt         -> Fetch source dari GitHub
│   ├── SettingsTransferHelper.kt      -> Serialize AppSettings (iO Control)
│   ├── RemoteControlClient.kt         -> HTTP client iO Control
│   ├── FonnteHelper.kt                -> Token & Group ID Fonnte
│   ├── FonnteSender.kt                -> Kirim WA via Fonnte API
│   ├── UpdateManager.kt               -> Cek update GitHub
│   ├── ApkDownloader.kt               -> Download & install APK
│   └── BuildHistoryFetcher.kt         -> Fetch build history GitHub
└── ui/
    ├── home/HomeScreen.kt             -> Tampilan utama (responsive)
    ├── focus/
    │   ├── PrayerFocusOverlay.kt      -> Mode fokus 4 fase
    │   └── QRISFocusOverlay.kt        -> Preview QRIS
    ├── slides/
    │   ├── QrisSlide.kt               -> Slide QRIS Infaq
    │   ├── LaporanSlide.kt            -> Slide Laporan Keuangan
    │   ├── KajianSlide.kt             -> Slide Jadwal Kajian
    │   └── SlideManager.kt            -> Manager rotasi slide
    ├── ramadhan/RamadhanOverlay.kt    -> Overlay Mode Ramadhan
    ├── cctv/CctvWidget.kt             -> Widget CCTV (RTSP/HTTP)
    ├── remote/
    │   ├── RemoteServer.kt            -> HTTP server (Remote + iO Control)
    │   ├── RemoteDashboard.kt         -> Info akses remote
    │   ├── DeviceDiscovery.kt         -> Discovery device via UDP
    │   ├── NetworkHelper.kt           -> Helper deteksi IP WiFi
    │   ├── IoControlScreen.kt         -> UI radar iO Control
    │   └── IoControlHelpSheet.kt      -> Panduan iO Control
    ├── settings/
    │   ├── SettingsScreen.kt          -> Menu utama (20 kategori)
    │   ├── DeveloperSettingsPane.kt   -> Opsi Developer
    │   ├── DeveloperPinDialog.kt      -> PIN 140399
    │   ├── AboutSettingsPane.kt       -> Tentang + Sosmed + APK install
    │   ├── AudioSettingsPane.kt       -> Audio & Adzan
    │   ├── CountdownSettingsPane.kt   -> Durasi & countdown
    │   ├── CustomBackgroundPane.kt    -> Background + Tema Makkah
    │   ├── TvDisplaySettingsPane.kt   -> Tampilan TV responsif (V1.30.3)
    │   ├── IdentitySettingsPane.kt    -> Identitas + logo
    │   ├── LocationSettingsPane.kt    -> Lokasi + GPS
    │   ├── PowerSettingsPane.kt       -> Daya & Booting
    │   ├── QrisSettingsPane.kt        -> Donasi QRIS
    │   ├── RamadhanSettingsPane.kt    -> Mode Ramadhan
    │   ├── RunningTextSettingsPane.kt -> Running text
    │   ├── SecuritySettingsPane.kt    -> Keamanan
    │   ├── SlideSettingsPane.kt       -> Slide Fullscreen
    │   ├── CctvSettingsPane.kt        -> CCTV Masjid
    │   ├── RemoteSettingsPane.kt      -> iO Control + Remote Server
    │   ├── TimeSettingsPane.kt        -> Waktu manual
    │   ├── VideoSettingsPane.kt       -> Video & Foto Slideshow
    │   ├── WeeklyOfficersSettingsPane.kt -> Petugas mingguan
    │   ├── WisdomSettingsPane.kt      -> Kartu nasihat
    │   ├── MiniCalendarPickerModal.kt -> Kalender mini
    │   ├── PinDialog.kt               -> Dialog PIN
    │   ├── RiwayatCrashScreen.kt      -> Riwayat crash
    │   └── RiwayatUpdateScreen.kt     -> Riwayat update
    ├── components/
    │   ├── MakkahDynamicBackground.kt -> Tema Makkah Dinamis (V1.30.2)
    │   ├── ResponsiveLayoutHelper.kt  -> Auto-scale TV (V1.30.3)
    │   ├── TvSlider.kt                -> Slider TV
    │   ├── TvToggle.kt                -> Toggle TV
    │   ├── TvFocusHelper.kt           -> Helper fokus D-pad
    │   ├── FocusHelper.kt             -> Helper fokus tambahan
    │   ├── NeonFocusBorder.kt         -> Border fokus neon
    │   ├── PhotoSlideshow.kt          -> Slideshow foto
    │   ├── MasjidVideoPlayer.kt       -> Video player (ExoPlayer)
    │   ├── UpdateDialog.kt            -> Dialog update
    │   ├── OfficerCarousel.kt         -> Panel imam/muadzin
    │   ├── PrayerCardsRow.kt          -> Kartu sholat (with foto)
    │   ├── PrayerProgressBar.kt       -> Bar progres sholat
    │   ├── RunningTextMarquee.kt      -> Running text bergulir
    │   ├── PinDialog.kt               -> Dialog PIN
    │   ├── ChangePinDialog.kt         -> Dialog ubah PIN
    │   ├── TopBar.kt                  -> Top bar
    │   ├── ClockAndDate.kt            -> Jam & tanggal
    │   ├── MosqueHeader.kt            -> Header nama masjid
    │   ├── ArabesquePattern.kt        -> Pola arabesque
    │   ├── IslamicEventCard.kt        -> Kartu event Islam
    │   ├── WeatherAmbientOverlay.kt   -> Efek cuaca ambient
    │   └── WisdomCardCarousel.kt      -> Karusel kartu nasihat
    └── theme/                          -> Warna & tipografi
    """.trimIndent()

    val APP_FEATURES = """
1. JADWAL SHOLAT
   - 6 waktu: Subuh, Syuruq, Dzuhur, Ashar, Maghrib, Isya
   - Countdown + progress bar visual

2. TAMPILAN UTAMA (Responsive)
   - Jam digital besar + tanggal Hijriah/Masehi
   - Kartu 6 waktu sejajar horizontal
   - Panel Imam & Muadzin auto-slide
   - Running text + Video/Foto Slideshow
   - Auto-scale untuk semua ukuran TV (V1.30.3)

3. TEMA MAKKAH DINAMIS (V1.30.2)
   - Langit bergerak real-time 24 jam
   - Matahari melengkung + bulan fase real
   - Awan, burung, bintang, hujan, petir otomatis
   - Siluet Masjidil Haram + Ka'bah
   - Default theme

4. RESPONSIVE AUTO-SCALE (BARU V1.30.3)
   - Deteksi otomatis resolusi TV (px, dp, aspect ratio)
   - Deteksi tipe layar: Ultrawide 21:9, Standar 16:9, Klasik 4:3
   - Base design 1920x1080 — auto-scale 0.6× s/d 2.5×
   - Safe Area Padding (0-10%) untuk hindari overscan bezel TV
   - Preset layout: AUTO / STANDAR / ULTRAWIDE / 4:3
   - Tombol Test Safe Area untuk kalibrasi visual
   - Menu baru: Tampilan TV (kategori ke-20)

5. MODE FOKUS SHOLAT (4 Fase)
   - Adzan / Qobliyah / Fardhu / Dzikir

6. AUDIO
   - Mode Beep Only / Full Adzan / Silent

7. SLIDE FULLSCREEN
   - Slide QRIS / Laporan / Kajian auto-rotate

8. CCTV MASJID
   - Widget PiP (RTSP + HTTP)

9. iO CONTROL
   - HP sebagai remote TV via WiFi
   - Auto-discovery UDP + UI radar
   - Transfer semua pengaturan antar device
   - Endpoint /api/io/receive public (fix 401)

10. REMOTE CONTROL WEB
    - HTTP server + dashboard browser HP

11. MODE RAMADHAN
    - Countdown Imsak/Iftar + Tarawih + Kultum

12. KIOSK MODE
    - Lock task + Watchdog + Auto-start

13. TENTANG APLIKASI
    - Periksa Update + Riwayat + Install APK
    - Tap file APK = install, tombol merah = hapus

14. OPSI DEVELOPER (PIN 140399)
    - Backup Aman + Riwayat Crash + WhatsApp Fonnte

15. VERSIONING OTOMATIS
    - Format V{inti}.{tanggal}.{countHariIni}

16. WHATSAPP REPORT (FONNTE)
    - Notifikasi crash otomatis ke grup admin

17. FOTO SLIDESHOW + 18. KONTEN ROTASI
    - Galeri foto + Ayat/Hadits/Asmaul Husna

19. DZIKIR SETELAH SHOLAT (8 dzikir)

20. AUTO-SHOW CRASH LOG

21. FULL SOURCE CODE EXPORT

22. FIX CRASH NESTED SCROLL + FADE ANIMASI MANUAL

23. SALIN URL OTOMATIS

24. APK INSTALL DARI FILE TERSIMPAN

25. TAMPILAN TV RESPONSIF (V1.30.3)
    - Auto-scale untuk TV 720p / FHD / 4K / Ultrawide / 4:3
    - Safe Area padding untuk overscan
    - 4 preset + Test Safe Area
    """.trimIndent()

    val UPDATE_HISTORY = """
V1.30.3 (02 Oktober 2026)
- Responsive Auto-Scale — tampilan otomatis semua ukuran TV
- Deteksi resolusi TV (px, dp, aspect ratio, density)
- Safe Area padding 0-10% hindari overscan bezel
- Preset: AUTO / STANDAR / ULTRAWIDE / 4:3
- Menu baru: Tampilan TV (kategori ke-20)
- File baru: ResponsiveLayoutHelper.kt + TvDisplaySettingsPane.kt

V1.30.2 (02 Oktober 2026)
- Tema Makkah Dinamis — langit real-time + cuaca otomatis
- Matahari melengkung, bulan fase asli, awan, burung, bintang
- Hujan + petir real-time dari cuaca lokasi
- Siluet Masjidil Haram + Ka'bah + refleksi marmer
- Fix: iO Control - transfer settings (fix 401)
- Fix: AboutSettingsPane - install APK dari file tersimpan

V1.30.1 (30 September 2026)
- iO Control: HP sebagai remote TV via WiFi
- Auto-discovery device (UDP broadcast)
- UI radar biru + 2 tombol KIRIM/TERIMA
- Transfer semua pengaturan antar device

V1.29.3 (29 September 2026)
- Fix crash nested scroll di SettingsScreen
- Ganti Crossfade ke Box + fade manual (300ms)
- Fix RunningTextMarquee (divide by zero)
- Auto-show dialog crash log setelah force close

V1.28.4 (29 September 2026)
- Slide Fullscreen (QRIS, Laporan, Kajian)
- Konten Rotasi (Ayat, Hadits, Asmaul Husna)
- Mode Ramadhan lengkap + CCTV + Remote Control

V1.28.3 (29 September 2026)
- Fokus D-pad lebih tebal, TvSlider + TvToggle
- LocationSettingsPane + IdentitySettingsPane lengkap

V1.28.2 (28 September 2026)
- Versioning otomatis + Keystore permanen
- Backup Aman + Log Crash + Periksa Update

V1.28.1 (28 September 2026)
- Foto per sesi petugas, HomeScreen video besar

V1.28.0 (27 September 2026)
- Versi dasar: jadwal sholat, mode fokus, kiosk, dll
    """.trimIndent()

    val KNOWN_ISSUES = """
=== SUDAH DIPERBAIKI ===
1. Foto ustadz tidak tampil → normalisasi nama hari
2. Suara beep terlalu pendek → TONE_CDMA_ALERT_CALL_GUARD
3. Update APK harus uninstall dulu → keystore permanen
4. File "File_paths.xml" error → rename "file_paths.xml"
5. Konflik GitHub Actions signing → commit debug.keystore
6. Setup keystore gagal .gitignore → git add -f debug.keystore
7. Backup Aman error EPERM → cek permission + fallback
8. KSP 2.3.5 NPE → upgrade KSP 2.3.12
9. phaseNum di luar scope → pindah ke luar Box
10. LaunchedEffect belum di-import → tambah import
11. Icon Battery tidak ada → ganti BatteryFull
12. LocationSettingsPane & IdentitySettingsPane hilang → restore
13. Location & Identity placeholder → buat file sendiri
14. Crash Vertically scrollable infinity → Box + fade manual
15. ArithmeticException divide by zero → guard + min 1000ms
16. Versi stuck UTC → timezone Asia/Jakarta
17. Tag release "autoVersionName" → extract via aapt
18. iO Control error 401 → /api/io/receive public route
19. APK tidak bisa di-tap install → kiri=install kanan=hapus
20. DeviceDiscovery butuh deviceId → kirim dari IoControlScreen
21. TV beda ukuran kepotong → ResponsiveLayoutHelper (V1.30.3)

=== BELUM DIPERBAIKI ===
(Tidak ada)
    """.trimIndent()

    val BUILD_ERROR_HISTORY = """
FORMAT:
---
[Tanggal] - [Versi]
Error: [deskripsi singkat]
File: [nama file]
Solusi: [cara memperbaiki]
---

=== RIWAYAT ===

[02-10-2026] - V1.30.3
Error: Tidak ada error build untuk V1.30.3 (fitur baru saja)
File: -
Solusi: -

[02-10-2026] - V1.30.2
Error: iO Control transfer antar device gagal — "Server tolak (kode 401)"
File: app/src/main/java/com/example/ui/remote/RemoteServer.kt
Solusi: Tambahkan "/api/io/receive" ke isPublicRoute

[02-10-2026] - V1.30.2
Error: APK tidak bisa di-tap untuk install (hanya tombol hapus)
File: app/src/main/java/com/example/ui/settings/AboutSettingsPane.kt
Solusi: ApkFileItem: kiri tap=install, kanan tombol=hapus

[02-10-2026] - V1.30.2
Error: IoControlScreen error "No value passed for parameter 'deviceId'"
File: app/src/main/java/com/example/ui/remote/IoControlScreen.kt
Solusi: Ambil Android ID + fallback UUID, kirim ke DeviceDiscovery.configure()

[30-09-2026] - V1.30.1
Error: Versi build stuck di V1.29.X padahal tanggal berubah
File: app/build.gradle.kts
Solusi: Set timezone Asia/Jakarta di SimpleDateFormat

[30-09-2026] - V1.30.1
Error: Tag release GitHub jadi "autoVersionName" (literal)
File: .github/workflows/build.yml
Solusi: Extract versionName dari APK pakai aapt dump badging

[29-09-2026] - V1.29.3
Error: ArithmeticException divide by zero di Compose
File: RunningTextMarquee.kt
Solusi: Guard text kosong + durasi minimal 1000ms

[29-09-2026] - V1.29.3
Error: IllegalStateException Vertically scrollable infinity
File: SettingsScreen.kt
Solusi: Ganti Crossfade ke Box + key() + alpha fade
    """.trimIndent()
    
    val WORKFLOW_INSTRUCTION = """
PRINSIP UTAMA:
- KERJAKAN PER BATCH
- JANGAN BUKA FILE YANG SAMA BERKALI-KALI
- KONFIRMASI SEBELUM LANJUT
- JANGAN ASUMSI - LIHAT KODE ASLI DULU
- JANGAN HAPUS FITUR LAMA TANPA IZIN

URUTAN BATCH:
1. Baca file asli
2. Konfirmasi rencana
3. Tulis kode timpa full
4. Update versi + UpdateHistory + AppKnowledge
5. Commit

8 ELEMEN WAJIB RESPON:
1. Visualisasi folder (emoji + indentasi)
2. Path lengkap file
3. Nama file
4. URL cepat
5. 2 opsi akses (A: link, B: navigasi)
6. Kode timpa full
7. Pesan commit
8. Bahasa Indonesia simple

LARANGAN:
- Jangan hapus fitur lama tanpa izin
- Jangan asumsi tanpa baca kode
- Jangan buka file sama 2x
- Jangan nebak kalau tidak tahu
- JANGAN nolak buka link GitHub publik (bisa dibuka)
- JANGAN gabung link 1 file dengan lainnya
    """.trimIndent()

    val DEVELOPER_INSTRUCTION = """
IDENTITAS USER:
- Nama  : Andi
- Email : andikune@gmail.com
- Device: HP Android (bukan PC)
- Aplikasi kerja: GitHub Mobile

REPO:
https://github.com/andikune-ux/Masjid.io (PUBLIC)

APLIKASI:
- Nama           : Masjid.io
- Platform       : Android TV (Jetpack Compose)
- Namespace      : com.example
- Application ID : com.aistudio.masjidio.tkvpmz
- Versi saat ini : V1.30.3
- PIN Developer  : 140399

UPDATE WAJIB TIAP BUILD:
1. Bump versionName (otomatis dari GITHUB_RUN_NUMBER)
2. Tambah entri baru di UpdateHistory.kt
3. Update AppKnowledge.kt (struktur + fitur + build error)

ATURAN FORMAT:
- Setiap respon wajib 8 elemen
- Kode timpa full
- Bahasa Indonesia simple
- 1 chat = 1 fitur utuh kalau muat
    """.trimIndent()

    val MEMORY_INSTRUCTION = """
ATURAN FORMAT:
- Setiap respon wajib 8 elemen (lihat WORKFLOW_INSTRUCTION)
- Kode timpa full, jangan suruh user edit manual
- Bahasa Indonesia yang mudah dipahami
- 1 chat = 1 fitur utuh kalau muat
- Kalau kode tidak muat 1 bubble → tulis "jangan commit dulu, masih ada sambungan"
- JANGAN nolak buka link GitHub publik (bisa dibuka via fitur web)
- JANGAN gabung link 1 file dengan lainnya — pisah per file

ATURAN KERJA:
- Konfirmasi dulu sebelum eksekusi
- Jujur kalau belum tahu, jangan menebak
- JANGAN asal hapus fitur lama
- Lihat kode asli dulu sebelum timpa
- Kerjakan 1 per 1, atau sekaligus kalau user minta
- Sebelum build, tanya "Harus Update / Skip"

STRUKTUR MENU SETTINGS (20 Kategori):
1.  Lokasi & Waktu Sholat
2.  Pengaturan Waktu
3.  Durasi & Hitungan Mundur
4.  Identitas Masjid
5.  Jadwal Petugas & Foto
6.  Donasi QRIS & Rekening
7.  Video Kegiatan Masjid
8.  Tampilan & Background (termasuk Tema Makkah Dinamis)
9.  Tampilan TV (responsive auto-scale — V1.30.3)
10. Kartu Nasihat & Mutiara
11. Running Text
12. Audio & Adzan
13. Mode Ramadhan
14. Keamanan
15. Daya & Booting
16. Slide Fullscreen
17. CCTV Masjid
18. iO Control
19. Tentang Aplikasi
20. Opsi Developer (PIN 140399)

SOSMED:
- WhatsApp : https://chat.whatsapp.com/ErJpG34fdzwL9FOmoh4fNN
- TikTok   : https://www.tiktok.com/@nayyra.une
- Instagram: https://www.instagram.com/nayyra.une

FILE PENTING:
- app/build.gradle.kts              -> versioning
- version.properties                -> data versi otomatis
- .github/workflows/build.yml       -> build & release
- debug.keystore                    -> keystore permanen
- gradle/libs.versions.toml         -> KSP & dependency

PATH BACKUP AMAN:
/sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT

PENANGANAN BUILD ERROR:
1. Cek log GitHub Actions step "Build Debug APK"
2. Cari baris diawali "e:" (error Kotlin)
3. Perbaiki file yang error
4. Update AppKnowledge.kt -> BUILD_ERROR_HISTORY
5. Commit ulang

FITUR YANG BELUM SELESAI:
- Tidak ada (semua fitur selesai per V1.30.3)
    """.trimIndent()

    val MEMORY_KNOWLEDGE = """
============================================================
MEMORY KNOWLEDGE & INSTRUCTION — MASJID.IO
Untuk AI baru yang membaca backup ini
============================================================

=== BAGIAN 1 — ATURAN FORMAT RESPON (8 ELEMEN WAJIB) ===

1. Visualisasi folder (emoji + indentasi, seperti file manager)
2. Path lengkap file
3. Nama file (agar mudah di-copy)
4. URL cepat (bisa di-tap)
5. 2 opsi akses file:
   - OPSI A: Buka pakai link
   - OPSI B: Navigasi folder step-by-step
6. Kode timpa full (bukan edit manual)
7. Pesan commit (text persis untuk kolom commit message)
8. Bahasa Indonesia: simple, tidak teknis rumit, ramah, sopan

=== BAGIAN 1B — ATURAN ANTI-TRUNCATION (WAJIB) ===

Jika kode terlalu panjang melebihi batas aman 1 bubble chat,
AI WAJIB otomatis memecah menjadi BEBERAPA BAGIAN,
TETAP dalam 1 bubble chat, dengan aturan:

1. Beri judul jelas: "BAGIAN 1 DARI N", "BAGIAN 2 DARI N", dst.
2. Di akhir setiap bagian (kecuali terakhir), tulis:
   "(lanjut di BAGIAN berikutnya - sambung di bawah)"
3. Di awal setiap bagian (kecuali pertama), tulis:
   "(sambungan dari BAGIAN sebelumnya)"
4. User cukup copy-paste SEMUA BAGIAN berurutan ke SATU file
5. JANGAN pernah memotong kode di tengah fungsi/blok
6. JANGAN pernah bilang "kode dilanjut di chat berikutnya"
7. Semua BAGIAN wajib ada di 1 bubble chat yang sama

Tujuan: user tinggal sambung tempel, tidak ada kode hilang.

=== BAGIAN 1C — ATURAN LINK GITHUB ===

1. JANGAN tolak buka link GitHub publik — bisa dibuka via fitur web
2. JANGAN gabung link 1 file dengan link file lain
   (tiap file punya section-nya sendiri dengan link-nya sendiri)
3. Kecuali link butuh login akun → baru boleh tolak dengan alasan logis
4. Repo Masjid.io PUBLIC → bebas diakses kapan saja

=== BAGIAN 1D — ATURAN SIMPLIFIKASI ===

1. Elemen 5 (2 Opsi Akses) TIDAK perlu ditampilkan lagi — user sudah paham
2. Elemen 8 (Bahasa Indonesia) TIDAK perlu ditampilkan lagi — cukup dipahami
3. Fokus tampilkan: folder, path, nama file, URL, kode, pesan commit
4. Kalau kode bisa 1 bubble → kirim 1 bubble
5. Kalau tidak muat → bilang "jangan commit dulu, masih ada sambungan"

=== BAGIAN 2 — ATURAN KERJA ===

1. Konfirmasi dulu sebelum eksekusi.
2. Jujur kalau belum tahu, jangan menebak.
3. JANGAN asal hapus fitur lama.
4. Lihat kode asli dulu sebelum timpa.
5. Kerjakan 1 per 1, atau sekaligus kalau user minta.
6. Sebelum build, tanya "Harus Update / Skip".
7. KERJAKAN PER BATCH.
8. JANGAN BUKA FILE YANG SAMA BERKALI-KALI.
9. KONFIRMASI SEBELUM LANJUT.

=== BAGIAN 3 — IDENTITAS USER & APLIKASI ===

USER:
  Nama  : Andi
  Email : andikune@gmail.com

DEVICE:
  HP Android (bukan PC/laptop)
  Aplikasi kerja: GitHub Mobile

REPO:
  https://github.com/andikune-ux/Masjid.io (PUBLIC)

APLIKASI:
  Nama           : Masjid.io
  Platform       : Android TV (Jetpack Compose)
  Namespace      : com.example
  Application ID : com.aistudio.masjidio.tkvpmz
  Versi saat ini : V1.30.3
  PIN Developer  : 140399

=== BAGIAN 4 — STRUKTUR MENU SETTINGS (20 KATEGORI) ===

1.  Lokasi & Waktu Sholat
2.  Pengaturan Waktu
3.  Durasi & Hitungan Mundur
4.  Identitas Masjid
5.  Jadwal Petugas & Foto
6.  Donasi QRIS & Rekening
7.  Video Kegiatan Masjid
8.  Tampilan & Background
9.  Tampilan TV
10. Kartu Nasihat & Mutiara
11. Running Text
12. Audio & Adzan
13. Mode Ramadhan
14. Keamanan
15. Daya & Booting
16. Slide Fullscreen
17. CCTV Masjid
18. iO Control
19. Tentang Aplikasi
20. Opsi Developer (PIN 140399)

=== BAGIAN 5 — SOSIAL MEDIA ===

WhatsApp  : https://chat.whatsapp.com/ErJpG34fdzwL9FOmoh4fNN
TikTok    : https://www.tiktok.com/@nayyra.une
Instagram : https://www.instagram.com/nayyra.une

=== BAGIAN 6 — FILE PENTING ===

app/build.gradle.kts              -> versioning
version.properties                -> data versi otomatis
.github/workflows/build.yml       -> build & release
debug.keystore                    -> keystore permanen
gradle/libs.versions.toml         -> KSP & dependency

=== BAGIAN 7 — PATH BACKUP AMAN ===

/sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT

=== BAGIAN 8 — UPDATE WAJIB TIAP BUILD ===

1. Bump versionName  -> GITHUB_RUN_NUMBER otomatis
2. Tambah entri baru -> UpdateHistory.kt
3. Update info baru  -> AppKnowledge.kt

=== BAGIAN 9 — PENANGANAN BUILD ERROR ===

1. Cek log GitHub Actions -> step "Build Debug APK"
2. Cari baris diawali "e:" (error Kotlin)
3. Perbaiki file yang error
4. Update AppKnowledge.kt -> BUILD_ERROR_HISTORY
5. Commit ulang

=== BAGIAN 10 — FORMAT RIWAYAT BUILD ERROR ===

---
[Tanggal] - [Versi]
Error: [deskripsi singkat]
File: [nama file]
Solusi: [cara memperbaiki]
---

=== BAGIAN 11 — PRINSIP UTAMA ===

1. KERJAKAN PER BATCH.
2. JANGAN BUKA FILE YANG SAMA BERKALI-KALI.
3. KONFIRMASI SEBELUM LANJUT.
4. JANGAN ASUMSI - LIHAT KODE ASLI DULU.
5. JANGAN HAPUS FITUR LAMA TANPA IZIN.

=== BAGIAN 12 — FULL SOURCE CODE EXPORT ===

Backup Aman WAJIB menyertakan ISI SEMUA FILE KODE.

FILE YANG HARUS DI-EXPORT:
- Semua file .kt, .java, .xml, .gradle, .kts
- Semua file .toml, .yml, .yaml, .properties, .pro

CARA C (folder lokal + fallback GitHub API):
1. Cek /sdcard/masjid.io/source/ dulu
2. Kalau tidak ada -> fetch dari GitHub API (repo public)
3. Kalau dua-duanya gagal -> kasih pesan di TXT

FORMAT EXPORT:
---BEGIN--- path/file.kt
[ISI KODE LENGKAP]
---END--- path/file.kt

TUJUAN: AI baru langsung paham tanpa tanya user.

=== BAGIAN 13 — FITUR AUTO-SHOW CRASH LOG ===

- Saat crash, log disimpan ke filesDir/crashes/
- Flag disimpan di SharedPreferences
- Saat app dibuka, dialog crash muncul otomatis
- Tombol: Salin + Kembali

=== BAGIAN 14 — ATURAN KERJA SAMA DENGAN AI BARU ===

Jika user ganti AI, AI baru WAJIB:
1. Baca BACKUP AMAN terlebih dahulu (BAGIAN 1-19).
2. Pahami struktur aplikasi + isi kode.
3. Ikuti aturan 8 elemen format.
4. Ikuti aturan anti-truncation (BAGIAN 1B).
5. Ikuti aturan link GitHub (BAGIAN 1C).
6. Konfirmasi dulu sebelum eksekusi.
7. Jangan menebak - tanya user kalau tidak tahu.

=== BAGIAN 15 — LINK GITHUB SOURCE CODE (UNTUK AI BARU) ===

AI baru DAPAT membuka sendiri kode lengkap dari GitHub.
Repo PUBLIC, tidak butuh login atau token.

LINK UTAMA:
  Repo Utama       : https://github.com/andikune-ux/Masjid.io
  Daftar Semua File: https://api.github.com/repos/andikune-ux/Masjid.io/git/trees/main?recursive=1

LINK RAW (untuk baca file langsung):
  Base Raw         : https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/

LINK BLOB (untuk lihat di browser):
  Base Blob        : https://github.com/andikune-ux/Masjid.io/blob/main/

FOLDER KUNCI YANG SERING DIBACA:
- app/src/main/java/com/example/MainActivity.kt
- app/src/main/java/com/example/util/BackupManager.kt
- app/src/main/java/com/example/util/CrashReporter.kt
- app/src/main/java/com/example/util/GithubSourceFetcher.kt
- app/src/main/java/com/example/util/SettingsTransferHelper.kt
- app/src/main/java/com/example/util/RemoteControlClient.kt
- app/src/main/java/com/example/util/ApkDownloader.kt
- app/src/main/java/com/example/data/AppKnowledge.kt
- app/src/main/java/com/example/data/UpdateHistory.kt
- app/src/main/java/com/example/data/local/SettingsRepository.kt
- app/src/main/java/com/example/data/local/SunMoonCalculator.kt (V1.30.2)
- app/src/main/java/com/example/data/model/AppSettings.kt
- app/src/main/java/com/example/ui/settings/SettingsScreen.kt
- app/src/main/java/com/example/ui/settings/AboutSettingsPane.kt
- app/src/main/java/com/example/ui/settings/CustomBackgroundPane.kt
- app/src/main/java/com/example/ui/settings/TvDisplaySettingsPane.kt (V1.30.3)
- app/src/main/java/com/example/ui/remote/RemoteServer.kt
- app/src/main/java/com/example/ui/remote/DeviceDiscovery.kt
- app/src/main/java/com/example/ui/remote/IoControlScreen.kt
- app/src/main/java/com/example/ui/components/MakkahDynamicBackground.kt (V1.30.2)
- app/src/main/java/com/example/ui/components/ResponsiveLayoutHelper.kt (V1.30.3)
- app/src/main/java/com/example/ui/home/HomeScreen.kt
- app/build.gradle.kts
- gradle/libs.versions.toml
- .github/workflows/build.yml

CATATAN UNTUK AI BARU:
- Repo PUBLIC -> bebas diakses kapan saja
- Kalau butuh SEMUA file sekaligus: buka tree API di atas
- Kalau butuh 1 file: pakai raw URL + path
- JANGAN minta user copy-paste manual kalau bisa buka sendiri

=== BAGIAN 16 — FITUR iO CONTROL ===

FITUR:
- HP sebagai remote tampilan TV via WiFi/Hotspot sama
- Auto-discovery via UDP broadcast (port 45678)
- UI radar biru (sweep 360° + pulse)
- Pilih device -> connect -> 2 tombol KIRIM/TERIMA
- Transfer semua pengaturan antar device
- Progress bar 0-100% -> penerima auto-restart

FILE TERKAIT:
- ui/remote/DeviceDiscovery.kt      -> UDP broadcast & listen
- ui/remote/IoControlScreen.kt      -> UI radar + progress
- ui/remote/RemoteServer.kt         -> HTTP endpoint /api/io/*
- ui/remote/NetworkHelper.kt        -> Deteksi IP WiFi
- util/SettingsTransferHelper.kt    -> Serialize JSON settings
- util/RemoteControlClient.kt       -> HTTP client kirim settings
- ui/settings/RemoteSettingsPane.kt -> Tombol BUKA iO CONTROL
- MainActivity.kt                   -> Integrasi screen + callback

CATATAN PENTING:
- Transfer via JSON (aman lintas versi)
- Field tidak dikenal di penerima -> otomatis di-skip
- Remote Control setting (port, token) TIDAK ditransfer
- PIN IKUT ditransfer (sesuai keputusan user)
- Endpoint /api/io/receive PUBLIC (fix 401 V1.30.2)

=== BAGIAN 17 — FITUR TEMA MAKKAH DINAMIS (V1.30.2) ===

FITUR BARU:
- Langit Makkah bergerak real-time (24 jam siklus)
- Matahari melengkung timur ke barat berdasarkan jam lokal
- Bulan bergeser + fase asli (sabit/purnama) via algoritma lunar
- 7 gradasi warna langit otomatis
- Awan bergerak + burung terbang + bintang berkelip
- Hujan + petir real-time dari WeatherService (Open-Meteo)
- Kabut subuh jam 04:00-06:00
- Siluet Masjidil Haram + Ka'bah + pita emas Hizam
- Refleksi marmer di lantai Mataf
- Tema ini jadi DEFAULT (menggantikan NATURE)

FILE TERKAIT:
- data/local/SunMoonCalculator.kt          -> Hitung posisi matahari/bulan/fase
- ui/components/MakkahDynamicBackground.kt -> Render canvas dinamis
- data/model/AppSettings.kt                -> Enum MAKKAH_DYNAMIC + default
- ui/home/HomeScreen.kt                    -> Integrasi background
- ui/settings/CustomBackgroundPane.kt      -> Opsi di Settings

CATATAN PENTING:
- Cuaca otomatis via WeatherService (Open-Meteo, gratis)
- Lat/Lon Makkah hardcoded (-21.42, 39.83) untuk sudut matahari
- Bintang muncul saat malam + cuaca cerah
- Burung tidak muncul saat hujan/badai
- Petir muncul acak 10-15 detik sekali

=== BAGIAN 18 — FITUR APK INSTALL DARI FILE (V1.30.2) ===

FITUR:
- Tap file APK tersimpan -> INSTALL
- Tombol merah terpisah -> HAPUS
- Visual feedback: border hijau + "TAP UNTUK INSTALL"
- File APK di /sdcard/masjid.io/pembaharuan aplikasi/

FILE TERKAIT:
- ui/settings/AboutSettingsPane.kt -> ApkFileItem
- util/ApkDownloader.kt            -> Install, hapus, list APK

CATATAN:
- Area kiri (ikon + nama file) = INSTALL
- Area kanan (tombol merah) = HAPUS
- Tidak tabrakan niat install vs hapus

=== BAGIAN 19 — FITUR RESPONSIVE AUTO-SCALE TV (V1.30.3) ===

FITUR BARU:
- Deteksi otomatis resolusi TV (px, dp, aspect ratio, density)
- Deteksi tipe layar: ULTRAWIDE, STANDARD_WIDE, STANDARD, CLASSIC_4_3
- Base design 1920x1080 — auto-scale 0.6× s/d 2.5×
- Safe Area Padding dinamis (0-10%) untuk overscan bezel TV
- Preset layout: AUTO / STANDAR / ULTRAWIDE / 4:3
- TV Ultrawide: konten di-center (max 88% lebar)
- TV 4:3 lama: spacing & font disesuaikan
- Menu baru: Tampilan TV (kategori ke-20)
- Tombol Test Safe Area untuk kalibrasi visual

FILE TERKAIT:
- ui/components/ResponsiveLayoutHelper.kt -> Deteksi & scale helper
- ui/settings/TvDisplaySettingsPane.kt    -> UI panel pengaturan
- ui/home/HomeScreen.kt                   -> ResponsiveRoot wrapper
- ui/settings/SettingsScreen.kt           -> Tambah kategori TV_DISPLAY
- data/model/AppSettings.kt               -> 3 field responsive
- data/local/SettingsRepository.kt        -> load/save 3 field

FIELD BARU:
- tvAutoScaleEnabled: Boolean = true
- tvSafeAreaPercent: Float = 3f
- tvLayoutPreset: String = "AUTO"

CATATAN:
- ScreenInfo dihitung di ResponsiveRoot (CompositionLocal)
- Extensi: screenInfo.sp(base), screenInfo.dp(base), screenInfo.safePadding()
- Safe area default 3% (aman untuk 95% TV)
- Kalau konten terpotong → naikkan safe area 5% s/d 8%

============================================================
END OF MEMORY KNOWLEDGE
============================================================
    """.trimIndent()

    // ============================================================
    // ALIAS (untuk kompatibilitas BackupManager)
    // ============================================================
    val STRUCTURE: String get() = APP_STRUCTURE
    val FEATURES: String get() = APP_FEATURES
}
