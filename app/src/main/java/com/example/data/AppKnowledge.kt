package com.example.data

object AppKnowledge {

    const val APP_NAME = "MASJID.IO"
    const val APP_DESCRIPTION = "Display jadwal sholat Android TV untuk masjid"
    const val REPO_URL = "https://github.com/andikune-ux/Masjid.io"

    val APP_STRUCTURE = """
============================================================
STRUKTUR APLIKASI MASJID.IO
============================================================

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
    
    val APP_FEATURES = """
============================================================
FITUR APLIKASI MASJID.IO
============================================================

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

5. SLIDE FULLSCREEN (Baru)
   - Slide QRIS Infaq (dengan animasi pulse)
   - Slide Laporan Keuangan (pemasukan/pengeluaran)
   - Slide Jadwal Kajian (foto ustadz + tema)
   - Auto-rotate setiap X detik
   - Hanya tampil saat idle

6. CCTV MASJID (Baru)
   - Widget PiP di sudut layar
   - Support RTSP (via ExoPlayer)
   - Support HTTP Snapshot / MJPEG / DVR Dashboard
   - Posisi & ukuran bisa diatur

7. REMOTE CONTROL (Baru)
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
    """.trimIndent()

    val UPDATE_HISTORY = """
============================================================
RIWAYAT UPDATE MASJID.IO (RINGKAS)
============================================================

(Versi lengkap ada di UpdateHistory.kt)

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

    val KNOWN_ISSUES = """
============================================================
MASALAH & SOLUSI APLIKASI MASJID.IO
============================================================

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
(Tidak ada — semua sudah diperbaiki)
    """.trimIndent()
    
    val BUILD_ERROR_HISTORY = """
============================================================
RIWAYAT BUILD ERROR & SOLUSI
============================================================

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

    val WORKFLOW_INSTRUCTION = """
============================================================
WORKFLOW INSTRUCTION — WAJIB DIPATUHI AI
============================================================

PRINSIP KERJA (PENTING!):
1. Kerjakan project per KATEGORI folder/file.
2. Kumpulkan SEMUA perubahan untuk 1 file dalam 1 sesi.
3. JANGAN buka file yang sama berkali-kali.
4. Konfirmasi status commit user SEBELUM lanjut batch berikutnya.
5. Jangan asumsi user sudah melakukan sesuatu tanpa konfirmasi.
6. Selesaikan 1 BATCH sebelum lanjut ke BATCH berikutnya.
7. Setiap prinsip baru → dokumentasikan di file ini.

URUTAN BATCH (untuk fitur baru):
- BATCH 1: Semua field di AppSettings.kt
- BATCH 2: Semua file baru di folder baru
- BATCH 3: Update file existing (integrasi)
- BATCH 4: Resource (multi-bahasa, dll)
- BATCH 5: Update AppKnowledge.kt + build final

ATURAN:
- File yang sudah final → JANGAN dibuka lagi
- Setiap file dibuka MAKSIMAL 1-2 kali sepanjang project
- Dokumentasikan setiap batch yang sudah selesai
- Ukuran chat: kirim utuh kalau muat, bagi kalau terpotong

=== 8 ELEMEN WAJIB SETIAP RESPON AI ===
1. Visualisasi folder (dengan emoji)
2. Path lengkap file
3. URL cepat (link langsung)
4. 2 opsi akses (link / navigasi folder)
5. Kode timpa full
6. Kode tidak boleh terpotong
7. Pesan commit (text persis, di BAWAH kode)
8. Bahasa Indonesia yang simple

=== LARANGAN ===
- Jangan suruh "Ctrl+F" (user pakai HP)
- Jangan suruh edit baris manual
- Jangan kirim kode panjang tanpa dibagi
- Jangan cuma kasih path
- Jangan asumsi user punya PC/laptop
- Jangan lupa pesan commit
- JANGAN ASAL HAPUS fitur lama
- Jangan nebak-nebak tanpa bukti
- Jangan asal eksekusi tanpa konfirmasi

=== ALUR KERJA ===
1. AI kirim kode timpa full + lokasi + URL + commit
2. User copy-paste di GitHub mobile
3. Commit → GitHub Actions build APK otomatis
4. Download APK dari Artifacts
5. Install di HP/TV → test
6. Screenshot hasil → kirim ke AI
7. Iterasi

=== SEBELUM BUILD ===
Selalu tawarkan 2 pilihan:
- Harus Update (kunci aplikasi sampai update)
- Boleh Skip / Nanti Update

=== VERSIONING ===
Format: V{inti}.{tanggal}.{countHariIni}
- Angka inti naik manual saat perubahan besar UI/UX
- Angka tanggal mengikuti tanggal build real-time
- Angka count naik otomatis, reset per hari
    """.trimIndent()

    val DEVELOPER_INSTRUCTION = """
============================================================
INSTRUKSI UNTUK AI / DEVELOPER
============================================================

USER: Andi (andikune@gmail.com)
DEVICE: HP Android (bukan PC/laptop)
APLIKASI: GitHub Mobile
REPO: https://github.com/andikune-ux/Masjid.io

(Lihat WORKFLOW_INSTRUCTION untuk aturan lengkap)

UPDATE WAJIB TIAP BUILD:
1. Bump versionName (build.gradle.kts)
2. Update UpdateHistory.kt (tambah entri baru)
3. Update AppKnowledge.kt (struktur + fitur + build error)
    """.trimIndent()

    val MEMORY_INSTRUCTION = """
============================================================
MEMORY KNOWLEDGE & INSTRUCTION (UNTUK AI BARU)
============================================================

Jika Anda AI baru yang membaca file ini, ikuti ATURAN berikut:

=== ATURAN FORMAT ===
- Setiap respon wajib 8 elemen (lihat WORKFLOW_INSTRUCTION)
- Kode timpa full, jangan suruh user edit manual
- Bahasa Indonesia yang mudah dipahami
- 1 chat = 1 fitur utuh kalau muat

=== ATURAN KERJA ===
- Konfirmasi dulu sebelum eksekusi
- Jujur kalau belum tahu, jangan menebak
- JANGAN asal hapus fitur lama
- Lihat kode asli dulu sebelum timpa
- Kerjakan 1 per 1, atau sekaligus kalau user minta
- Sebelum build, tanya "Harus Update / Skip"

=== STRUKTUR MENU SETTINGS (19 Kategori) ===
1. Lokasi & Waktu Sholat
2. Pengaturan Waktu
3. Durasi & Hitungan Mundur
4. Identitas Masjid
5. Jadwal Petugas & Foto
6. Donasi QRIS & Rekening
7. Video Kegiatan Masjid
8. Tampilan & Background
9. Kartu Nasihat & Mutiara
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

=== SOSMED ===
- WhatsApp : https://chat.whatsapp.com/ErJpG34fdzwL9FOmoh4fNN
- TikTok   : https://www.tiktok.com/@nayyra.une
- Instagram: https://www.instagram.com/nayyra.une

=== FILE PENTING ===
- app/build.gradle.kts                -> versioning
- version.properties                    -> data versi otomatis
- .github/workflows/build.yml          -> build & release
- debug.keystore                        -> keystore permanen
- gradle/libs.versions.toml            -> KSP & dependency

=== PATH BACKUP AMAN ===
/sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT

=== PENANGANAN BUILD ERROR ===
1. Cek log GitHub Actions step "Build Debug APK"
2. Cari baris diawali "e:" (error Kotlin)
3. Perbaiki file yang error
4. Update AppKnowledge.kt -> BUILD_ERROR_HISTORY
5. Commit ulang

=== FITUR YANG BELUM SELESAI ===
(Tidak ada — semua fitur sudah selesai)

=== PRINSIP UTAMA ===
KERJAKAN PER BATCH. JANGAN BUKA FILE YANG SAMA BERKALI-KALI.
KONFIRMASI SEBELUM LANJUT.
    """.trimIndent()
}
