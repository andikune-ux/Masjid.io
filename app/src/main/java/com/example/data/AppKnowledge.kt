package dev.andikune.masjidio.data

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
app/src/main/java/dev/andikune/masjidio/
├── MainActivity.kt                    -> Activity utama (V1.04.426)
├── audio/
│   └── SoundManager.kt                -> Suara beep & adzan
├── data/
│   ├── model/
│   │   └── AppSettings.kt             -> Model pengaturan (130+ field + PinLockMode)
│   ├── local/
│   │   ├── SettingsRepository.kt      -> Simpan/load setting + exportSummary
│   │   ├── PrayerTimesCalculator.kt   -> Hitung jadwal sholat
│   │   ├── IslamicCalendar.kt         -> Kalender Hijriah
│   │   ├── IslamicWisdomStore.kt      -> Data kartu nasihat
│   │   ├── SunMoonCalculator.kt       -> Posisi matahari/bulan real-time
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
│   ├── AutoStartService.kt            -> Auto-start saat boot
│   └── TransferForegroundService.kt   -> V1.04.426 BARU (foreground + WiFiLock)
├── receiver/BootReceiver.kt           -> Terima broadcast boot
├── util/
│   ├── BackupManager.kt               -> Backup Aman (TXT)
│   ├── CrashReporter.kt               -> Log crash + WA Fonnte
│   ├── CrashAutoShowHelper.kt         -> Auto-show dialog crash
│   ├── GithubSourceFetcher.kt         -> Fetch source dari GitHub
│   ├── SettingsTransferHelper.kt      -> Serialize + upload media
│   ├── RemoteControlClient.kt         -> HTTP client iO Control (V1.04.426 streaming)
│   ├── MediaTransferHelper.kt         -> Chunk + kompres media (V1.04.426)
│   ├── MediaPersistenceHelper.kt      -> Copy file ke folder permanen
│   ├── IoBundleHelper.kt              -> Bundle .iO (V1.04.426 color coding)
│   ├── FonnteHelper.kt                -> Token & Group ID Fonnte
│   ├── FonnteSender.kt                -> Kirim WA via Fonnte API
│   ├── UpdateManager.kt               -> Cek update GitHub
│   ├── ApkDownloader.kt               -> Download & install APK
│   ├── QrCodeGenerator.kt             -> Generator barcode QR
│   └── BuildHistoryFetcher.kt         -> Fetch build history GitHub
└── ui/
    ├── home/HomeScreen.kt             -> Tampilan utama
    ├── focus/
    │   ├── AdzanSequenceOverlay.kt    -> 3 tahap alur sholat
    │   ├── PrayerFocusOverlay.kt      -> Mode Fokus 2 fase
    │   └── QRISFocusOverlay.kt        -> Preview QRIS
    ├── slides/
    │   ├── QrisSlide.kt               -> Slide QRIS Infaq
    │   ├── LaporanSlide.kt            -> Slide Laporan Keuangan
    │   ├── KajianSlide.kt             -> Slide Jadwal Kajian
    │   └── SlideManager.kt            -> Manager rotasi slide (CLEAN VIEW)
    ├── ramadhan/RamadhanOverlay.kt    -> Overlay Mode Ramadhan
    ├── cctv/CctvWidget.kt             -> Widget CCTV (RTSP/HTTP)
    ├── remote/
    │   ├── RemoteServer.kt            -> HTTP server (V1.04.426 streaming upload)
    │   ├── RemoteDashboard.kt         -> Info akses remote
    │   ├── DeviceDiscovery.kt         -> Discovery device via UDP
    │   ├── NetworkHelper.kt           -> Helper deteksi IP WiFi
    │   ├── IoControlScreen.kt         -> UI radar + trigger FGS (V1.04.426)
    │   ├── IoControlHelpSheet.kt      -> Panduan iO Control
    │   └── RestartCountdownOverlay.kt -> Overlay countdown restart
    ├── settings/
    │   ├── SettingsScreen.kt          -> Menu utama (20 kategori)
    │   ├── DeveloperSettingsPane.kt   -> Opsi Developer
    │   ├── DeveloperPinDialog.kt      -> PIN 140399
    │   ├── AboutSettingsPane.kt       -> Tentang + Sosmed + APK install
    │   ├── AudioSettingsPane.kt       -> Audio & Adzan
    │   ├── CountdownSettingsPane.kt   -> Durasi & countdown
    │   ├── CustomBackgroundPane.kt    -> Background + Tema Makkah + .iO (V1.04.426 GALERI)
    │   ├── IoBundleListSection.kt     -> Daftar .iO (V1.04.426 color coding)
    │   ├── TvDisplaySettingsPane.kt   -> Tampilan TV responsif
    │   ├── IdentitySettingsPane.kt    -> Identitas + logo (V1.04.426 GALERI)
    │   ├── LocationSettingsPane.kt    -> Lokasi + GPS
    │   ├── PowerSettingsPane.kt       -> Daya & Booting (jadwal on/off otomatis)
    │   ├── QrisSettingsPane.kt        -> Donasi QRIS (V1.04.426 GALERI)
    │   ├── RamadhanSettingsPane.kt    -> Mode Ramadhan
    │   ├── RunningTextSettingsPane.kt -> Running text
    │   ├── SecuritySettingsPane.kt    -> Keamanan (PIN LOCK 3 opsi)
    │   ├── SlideSettingsPane.kt       -> Slide Fullscreen
    │   ├── CctvSettingsPane.kt        -> CCTV Masjid
    │   ├── RemoteSettingsPane.kt      -> iO Control + Remote Server
    │   ├── TimeSettingsPane.kt        -> Waktu manual
    │   ├── VideoSettingsPane.kt       -> Video + Auto-Switch (V1.04.426 GALERI)
    │   ├── WeeklyOfficersSettingsPane.kt -> Petugas (V1.04.426 menu GALERI/FILE/HAPUS)
    │   ├── WisdomSettingsPane.kt      -> Kartu nasihat
    │   ├── MiniCalendarPickerModal.kt -> Kalender mini
    │   ├── PinDialog.kt               -> Dialog PIN
    │   ├── RiwayatCrashScreen.kt      -> Riwayat crash
    │   └── RiwayatUpdateScreen.kt     -> Riwayat update
    ├── components/
    │   ├── MakkahDynamicBackground.kt -> Tema Makkah Dinamis
    │   ├── ResponsiveLayoutHelper.kt  -> Auto-scale TV
    │   ├── AutoFocusPane.kt           -> Auto-focus D-pad
    │   ├── TvSlider.kt                -> Slider TV
    │   ├── TvToggle.kt                -> Toggle TV
    │   ├── TvFocusHelper.kt           -> Helper fokus D-pad
    │   ├── FocusHelper.kt             -> Helper fokus tambahan
    │   ├── NeonFocusBorder.kt         -> Border fokus berputar
    │   ├── PhotoSlideshow.kt          -> Slideshow foto
    │   ├── MasjidVideoPlayer.kt       -> Video player (onVideoLooped + zoomFactor)
    │   ├── UpdateDialog.kt            -> Dialog update
    │   ├── OfficerCarousel.kt         -> Panel imam/muadzin
    │   ├── PrayerCardsRow.kt          -> Kartu sholat
    │   ├── PrayerProgressBar.kt       -> Bar progres sholat
    │   ├── RunningTextMarquee.kt      -> Running text
    │   ├── PinDialog.kt               -> Dialog PIN
    │   ├── ChangePinDialog.kt         -> Dialog ubah PIN
    │   ├── TopBar.kt                  -> Top bar
    │   ├── ClockAndDate.kt            -> Jam & tanggal
    │   ├── MosqueHeader.kt            -> Header nama masjid
    │   ├── ArabesquePattern.kt        -> Pola arabesque
    │   ├── IslamicEventCard.kt        -> Kartu event Islam
    │   ├── WeatherAmbientOverlay.kt   -> Efek cuaca ambient
    │   ├── WisdomCardCarousel.kt      -> Karusel kartu nasihat (auto-shrink)
    │   ├── VideoFilePickerDialog.kt   -> Custom file picker TV
    │   └── AutoOffDialog.kt           -> Popup konfirmasi jadwal off
    └── theme/                          -> Warna & tipografi
    """.trimIndent()
        val APP_FEATURES = """
1. JADWAL SHOLAT
   - 6 waktu: Subuh, Syuruq, Dzuhur, Ashar, Maghrib, Isya
   - Countdown + progress bar visual
   - Icon berbeda per waktu (bukan emoji)

2. TAMPILAN UTAMA (Responsive)
   - Jam digital besar + tanggal Hijriah/Masehi
   - Kartu 6 waktu sejajar horizontal
   - Panel Imam & Muadzin auto-slide
   - Running text + Video/Foto Slideshow
   - Auto-scale untuk semua ukuran TV
   - 2 mode otomatis: MODE VIDEO (Opsi H) & MODE NORMAL

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
   - Menu: Tampilan TV

5. NAVIGASI D-PAD (V1.30.5 + V1.30.6)
   - Auto-focus ke elemen pertama pane (AutoFocusPane.kt)
   - Border fokus berputar (NeonFocusBorder.kt)
   - Fokus pindah INSTANT, animasi scale smooth 150ms
   - Blur glow tetap dipertahankan
   - Fokus tidak mendarat di tombol Kembali

6. ALUR SHOLAT BARU (V1.04.421)
   - Beep bunyi saat waktu sholat tiba
   - Tahap 1: Overlay Adzan fullscreen ('ADZAN [waktu]')
   - Tahap 2: Overlay Himbauan HP fullscreen
   - Tahap 3: Overlay Niat Qobliyah + Countdown Iqomah
   - Tahap 4: Mode Fokus (Niat Fardhu -> Dzikir)

7. MODE FOKUS SHOLAT (V1.04.421)
   - 2 fase baru: Niat Fardhu + Dzikir
   - Niat sholat fardhu (arab + latin + arti) - 5 waktu
   - Tombol OK: skip ke fase berikutnya
   - Tombol BACK: keluar dari Mode Fokus

8. AUDIO
   - Mode Beep Only / Full Adzan / Silent

9. SLIDE FULLSCREEN (V1.04.423 - CLEAN VIEW)
   - Slide QRIS / Laporan / Kajian auto-rotate
   - Tampilan BERSIH tanpa overlay navigasi
   - Tidak muncul saat Mode Fokus aktif
   - OK/BACK keluar, KIRI/KANAN pindah slide (fungsi remote tetap)
   - Cooldown 5 menit setelah dismiss

10. CCTV MASJID
    - Widget PiP (RTSP + HTTP)

11. iO CONTROL (V1.30.1 + V1.30.4 + V1.30.7 + V1.04.426)
    - HP sebagai remote TV via WiFi
    - Auto-discovery UDP + UI radar
    - Transfer semua pengaturan antar device
    - TRANSFER MEDIA (foto + video) via STREAMING UPLOAD (V1.04.426)
    - Foreground Service + WiFiLock (V1.04.426)
    - Progress bar per-file + total
    - UPDATE PATH LOKAL setelah media masuk TV
    - VERIFIKASI transfer sebelum restart
    - Konfirmasi restart MANUAL via tombol

12. REMOTE CONTROL WEB
    - HTTP server + dashboard browser HP
    - Token Fonnte DISEMBUNYIKAN dari web (hanya di Opsi Developer)

13. MODE RAMADHAN
    - Countdown Imsak/Iftar + Tarawih + Kultum

14. KIOSK MODE
    - Lock task + Watchdog + Auto-start

15. TENTANG APLIKASI
    - Periksa Update + Riwayat + Install APK
    - Tap file APK = install, tombol merah = hapus

16. OPSI DEVELOPER (PIN 140399)
    - Backup Aman + Riwayat Crash + WhatsApp Fonnte
    - Token Fonnte hanya bisa diubah di sini

17. VERSIONING OTOMATIS
    - Format V{inti}.{tanggal}.{countHariIni}

18. WHATSAPP REPORT (FONNTE)
    - Notifikasi crash otomatis ke grup admin

19. FOTO SLIDESHOW + 20. KONTEN ROTASI

21. DZIKIR SETELAH SHOLAT (8 dzikir)

22. AUTO-SHOW CRASH LOG

23. FULL SOURCE CODE EXPORT

24. FIX CRASH NESTED SCROLL + FADE ANIMASI

25. SALIN URL OTOMATIS

26. APK INSTALL DARI FILE TERSIMPAN

27. TAMPILAN TV RESPONSIF

28. TRANSFER MEDIA iO CONTROL
    - Foto & video ikut terkirim via chunk
    - Progress per-file + total
    - Retry otomatis

29. FIX MEDIA PATH
    - Update settings TV dengan path lokal setelah media masuk

30. VERIFIKASI TRANSFER + KONFIRMASI RESTART MANUAL (V1.30.7)
    - Cek semua file benar-benar terkirim sebelum restart
    - Restart MANUAL via tombol konfirmasi
    - Tombol COBA LAGI untuk kirim ulang file gagal
    - Tombol LIHAT LOG untuk stack trace Kotlin asli
    - Tombol RESTART SAJA kalau ada file gagal
    - Tombol LEWATI -> template aktif otomatis saat app dibuka ulang

31. FILE TEMPLATE .iO (V1.30.7)
    - Format .iO = ZIP (settings.json + metadata.json + media/)
    - Auto-bikin setiap transfer selesai
    - Nama file: {Merk HP}-{dd-MM-yyyy HH.mm}.iO
    - Lokasi: /sdcard/masjid.io/Terima/
    - File TIDAK dihapus otomatis
    - Daftar file template di menu Tampilan & Background
    - 3 tombol per file: GUNAKAN / INFO / HAPUS

32. STORAGE PERMISSION DIALOG (V1.04.418)
    - Muncul otomatis saat pertama buka app
    - Dialog penjelasan izin akses file
    - Tombol BERI IZIN -> buka Manage All Files Access

33. LAYOUT OPSI H — MODE VIDEO (V1.04.420)
    - Panel kiri 24%: logo + kotak gabungan jam/tanggal + list sholat vertikal + progress bar
    - Panel kanan 76%: video/foto dengan lock frame
    - List sholat vertikal 6 baris (Subuh -> Isya)
    - Highlight NEXT: emas + border tebal + pulse animation

34. AUTO-SWITCH MODE (V1.04.420 + V1.04.423)
    - Bolak-balik Mode Video <-> Mode Normal otomatis
    - Interval video (1-60 menit), Durasi normal (1-30 menit)
    - V1.04.423: Tunggu video selesai 1x putaran sebelum switch
    - Polling posisi video (500ms) deteksi loop
    - Kalau belum ada video/foto -> mode normal permanen

35. PENGATURAN UKURAN FRAME (V1.04.420 + V1.04.423)
    - 5 mode: POTONG / PAS / ZOOM / FULL / FIT
    - POTONG (Crop) seperti Instagram Reels
    - PAS (Fit/Letterbox) seperti Netflix
    - ZOOM (Fill) - V1.04.423: pakai zoomFactor 1.15f (beda dari POTONG)
    - FULL (Fullscreen) panel kiri hilang
    - FIT (Stretch) paksa video/foto sesuaikan frame
    - Frame LOCK ukuran tetap (16:9, 9:16, 1:1 semua sama)

36. MEDIA PERSISTENCE (V1.04.421)
    - File foto/video di-copy ke filesDir/masjid_io/ (folder permanen)
    - Fix masalah file hilang setelah app tutup/update/reboot
    - File baru: MediaPersistenceHelper.kt

37. FIX MODE VIDEO FULL-FRAME (V1.04.422)
    - Video/foto sekarang PENUH sampai tepi kanan layar
    - Bug resizeMode shadowing di MasjidVideoPlayer sudah diperbaiki
    - contentScale diteruskan dari settings.videoFrameScale ke player
    - 5 mode ukuran frame (POTONG/PAS/ZOOM/FULL/FIT) benar-benar beda

38. PIN LOCK MODE (V1.04.422)
    - 3 opsi di Settings -> Keamanan -> PIN LOCK:
      • IMMEDIATE   = Setiap tekan tombol Settings -> langsung minta PIN
      • TIMEOUT_5MIN = Minta PIN 5 menit setelah kembali ke menu utama
      • UNTIL_EXIT   = Tidak minta PIN sampai aplikasi ditutup (default)
    - Enum PinLockMode di AppSettings.kt
    - Logic di MainActivity: sessionPinVerified + lastPinVerifiedTime

39. SLIDE NAVIGASI REMOTE (V1.04.422 + V1.04.423)
    - Tekan OK / BACK / ENTER / DPAD_CENTER -> keluar slide kembali ke Home
    - Tekan KIRI / KANAN -> pindah slide (Qris <-> Laporan <-> Kajian)
    - CLEAN VIEW - hapus semua overlay navigasi visual
    - Cooldown 5 menit setelah dismiss -> slide muncul lagi sesuai jadwal

40. STRUKTUR MODE VIDEO BARU (V1.04.422)
    - Root Column (bukan Box)
    - Row (weight 1f) = panel kiri + panel kanan
    - Running Text (height 52dp) = fixed di paling bawah
    - Video/foto full hingga tepi kanan panel (no outer padding)

41. JADWAL ON/OFF OTOMATIS (V1.04.423)
    - ON otomatis = Subuh - X menit (X bisa diatur 5-120, default 15)
    - OFF otomatis = Isya + Y menit (Y bisa diatur 5-120, default 30)
    - Brightness 0 (layar redup total) saat jam OFF
    - Polling jadwal setiap 30 detik - real-time ikut waktu
    - Tampil kartu 'Jadwal Hari Ini' di menu Daya & Booting

42. AUTO-OFF DIALOG (V1.04.423)
    - Popup konfirmasi saat user tekan remote di jam OFF
    - Tombol YA (kiri, hijau) = matikan jadwal -> layar nyala terus
    - Tombol TIDAK (kanan, abu-abu) = popup hilang, layar redup lagi
    - Auto-dismiss 2 menit -> dianggap TIDAK
    - File baru: AutoOffDialog.kt

43. VIDEO FILE PICKER CUSTOM (V1.04.423)
    - Custom file picker dalam app (TV friendly, D-pad navigasi)
    - Scan rekursif /sdcard/ + semua subfolder
    - 3 mode: VIDEO / IMAGE / ALL
    - File baru: VideoFilePickerDialog.kt

44. WISDOM CARD AUTO-SHRINK (V1.04.423)
    - Fix tulisan terpotong dengan auto-shrink font + multi-line dinamis
    - TextOverflow.Clip (bukan Ellipsis) -> teks utuh tanpa '...'
    - Wisdom card full sampai tepi kanan layar (76% panel kanan)

45. ICON JADWAL SHOLAT BERBEDA (V1.04.423)
    - Icon berbeda per waktu sholat (bukan emoji)
    - Subuh -> WbTwilight, Syuruq -> WbSunny, Dzuhur -> LightMode
    - Ashar -> WbCloudy, Maghrib -> NightsStay, Isya -> Nightlight

46. STREAMING UPLOAD TRANSFER MEDIA (V1.04.426) — BARU
    - Fix CRITICAL: transfer media yang selalu gagal via iO Control
    - Metode: 1 request HTTP per file (bukan chunk + Base64)
    - TCP handle retransmisi otomatis — tidak perlu retry manual
    - Hemat bandwidth: tidak ada Base64 overhead (33% lebih efisien)
    - Timeout diperpanjang: 30 detik → 10 menit per file
    - Progress report real-time setiap ~256 KB
    - File dibaca langsung dari disk → socket (tidak buffer besar)
    - Support path lokal & content URI (auto-copy ke cache)
    - Endpoint baru: POST /api/io/upload-stream
    - Header: X-File-Id, X-Field-Key, X-File-Name, X-File-Type, X-Total-Bytes, X-Mime-Type
    - Backward compatible: endpoint chunk lama tetap ada

47. FOREGROUND SERVICE + WIFILOCK (V1.04.426) — BARU
    - File baru: TransferForegroundService.kt
    - Foreground Service: Android tidak boleh bunuh proses saat transfer
    - WiFiLock: WiFi tidak mati saat layar TV off
    - WakeLock: CPU tetap jalan saat layar TV off (max 30 menit)
    - Notifikasi permanen di status bar TV
    - Auto-start saat kirim media, auto-stop setelah selesai
    - Trigger dari IoControlScreen (SENDING_MEDIA phase)

48. COLOR CODING TEMPLATE .iO (V1.04.426) — BARU
    - Warna tombol template sesuai persentase transfer
    - HIJAU (100%) = semua file berhasil dikirim
    - KUNING (50-99%) = sebagian besar berhasil
    - MERAH (1-49%) = sebagian besar gagal
    - HITAM (0%) = gagal total
    - Badge persentase ditampilkan di kotak icon
    - Badge kategori (label teks) di kanan atas
    - Ringkasan kategori di atas daftar
    - Border kartu template ikut warna kategori
    - Dialog INFO/DELETE/RESTART juga pakai warna kategori
    - File template gagal sebagian tetap bisa dipakai (file yang berhasil tetap masuk)

49. FOLDER PROTECTION .NOMEDIA (V1.04.426) — BARU
    - Auto-bikin file .nomedia di folder /sdcard/masjid.io/Terima/
    - Folder Terima tidak dianggap cache oleh aplikasi cleaner
    - File .iO tidak akan dihapus otomatis oleh Clean Master, dll

50. TOMBOL GALERI DI SEMUA PICKER (V1.04.426) — BARU
    - Tombol GALERI di samping FILE untuk semua picker media
    - Video Kegiatan: GALERI + FILE
    - Logo Masjid: GALERI + FILE
    - QRIS: GALERI + FILE
    - Background Custom: GALERI + FILE
    - Foto Petugas: menu 3 opsi (GALERI / FILE / HAPUS) saat tap foto
    - GALERI pakai ActivityResultContracts.GetContent (galeri bawaan HP)
    - FILE pakai VideoFilePickerDialog custom (TV friendly)
    - Hint text jelas beda GALERI vs FILE

51. INSTALL DIALOG FIX (V1.04.426) — BARU
    - InstallPermissionDialog tidak muncul berulang setelah user grant/skip
    - Tambah state installDialogAlreadyShown (muncul sekali per sesi)
    - Tombol dialog izin height fixed 56dp -> teks & ikon muncul
    - Ganti Row dengan padding ke Box dengan height fixed + contentAlignment Center

52. NAMESPACE CONSISTENCY (V1.04.426) — BARU
    - Namespace konsisten dev.andikune.masjidio di semua file
    - Ganti namespace dari com.example ke dev.andikune.masjidio
    - Update import di semua file yang terdampak
    - Folder fisik tetap com/example/ (Kotlin izinkan package beda dari folder)
    """.trimIndent()
        val UPDATE_HISTORY = """
V1.04.426 (06 Oktober 2026)
- Fix Transfer Media Gagal + Color Coding Template .iO + Namespace Consistency
- Streaming upload: 1 request HTTP per file (bukan chunk + Base64)
- TransferForegroundService: WiFiLock + WakeLock saat transfer
- Color coding template .iO (HIJAU/KUNING/MERAH/HITAM)
- Tombol GALERI di semua picker media
- Folder protection .nomedia
- Namespace konsisten dev.andikune.masjidio
- Install dialog fix: tidak muncul berulang

V1.04.423 (06 Oktober 2026)
- Jadwal On/Off Otomatis + File Picker Custom + Fix Video Stage + Wisdom Card Full
- Jadwal On/Off otomatis dari jadwal sholat (ON=Subuh-X, OFF=Isya+X)
- Popup konfirmasi AutoOffDialog saat tekan remote di jam OFF
- Custom file picker VideoFilePickerDialog (TV friendly, rekursif)
- Fix video stage full-bleed sampai tepi kanan
- Fix wisdom card full sampai tepi kanan (auto-shrink)
- Fix Auto-Switch dengan polling posisi video (deteksi loop)
- Slide CLEAN VIEW (hapus semua overlay navigasi)
- Icon jadwal sholat berbeda per waktu

V1.04.422 (06 Oktober 2026)
- Fix Mode Video Full-Frame + Auto-Switch + PIN Lock Options + Slide Navigasi
- Video sekarang PENUH sampai tepi kanan (fix bug resizeMode)
- 5 mode frame (POTONG/PAS/ZOOM/FULL/FIT) benar-benar bekerja
- Struktur root Mode Video: Box -> Column
- Tombol ⚙ Settings di sudut kanan atas LAYAR
- Auto-Switch tunggu video selesai 1x putaran
- PIN Lock Mode 3 opsi (IMMEDIATE / TIMEOUT_5MIN / UNTIL_EXIT)
- Slide OK/BACK keluar + tombol KIRI/KANAN pindah slide

V1.04.421 (04 Oktober 2026)
- Alur Sholat Baru + Media Persistence + Fix Mode Fokus
- Alur: Beep -> Adzan -> Himbauan HP -> Niat Qobliyah -> Mode Fokus
- Mode Fokus: 2 fase (Niat Fardhu + Dzikir)
- Media persistence: copy file ke folder permanen

V1.04.420 (04 Oktober 2026)
- Layout Opsi H + Auto-Switch Mode + Ukuran Frame
- Panel kiri 24% + video 76% (mode video/foto)
- 5 mode ukuran frame: POTONG / PAS / ZOOM / FULL / FIT
- Auto-switch Mode Video <-> Mode Normal otomatis

V1.04.418 (04 Oktober 2026)
- Storage Permission Dialog muncul otomatis saat pertama buka
- Hapus token Fonnte dari web dashboard
- Perlambat speed running text (speed 1 = 40x lebih lambat)

V1.30.7 (03 Oktober 2026)
- Verifikasi transfer + konfirmasi restart manual + file template .iO
- Setiap transfer sukses -> auto-bikin file .iO (bundle ZIP)
- Daftar file template di menu Tampilan & Background
- Tombol GUNAKAN / INFO / HAPUS untuk tiap file
- Retry otomatis hanya file yang gagal
- Error detail: stack trace Kotlin asli

V1.30.6 (03 Oktober 2026)
- Fix fokus D-pad TV + fix transfer media iO Control
- Foto/video tidak muncul di TV (fix path lokal)
- AutoFocusPane: auto-fokus ke elemen pertama pane
- Border fokus berputar kembali (NeonFocusBorder)
- Icon dompet di Laporan Keuangan
- UpdateDialog: tombol selalu terlihat

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
1. Foto ustadz tidak tampil -> normalisasi nama hari
2. Suara beep terlalu pendek -> TONE_CDMA_ALERT_CALL_GUARD
3. Update APK harus uninstall dulu -> keystore permanen
4. File "File_paths.xml" error -> rename "file_paths.xml"
5. Konflik GitHub Actions signing -> commit debug.keystore
6. Setup keystore gagal .gitignore -> git add -f debug.keystore
7. Backup Aman error EPERM -> cek permission + fallback
8. KSP 2.3.5 NPE -> upgrade KSP 2.3.12
9. phaseNum di luar scope -> pindah ke luar Box
10. LaunchedEffect belum di-import -> tambah import
11. Icon Battery tidak ada -> ganti BatteryFull
12. LocationSettingsPane & IdentitySettingsPane hilang -> restore
13. Location & Identity placeholder -> buat file sendiri
14. Crash Vertically scrollable infinity -> Box + fade manual
15. ArithmeticException divide by zero -> guard + min 1000ms
16. Versi stuck UTC -> timezone Asia/Jakarta
17. Tag release "autoVersionName" -> extract via aapt
18. iO Control error 401 -> /api/io/receive public route
19. APK tidak bisa di-tap install -> kiri=install kanan=hapus
20. DeviceDiscovery butuh deviceId -> kirim dari IoControlScreen
21. TV beda ukuran kepotong -> ResponsiveLayoutHelper
22. Extension .dp() bentrok dengan Compose -> rename .scaledDp()
23. Foto & video tidak ikut transfer iO Control -> chunk upload
24. Foto/video tidak muncul di TV -> update path lokal
25. Fokus D-pad hilang/tidak jelas -> AutoFocusPane
26. Border fokus tidak terlihat -> tipis dim gold
27. Icon celengan babi di Laporan Keuangan -> dompet
28. Tombol UpdateDialog tidak terlihat -> fillMaxHeight 0.92f
29. Restart otomatis padahal file belum semua terkirim -> verifikasi + restart manual
30. Error transfer tidak jelas penyebabnya -> simpan stack trace Kotlin asli
31. Izin kelola file muncul tiap buka app -> StoragePermissionDialog otomatis
32. Download update gagal karena izin -> cek permission dulu sebelum download
33. Token Fonnte terlihat di web dashboard -> hapus dari web
34. Running text speed 1 masih ngebut -> multiplier 40x
35. Kotak cuaca di TopBar tidak center -> layout 3-zona weight
36. Layout video/foto berantakan -> Opsi H
37. Frame video tidak konsisten dimensi -> 5 mode ukuran frame
38. File foto/video hilang setelah app tutup/update -> media persistence
39. Mode Fokus tidak bisa keluar -> tombol OK/Back berfungsi
40. Mode Fokus tampil slide QRIS/Laporan -> dikondisikan tidak muncul saat fokus
41. Niat sholat belum ada di Mode Fokus -> tambah Niat Fardhu arab + latin + arti
42. Import HomeScreen.kt kurang 4 file -> tambah import
43. MainActivity gagal commit (paste terpotong) -> bagi jadi 7 BAGIAN lebih kecil
44. Video tidak full ke kanan (gap hitam) -> fix bug resizeMode shadowing di MasjidVideoPlayer
45. Mode POTONG/PAS/ZOOM/FIT hasilnya sama saja -> rename variable lokal + teruskan contentScale
46. Jadwal sholat di panel kiri kepotong running text -> struktur root Box -> Column
47. Tombol Settings overlay di posisi salah -> pindah ke root Box
48. Auto-Switch tidak jalan -> reset videoFinishedOnce + tunggu video selesai 1x putaran
49. Slide tidak bisa keluar via remote -> tambah onKeyEvent di SlideManager
50. PIN tidak diminta kembali setelah keluar Settings -> tambah field pinLockMode + logic di MainActivity
51. Video tidak pernah trigger STATE_ENDED (karena REPEAT_MODE_ALL) -> polling posisi video 500ms
52. Wisdom card tulisan terpotong -> auto-shrink font + multi-line dinamis + TextOverflow.Clip
53. Wisdom card tidak full ke kanan -> hapus Box wrapper + AnimatedContent fillMaxWidth
54. Panel kiri tidak presisi 100% -> pakai weight() bukan fillMaxWidth(fraction)
55. Tombol GALERI & FILE di TV tidak bisa pilih file -> custom VideoFilePickerDialog
56. Jadwal on/off tidak berfungsi (hanya UI) -> logic brightness otomatis dari jadwal sholat
57. Transfer media selalu gagal (chunk + Base64 + timeout 30 detik) -> streaming upload 1 request + timeout 10 menit
58. WiFi mati saat transfer (layar TV off) -> TransferForegroundService + WiFiLock
59. Namespace tidak konsisten (com.example vs dev.andikune.masjidio) -> ubah semua ke dev.andikune.masjidio
60. Tombol dialog izin kosong (teks tidak render) -> Box + height(56dp) + contentAlignment Center
61. Install dialog muncul berulang setelah user grant -> state installDialogAlreadyShown (muncul sekali per sesi)
62. Foto QRIS ketuker dengan foto ustadz -> folder terpisah + nama file = fieldKey + settings.json (sudah benar sejak awal)
63. File .iO dianggap cache oleh cleaner -> auto-bikin file .nomedia di folder Terima

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

[06-10-2026] - V1.04.426
Error: Namespace tidak konsisten (com.example vs dev.andikune.masjidio) -> build gagal ratusan error
File: Semua file .kt di app/src/main/java/com/example/
Solusi: Ubah SEMUA file yang masih com.example ke dev.andikune.masjidio (package + import). Folder fisik tetap com/example/ (Kotlin izinkan package beda dari folder).

[06-10-2026] - V1.04.426
Error: Transfer media selalu gagal (foto & video tidak terkirim)
File: app/src/main/java/com/example/util/RemoteControlClient.kt
Solusi: Ganti chunk upload + Base64 dengan STREAMING UPLOAD (1 request HTTP per file). Timeout 30 detik -> 10 menit. Tidak ada Base64 overhead.

[06-10-2026] - V1.04.426
Error: WiFi TV mati saat transfer (layar off) -> transfer putus
File: app/src/main/java/com/example/kiosk/TransferForegroundService.kt
Solusi: Buat Foreground Service baru dengan WiFiLock + WakeLock. Start saat kirim media, stop setelah selesai.

[06-10-2026] - V1.04.426
Error: Tombol dialog izin kosong (teks & ikon tidak render)
File: app/src/main/java/com/example/MainActivity.kt
Solusi: Ganti Row dengan padding ke Box dengan height(56.dp) fixed + contentAlignment Center. Tambah .onFocusChanged sebelum .focusable().

[06-10-2026] - V1.04.426
Error: InstallPermissionDialog muncul berulang setelah user grant/skip
File: app/src/main/java/com/example/MainActivity.kt
Solusi: Tambah state installDialogAlreadyShown (Boolean). Set true setelah user grant/skip. Dialog hanya muncul kalau masih false.

[06-10-2026] - V1.04.423
Error: Video tidak pernah trigger STATE_ENDED untuk auto-switch
File: app/src/main/java/com/example/ui/components/MasjidVideoPlayer.kt
Solusi: Video pakai REPEAT_MODE_ALL -> tidak pernah STATE_ENDED. Solusi: polling posisi video setiap 500ms deteksi loop (posisi turun drastis dari >80% ke <20%).

[06-10-2026] - V1.04.423
Error: Compile error 'Unresolved reference videoFinishedOnce' + 'onVideoEnded not found'
File: app/src/main/java/com/example/ui/home/HomeScreen.kt
Solusi: Rename field state videoFinishedOnce -> videoLoopedOnce, parameter onVideoEnded -> onVideoLooped. Kirim file FULL TIMPA supaya tidak campuran.

[06-10-2026] - V1.04.423
Error: Tombol GALERI & FILE di TV muncul 'Anda tidak memiliki aplikasi untuk melakukan tindakan ini'
File: app/src/main/java/com/example/ui/settings/VideoSettingsPane.kt
Solusi: ActivityResultContracts butuh DocumentsUI yang tidak ada di TV. Buat custom VideoFilePickerDialog.

[06-10-2026] - V1.04.423
Error: Wisdom card tidak full sampai tepi kanan
File: app/src/main/java/com/example/ui/components/WisdomCardCarousel.kt
Solusi: AnimatedContent tidak dikasih Modifier.fillMaxWidth(). Fix: hapus Box wrapper + AnimatedContent langsung pakai modifier.fillMaxWidth().

[06-10-2026] - V1.04.422
Error: Video tidak full ke kanan (gap hitam di sisi kanan panel)
File: app/src/main/java/com/example/ui/components/MasjidVideoPlayer.kt
Solusi: Rename variable lokal `resizeMode` -> `aspectResizeMode`. Sebelumnya nama variable bentrok dengan property PlayerView.resizeMode.

[06-10-2026] - V1.04.422
Error: Mode POTONG/PAS/ZOOM/FIT hasilnya sama saja
File: app/src/main/java/com/example/ui/home/HomeScreen.kt
Solusi: Teruskan parameter `contentScale` dari settings.videoFrameScale ke MasjidVideoPlayer dan PhotoSlideshow.

[06-10-2026] - V1.04.422
Error: Jadwal sholat di panel kiri kepotong oleh running text
File: app/src/main/java/com/example/ui/home/HomeScreen.kt
Solusi: Ubah struktur root Mode Video dari `Box` (overlay) -> `Column`.

[06-10-2026] - V1.04.422
Error: Tombol ⚙ Settings overlay posisi salah (masuk ke dalam panel)
File: app/src/main/java/com/example/ui/home/HomeScreen.kt
Solusi: Pindah tombol ⚙ dari dalam panel kanan ke root Box dengan `align(Alignment.TopEnd)`.

[06-10-2026] - V1.04.422
Error: Auto-Switch Mode tidak berfungsi (tidak berganti mode)
File: app/src/main/java/com/example/ui/home/HomeScreen.kt
Solusi: Reset `videoFinishedOnce = false` sebelum tunggu + tunggu video selesai 1x putaran.

[06-10-2026] - V1.04.422
Error: Slide tidak bisa keluar via tombol OK/BACK remote
File: app/src/main/java/com/example/ui/slides/SlideManager.kt
Solusi: Tambah `onKeyEvent` handler + FocusRequester.

[04-10-2026] - V1.04.421
Error: MainActivity gagal commit (paste terpotong)
File: app/src/main/java/com/example/MainActivity.kt
Solusi: Bagi jadi 7 BAGIAN lebih kecil supaya tidak kepotong saat paste

[04-10-2026] - V1.04.421
Error: File foto/video hilang setelah app tutup/update
File: 5 file settings (CustomBackground, Identity, Qris, Video, WeeklyOfficers)
Solusi: Buat MediaPersistenceHelper.kt + copy file ke filesDir/masjid_io/

[04-10-2026] - V1.04.421
Error: Import HomeScreen.kt kurang 4 file
File: app/src/main/java/com/example/ui/home/HomeScreen.kt
Solusi: Tambah import ClockAndDate, MosqueHeader, QRISFocusOverlay, SlideManager

[04-10-2026] - V1.04.420
Error: Import HomeScreen.kt kurang (ClockAndDate, MosqueHeader, QRISFocusOverlay, SlideManager)
File: app/src/main/java/com/example/ui/home/HomeScreen.kt
Solusi: Tambah 4 import

[04-10-2026] - V1.04.419
Error: Kotak cuaca di TopBar tidak presisi di tengah
File: app/src/main/java/com/example/ui/components/TopBar.kt
Solusi: Ganti Arrangement.SpaceBetween ke 3-zona weight(1f)

[04-10-2026] - V1.04.418
Error: Aplikasi tidak bisa download update karena belum izin akses file
File: app/src/main/java/com/example/MainActivity.kt
Solusi: Tambah StoragePermissionDialog yang muncul otomatis + cek permission sebelum download

[04-10-2026] - V1.04.418
Error: Token Fonnte tampil di web dashboard (bisa diubah via web)
File: app/src/main/java/com/example/ui/remote/RemoteServer.kt
Solusi: Hapus fonnteToken & fonnteGroupId dari HTML + saveFields + applySettingsUpdate + getFullSettingsJson

[04-10-2026] - V1.04.418
Error: Aplikasi bentrok dengan paket yang sudah ada saat install update
File: debug.keystore / app/build.gradle.kts / .github/workflows/build.yml
Solusi: Upload ulang debug.keystore asli dari riwayat commit sebelum AI Studio ubah

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
Solusi: Rename .dp() -> .scaledDp() dan .sp() -> .scaledSp()

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

ATURAN FORMAT (V1.04.426+):
1. Visualisasi folder (emoji + indentasi)
2. Path lengkap file
3. Nama file
4. URL edit (file lama) atau URL new (file baru)
5. Kode timpa full
6. Pesan commit

CATATAN FORMAT:
- Elemen "2 opsi akses" TIDAK perlu lagi
- Elemen "Bahasa Indonesia" TIDAK perlu lagi
- Pakai code block (3 backtick) untuk path/URL/nama file
- Kalau paste gagal -> bagi jadi BAGIAN lebih kecil

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
- Namespace      : dev.andikune.masjidio
- Application ID : dev.andikune.masjidio.tv
- Versi saat ini : V1.04.426
- PIN Developer  : 140399

UPDATE WAJIB TIAP BUILD:
1. Bump versionName (otomatis dari GITHUB_RUN_NUMBER)
2. Tambah entri baru di UpdateHistory.kt
3. Update AppKnowledge.kt (struktur + fitur + build error)

ATURAN FORMAT:
- Setiap respon pakai code block untuk path/URL/nama file
- Kode timpa full
- Bahasa Indonesia simple
- 1 chat = 1 fitur utuh kalau muat
- Kalau paste gagal -> bagi jadi BAGIAN lebih kecil (max 3-4 BAGIAN per file)

=== ATURAN SIGNATURE / KEYSTORE (WAJIB) ===

JANGAN PERNAH ubah file-file ini:
1. debug.keystore (root repo)
2. Blok signingConfigs di app/build.gradle.kts
3. Bagian signing di .github/workflows/build.yml

KALAU PAKAI AI STUDIO:
- Tekankan di prompt: "JANGAN ubah debug.keystore,
  JANGAN ubah signingConfigs, JANGAN ubah workflow signing."
- AI Studio cuma boleh EDIT kode Kotlin/XML saja
- Build tetap via GitHub Actions

ATURAN INSTALL APK:
- SELALU install dari GitHub Release
- JANGAN install APK dari AI Studio
- Alasan: signature beda -> Android tolak -> bentrok

RIWAYAT KEJADIAN:
- 04-10-2026: AI Studio ubah debug.keystore -> bentrok
- Solusi: restore debug.keystore dari commit lama

FITUR YANG BELUM SELESAI:
- Tidak ada (semua fitur selesai per V1.04.426)
    """.trimIndent()
        val MEMORY_INSTRUCTION = """
ATURAN FORMAT:
- Kode timpa full, jangan suruh user edit manual
- Bahasa Indonesia yang mudah dipahami
- 1 chat = 1 fitur utuh kalau muat
- Kalau kode tidak muat 1 bubble -> tulis "jangan commit dulu, masih ada sambungan"
- JANGAN nolak buka link GitHub publik
- JANGAN gabung link 1 file dengan lainnya - pisah per file
- Pakai code block (3 backtick) untuk path/URL/nama file
- Kalau file panjang (>300 baris) -> bagi jadi 3-4 BAGIAN lebih kecil
- Kalau paste gagal di HP -> bagi jadi lebih banyak BAGIAN

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
9.  Tampilan TV
10. Kartu Nasihat & Mutiara
11. Running Text
12. Audio & Adzan
13. Mode Ramadhan
14. Keamanan (PIN LOCK 3 opsi)
15. Daya & Booting (jadwal on/off otomatis)
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
- debug.keystore                    -> keystore permanen (JANGAN DIUBAH)
- gradle/libs.versions.toml         -> KSP & dependency

PATH BACKUP AMAN:
/sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT

PENANGANAN BUILD ERROR:
1. Cek log GitHub Actions step "Build Debug APK"
2. Cari baris diawali "e:" (error Kotlin)
3. Perbaiki file yang error
4. Update AppKnowledge.kt -> BUILD_ERROR_HISTORY
5. Commit ulang
    """.trimIndent()

    val MEMORY_KNOWLEDGE = """
============================================================
MEMORY KNOWLEDGE & INSTRUCTION — MASJID.IO
Untuk AI baru yang membaca backup ini
============================================================

=== BAGIAN 1 — ATURAN FORMAT RESPON ===

Elemen wajib (V1.04.426+):
1. Visualisasi folder (emoji + indentasi)
2. Path lengkap file
3. Nama file
4. URL edit / URL new
5. Kode timpa full
6. Pesan commit

CATATAN:
- Elemen "2 opsi akses" TIDAK perlu lagi
- Elemen "Bahasa Indonesia" TIDAK perlu lagi
- Pakai code block untuk path/URL/nama file
- Kalau file >300 baris -> bagi jadi 3-7 BAGIAN kecil

=== BAGIAN 1B — ATURAN ANTI-TRUNCATION ===

Kalau kode kepanjangan:
1. Judul: "BAGIAN 1 DARI N"
2. Akhir: "(lanjut di BAGIAN berikutnya)"
3. Awal: "(sambungan dari BAGIAN sebelumnya)"
4. JANGAN potong di tengah fungsi
5. JANGAN bilang "kode dilanjut di chat berikutnya"
6. Kalau paste gagal di HP -> bagi jadi lebih banyak BAGIAN

=== BAGIAN 1C — ATURAN LINK GITHUB ===

1. JANGAN tolak buka link GitHub publik
2. JANGAN gabung link 1 file dengan link lain
3. Repo Masjid.io PUBLIC -> bebas diakses

=== BAGIAN 2 — ATURAN KERJA ===

1. Konfirmasi dulu sebelum eksekusi
2. Jujur kalau belum tahu
3. JANGAN hapus fitur lama tanpa izin
4. Lihat kode asli dulu sebelum timpa
5. Kerjakan per batch
6. JANGAN buka file sama 2x
7. Konfirmasi sebelum lanjut

=== BAGIAN 3 — IDENTITAS ===

USER:
  Nama : Andi
  Email: andikune@gmail.com

DEVICE: HP Android (bukan PC)
REPO: https://github.com/andikune-ux/Masjid.io (PUBLIC)

APLIKASI:
  Nama           : Masjid.io
  Platform       : Android TV (Jetpack Compose)
  Namespace      : dev.andikune.masjidio
  Application ID : dev.andikune.masjidio.tv
  Versi saat ini : V1.04.426
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
14. Keamanan (PIN LOCK 3 opsi)
15. Daya & Booting (jadwal on/off otomatis)
16. Slide Fullscreen
17. CCTV Masjid
18. iO Control
19. Tentang Aplikasi
20. Opsi Developer (PIN 140399)

=== BAGIAN 5 — SOSIAL MEDIA ===

WhatsApp : https://chat.whatsapp.com/ErJpG34fdzwL9FOmoh4fNN
TikTok   : https://www.tiktok.com/@nayyra.une
Instagram: https://www.instagram.com/nayyra.une

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

1. KERJAKAN PER BATCH
2. JANGAN BUKA FILE YANG SAMA BERKALI-KALI
3. KONFIRMASI SEBELUM LANJUT
4. JANGAN ASUMSI - LIHAT KODE ASLI DULU
5. JANGAN HAPUS FITUR LAMA TANPA IZIN

=== BAGIAN 12 — FULL SOURCE CODE EXPORT ===

Backup Aman WAJIB menyertakan ISI SEMUA FILE KODE.
Format:
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
1. Baca BACKUP AMAN terlebih dahulu
2. Pahami struktur aplikasi + isi kode
3. Ikuti aturan format respon
4. Ikuti aturan anti-truncation
5. Ikuti aturan link GitHub
6. Konfirmasi dulu sebelum eksekusi
7. Jangan menebak - tanya user kalau tidak tahu

=== BAGIAN 15 — LINK GITHUB SOURCE CODE ===

Repo Utama: https://github.com/andikune-ux/Masjid.io
Tree API: https://api.github.com/repos/andikune-ux/Masjid.io/git/trees/main?recursive=1
RAW: https://raw.githubusercontent.com/andikune-ux/Masjid.io/main/
BLOB: https://github.com/andikune-ux/Masjid.io/blob/main/

LINK EDIT (file lama):
https://github.com/andikune-ux/Masjid.io/edit/main/{path}

LINK NEW (file baru):
https://github.com/andikune-ux/Masjid.io/new/main/{path}

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
- Base 1920x1080 — auto-scale 0.6x s/d 2.5x
- Safe Area Padding 0-10%
- Preset: AUTO / STANDAR / ULTRAWIDE / 4:3
- Menu: Tampilan TV
- Extension: .scaledDp() & .scaledSp() — JANGAN pakai .dp()/.sp()

=== BAGIAN 20 — FITUR TRANSFER MEDIA iO CONTROL ===

- Foto & video ikut terkirim via STREAMING UPLOAD (V1.04.426)
- 1 request HTTP per file (bukan chunk + Base64)
- Timeout 10 menit per file (untuk video besar)
- Progress report real-time setiap ~256 KB
- File di filesDir/masjid_io/{qris|logo|background|video|slideshow|prayer_card}
=== BAGIAN 21 — FITUR AUTO-FOCUS D-PAD (V1.30.5) ===

- AutoFocusPane.kt: auto-fokus ke elemen pertama pane
- Fokus tidak mendarat di tombol Kembali
- Border fokus INSTANT pindah
- Scale smooth 150ms menyusul
- Blur glow tetap ada

=== BAGIAN 22 — FITUR MEDIA PATH FIX (V1.30.6) ===

- Foto/video tidak muncul di TV (fix path lokal)
- applyMediaPathToSettings() di RemoteServer.kt
- Ganti path HP -> path lokal setelah media tersimpan

=== BAGIAN 23 — FITUR BORDER BERPUTAR (V1.30.6) ===

- NeonFocusBorder: core border tipis (dim gold 20%)
- Glow berputar 2-kutub (putih + emas)
- Tail 30% dari keliling

=== BAGIAN 24 — FITUR UPDATE DIALOG FIX (V1.30.6) ===

- Dialog pakai fillMaxHeight(0.92f)
- Changelog pakai weight(1f) + verticalScroll
- Tombol SKIP / NANTI / UPDATE selalu terlihat

=== BAGIAN 25 — FITUR VERIFIKASI + RESTART MANUAL (V1.30.7) ===

- Phase baru: VERIFYING + READY_TO_RESTART
- Restart MANUAL via tombol KONFIRMASI RESTART
- Tombol COBA LAGI: retry HANYA file gagal
- Tombol LIHAT LOG: popup stack trace Kotlin asli
- Tombol LEWATI: template aktif otomatis saat app dibuka ulang

=== BAGIAN 26 — FITUR FILE TEMPLATE .iO (V1.30.7) ===

- Format .iO = ZIP (settings.json + metadata.json + media/)
- Auto-bikin setiap transfer selesai
- Nama file: {Merk HP}-{dd-MM-yyyy HH.mm}.iO
- Lokasi: /sdcard/masjid.io/Terima/
- File TIDAK dihapus otomatis
- 3 tombol per file: GUNAKAN / INFO / HAPUS
- File baru: IoBundleHelper.kt + IoBundleListSection.kt

=== BAGIAN 27 — ATURAN SIGNATURE / KEYSTORE (WAJIB) ===

JANGAN PERNAH ubah file-file ini:
1. debug.keystore (root repo)
2. Blok signingConfigs di app/build.gradle.kts
3. Bagian signing di .github/workflows/build.yml

KALAU PAKAI AI STUDIO:
- Tekankan di prompt: "JANGAN ubah debug.keystore,
  JANGAN ubah signingConfigs, JANGAN ubah workflow signing."
- AI Studio cuma boleh EDIT kode Kotlin/XML saja
- Build tetap via GitHub Actions

ATURAN INSTALL APK:
- SELALU install dari GitHub Release
- JANGAN install APK dari AI Studio
- Alasan: signature beda -> Android tolak -> bentrok

RIWAYAT KEJADIAN:
- 04-10-2026: AI Studio ubah debug.keystore -> bentrok
- Solusi: restore debug.keystore dari commit lama

=== BAGIAN 28 — FITUR STORAGE PERMISSION DIALOG (V1.04.418) ===

- Dialog muncul otomatis saat pertama buka app
- Tombol BERI IZIN -> buka Manage All Files Access
- Auto-detect kalau izin sudah diberikan
- Cek permission sebelum download update

=== BAGIAN 29 — FITUR LAYOUT OPSI H (V1.04.420) ===

- Mode Video/Foto: panel kiri 24% + panel kanan 76%
- Panel kiri: logo + kotak gabungan jam/tanggal + list sholat vertikal + progress bar
- List sholat vertikal 6 baris (Subuh -> Isya)
- Highlight NEXT: emas + border tebal + pulse animation
- Kotak gabungan: jam 32sp + garis pemisah + hijriah + masehi
- Icon sholat pakai Material Icon (bukan emoji)
- Overlay wisdom card di bawah video (bar tipis)
- Tombol Settings overlay kanan atas
- Running text full width bawah
- Mode Normal: layout lengkap seperti sebelumnya

=== BAGIAN 30 — FITUR AUTO-SWITCH MODE (V1.04.420 + V1.04.423) ===

- Bolak-balik Mode Video <-> Mode Normal
- Interval video: 1-60 menit
- Durasi normal: 1-30 menit
- V1.04.423: Tunggu video selesai 1x putaran sebelum switch
- V1.04.423: Polling posisi video (500ms) deteksi loop
- Kalau belum ada video/foto -> mode normal permanen
- JEDA otomatis saat: Mode Fokus / Slide Fullscreen / Ramadhan aktif

=== BAGIAN 31 — FITUR UKURAN FRAME (V1.04.420 + V1.04.423) ===

- 5 mode: POTONG / PAS / ZOOM / FULL / FIT
- POTONG (Crop) seperti Instagram Reels
- PAS (Fit/Letterbox) seperti Netflix
- ZOOM (Fill) - V1.04.423: pakai zoomFactor 1.15f (beda dari POTONG)
- FULL (Fullscreen) panel kiri hilang
- FIT (Stretch) paksa video/foto sesuaikan frame
- Frame LOCK ukuran tetap (16:9, 9:16, 1:1 semua sama)

=== BAGIAN 32 — ALUR SHOLAT BARU (V1.04.421) ===

Alur saat waktu sholat tiba:
1. Beep berbunyi
2. Tahap 1: Overlay Adzan fullscreen ('ADZAN [waktu]')
3. Tahap 2: Overlay Himbauan HP ('HENINGKAN HP ANDA')
4. Tahap 3: Overlay Niat Qobliyah + Countdown Iqomah
5. Tahap 4: Mode Fokus Sholat (Niat Fardhu -> Dzikir)

File terkait:
- AdzanSequenceOverlay.kt (3 tahap pembuka)
- PrayerFocusOverlay.kt (2 fase Mode Fokus)

Tombol remote:
- OK di Adzan Sequence: lanjut tahap berikutnya
- BACK di Adzan Sequence: skip ke Mode Fokus
- OK di Mode Fokus: skip ke fase berikutnya
- BACK di Mode Fokus: keluar (kalau diizinkan)

=== BAGIAN 33 — FITUR MEDIA PERSISTENCE (V1.04.421) ===

- File foto/video di-copy ke filesDir/masjid_io/ (folder permanen)
- Fix masalah file hilang setelah app tutup/update/reboot
- File baru: MediaPersistenceHelper.kt
- Support: QRIS, Logo, Background, Video, Foto Slideshow, Foto Petugas
- Hapus file lama otomatis saat user ganti/hapus
- Fallback ke URI asli kalau copy gagal

=== BAGIAN 34 — FIX MODE VIDEO FULL-FRAME (V1.04.422) ===

Masalah yang diperbaiki:
1. Video tidak full ke kanan (gap hitam di sisi kanan panel)
   - Penyebab: bug resizeMode shadowing di MasjidVideoPlayer
   - Solusi: rename variable lokal resizeMode -> aspectResizeMode
2. Mode POTONG/PAS/ZOOM/FIT hasilnya sama saja
   - Penyebab: contentScale tidak diteruskan dari settings
   - Solusi: teruskan contentScale dari settings.videoFrameScale
3. Jadwal sholat di panel kiri kepotong running text
   - Penyebab: struktur root Box (overlay)
   - Solusi: ubah ke Column
4. Tombol Settings overlay posisi salah
   - Solusi: pindah ke root Box (align TopEnd)
5. Auto-Switch tidak jalan
   - Solusi: reset sebelum tunggu + tunggu video selesai 1x putaran

=== BAGIAN 35 — FITUR PIN LOCK MODE (V1.04.422) ===

3 opsi di Settings -> Keamanan -> PIN LOCK:
- IMMEDIATE    = Setiap tekan tombol Settings -> langsung minta PIN
- TIMEOUT_5MIN = Minta PIN 5 menit setelah kembali ke menu utama
- UNTIL_EXIT   = Tidak minta PIN sampai aplikasi ditutup (default)

File terkait:
- AppSettings.kt (enum PinLockMode + field pinLockMode)
- SettingsRepository.kt (load/save pinLockMode)
- SecuritySettingsPane.kt (UI 3 radio opsi)
- MainActivity.kt (logic: sessionPinVerified + lastPinVerifiedTime)

=== BAGIAN 36 — SLIDE NAVIGASI REMOTE (V1.04.422 + V1.04.423) ===

- Tekan OK / BACK / ENTER / DPAD_CENTER -> keluar slide
- Tekan KIRI / KANAN -> pindah slide
- V1.04.423: CLEAN VIEW - hapus semua overlay navigasi
- Cooldown 5 menit setelah dismiss

=== BAGIAN 37 — FITUR JADWAL ON/OFF OTOMATIS (V1.04.423) ===

Cara kerja:
- ON  otomatis = Subuh - X menit (X: 5-120, default 15)
- OFF otomatis = Isya  + Y menit (Y: 5-120, default 30)
- Brightness 0 saat jam OFF
- Polling jadwal setiap 30 detik

File terkait:
- AppSettings.kt (autoOnMinutesBeforeSubuh + autoOffMinutesAfterIsya + autoOffDialogEnabled)
- SettingsRepository.kt (load/save)
- PowerSettingsPane.kt (UI + kartu jadwal + slider)
- MainActivity.kt (logic brightness + deteksi jam OFF)
- AutoOffDialog.kt (popup konfirmasi)

=== BAGIAN 38 — AUTO-OFF DIALOG (V1.04.423) ===

- Popup muncul saat user tekan remote di jam OFF
- Tombol YA (kiri, hijau) = matikan jadwal
- Tombol TIDAK (kanan, abu-abu) = popup hilang, layar redup lagi
- Auto-dismiss 2 menit -> dianggap TIDAK
- File baru: AutoOffDialog.kt

=== BAGIAN 39 — VIDEO FILE PICKER CUSTOM (V1.04.423) ===

- File baru: VideoFilePickerDialog.kt
- Custom file picker dalam app (TV friendly)
- Scan rekursif /sdcard/ + semua subfolder
- 3 mode: VIDEO / IMAGE / ALL
- Dipakai di VideoSettingsPane (mode VIDEO & IMAGE)

=== BAGIAN 40 — WISDOM CARD AUTO-SHRINK (V1.04.423) ===

- Auto-shrink font berdasarkan panjang teks
- TextOverflow.Clip (bukan Ellipsis)
- AnimatedContent pakai fillMaxWidth()
- Wisdom card full sampai tepi kanan layar

=== BAGIAN 41 — ICON JADWAL SHOLAT BERBEDA (V1.04.423) ===

- Subuh -> WbTwilight
- Syuruq -> WbSunny
- Dzuhur -> LightMode
- Ashar -> WbCloudy
- Maghrib -> NightsStay
- Isya -> Nightlight

=== BAGIAN 42 — STREAMING UPLOAD TRANSFER MEDIA (V1.04.426) ===

Masalah lama:
- Chunk upload + Base64 (1000 request HTTP per file)
- Timeout 30 detik per chunk
- Buffer semua di RAM
- WiFi mati saat layar TV off
- Hasil: transfer sering gagal

Solusi V1.04.426:
- STREAMING UPLOAD: 1 request HTTP per file
- File dibaca langsung dari disk -> socket
- Timeout 10 menit per file (untuk video besar)
- Tidak ada Base64 overhead (33% lebih efisien)
- TCP handle retransmisi otomatis

File terkait:
- RemoteServer.kt: endpoint baru POST /api/io/upload-stream
- RemoteControlClient.kt: sendSingleMediaFileStreaming()
- TransferForegroundService.kt (BARU): WiFiLock + WakeLock

Header yang dipakai:
- X-File-Id, X-Field-Key, X-File-Name, X-File-Type
- X-Total-Bytes, X-Mime-Type

=== BAGIAN 43 — FOREGROUND SERVICE + WIFILOCK (V1.04.426) ===

File baru: TransferForegroundService.kt

Fungsi:
1. Foreground Service dengan notifikasi permanen
2. WiFiLock (WIFI_MODE_FULL_HIGH_PERF)
3. WakeLock (PARTIAL_WAKE_LOCK, max 30 menit)

Cara pakai:
- TransferForegroundService.start(context) — mulai
- TransferForegroundService.stop(context) — berhenti

Trigger dari IoControlScreen:
- Start: fase SENDING_MEDIA
- Stop: setelah mediaResult

=== BAGIAN 44 — COLOR CODING TEMPLATE .iO (V1.04.426) ===

Kategori warna berdasarkan successPercentage:
- HIJAU (100%)    = semua file berhasil
- KUNING (50-99%) = sebagian besar berhasil
- MERAH (1-49%)   = sebagian besar gagal
- HITAM (0%)      = gagal total

File terkait:
- IoBundleHelper.kt: BundleInfo.successPercentage + colorCategory
- IoBundleListSection.kt: getColorCategory() + UI warna

Element yang berubah warna:
- Background kartu
- Border kartu (2dp)
- Badge persentase (di kotak icon)
- Badge kategori (kanan atas)
- Ringkasan kategori (atas daftar)
- Border dialog INFO/DELETE/RESTART

=== BAGIAN 45 — TOMBOL GALERI DI SEMUA PICKER (V1.04.426) ===

Tombol GALERI di samping FILE untuk:
- Video Kegiatan Masjid (VideoSettingsPane.kt)
- Logo Masjid (IdentitySettingsPane.kt)
- QRIS (QrisSettingsPane.kt)
- Background Custom (CustomBackgroundPane.kt)
- Foto Petugas (WeeklyOfficersSettingsPane.kt) — menu 3 opsi

Implementasi:
- GALERI: ActivityResultContracts.GetContent()
- FILE: VideoFilePickerDialog (custom)

Kenapa dipisah:
- Di HP: user pakai GALERI (lebih mudah)
- Di TV: user pakai FILE (custom picker, D-pad)

=== BAGIAN 46 — FOLDER PROTECTION .NOMEDIA (V1.04.426) ===

- Auto-bikin file .nomedia di folder /sdcard/masjid.io/Terima/
- Folder Terima tidak dianggap cache
- File .iO tidak dihapus otomatis oleh Clean Master, dll
- Fungsi: IoBundleHelper.ensureNoMediaFile()

=== BAGIAN 47 — NAMESPACE CONSISTENCY (V1.04.426) ===

- Namespace konsisten: dev.andikune.masjidio
- Application ID: dev.andikune.masjidio.tv
- Folder fisik tetap com/example/ (Kotlin izinkan package beda dari folder)
- Semua file .kt wajib pakai package dev.andikune.masjidio.*

Kalau nambah file baru:
- Package declaration: package dev.andikune.masjidio.xxx
- Import: import dev.andikune.masjidio.xxx

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
