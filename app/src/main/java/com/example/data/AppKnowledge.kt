package com.example.data

/**
 * AppKnowledge — Data statis aplikasi MASJID.IO.
 */
object AppKnowledge {

    // ============================================================
    // KONSTANTA UTAMA
    // ============================================================
    const val APP_NAME = "MASJID.IO"
    const val APP_DESCRIPTION = "Display jadwal sholat Android TV untuk masjid"
    const val REPO_URL = "https://github.com/andikune-ux/Masjid.io"

    // ============================================================
    // LINK GITHUB — UNTUK AI BARU BUKA SENDIRI
    // ============================================================
    const val GITHUB_REPO = "https://github.com/andikune-ux/Masjid.io"
    const val GITHUB_TREE_API = "https://api.github.com/repos/andikune-ux/Masjid.io/git/trees/main?recursive=1"
    const val GITHUB_RAW_BASE = "https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/"
    const val GITHUB_BLOB_BASE = "https://github.com/andikune-ux/Masjid.io/blob/main/"

    // ============================================================
    // STRUKTUR APLIKASI
    // ============================================================
    val APP_STRUCTURE = """
app/src/main/java/com/example/
├── MainActivity.kt                    -> Activity utama
├── audio/
│   └── SoundManager.kt                -> Suara beep & adzan
├── data/
│   ├── model/
│   │   └── AppSettings.kt             -> Model pengaturan
│   ├── local/
│   │   ├── SettingsRepository.kt      -> Simpan/load setting
│   │   ├── PrayerTimesCalculator.kt   -> Hitung jadwal sholat
│   │   ├── IslamicCalendar.kt         -> Kalender Hijriah
│   │   ├── IslamicWisdomStore.kt      -> Data kartu nasihat
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
├── receiver/
│   └── BootReceiver.kt                -> Terima broadcast boot
├── util/
│   ├── BackupManager.kt               -> Backup Aman (TXT)
│   ├── CrashReporter.kt               -> Log crash + WA Fonnte
│   ├── CrashAutoShowHelper.kt         -> Auto-show dialog crash (BARU)
│   ├── GithubSourceFetcher.kt         -> Fetch source dari GitHub (BARU)
│   ├── FonnteSender.kt                -> Kirim WA via Fonnte API
│   ├── UpdateManager.kt               -> Cek update GitHub
│   ├── ApkDownloader.kt               -> Download & install APK
│   └── BuildHistoryFetcher.kt         -> Fetch build history GitHub
└── ui/
    ├── home/HomeScreen.kt             -> Tampilan utama
    ├── focus/PrayerFocusOverlay.kt    -> Mode fokus 4 fase
    ├── slides/
    │   ├── QrisSlide.kt               -> Slide QRIS Infaq
    │   ├── LaporanSlide.kt            -> Slide Laporan Keuangan
    │   ├── KajianSlide.kt             -> Slide Jadwal Kajian
    │   └── SlideManager.kt            -> Manager rotasi slide
    ├── ramadhan/
    │   └── RamadhanOverlay.kt         -> Overlay Mode Ramadhan
    ├── cctv/
    │   └── CctvWidget.kt              -> Widget CCTV (RTSP/HTTP)
    ├── remote/
    │   ├── RemoteServer.kt            -> HTTP server untuk remote
    │   └── RemoteDashboard.kt         -> Info akses remote
    ├── settings/
    │   ├── SettingsScreen.kt          -> Menu utama (19 kategori)
    │   ├── DeveloperSettingsPane.kt   -> Opsi Developer
    │   ├── DeveloperPinDialog.kt      -> PIN 140399
    │   ├── AboutSettingsPane.kt       -> Tentang + Sosmed
    │   ├── AudioSettingsPane.kt       -> Audio & Adzan
    │   ├── CountdownSettingsPane.kt   -> Durasi & countdown
    │   ├── CustomBackgroundPane.kt    -> Background
    │   ├── IdentitySettingsPane.kt    -> Identitas + logo
    │   ├── LocationSettingsPane.kt    -> Lokasi + GPS
    │   ├── PowerSettingsPane.kt       -> Daya & Booting
    │   ├── QrisSettingsPane.kt        -> Donasi QRIS
    │   ├── RamadhanSettingsPane.kt    -> Mode Ramadhan
    │   ├── RunningTextSettingsPane.kt -> Running text
    │   ├── SecuritySettingsPane.kt    -> Keamanan
    │   ├── SlideSettingsPane.kt       -> Slide Fullscreen
    │   ├── CctvSettingsPane.kt        -> CCTV Masjid
    │   ├── RemoteSettingsPane.kt      -> Remote Control
    │   ├── TimeSettingsPane.kt        -> Waktu manual
    │   ├── VideoSettingsPane.kt       -> Video & Foto Slideshow
    │   ├── WeeklyOfficersSettingsPane.kt -> Petugas mingguan
    │   ├── WisdomSettingsPane.kt      -> Kartu nasihat
    │   ├── RiwayatCrashScreen.kt      -> Riwayat crash
    │   └── RiwayatUpdateScreen.kt     -> Riwayat update
    ├── components/
    │   ├── TvSlider.kt                -> Slider TV
    │   ├── TvToggle.kt                -> Toggle TV
    │   ├── TvFocusHelper.kt           -> Helper fokus D-pad
    │   ├── PhotoSlideshow.kt          -> Slideshow foto
    │   ├── MasjidVideoPlayer.kt       -> Video player (ExoPlayer)
    │   ├── UpdateDialog.kt            -> Dialog update
    │   ├── OfficerCarousel.kt         -> Panel imam/muadzin
    │   ├── PrayerCardsRow.kt          -> Kartu sholat (with foto)
    │   ├── TopBar.kt                  -> Top bar
    │   └── ... (komponen lain)
    └── theme/                          -> Warna & tipografi
    """.trimIndent()
    
    // ============================================================
    // FITUR APLIKASI
    // ============================================================
    val APP_FEATURES = """
1. JADWAL SHOLAT
   - 6 waktu: Subuh, Syuruq, Dzuhur, Ashar, Maghrib, Isya
   - Countdown + progress bar visual
   - Background foto di kartu (opsional)

2. TAMPILAN UTAMA
   - Jam digital besar + tanggal Hijriah/Masehi
   - Nama masjid & logo
   - Kartu 6 waktu sejajar horizontal
   - Panel Imam & Muadzin auto-slide
   - Running text + Video/Foto Slideshow
   - Konten rotasi (Ayat/Hadits/Asmaul Husna)
   - CCTV Widget (PiP sudut layar)

3. MODE FOKUS SHOLAT (4 Fase)
   - Fase 1 Adzan: doa + countdown iqamah
   - Fase 2 Qobliyah: countdown 5 menit
   - Fase 3 Fardhu: panduan sholat
   - Fase 4 Dzikir: rotasi dzikir setelah sholat

4. AUDIO
   - Mode Beep Only (bip...bip...bip)
   - Mode Full Adzan (Makkah/Madinah/Indonesia)
   - Mode Silent
   - Volume & durasi beep bisa diatur

5. SLIDE FULLSCREEN
   - Slide QRIS Infaq (dengan animasi pulse)
   - Slide Laporan Keuangan (pemasukan/pengeluaran)
   - Slide Jadwal Kajian (foto ustadz + tema)
   - Auto-rotate setiap X detik
   - Hanya tampil saat idle

6. CCTV MASJID
   - Widget PiP di sudut layar
   - Support RTSP (via ExoPlayer)
   - Support HTTP Snapshot / MJPEG / DVR Dashboard
   - Posisi & ukuran bisa diatur

7. REMOTE CONTROL
   - HTTP Server mini berjalan di background
   - Akses dashboard dari HP via browser
   - Bisa ubah running text, PIN, lihat status

8. MODE RAMADHAN
   - Countdown Imsak & Iftar besar
   - Jadwal Tarawih + Kultum
   - Menu Sahur/Iftar
   - Doa Berbuka otomatis

9. KIOSK MODE
   - Lock task + Watchdog + Auto-start
   - Launcher default Android TV

10. TENTANG APLIKASI
    - Versi + Periksa Update (progress bar)
    - Riwayat Update + Install APK
    - Icon Sosmed (WhatsApp, TikTok, Instagram)

11. OPSI DEVELOPER (PIN 140399)
    - Backup Aman (termasuk build history)
    - Riwayat Crash + Refresh Build History
    - WhatsApp Fonnte (notifikasi crash)

12. VERSIONING OTOMATIS
    - Format: V{inti}.{tanggal}.{countHariIni}
    - Auto GitHub Release setiap build

13. WHATSAPP REPORT (FONNTE)
    - Kirim notifikasi WA otomatis saat crash
    - Token + Group ID diisi di Opsi Developer

14. FOTO SLIDESHOW
    - Galeri foto kegiatan masjid
    - Auto-ganti setiap X detik
    - Tampil di posisi video (kalau video tidak aktif)

15. KONTEN ROTASI
    - Ayat Al-Quran (10 ayat pilihan)
    - Hadits (10 hadits pilihan)
    - Asmaul Husna (99 nama Allah)
    - Rotasi otomatis dengan animasi

16. DZIKIR SETELAH SHOLAT
    - 8 dzikir lengkap (Arab + Latin + arti)
    - Auto-rotate 8 detik
    - Phase 4 di Mode Fokus

17. AUTO-SHOW CRASH LOG (V1.29.3+)
    - Deteksi crash saat force close
    - Saat dibuka kembali, dialog crash muncul otomatis
    - Tombol Salin + Tombol Kembali

18. FULL SOURCE CODE EXPORT (V1.29.3+)
    - Backup Aman menyertakan ISI SEMUA FILE kode
    - Cara C: folder lokal + fallback GitHub API
    - Format: ---BEGIN--- path ---END--- path
    - AI baru langsung paham tanpa tanya user
    """.trimIndent()

    // ============================================================
    // RIWAYAT UPDATE (RINGKAS)
    // ============================================================
    val UPDATE_HISTORY = """
V1.28.4 (29 September 2026)
- Slide Fullscreen (QRIS, Laporan, Kajian)
- Konten Rotasi (Ayat, Hadits, Asmaul Husna)
- Mode Ramadhan lengkap (Imsak/Iftar/Tarawih)
- CCTV Widget (RTSP + HTTP)
- Remote Control via HP
- Dzikir setelah sholat (Phase 4)
- Background foto di kartu sholat

V1.28.3 (29 September 2026)
- Fokus D-pad lebih tebal
- TvSlider + TvToggle (remote-friendly)
- LocationSettingsPane + IdentitySettingsPane lengkap
- RunningTextSettingsPane lengkap
- Bersihkan lint warning

V1.28.2 (28 September 2026)
- Versioning otomatis + Keystore permanen
- Backup Aman + Log Crash + Riwayat Crash
- Periksa Update + ApkDownloader
- WhatsApp Fonnte
- Foto Slideshow
- Build History Fetcher

V1.28.1 (28 September 2026)
- Foto per sesi petugas
- Foto profil kotak tumpul
- HomeScreen: video besar, jam kecil
- Suara beep diperbaiki

V1.28.0 (27 September 2026)
- Versi dasar: jadwal sholat, mode fokus, kiosk, dll
    """.trimIndent()
    
    // ============================================================
    // MASALAH & SOLUSI
    // ============================================================
    val KNOWN_ISSUES = """
=== SUDAH DIPERBAIKI ===
1. Foto ustadz tidak tampil di HomeScreen
   Solusi: Normalisasi nama hari

2. Suara beep terlalu pendek & lucu
   Solusi: TONE_CDMA_ALERT_CALL_GUARD + durasi dinamis

3. Update APK harus uninstall dulu
   Solusi: Keystore permanen di repo

4. File "File_paths.xml" error (huruf besar F)
   Solusi: Rename jadi "file_paths.xml"

5. Konflik GitHub Actions - signing error
   Solusi: Commit debug.keystore ke repo

6. Setup keystore gagal karena .gitignore
   Solusi: Pakai "git add -f debug.keystore"

7. Backup Aman error EPERM
   Solusi: Cek permission + fallback ke app dir

8. Build error: KSP 2.3.5 NPE di GitHub Actions
   Solusi: Upgrade KSP ke 2.3.12

9. Build error: phaseNum di luar scope
   Solusi: Pindah phaseNum ke luar Box

10. Build error: LaunchedEffect belum di-import
    Solusi: Tambah import LaunchedEffect

11. Build error: Icon Battery tidak ada
    Solusi: Ganti ke BatteryFull

12. Build error: LocationSettingsPane & IdentitySettingsPane hilang
    Solusi: Restore + buat file sendiri

13. Fitur Location & Identity pakai placeholder
    Solusi: Buat file sendiri + hapus placeholder di SettingsScreen

=== BELUM DIPERBAIKI ===
(Tidak ada - semua sudah diperbaiki)
    """.trimIndent()

    // ============================================================
    // BUILD ERROR HISTORY
    // ============================================================
    val BUILD_ERROR_HISTORY = """
FORMAT:
---
[Tanggal] - [Versi]
Error: [deskripsi singkat]
File: [nama file]
Solusi: [cara memperbaiki]
---

=== RIWAYAT ===

[29-09-2026] - V1.28.4
Error: PrayerCardsRow belum terima parameter settings
File: HomeScreen.kt
Solusi: Update pemanggilan PrayerCardsRow(prayerItems, settings)

[29-09-2026] - V1.28.4
Error: SettingsScreen belum terima isRemoteServerRunning
File: MainActivity.kt
Solusi: Passing isRemoteServerRunning ke SettingsScreen

[29-09-2026] - V1.28.3
Error: LocationSettingsPane & IdentitySettingsPane file terpisah hilang
File: SettingsScreen.kt
Solusi: Restore dengan membuat file sendiri + hapus placeholder

[29-09-2026] - V1.28.3
Error: phaseNum tidak ditemukan di scope Box
File: PrayerFocusOverlay.kt
Solusi: Pindah phaseNum ke atas Box

[29-09-2026] - V1.28.2
Error: LaunchedEffect tidak dikenal
File: AboutSettingsPane.kt
Solusi: Tambah import LaunchedEffect

[29-09-2026] - V1.28.2
Error: Icon Battery tidak tersedia
File: PowerSettingsPane.kt
Solusi: Ganti ke Icons.Default.BatteryFull

[29-09-2026] - V1.28.2
Error: KSP 2.3.5 NullPointerException di GitHub Actions
File: gradle/libs.versions.toml
Solusi: Upgrade KSP dari 2.3.5 ke 2.3.12
    """.trimIndent()

    // ============================================================
    // WORKFLOW INSTRUCTION
    // ============================================================
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
1. Visualisasi folder
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

VERSIONING:
- Format: V{inti}.{tanggal}.{countHariIni}
- Auto GitHub Release tiap build
- Keystore permanen di repo
    """.trimIndent()

    // ============================================================
    // DEVELOPER INSTRUCTION
    // ============================================================
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
- Versi saat ini : V1.29.2
- PIN Developer  : 140399

UPDATE WAJIB TIAP BUILD:
1. Bump versionName (otomatis dari version.properties)
2. Tambah entri baru di UpdateHistory.kt
3. Update AppKnowledge.kt (struktur + fitur + build error)
    """.trimIndent()

    // ============================================================
    // MEMORY INSTRUCTION
    // ============================================================
    val MEMORY_INSTRUCTION = """
ATURAN FORMAT:
- Setiap respon wajib 8 elemen (lihat WORKFLOW_INSTRUCTION)
- Kode timpa full, jangan suruh user edit manual
- Bahasa Indonesia yang mudah dipahami
- 1 chat = 1 fitur utuh kalau muat

ATURAN KERJA:
- Konfirmasi dulu sebelum eksekusi
- Jujur kalau belum tahu, jangan menebak
- JANGAN asal hapus fitur lama
- Lihat kode asli dulu sebelum timpa
- Kerjakan 1 per 1, atau sekaligus kalau user minta
- Sebelum build, tanya "Harus Update / Skip"

STRUKTUR MENU SETTINGS (19 Kategori):
1.  Lokasi & Waktu Sholat
2.  Pengaturan Waktu
3.  Durasi & Hitungan Mundur
4.  Identitas Masjid
5.  Jadwal Petugas & Foto
6.  Donasi QRIS & Rekening
7.  Video Kegiatan Masjid
8.  Tampilan & Background
9.  Kartu Nasihat & Mutiara
10. Running Text
11. Audio & Adzan
12. Mode Ramadhan
13. Keamanan
14. Daya & Booting
15. Slide Fullscreen
16. CCTV Masjid
17. Remote Control
18. Tentang Aplikasi
19. Opsi Developer (PIN 140399)

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
(Tidak ada - semua fitur sudah selesai)
    """.trimIndent()
    
    // ============================================================
    // MEMORY KNOWLEDGE (BARU — LENGKAP + LINK GITHUB)
    // ============================================================
    val MEMORY_KNOWLEDGE = """
============================================================
MEMORY KNOWLEDGE & INSTRUCTION — MASJID.IO
Untuk AI baru yang membaca backup ini
============================================================

=== BAGIAN 1 — ATURAN FORMAT RESPON (8 ELEMEN WAJIB) ===

1. Visualisasi folder (emoji + indentasi, seperti file manager)
2. Path lengkap file (contoh: app/src/main/java/com/example/MainActivity.kt)
3. Nama file (agar mudah di-copy)
4. URL cepat (format: https://github.com/USER/REPO/blob/main/PATH)
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
  Versi saat ini : V1.29.2
  PIN Developer  : 140399

=== BAGIAN 4 — STRUKTUR MENU SETTINGS (19 KATEGORI) ===

1.  Lokasi & Waktu Sholat
2.  Pengaturan Waktu
3.  Durasi & Hitungan Mundur
4.  Identitas Masjid
5.  Jadwal Petugas & Foto
6.  Donasi QRIS & Rekening
7.  Video Kegiatan Masjid
8.  Tampilan & Background
9.  Kartu Nasihat & Mutiara
10. Running Text
11. Audio & Adzan
12. Mode Ramadhan
13. Keamanan
14. Daya & Booting
15. Slide Fullscreen
16. CCTV Masjid
17. Remote Control
18. Tentang Aplikasi
19. Opsi Developer (PIN 140399)

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

1. Bump versionName  -> app/build.gradle.kts
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
1. Baca BACKUP AMAN terlebih dahulu (BAGIAN 1-15).
2. Pahami struktur aplikasi + isi kode.
3. Ikuti aturan 8 elemen format.
4. Ikuti aturan anti-truncation (BAGIAN 1B).
5. Konfirmasi dulu sebelum eksekusi.
6. Jangan menebak - tanya user kalau tidak tahu.

=== BAGIAN 15 — LINK GITHUB SOURCE CODE (UNTUK AI BARU) ===

AI baru DAPAT membuka sendiri kode lengkap dari GitHub.
Repo PUBLIC, tidak butuh login atau token.

LINK UTAMA:
  Repo Utama       : https://github.com/andikune-ux/Masjid.io
  Daftar Semua File: https://api.github.com/repos/andikune-ux/Masjid.io/git/trees/main?recursive=1

LINK RAW (untuk baca file langsung):
  Base Raw         : https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/
  Contoh:
    MainActivity.kt
    https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/app/src/main/java/com/example/MainActivity.kt

LINK BLOB (untuk lihat di browser):
  Base Blob        : https://github.com/andikune-ux/Masjid.io/blob/main/
  Contoh:
    SettingsScreen.kt
    https://github.com/andikune-ux/Masjid.io/blob/main/app/src/main/java/com/example/ui/settings/SettingsScreen.kt

FOLDER KUNCI YANG SERING DIBACA:
- app/src/main/java/com/example/MainActivity.kt
- app/src/main/java/com/example/util/BackupManager.kt
- app/src/main/java/com/example/util/CrashReporter.kt
- app/src/main/java/com/example/util/GithubSourceFetcher.kt
- app/src/main/java/com/example/data/AppKnowledge.kt
- app/src/main/java/com/example/data/UpdateHistory.kt
- app/src/main/java/com/example/ui/settings/SettingsScreen.kt
- app/src/main/java/com/example/ui/settings/DeveloperSettingsPane.kt
- app/build.gradle.kts
- gradle/libs.versions.toml
- .github/workflows/build.yml

CATATAN UNTUK AI BARU:
- Repo PUBLIC -> bebas diakses kapan saja
- Kalau butuh SEMUA file sekaligus: buka tree API di atas
- Kalau butuh 1 file: pakai raw URL + path
- JANGAN minta user copy-paste manual kalau bisa buka sendiri

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
