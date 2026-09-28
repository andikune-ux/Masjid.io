package com.example.data

object AppKnowledge {

    const val APP_NAME = "MASJID.IO"
    const val APP_DESCRIPTION = "Display jadwal sholat Android TV untuk masjid"

    val APP_STRUCTURE = """
============================================================
STRUKTUR APLIKASI MASJID.IO
============================================================

app/src/main/java/com/example/
├── MainActivity.kt                    -> Activity utama
├── audio/
│   └── SoundManager.kt                 -> Suara beep & adzan
├── data/
│   ├── model/
│   │   └── AppSettings.kt              -> Model pengaturan
│   ├── local/
│   │   ├── SettingsRepository.kt        -> Simpan/load setting
│   │   ├── PrayerTimesCalculator.kt     -> Hitung jadwal sholat
│   │   ├── IslamicCalendar.kt           -> Kalender Hijriah
│   │   └── WeatherService.kt            -> Data cuaca
│   └── AppKnowledge.kt                  -> File ini (data statis)
├── kiosk/
│   ├── KioskManager.kt                 -> Mode kiosk (lock task)
│   ├── WatchdogService.kt               -> Auto-restart
│   └── AutoStartService.kt              -> Auto-start saat boot
├── receiver/
│   └── BootReceiver.kt                 -> Terima broadcast boot
├── util/
│   └── BackupManager.kt                -> Backup Aman (file ini)
└── ui/
    ├── home/HomeScreen.kt               -> Tampilan utama
    ├── focus/                            -> Mode fokus sholat
    ├── settings/                         -> Menu pengaturan
    ├── components/                       -> Komponen UI
    └── theme/                            -> Warna & tipografi
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
   - Kartu 6 waktu sholat sejajar (1 baris horizontal)
   - Panel Imam & Muadzin (auto-slide 10 detik)
   - Running text pengumuman

3. MODE FOKUS SHOLAT (30 menit, otomatis)
   - Fase Adzan (0-10 menit): doa + countdown iqamah
   - Fase Qobliyah (10-15 menit): countdown 5 menit
   - Fase Fardhu (15-30 menit): panduan sholat

4. AUDIO
   - Mode Beep Only (isyarat masuk waktu sholat)
   - Mode Full Adzan (Makkah/Madinah/Indonesia/Custom)
   - Mode Silent (hanya visual)

5. PETUGAS MASJID
   - Jadwal imam/muadzin/khatib/ustadz per hari
   - Foto per sesi (setiap imam bisa foto sendiri)
   - Auto-slide di HomeScreen

6. PENGATURAN LENGKAP (via ikon gerigi)
   - Lokasi & Waktu Sholat
   - Identitas Masjid
   - Running Text
   - Jadwal Petugas & Foto
   - Audio & Adzan
   - Tampilan & Background
   - Mode Ramadhan
   - Konten Tambahan
   - Keamanan & Kiosk
   - Power Management
   - Opsi Developer (PIN 140399)

7. KIOSK MODE
   - Lock task (kunci aplikasi)
   - Watchdog auto-restart jika crash
   - Auto-start saat TV nyala
   - Bisa jadi launcher default Android TV

8. BACKUP AMAN
   - Export semua info aplikasi ke file .TXT
   - Lokasi: /sdcard/masjid.io/backup aman/
   - Nama file: Backup Aman-masjid.io-DD-MM-YYYY.TXT
    """.trimIndent()

    val UPDATE_HISTORY = """
============================================================
RIWAYAT UPDATE MASJID.IO
============================================================

Versi Awal (sebelum V1.28.x)
- Versi dasar dengan jadwal sholat 6 waktu
- Tampilan jam digital + tanggal Hijriah
- Mode fokus 30 menit
- Audio beep/adzan/silent

V1.28.1 (28 Sep 2026)
- Upload foto per sesi petugas (imam/muadzin/khatib/ustadz)
- Foto profil kotak sudut tumpul
- HomeScreen: video diperbesar, jam diperkecil
- Kartu sholat digeser ke kiri saat video aktif
- Suara beep diperbaiki (lebih nyaring, panjang, serius)
- Integrasi KioskManager & WatchdogService

V1.28.2 (28 Sep 2026)
- Implementasi versioning otomatis (V1.28.x)
- Setup keystore permanen (update tanpa uninstall)
- Auto GitHub Release setiap build
- Fitur Backup Aman (fitur ini)
    """.trimIndent()

    val KNOWN_ISSUES = """
============================================================
MASALAH & SOLUSI APLIKASI MASJID.IO
============================================================

=== SUDAH DIPERBAIKI ===
1. Foto ustadz tidak tampil di HomeScreen
   Solusi: Normalisasi nama hari (Jum'at vs Jumat, dst)

2. Suara beep terlalu pendek & lucu
   Solusi: Ganti tone ke TONE_CDMA_ALERT_CALL_GUARD,
           durasi 1200ms, jeda 1600ms

3. Update APK selalu harus uninstall dulu
   Solusi: Pakai keystore permanen di repo

4. File "File_paths.xml" error (huruf besar F)
   Solusi: Rename jadi "file_paths.xml" (huruf kecil)

5. Konflik GitHub Actions - build gagal di signing
   Solusi: Commit debug.keystore ke repo

6. Setup keystore gagal karena .gitignore
   Solusi: Pakai "git add -f debug.keystore"

=== BELUM DIPERBAIKI ===
1. Widget foto kegiatan masjid di kanan atas kurang rapi
2. Slider waktu belum bisa digeser pakai remote D-pad
3. Fokus D-pad kurang tebal (perlu diperjelas)
4. Menu Opsi Developer belum lengkap
5. Kirim WA otomatis (Fonnte) belum diimplementasi
6. Log Crash belum ada (sedang dikerjakan)
    """.trimIndent()

    val DEVELOPER_INSTRUCTION = """
============================================================
INSTRUKSI UNTUK AI / DEVELOPER BERIKUTNYA
============================================================

USER: Andi (andikune@gmail.com)
DEVICE USER: HP Android (bukan PC/laptop)
APLIKASI: GitHub Mobile
REPO: https://github.com/andikune-ux/Masjid.io

=== ATURAN WAJIB SETIAP RESPON AI ===
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
- Jangan cuma kasih path (harus ada URL + navigasi)
- Jangan asumsi user punya PC/laptop
- Jangan suruh screenshot kalau bisa langsung kasih link
- Jangan lupa kasih pesan commit

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
Contoh: V1.28.2 = versi inti 1, tanggal 28, build ke-2 hari ini
- Angka inti naik manual saat ada perubahan besar UI/UX
- Angka tanggal mengikuti tanggal build real-time
- Angka count naik otomatis per build, reset per hari
    """.trimIndent()
}
