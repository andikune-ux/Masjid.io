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
│   └── AppKnowledge.kt                -> File ini (data statis)
├── kiosk/
│   ├── KioskManager.kt                -> Mode kiosk (lock task)
│   ├── WatchdogService.kt             -> Auto-restart
│   └── AutoStartService.kt            -> Auto-start saat boot
├── receiver/
│   └── BootReceiver.kt                -> Terima broadcast boot
├── util/
│   ├── BackupManager.kt               -> Backup Aman (TXT)
│   ├── CrashReporter.kt               -> Log crash
│   └── UpdateManager.kt               -> Cek update GitHub
└── ui/
    ├── home/HomeScreen.kt             -> Tampilan utama
    ├── focus/                          -> Mode fokus sholat
    ├── settings/                       -> Menu pengaturan
    │   ├── SettingsScreen.kt          -> Menu utama
    │   ├── DeveloperSettingsPane.kt   -> Opsi Developer
    │   ├── DeveloperPinDialog.kt      -> PIN 140399
    │   ├── AboutSettingsPane.kt       -> Tentang + Sosmed
    │   ├── RamadhanSettingsPane.kt    -> Mode Ramadhan
    │   ├── SecuritySettingsPane.kt    -> Keamanan
    │   ├── PowerSettingsPane.kt       -> Daya & Booting
    │   ├── RiwayatCrashScreen.kt      -> Riwayat Crash
    │   └── RiwayatUpdateScreen.kt     -> Riwayat Update
    ├── components/                     -> Komponen UI
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

3. MODE FOKUS SHOLAT (30 menit, otomatis)
   - Fase Adzan (0-10 menit)
   - Fase Qobliyah (10-15 menit)
   - Fase Fardhu (15-30 menit)

4. AUDIO
   - Mode Beep Only
   - Mode Full Adzan
   - Mode Silent

5. PETUGAS MASJID
   - Jadwal imam/muadzin/khatib/ustadz per hari
   - Foto per sesi
   - Auto-slide di HomeScreen

6. PENGATURAN LENGKAP (via ikon gerigi)
   - Lokasi & Waktu Sholat
   - Pengaturan Waktu
   - Durasi & Hitungan Mundur
   - Identitas Masjid
   - Jadwal Petugas & Foto
   - Donasi QRIS & Rekening
   - Video Kegiatan Masjid
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
   - Tombol Install APK (kalau ada download)
   - Icon Sosmed (WhatsApp, TikTok, Instagram)

9. OPSI DEVELOPER (PIN 140399)
   - Backup Aman (Export TXT)
   - Riwayat Crash (lihat, copy, hapus)

10. VERSIONING OTOMATIS
    - Format: V{inti}.{tanggal}.{countHariIni}
    - Contoh: V1.28.1

11. BACKUP AMAN
    - Export semua info aplikasi ke TXT
    - Lokasi: /sdcard/masjid.io/backup aman/
    - Nama: Backup Aman-masjid.io-DD-MM-YYYY.TXT
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
- Restore 4 pane yang hilang:
  * Mode Ramadhan
  * Keamanan
  * Daya & Booting
  * Tentang Aplikasi (dengan Sosmed)
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

=== BELUM DIPERBAIKI ===
1. Widget foto kegiatan masjid di kanan atas
2. Slider waktu belum bisa digeser pakai remote
3. Fokus D-pad kurang tebal
4. Tombol download APK belum berfungsi
5. Auto install APK belum ada
6. Kirim WA Fonnte (crash ke grup) belum ada
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
7. Video Kegiatan Masjid
8. Tampilan & Background
9. Kartu Nasihat & Mutiara
10. Running Text
11. Audio & Adzan
12. Mode Ramadhan
13. Keamanan
14. Daya & Booting
15. Tentang Aplikasi
    - Versi
    - Periksa Update (dengan progress bar download)
    - Riwayat Update (detail fitur per versi)
    - Tombol Install APK (kalau sudah download)
    - Icon Sosmed (WhatsApp, TikTok, Instagram)
16. Opsi Developer (PIN 140399)
    - Backup Aman (Export TXT)
    - Riwayat Crash

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

=== PATH BACKUP AMAN ===
/sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT
    """.trimIndent()
}
