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
│   │   └── AppSettings.kt             -> Model pengaturan (110+ field)
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
│   ├── SettingsTransferHelper.kt      -> Serialize + upload media (V1.30.4)
│   ├── RemoteControlClient.kt         -> HTTP client iO Control (V1.30.4)
│   ├── MediaTransferHelper.kt         -> Chunk + kompres media (V1.30.4)
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
    │   ├── RemoteServer.kt            -> HTTP server (Remote + iO Control + media)
    │   ├── RemoteDashboard.kt         -> Info akses remote
    │   ├── DeviceDiscovery.kt         -> Discovery device via UDP
    │   ├── NetworkHelper.kt           -> Helper deteksi IP WiFi
    │   ├── IoControlScreen.kt         -> UI radar iO Control + media progress
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
   - Auto-scale untuk semua ukuran TV

3. TEMA MAKKAH DINAMIS (V1.30.2)
   - Langit bergerak real-time 24 jam
   - Matahari melengkung + bulan fase real
   - Awan, burung, bintang, hujan, petir otomatis
   - Siluet Masjidil Haram + Ka'bah
   - Default theme

4. RESPONSIVE AUTO-SCALE (V1.30.3)
   - Deteksi otomatis resolusi TV
   - Deteksi tipe: Ultrawide, Standar, 4:3
   - Safe Area Padding untuk hindari overscan
   - Preset: AUTO / STANDAR / ULTRAWIDE / 4:3
   - Menu baru: Tampilan TV (kategori ke-20)

5. MODE FOKUS SHOLAT (4 Fase)
   - Adzan / Qobliyah / Fardhu / Dzikir

6. AUDIO
   - Mode Beep Only / Full Adzan / Silent

7. SLIDE FULLSCREEN
   - Slide QRIS / Laporan / Kajian auto-rotate

8. CCTV MASJID
   - Widget PiP (RTSP + HTTP)

9. iO CONTROL (V1.30.4 + V1.30.3)
   - HP sebagai remote TV via WiFi
   - Auto-discovery UDP + UI radar
   - Transfer semua pengaturan antar device
   - TRANSFER MEDIA (foto + video) via chunk upload — V1.30.4
   - Kompres foto otomatis (1920px, 85%)
   - Kompres video otomatis (MediaMuxer)
   - Progress bar per-file + total
   - Retry otomatis 3x per chunk
   - Auto-restart penerima setelah transfer selesai

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

19. DZIKIR SETELAH SHOLAT (8 dzikir)

20. AUTO-SHOW CRASH LOG

21. FULL SOURCE CODE EXPORT

22. FIX CRASH NESTED SCROLL + FADE ANIMASI

23. SALIN URL OTOMATIS

24. APK INSTALL DARI FILE TERSIMPAN

25. TAMPILAN TV RESPONSIF (V1.30.3)

26. TRANSFER MEDIA iO CONTROL (V1.30.4) — BARU
    - Foto & video ikut terkirim via chunk
    - Progress per-file + total
    - Retry otomatis
    - Notifikasi status (sukses/gagal/berjalan)
    """.trimIndent()
    
    val UPDATE_HISTORY = """
V1.30.4 (02 Oktober 2026)
- Transfer media iO Control — foto & video antar device
- Chunk upload 1 MB per request (support video besar)
- Kompres foto + video otomatis
- Progress bar per-file + total keseluruhan
- Retry otomatis 3x per chunk
- Alur: kirim settings → auto lanjut kirim media
- File baru: MediaTransferHelper.kt

V1.30.3 (02 Oktober 2026)
- Responsive Auto-Scale — tampilan otomatis semua ukuran TV
- Deteksi resolusi TV (px, dp, aspect ratio, density)
- Safe Area padding 0-10% hindari overscan bezel
- Preset: AUTO / STANDAR / ULTRAWIDE / 4:3
- Menu baru: Tampilan TV (kategori ke-20)

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
21. TV beda ukuran kepotong → ResponsiveLayoutHelper
22. Extension .dp() bentrok dengan Compose → rename .scaledDp()
23. Foto & video tidak ikut transfer iO Control → chunk upload (V1.30.4)

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

[02-10-2026] - V1.30.4
Error: Foto & video tidak ikut terkirim via iO Control (hanya path)
File: app/src/main/java/com/example/util/SettingsTransferHelper.kt
Solusi: Kirim file fisik via chunk upload + Base64 encode

[02-10-2026] - V1.30.3
Error: Extension ScreenInfo.dp() bentrok dengan androidx.compose.ui.unit.dp
File: app/src/main/java/com/example/ui/components/ResponsiveLayoutHelper.kt
Solusi: Rename .dp() → .scaledDp() dan .sp() → .scaledSp()

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
- JANGAN nolak buka link GitHub publik
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
- Versi saat ini : V1.30.4
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
8.  Tampilan & Background
9.  Tampilan TV (responsive — V1.30.3)
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
- Tidak ada (semua fitur selesai per V1.30.4)
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
5. 2 opsi akses file (OPSI A: link, OPSI B: navigasi)
6. Kode timpa full (bukan edit manual)
7. Pesan commit (text persis untuk kolom commit message)
8. Bahasa Indonesia: simple, tidak teknis rumit, ramah, sopan

=== BAGIAN 1B — ATURAN ANTI-TRUNCATION ===

Jika kode terlalu panjang melebihi batas aman 1 bubble chat,
AI WAJIB otomatis memecah menjadi BEBERAPA BAGIAN:

1. Beri judul: "BAGIAN 1 DARI N", "BAGIAN 2 DARI N"
2. Akhir tiap bagian: "(lanjut di BAGIAN berikutnya)"
3. Awal tiap bagian: "(sambungan dari BAGIAN sebelumnya)"
4. User cukup copy-paste semua BAGIAN berurutan
5. JANGAN potong kode di tengah fungsi/blok
6. JANGAN bilang "kode dilanjut di chat berikutnya"
7. BAGIAN wajib seimbang (rata)

=== BAGIAN 1C — ATURAN LINK GITHUB ===

1. JANGAN tolak buka link GitHub publik
2. JANGAN gabung link 1 file dengan link lain
3. Kecuali butuh login akun → baru boleh tolak
4. Repo Masjid.io PUBLIC → bebas diakses

=== BAGIAN 1D — ATURAN SIMPLIFIKASI ===

1. Elemen 5 (2 Opsi Akses) TIDAK perlu ditampilkan lagi
2. Elemen 8 (Bahasa Indonesia) TIDAK perlu ditampilkan lagi
3. Fokus: folder, path, nama file, URL, kode, pesan commit
4. Kalau kode muat 1 bubble → kirim 1 bubble
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

DEVICE: HP Android (bukan PC/laptop)
REPO: https://github.com/andikune-ux/Masjid.io (PUBLIC)

APLIKASI:
  Nama           : Masjid.io
  Platform       : Android TV (Jetpack Compose)
  Namespace      : com.example
  Application ID : com.aistudio.masjidio.tkvpmz
  Versi saat ini : V1.30.4
  PIN Developer  : 140399

=== BAGIAN 4 — STRUKTUR MENU SETTINGS (20 KATEGORI) ===

1. Lokasi & Waktu Sholat
2. Pengaturan Waktu
3. Durasi & Hitungan Mundur
4. Identitas Masjid
5. Jadwal Petugas & Foto
6. Donasi QRIS & Rekening
7. Video Kegiatan Masjid
8. Tampilan & Background
9. Tampilan TV
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

FORMAT EXPORT:
---BEGIN--- path/file.kt
[ISI KODE LENGKAP]
---END--- path/file.kt

=== BAGIAN 13 — FITUR AUTO-SHOW CRASH LOG ===

- Saat crash, log disimpan ke filesDir/crashes/
- Flag disimpan di SharedPreferences
- Saat app dibuka, dialog crash muncul otomatis
- Tombol: Salin + Kembali

=== BAGIAN 14 — ATURAN KERJA SAMA DENGAN AI BARU ===

Jika user ganti AI, AI baru WAJIB:
1. Baca BACKUP AMAN terlebih dahulu.
2. Pahami struktur aplikasi + isi kode.
3. Ikuti aturan 8 elemen format.
4. Ikuti aturan anti-truncation.
5. Ikuti aturan link GitHub.
6. Konfirmasi dulu sebelum eksekusi.
7. Jangan menebak - tanya user kalau tidak tahu.

=== BAGIAN 15 — LINK GITHUB SOURCE CODE ===

AI baru DAPAT membuka sendiri kode lengkap dari GitHub.
Repo PUBLIC, tidak butuh login atau token.

LINK UTAMA:
  Repo Utama: https://github.com/andikune-ux/Masjid.io
  Daftar Semua File: https://api.github.com/repos/andikune-ux/Masjid.io/git/trees/main?recursive=1

LINK RAW (baca file langsung):
  Base Raw: https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/

LINK BLOB (lihat di browser):
  Base Blob: https://github.com/andikune-ux/Masjid.io/blob/main/

FOLDER KUNCI:
- MainActivity.kt
- util/BackupManager.kt
- util/CrashReporter.kt
- util/GithubSourceFetcher.kt
- util/SettingsTransferHelper.kt
- util/RemoteControlClient.kt
- util/MediaTransferHelper.kt (V1.30.4)
- data/AppKnowledge.kt
- data/UpdateHistory.kt
- data/local/SettingsRepository.kt
- data/local/SunMoonCalculator.kt
- data/model/AppSettings.kt
- ui/settings/SettingsScreen.kt
- ui/settings/AboutSettingsPane.kt
- ui/settings/CustomBackgroundPane.kt
- ui/settings/TvDisplaySettingsPane.kt
- ui/remote/RemoteServer.kt
- ui/remote/DeviceDiscovery.kt
- ui/remote/IoControlScreen.kt
- ui/components/MakkahDynamicBackground.kt
- ui/components/ResponsiveLayoutHelper.kt
- ui/home/HomeScreen.kt
- app/build.gradle.kts
- gradle/libs.versions.toml
- .github/workflows/build.yml

CATATAN:
- Repo PUBLIC -> bebas diakses kapan saja
- JANGAN minta user copy-paste manual kalau bisa buka sendiri

=== BAGIAN 16 — FITUR iO CONTROL ===

FITUR:
- HP sebagai remote tampilan TV via WiFi/Hotspot sama
- Auto-discovery via UDP broadcast (port 45678)
- UI radar biru (sweep 360° + pulse)
- Transfer semua pengaturan antar device
- Endpoint /api/io/receive PUBLIC (fix 401)

FILE: DeviceDiscovery.kt, IoControlScreen.kt, RemoteServer.kt,
      NetworkHelper.kt, SettingsTransferHelper.kt, RemoteControlClient.kt

=== BAGIAN 17 — FITUR TEMA MAKKAH DINAMIS ===

FITUR:
- Langit Makkah bergerak real-time 24 jam
- Matahari melengkung, bulan fase asli via algoritma lunar
- Awan 12 bulatan 3-layer, burung V-formation, bintang berkelip
- Hujan + petir real-time (Open-Meteo)
- Siluet Masjidil Haram + Ka'bah HD + pita emas Hizam
- Lampu arcade + menara NYALA saat malam
- Tema DEFAULT

FILE: SunMoonCalculator.kt, MakkahDynamicBackground.kt,
      AppSettings.kt, HomeScreen.kt, CustomBackgroundPane.kt

=== BAGIAN 18 — FITUR APK INSTALL DARI FILE ===

FITUR:
- Tap file APK tersimpan -> INSTALL
- Tombol merah terpisah -> HAPUS
- Visual: border hijau + "TAP UNTUK INSTALL"
- Folder: /sdcard/masjid.io/pembaharuan aplikasi/

FILE: AboutSettingsPane.kt, ApkDownloader.kt

=== BAGIAN 19 — FITUR RESPONSIVE AUTO-SCALE TV ===

FITUR:
- Deteksi otomatis resolusi TV (px, dp, aspect, density)
- Base 1920x1080 — auto-scale 0.6× s/d 2.5×
- Safe Area Padding 0-10% untuk overscan bezel
- Preset: AUTO / STANDAR / ULTRAWIDE / 4:3
- Menu baru: Tampilan TV (kategori ke-20)

FILE: ResponsiveLayoutHelper.kt, TvDisplaySettingsPane.kt,
      HomeScreen.kt, SettingsScreen.kt, AppSettings.kt

FIELD: tvAutoScaleEnabled, tvSafeAreaPercent, tvLayoutPreset

CATATAN:
- Extension .scaledDp() dan .scaledSp() — JANGAN pakai .dp()/.sp()
- ScreenInfo via CompositionLocal (ResponsiveRoot)

=== BAGIAN 20 — FITUR TRANSFER MEDIA iO CONTROL (V1.30.4) ===

FITUR BARU:
- Foto & video IKUT terkirim via iO Control (fix bug lama)
- Chunk upload 1 MB per request (support video besar)
- Base64 encoding untuk transfer via HTTP POST
- Kompres foto otomatis (1920px, quality 85%)
- Kompres video otomatis (MediaMuxer remux tanpa re-encode)
- Progress bar per-file + total keseluruhan
- Retry otomatis 3x per chunk
- Alur: kirim settings → auto lanjut kirim media
- File disimpan di filesDir/masjid_io/{qris|logo|background|video|slideshow|prayer_card}
- Endpoint media PUBLIC (tidak perlu login)

FILE TERKAIT:
- util/MediaTransferHelper.kt          -> Chunk + kompres (BARU)
- util/RemoteControlClient.kt          -> sendMediaFilesChunked()
- util/SettingsTransferHelper.kt       -> uploadMediaFiles()
- ui/remote/RemoteServer.kt            -> 4 endpoint media baru
- ui/remote/IoControlScreen.kt         -> MediaProgressView + enum baru

ENDPOINT BARU:
- POST /api/io/receive-media-start     -> mulai transfer
- POST /api/io/receive-media-chunk     -> kirim chunk (base64)
- POST /api/io/receive-media-finish    -> selesaikan + simpan
- GET  /api/io/media-status            -> cek status aktif

CATATAN PENTING:
- File besar (500 MB) butuh 10+ menit
- Jangan tutup aplikasi saat transfer
- Kompres video pakai MediaMuxer (stream copy, tidak re-encode)
- Progress 2 tingkat: per-file + total keseluruhan

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
