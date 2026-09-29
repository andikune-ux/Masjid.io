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
    ├── focus/PrayerFocusOverlay.kt    -> Mode fokus 30 menit
    ├── settings/
    │   ├── SettingsScreen.kt          -> Menu utama
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
   - Countdown ke sholat berikutnya
   - Progress bar visual

2. TAMPILAN UTAMA
   - Jam digital besar
   - Tanggal Hijriah + Masehi
   - Nama masjid & logo
   - Kartu 6 waktu sholat sejajar
   - Panel Imam & Muadzin (auto-slide 10 detik)
   - Running text pengumuman
   - Video kegiatan (opsional)
   - Foto slideshow (opsional)

3. MODE FOKUS SHOLAT (30 menit, otomatis)
   - Fase Adzan (0-10 menit): doa + countdown iqamah
   - Fase Qobliyah (10-15 menit): countdown 5 menit
   - Fase Fardhu (15-30 menit): panduan sholat

4. AUDIO
   - Mode Beep Only (bip...bip...bip)
   - Mode Full Adzan (Makkah/Madinah/Indonesia)
   - Mode Silent
   - Volume & durasi beep bisa diatur

5. PETUGAS MASJID
   - Jadwal imam/muadzin/khatib/ustadz per hari
   - Foto per sesi petugas
   - Auto-slide di HomeScreen

6. PENGATURAN LENGKAP (PIN default 1234)
   - Lokasi & Waktu Sholat (GPS + manual + Kemenag)
   - Pengaturan Waktu (manual time untuk TV offline)
   - Durasi & Hitungan Mundur (6 slider)
   - Identitas Masjid (nama, alamat, logo, takmir)
   - Jadwal Petugas & Foto (7 hari)
   - Donasi QRIS & Rekening
   - Video Kegiatan + Foto Slideshow
   - Tampilan & Background (4 mode)
   - Kartu Nasihat & Mutiara
   - Running Text
   - Audio & Adzan
   - Mode Ramadhan
   - Keamanan (kiosk, PIN, auto-start)
   - Daya & Booting
   - Tentang Aplikasi
   - Opsi Developer (PIN 140399)

7. KIOSK MODE
   - Lock task (kunci aplikasi)
   - Watchdog auto-restart
   - Auto-start saat TV nyala
   - Launcher default Android TV

8. TENTANG APLIKASI
   - Versi aplikasi
   - Tombol Periksa Update (progress bar)
   - Tombol Riwayat Update
   - Tombol Install APK
   - Icon Sosmed (WhatsApp, TikTok, Instagram)

9. OPSI DEVELOPER (PIN 140399)
   - Backup Aman (export TXT, include build history)
   - Riwayat Crash (lihat, copy, hapus)
   - WhatsApp Fonnte (kirim WA saat crash)
   - Refresh Build History (dari GitHub)

10. VERSIONING OTOMATIS
    - Format: V{inti}.{tanggal}.{countHariIni}
    - Auto-release GitHub setiap build

11. FONNTE (WHATSAPP REPORT)
    - Kirim notifikasi WA otomatis saat crash
    - Token + Group ID diisi di Opsi Developer

12. UPDATE APK
    - Cek update otomatis saat buka app
    - Download & install tanpa ke Play Store
    """.trimIndent()

    val UPDATE_HISTORY = """
============================================================
RIWAYAT UPDATE MASJID.IO
============================================================

V1.28.3 (29 September 2026)
- Fokus D-pad lebih tebal (TvFocusHelper, TvSlider, TvToggle)
- Semua pane pakai TvSlider + TvToggle (remote-friendly)
- LocationSettingsPane lengkap: GPS + input manual + Kemenag
- IdentitySettingsPane lengkap: nama, alamat, logo, takmir
- RunningTextSettingsPane lengkap: input + kecepatan + preview
- Lint warning dibersihkan (Locale, BorderStroke, icon deprecated)
- Restore fitur lama yang terhapus (Location & Identity)

V1.28.2
- Versioning otomatis + keystore permanen
- Auto GitHub Release setiap build
- Backup Aman (TXT)
- Log Crash + Riwayat Crash
- Periksa Update + Riwayat Update
- ApkDownloader (download & install APK)
- WhatsApp Fonnte (crash → grup admin)
- Foto Slideshow
- Build History Fetcher
- Restore 4 pane: Ramadhan, Keamanan, Daya, Tentang

V1.28.1
- Upload foto per sesi petugas
- Foto profil kotak sudut tumpul
- HomeScreen: video diperbesar, jam diperkecil
- Suara beep diperbaiki
- Integrasi KioskManager & WatchdogService

V1.28.0 (Versi Dasar)
- Versi dasar jadwal sholat 6 waktu
- Jam digital + tanggal Hijriah
- Mode fokus 30 menit
- Audio beep/adzan/silent
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

=== BELUM DIPERBAIKI ===
1. Slider manual di atas 30 menit (kalau ada)
2. Widget foto di kartu sholat (belum direncanakan)
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

[29-09-2026] - V1.28.3
Error: LocationSettingsPane & IdentitySettingsPane sebagai file terpisah hilang
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
        val DEVELOPER_INSTRUCTION = """
============================================================
INSTRUKSI UNTUK AI / DEVELOPER
============================================================

USER: Andi (andikune@gmail.com)
DEVICE: HP Android (bukan PC/laptop)
APLIKASI: GitHub Mobile
REPO: https://github.com/andikune-ux/Masjid.io

=== 8 ELEMEN WAJIB SETIAP RESPON AI ===
1. Visualisasi folder (dengan emoji, seperti file manager)
2. Path lengkap file
3. URL cepat (link langsung bisa ditap)
4. 2 opsi akses (link / navigasi folder)
5. Kode timpa full (BUKAN edit manual)
6. Kode tidak boleh terpotong
7. Pesan commit (text persis)
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
3. Commit -> GitHub Actions build APK otomatis
4. Download APK dari Artifacts
5. Install di HP/TV -> test
6. Screenshot hasil -> kirim ke AI
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

=== UPDATE WAJIB TIAP BUILD ===
1. Bump versionName (build.gradle.kts)
2. Update AppKnowledge.kt (riwayat + build error)
3. Update UpdateHistory.kt (fitur baru)
    """.trimIndent()

    val MEMORY_INSTRUCTION = """
============================================================
MEMORY KNOWLEDGE & INSTRUCTION (UNTUK AI BARU)
============================================================

Jika Anda AI baru yang membaca file ini, ikuti ATURAN berikut:

=== ATURAN FORMAT ===
- Setiap respon wajib 8 elemen (lihat DEVELOPER_INSTRUCTION)
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

=== STRUKTUR MENU SETTINGS ===
1. Lokasi & Waktu Sholat (GPS + manual + Kemenag)
2. Pengaturan Waktu (manual time)
3. Durasi & Hitungan Mundur (6 slider)
4. Identitas Masjid (nama, alamat, logo, takmir)
5. Jadwal Petugas & Foto (7 hari)
6. Donasi QRIS & Rekening
7. Video Kegiatan + Foto Slideshow
8. Tampilan & Background (4 mode)
9. Kartu Nasihat & Mutiara
10. Running Text
11. Audio & Adzan
12. Mode Ramadhan
13. Keamanan
14. Daya & Booting
15. Tentang Aplikasi
16. Opsi Developer (PIN 140399)

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
- Slider manual >30 menit
- Widget foto di kartu sholat
    """.trimIndent()
}
