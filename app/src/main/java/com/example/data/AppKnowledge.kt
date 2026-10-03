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
│   ├── RemoteControlClient.kt         -> HTTP client iO Control (V1.30.7)
│   ├── MediaTransferHelper.kt         -> Chunk + kompres media (V1.30.4)
│   ├── IoBundleHelper.kt              -> Bundle .iO (V1.30.7 BARU)
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
    │   ├── LaporanSlide.kt            -> Slide Laporan Keuangan (V1.30.6)
    │   ├── KajianSlide.kt             -> Slide Jadwal Kajian
    │   └── SlideManager.kt            -> Manager rotasi slide
    ├── ramadhan/RamadhanOverlay.kt    -> Overlay Mode Ramadhan
    ├── cctv/CctvWidget.kt             -> Widget CCTV (RTSP/HTTP)
    ├── remote/
    │   ├── RemoteServer.kt            -> HTTP server (Remote + iO Control + media)
    │   ├── RemoteDashboard.kt         -> Info akses remote
    │   ├── DeviceDiscovery.kt         -> Discovery device via UDP
    │   ├── NetworkHelper.kt           -> Helper deteksi IP WiFi
    │   ├── IoControlScreen.kt         -> UI radar + konfirmasi restart (V1.30.7)
    │   ├── IoControlHelpSheet.kt      -> Panduan iO Control
    │   └── RestartCountdownOverlay.kt -> Overlay countdown restart
    ├── settings/
    │   ├── SettingsScreen.kt          -> Menu utama (20 kategori)
    │   ├── DeveloperSettingsPane.kt   -> Opsi Developer
    │   ├── DeveloperPinDialog.kt      -> PIN 140399
    │   ├── AboutSettingsPane.kt       -> Tentang + Sosmed + APK install
    │   ├── AudioSettingsPane.kt       -> Audio & Adzan
    │   ├── CountdownSettingsPane.kt   -> Durasi & countdown
    │   ├── CustomBackgroundPane.kt    -> Background + Tema Makkah (V1.30.7)
    │   ├── IoBundleListSection.kt     -> Daftar file template .iO (V1.30.7 BARU)
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
    │   ├── AutoFocusPane.kt           -> Auto-focus D-pad (V1.30.5)
    │   ├── TvSlider.kt                -> Slider TV
    │   ├── TvToggle.kt                -> Toggle TV
    │   ├── TvFocusHelper.kt           -> Helper fokus D-pad
    │   ├── FocusHelper.kt             -> Helper fokus tambahan
    │   ├── NeonFocusBorder.kt         -> Border fokus berputar (V1.30.6)
    │   ├── PhotoSlideshow.kt          -> Slideshow foto
    │   ├── MasjidVideoPlayer.kt       -> Video player (ExoPlayer)
    │   ├── UpdateDialog.kt            -> Dialog update (V1.30.6 fix tombol)
    │   ├── OfficerCarousel.kt         -> Panel imam/muadzin
    │   ├── PrayerCardsRow.kt          -> Kartu sholat (with foto)
    │   ├── PrayerProgressBar.kt       -> Bar progres sholat
    │   ├── RunningTextMarquee.kt      -> Running text bergulir
    │   ├── PinDialog.kt               -> Dialog PIN
    │   ├── ChangePinDialog.kt         -> Dialog ubah PIN
    │   ├── TopBar.kt                  -> Top bar (V1.30.6 NeonFocusBorder)
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

5. NAVIGASI D-PAD (V1.30.5 + V1.30.6)
   - Auto-focus ke elemen pertama pane (AutoFocusPane.kt)
   - Border fokus berputar (NeonFocusBorder.kt)
   - Fokus pindah INSTANT, animasi scale smooth 150ms
   - Blur glow tetap dipertahankan
   - Fokus tidak mendarat di tombol Kembali
   - Konsisten di semua layar (Settings, iO Control, HomeScreen)

6. MODE FOKUS SHOLAT (4 Fase)
   - Adzan / Qobliyah / Fardhu / Dzikir

7. AUDIO
   - Mode Beep Only / Full Adzan / Silent

8. SLIDE FULLSCREEN
   - Slide QRIS / Laporan / Kajian auto-rotate
   - Slide Laporan pakai icon dompet (V1.30.6, bukan celengan babi)

9. CCTV MASJID
   - Widget PiP (RTSP + HTTP)

10. iO CONTROL (V1.30.1 + V1.30.4 + V1.30.7)
    - HP sebagai remote TV via WiFi
    - Auto-discovery UDP + UI radar
    - Transfer semua pengaturan antar device
    - TRANSFER MEDIA (foto + video) via chunk upload
    - Kompres foto otomatis (1920px, 85%)
    - Kompres video otomatis (MediaMuxer)
    - Progress bar per-file + total
    - Retry otomatis 3x per chunk
    - UPDATE PATH LOKAL setelah media masuk TV (V1.30.6)
    - VERIFIKASI transfer sebelum restart (V1.30.7)
    - Konfirmasi restart MANUAL via tombol (V1.30.7)

11. REMOTE CONTROL WEB
    - HTTP server + dashboard browser HP

12. MODE RAMADHAN
    - Countdown Imsak/Iftar + Tarawih + Kultum

13. KIOSK MODE
    - Lock task + Watchdog + Auto-start

14. TENTANG APLIKASI
    - Periksa Update + Riwayat + Install APK
    - Tap file APK = install, tombol merah = hapus
    - Dialog update fix: tombol selalu terlihat (V1.30.6)

15. OPSI DEVELOPER (PIN 140399)
    - Backup Aman + Riwayat Crash + WhatsApp Fonnte

16. VERSIONING OTOMATIS
    - Format V{inti}.{tanggal}.{countHariIni}

17. WHATSAPP REPORT (FONNTE)
    - Notifikasi crash otomatis ke grup admin

18. FOTO SLIDESHOW + 19. KONTEN ROTASI

20. DZIKIR SETELAH SHOLAT (8 dzikir)

21. AUTO-SHOW CRASH LOG

22. FULL SOURCE CODE EXPORT

23. FIX CRASH NESTED SCROLL + FADE ANIMASI

24. SALIN URL OTOMATIS

25. APK INSTALL DARI FILE TERSIMPAN

26. TAMPILAN TV RESPONSIF (V1.30.3)

27. TRANSFER MEDIA iO CONTROL (V1.30.4)
    - Foto & video ikut terkirim via chunk
    - Progress per-file + total
    - Retry otomatis
    - Notifikasi status (sukses/gagal/berjalan)

28. FIX MEDIA PATH (V1.30.6)
    - Update settings TV dengan path lokal setelah media masuk
    - Foto/video langsung muncul di TV setelah transfer
    - Support: QRIS, Logo, Background, Kartu Sholat, Video, Slideshow

29. VERIFIKASI TRANSFER + KONFIRMASI RESTART MANUAL (V1.30.7) — BARU
    - Cek semua file benar-benar terkirim sebelum restart
    - Restart MANUAL via tombol konfirmasi (bukan auto)
    - Tombol COBA LAGI untuk kirim ulang file gagal saja
    - Tombol LIHAT LOG untuk stack trace Kotlin asli
    - Tombol RESTART SAJA kalau ada file gagal
    - Tombol LEWATI → template aktif otomatis saat app dibuka ulang

30. FILE TEMPLATE .iO (V1.30.7) — BARU
    - Setiap transfer sukses → auto-bikin file .iO
    - File .iO = ZIP (settings.json + metadata.json + media/)
    - Nama file: {Merk HP}-{dd-MM-yyyy HH.mm}.iO
    - Lokasi: /sdcard/masjid.io/Terima/
    - Daftar file template di menu Tampilan & Background
    - Tombol GUNAKAN / INFO / HAPUS per file
    - File TIDAK dihapus otomatis (kecuali user hapus manual)
    - Apply template → media lama DITAMBAH (bukan ditimpa)
    """.trimIndent()

    val UPDATE_HISTORY = """
V1.30.7 (03 Oktober 2026)
- Verifikasi transfer + konfirmasi restart manual + file template .iO
- Setiap transfer sukses → auto-bikin file .iO (bundle ZIP)
- Daftar file template di menu Tampilan & Background
- Tombol GUNAKAN / INFO / HAPUS untuk tiap file
- Retry otomatis hanya file yang gagal
- Error detail: stack trace Kotlin asli

V1.30.6 (03 Oktober 2026)
- Fix fokus D-pad TV + fix transfer media iO Control
- Foto/video tidak muncul di TV (fix path lokal)
- AutoFocusPane: auto-fokus ke elemen pertama pane
- Border fokus berputar kembali (NeonFocusBorder)
- Icon dompet di Laporan Keuangan (ganti celengan babi)
- UpdateDialog: tombol selalu terlihat + changelog scrollable

V1.30.5 (03 Oktober 2026)
- Auto-Focus Pane & Smooth D-pad Navigation
- Fokus langsung ke elemen pertama pane
- Border INSTANT + scale smooth 150ms
- File baru: AutoFocusPane.kt

V1.30.4 (02 Oktober 2026)
- Transfer Media iO Control — Foto & Video Antar Device
- Chunk upload 1 MB per request
- Kompres foto + video otomatis
- Progress bar per-file + total
- Retry otomatis 3x

V1.30.3 (02 Oktober 2026)
- Responsive Auto-Scale semua ukuran TV
- Deteksi resolusi, aspect ratio, density
- Safe Area padding 0-10%
- Menu baru: Tampilan TV

V1.30.2 (02 Oktober 2026)
- Tema Makkah Dinamis (default)
- Langit real-time + cuaca otomatis
- Fix iO Control transfer (fix 401)

V1.30.1 (30 September 2026)
- iO Control: HP sebagai remote TV via WiFi
- Auto-discovery device (UDP broadcast)

V1.29.3 (29 September 2026)
- Fix crash nested scroll
- Ganti Crossfade ke Box + fade manual
- Auto-show dialog crash log

V1.28.4 (29 September 2026)
- Slide Fullscreen (QRIS, Laporan, Kajian)
- Konten Rotasi (Ayat, Hadits, Asmaul Husna)
- CCTV + Remote Control

V1.28.3 (29 September 2026)
- Fokus D-pad, TvSlider + TvToggle
- LocationSettingsPane + IdentitySettingsPane

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
24. Foto/video tidak muncul di TV → update path lokal (V1.30.6)
25. Fokus D-pad hilang/tidak jelas → AutoFocusPane (V1.30.5)
26. Border fokus tidak terlihat (tertutup solid) → tipis dim gold (V1.30.6)
27. Icon celengan babi di Laporan Keuangan → dompet (V1.30.6)
28. Tombol UpdateDialog tidak terlihat → fillMaxHeight 0.92f (V1.30.6)
29. Restart otomatis padahal file belum semua terkirim → verifikasi + restart manual (V1.30.7)
30. Error transfer tidak jelas penyebabnya → simpan stack trace Kotlin asli (V1.30.7)

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

[03-10-2026] - V1.30.7
Error: File CustomBackgroundPane.kt refer ke IoBundleListSection yang belum ada
File: app/src/main/java/com/example/ui/settings/CustomBackgroundPane.kt
Solusi: Buat IoBundleListSection.kt dulu (BATCH 5B) sebelum commit CustomBackgroundPane.kt

[03-10-2026] - V1.30.7
Error: RemoteControlClient.kt mediaChunk() dan mediaFinish() swallow exception
File: app/src/main/java/com/example/util/RemoteControlClient.kt
Solusi: Ganti return false jadi throw e — biar stack trace lengkap tersimpan

[03-10-2026] - V1.30.6
Error: Fungsi applyMediaPathToSettings tidak ditemukan
File: app/src/main/java/com/example/ui/remote/RemoteServer.kt
Solusi: Tambah fungsi applyMediaPathToSettings setelah saveMediaFile

[03-10-2026] - V1.30.6
Error: Konten RemoteServer.kt terpotong saat paste
File: app/src/main/java/com/example/ui/remote/RemoteServer.kt
Solusi: Bagi jadi 8 BAGIAN rata, paste berurutan

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
- Versi saat ini : V1.30.7
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
- Tidak ada (semua fitur selesai per V1.30.7)
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
  Versi saat ini : V1.30.7
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

LINK RAW: https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/
LINK BLOB: https://github.com/andikune-ux/Masjid.io/blob/main/

FOLDER KUNCI:
- app/src/main/java/com/example/MainActivity.kt
- app/src/main/java/com/example/util/BackupManager.kt
- app/src/main/java/com/example/util/CrashReporter.kt
- app/src/main/java/com/example/util/GithubSourceFetcher.kt
- app/src/main/java/com/example/util/SettingsTransferHelper.kt
- app/src/main/java/com/example/util/RemoteControlClient.kt
- app/src/main/java/com/example/util/MediaTransferHelper.kt
- app/src/main/java/com/example/util/IoBundleHelper.kt
- app/src/main/java/com/example/data/AppKnowledge.kt
- app/src/main/java/com/example/data/UpdateHistory.kt
- app/src/main/java/com/example/data/local/SettingsRepository.kt
- app/src/main/java/com/example/data/local/SunMoonCalculator.kt
- app/src/main/java/com/example/data/model/AppSettings.kt
- app/src/main/java/com/example/ui/settings/SettingsScreen.kt
- app/src/main/java/com/example/ui/settings/AboutSettingsPane.kt
- app/src/main/java/com/example/ui/settings/TvDisplaySettingsPane.kt
- app/src/main/java/com/example/ui/settings/CustomBackgroundPane.kt
- app/src/main/java/com/example/ui/settings/IoBundleListSection.kt
- app/src/main/java/com/example/ui/remote/RemoteServer.kt
- app/src/main/java/com/example/ui/remote/DeviceDiscovery.kt
- app/src/main/java/com/example/ui/remote/IoControlScreen.kt
- app/src/main/java/com/example/ui/components/MakkahDynamicBackground.kt
- app/src/main/java/com/example/ui/components/ResponsiveLayoutHelper.kt
- app/src/main/java/com/example/ui/components/AutoFocusPane.kt
- app/src/main/java/com/example/ui/components/NeonFocusBorder.kt
- app/src/main/java/com/example/ui/home/HomeScreen.kt
- app/build.gradle.kts
- gradle/libs.versions.toml
- .github/workflows/build.yml

=== BAGIAN 16 — FITUR iO CONTROL ===

- HP sebagai remote TV via WiFi
- Auto-discovery UDP (port 45678)
- Transfer semua pengaturan antar device
- Endpoint /api/io/receive PUBLIC (fix 401)

=== BAGIAN 17 — FITUR TEMA MAKKAH DINAMIS ===

- Langit real-time 24 jam
- Matahari melengkung + bulan fase asli
- Awan, burung, bintang, hujan, petir
- Siluet Masjidil Haram + Ka'bah + Hizam emas
- Lampu arcade + menara NYALA saat malam
- Default theme

=== BAGIAN 18 — FITUR APK INSTALL DARI FILE ===

- Tap file APK = INSTALL
- Tombol merah terpisah = HAPUS
- Folder: /sdcard/masjid.io/pembaharuan aplikasi/

=== BAGIAN 19 — FITUR RESPONSIVE AUTO-SCALE TV ===

- Deteksi resolusi TV otomatis
- Base 1920x1080 — auto-scale 0.6× s/d 2.5×
- Safe Area Padding 0-10%
- Preset: AUTO / STANDAR / ULTRAWIDE / 4:3
- Menu: Tampilan TV (kategori ke-20)
- Extension: .scaledDp() & .scaledSp() — JANGAN pakai .dp()/.sp()

=== BAGIAN 20 — FITUR TRANSFER MEDIA iO CONTROL ===

- Foto & video ikut terkirim via chunk upload (1 MB/chunk)
- Base64 encode untuk HTTP POST
- Kompres foto (1920px, 85%) + video (MediaMuxer)
- Progress bar per-file + total
- Retry otomatis 3x per chunk
- Alur: settings → media → finalize
- File di filesDir/masjid_io/{qris|logo|background|video|slideshow|prayer_card}
- Endpoint media PUBLIC

=== BAGIAN 21 — FITUR AUTO-FOCUS D-PAD (V1.30.5) ===

- AutoFocusPane.kt: auto-fokus ke elemen pertama pane
- Fokus tidak mendarat di tombol Kembali
- Border fokus INSTANT pindah
- Scale smooth 150ms menyusul
- Blur glow tetap ada

=== BAGIAN 22 — FITUR MEDIA PATH FIX (V1.30.6) ===

- Foto/video tidak muncul di TV (fix path lokal)
- applyMediaPathToSettings() di RemoteServer.kt
- Ganti path HP → path lokal setelah media tersimpan
- Support: QRIS, Logo, Background, Kartu Sholat, Video, Slideshow

=== BAGIAN 23 — FITUR BORDER BERPUTAR (V1.30.6) ===

- NeonFocusBorder: core border tipis (dim gold 20%)
- Glow berputar 2-kutub (putih + emas)
- Tail 30% dari keliling
- Blur tetap ada
- Animasi berputar terlihat jelas

=== BAGIAN 24 — FITUR UPDATE DIALOG FIX (V1.30.6) ===

- Dialog pakai fillMaxHeight(0.92f) — maks 92% tinggi layar
- Changelog pakai weight(1f) + verticalScroll
- Tombol SKIP / NANTI / UPDATE selalu terlihat
- build.yml: extract changelog dari UpdateHistory.kt

=== BAGIAN 25 — FITUR VERIFIKASI + RESTART MANUAL (V1.30.7) ===

- Transfer dianggap selesai kalau ada hasil verifikasi
- Phase baru: VERIFYING + READY_TO_RESTART
- Restart MANUAL via tombol KONFIRMASI RESTART
- Tombol COBA LAGI: retry HANYA file yang gagal
- Tombol LIHAT LOG: popup stack trace Kotlin asli
- Tombol RESTART SAJA: kalau ada gagal, tetap bisa restart
- Tombol LEWATI: template aktif otomatis saat app dibuka ulang
- Stack trace disimpan dari setiap catch — bukan kode singkatan
- FailureLogDialog: tombol SALIN LOG ke clipboard

=== BAGIAN 26 — FITUR FILE TEMPLATE .iO (V1.30.7) ===

- Format .iO = ZIP (settings.json + metadata.json + media/)
- Auto-bikin setiap transfer selesai (sukses/gagal)
- Nama file: {Merk HP}-{dd-MM-yyyy HH.mm}.iO
- Lokasi: /sdcard/masjid.io/Terima/
- File TIDAK dihapus otomatis (kecuali user hapus manual)
- Daftar file template di menu Tampilan & Background
- 3 tombol per file: GUNAKAN / INFO / HAPUS
- Title kecil: "8 sukses · 2 gagal · 📷 12 foto · 🎬 1 video"
- Apply template → media lama DITAMBAH (bukan ditimpa)
- Setelah apply → dialog konfirmasi restart 5 detik
- File baru: IoBundleHelper.kt
- File baru: IoBundleListSection.kt
- Endpoint baru: /api/io/list-bundles, /api/io/delete-bundle, /api/io/restore-bundle

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
