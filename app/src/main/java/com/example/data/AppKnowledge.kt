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
    ├── focus/                          -> Mode fokus sholat
    ├── settings/                       -> Menu pengaturan
    │   ├── SettingsScreen.kt          -> Menu utama
    │   ├── DeveloperSettingsPane.kt   -> Opsi Developer
    │   ├── DeveloperPinDialog.kt      -> PIN 140399
    │   ├── AboutSettingsPane.kt       -> Tentang + Sosmed
    │   ├── AudioSettingsPane.kt       -> Audio & Adzan
    │   ├── RamadhanSettingsPane.kt    -> Mode Ramadhan
    │   ├── SecuritySettingsPane.kt    -> Keamanan
    │   ├── PowerSettingsPane.kt       -> Daya & Booting
    │   ├── VideoSettingsPane.kt       -> Video & Foto Slideshow
    │   ├── RiwayatCrashScreen.kt      -> Riwayat Crash
    │   └── RiwayatUpdateScreen.kt     -> Riwayat Update
    ├── components/                     -> Komponen UI
    │   ├── TvSlider.kt                -> Slider khusus TV
    │   ├── TvToggle.kt                -> Toggle khusus TV
    │   ├── FocusHelper.kt             -> Helper fokus D-pad
    │   ├── PhotoSlideshow.kt          -> Slideshow foto
    │   ├── MasjidVideoPlayer.kt       -> Video player (ExoPlayer)
    │   ├── UpdateDialog.kt            -> Dialog update
    │   └── OfficerCarousel.kt         -> Panel imam/muadzin
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
   - Nama masjid
   - Kartu 6 waktu sholat sejajar
   - Panel Imam & Muadzin (auto-slide 10 detik)
   - Running text pengumuman
   - Video kegiatan masjid (opsional)
   - Foto slideshow kegiatan (opsional)

3. MODE FOKUS SHOLAT (30 menit, otomatis)
   - Fase Adzan (0-10 menit): doa + countdown iqamah
   - Fase Qobliyah (10-15 menit): countdown 5 menit
   - Fase Fardhu (15-30 menit): panduan sholat

4. AUDIO
   - Mode Beep Only (bip...bip...bip panjang & terputus)
   - Mode Full Adzan (Makkah/Madinah/Indonesia)
   - Mode Silent
   - Volume & durasi beep bisa diatur

5. PETUGAS MASJID
   - Jadwal imam/muadzin/khatib/ustadz per hari
   - Foto per sesi (setiap petugas bisa foto sendiri)
   - Auto-slide di HomeScreen

6. PENGATURAN LENGKAP (via ikon gerigi, PIN default 1234)
   - Lokasi & Waktu Sholat
   - Pengaturan Waktu
   - Durasi & Hitungan Mundur
   - Identitas Masjid
   - Jadwal Petugas & Foto
   - Donasi QRIS & Rekening
   - Video Kegiatan Masjid + Foto Slideshow
   - Tampilan & Background
   - Kartu Nasihat & Mutiara
   - Running Text
   - Audio & Adzan
   - Mode Ramadhan
   - Keamanan
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
   - Tombol Periksa Update (progress bar download)
   - Tombol Riwayat Update
   - Tombol Install APK (kalau sudah download)
   - Icon Sosmed (WhatsApp, TikTok, Instagram)

9. OPSI DEVELOPER (PIN 140399)
   - Backup Aman (Export TXT)
   - Riwayat Crash (lihat, copy, hapus)
   - WhatsApp Fonnte (kirim WA saat crash)
   - Refresh Build History (from GitHub)

10. VERSIONING OTOMATIS
    - Format: V{inti}.{tanggal}.{countHariIni}
    - Contoh: V1.28.1

11. BACKUP AMAN
    - Export semua info aplikasi ke TXT
    - Lokasi: /sdcard/masjid.io/backup aman/
    - Nama: Backup Aman-masjid.io-DD-MM-YYYY.TXT
    - Isi: struktur, fitur, riwayat update, known issues, 
           riwayat build error, memory instruction

12. WHATSAPP REPORT (FONNTE)
    - Kirim notifikasi otomatis ke grup WA admin saat crash
    - Butuh Token Fonnte + Group ID
    """.trimIndent()

    val UPDATE_HISTORY = """
============================================================
RIWAYAT UPDATE MASJID.IO
============================================================

Versi Awal
- Versi dasar jadwal sholat 6 waktu
- Jam digital + tanggal Hijriah
- Mode fokus 30 menit
- Audio beep/adzan/silent

V1.28.1
- Upload foto per sesi petugas
- Foto profil kotak sudut tumpul
- HomeScreen: video diperbesar, jam diperkecil
- Kartu sholat digeser kiri saat video aktif
- Suara beep diperbaiki (nyaring, panjang, serius)
- Integrasi KioskManager & WatchdogService

V1.28.2
- Implementasi versioning otomatis
- Setup keystore permanen di repo
- Auto GitHub Release setiap build
- Fitur Backup Aman
- Fitur Log Crash + Riwayat Crash
- Fitur Periksa Update + Riwayat Update
- Restore 4 pane: Ramadhan, Keamanan, Daya, Tentang

V1.28.3
- Integrasi ApkDownloader (download + install APK)
- TvSlider + TvToggle (remote-friendly)
- FocusHelper (fokus D-pad lebih jelas)
- Integrasi WhatsApp Fonnte (crash → grup admin)
- Widget Foto Slideshow (ganti otomatis)
- Build History Fetcher (auto-fetch dari GitHub)
    """.trimIndent()

    val KNOWN_ISSUES = """
============================================================
MASALAH & SOLUSI APLIKASI MASJID.IO
============================================================

=== SUDAH DIPERBAIKI ===
1. Foto ustadz tidak tampil di HomeScreen
   Solusi: Normalisasi nama hari (Jum'at vs Jumat)

2. Suara beep terlalu pendek & lucu
   Solusi: TONE_CDMA_ALERT_CALL_GUARD, durasi 1200ms

3. Update APK selalu harus uninstall dulu
   Solusi: Keystore permanen di repo

4. File "File_paths.xml" error (huruf besar F)
   Solusi: Rename jadi "file_paths.xml"

5. Konflik GitHub Actions - build gagal di signing
   Solusi: Commit debug.keystore ke repo

6. Setup keystore gagal karena .gitignore
   Solusi: Pakai "git add -f debug.keystore"

7. Backup Aman error EPERM
   Solusi: Cek permission + fallback ke app-specific dir

8. Build error: KSP 2.3.5 NPE di GitHub Actions
   Solusi: Upgrade KSP ke 2.3.12 di libs.versions.toml

9. Build error: phaseNum di luar scope PrayerFocusOverlay
   Solusi: Pindah phaseNum ke luar Box

10. Build error: LaunchedEffect belum di-import (About)
    Solusi: Tambah import androidx.compose.runtime.LaunchedEffect

11. Build error: Icon Battery tidak ada (Power)
    Solusi: Ganti ke BatteryFull

=== BELUM DIPERBAIKI ===
1. Slider waktu belum semua pakai TvSlider
2. Fokus D-pad di beberapa tombol masih kurang tebal
3. Beberapa pane masih placeholder sederhana:
   - Countdown Settings
   - Running Text Settings
   - Location Settings
   - Identity Settings
   - Wisdom Cards
    """.trimIndent()

    val BUILD_ERROR_HISTORY = """
============================================================
RIWAYAT BUILD ERROR & SOLUSI (MANUAL UPDATE)
============================================================

Section ini di-update secara MANUAL oleh developer/user
setiap kali ada build error baru yang sudah diperbaiki.

FORMAT:
---
[Tanggal] - [Versi]
Error: [deskripsi singkat]
File: [nama file yang bermasalah]
Solusi: [cara memperbaiki]
---

=== RIWAYAT ===

[29-09-2026] - V1.28.2
Error: phaseNum tidak ditemukan di scope Box
File: PrayerFocusOverlay.kt
Solusi: Pindah phaseNum ke atas Box, bukan di dalam

[29-09-2026] - V1.28.2
Error: LaunchedEffect tidak dikenal
File: AboutSettingsPane.kt
Solusi: Tambah import androidx.compose.runtime.LaunchedEffect

[29-09-2026] - V1.28.2
Error: Icon Battery tidak tersedia di Material Icons
File: PowerSettingsPane.kt
Solusi: Ganti Icons.Default.Battery ke Icons.Default.BatteryFull

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
1. Visualisasi folder (seperti file manager, dengan emoji)
2. Path lengkap file
3. URL cepat (link langsung bisa ditap)
4. 2 opsi akses:
   - OPSI A: Buka pakai link
   - OPSI B: Navigasi folder step-by-step
5. Kode timpa full (BUKAN edit manual)
6. Kode tidak boleh terpotong (bagi kalau > 200 baris)
7. Pesan commit (text persis untuk diisi)
8. Bahasa Indonesia yang simple

=== LARANGAN ===
- Jangan suruh "Ctrl+F" (user pakai HP)
- Jangan suruh edit baris manual
- Jangan kirim kode panjang tanpa dibagi
- Jangan cuma kasih path
- Jangan asumsi user punya PC/laptop
- Jangan suruh screenshot kalau bisa langsung kasih link
- Jangan lupa kasih pesan commit
- Jangan asal hapus fitur lama
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
Contoh: V1.28.2
- Angka inti naik manual saat perubahan besar UI/UX
- Angka tanggal mengikuti tanggal build real-time
- Angka count naik otomatis, reset per hari

=== CATATAN BUILD ERROR ===
Setiap build error WAJIB ditambahkan ke:
1. AppKnowledge.kt -> BUILD_ERROR_HISTORY
2. Setelah di-fix, commit agar Backup Aman include

    """.trimIndent()

    val MEMORY_INSTRUCTION = """
============================================================
MEMORY KNOWLEDGE & INSTRUCTION (UNTUK AI BARU)
============================================================

Jika Anda AI baru yang membaca file ini, ikuti ATURAN berikut:

=== ATURAN FORMAT ===
- Setiap respon wajib ada 8 elemen (lihat DEVELOPER_INSTRUCTION)
- Kode timpa full, jangan suruh user edit manual
- Bahasa Indonesia yang mudah dipahami
- 1 chat = 1 fitur utuh kalau muat

=== ATURAN KERJA ===
- Konfirmasi dulu sebelum eksekusi
- Jujur kalau belum tahu, jangan menebak
- Jangan asal hapus fitur lama
- Lihat kode asli dulu sebelum timpa
- Kerjakan 1 per 1, atau sekaligus kalau user minta
- Sebelum build, tanya "Harus Update / Skip"

=== STRUKTUR MENU SETTINGS ===
1. Lokasi & Waktu Sholat
2. Pengaturan Waktu
3. Durasi & Hitungan Mundur
4. Identitas Masjid
5. Jadwal Petugas & Foto
6. Donasi QRIS & Rekening
7. Video Kegiatan Masjid (+ Foto Slideshow)
8. Tampilan & Background
9. Kartu Nasihat & Mutiara
10. Running Text
11. Audio & Adzan
12. Mode Ramadhan
13. Keamanan
14. Daya & Booting
15. Tentang Aplikasi
    - Versi
    - Periksa Update (progress bar download)
    - Riwayat Update (detail fitur per versi)
    - Tombol Install APK
    - Icon Sosmed (WhatsApp, TikTok, Instagram)
16. Opsi Developer (PIN 140399)
    - Backup Aman (Export TXT)
    - Riwayat Crash
    - WhatsApp Fonnte (kirim WA saat crash)
    - Refresh Build History

=== ATURAN PRIORITAS ===
- Kerjakan sesuai prioritas user
- Laporkan yang sudah/belum selesai
- Jangan lupa yang tertunda
- Update version + changelog + backup aman setiap build

=== SOSMED (di Tentang Aplikasi) ===
- WhatsApp : https://chat.whatsapp.com/ErJpG34fdzwL9FOmoh4fNN
- TikTok   : https://www.tiktok.com/@nayyra.une
- Instagram: https://www.instagram.com/nayyra.une

=== FILE PENTING ===
- app/build.gradle.kts                -> versioning
- version.properties                    -> data versi otomatis
- .github/workflows/build.yml          -> build & release
- debug.keystore                        -> keystore permanen
- app/src/main/AndroidManifest.xml     -> permission
- gradle/libs.versions.toml            -> KSP & dependency

=== PATH BACKUP AMAN ===
/sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT

=== PENANGANAN BUILD ERROR ===
Kalau build gagal:
1. Cek log GitHub Actions (step Build Debug APK)
2. Cari baris yang diawali "e:" (error Kotlin)
3. Perbaiki file yang error
4. Update AppKnowledge.kt -> BUILD_ERROR_HISTORY
5. Commit ulang
    """.trimIndent()
}
