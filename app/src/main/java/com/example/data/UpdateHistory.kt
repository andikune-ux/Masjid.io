package com.example.data

/**
 * UpdateHistory — Riwayat update aplikasi MASJID.IO.
 */
object UpdateHistory {

    data class UpdateEntry(
        val version: String,
        val date: String,
        val title: String,
        val features: List<String>
    )

    val entries: List<UpdateEntry> = listOf(
        UpdateEntry(
            version = "V1.04.423",
            date = "06 Oktober 2026",
            title = "Jadwal On/Off Otomatis + File Picker Custom + Fix Video Stage + Wisdom Card Full",
            features = listOf(
                // ===== JADWAL ON/OFF OTOMATIS =====
                "Fitur BARU: Jadwal On/Off layar OTOMATIS dari jadwal sholat",
                "ON otomatis = Subuh - X menit (X bisa diatur 5-120 menit, default 15)",
                "OFF otomatis = Isya + Y menit (Y bisa diatur 5-120 menit, default 30)",
                "Brightness otomatis jadi 0 (layar redup total) saat jam OFF",
                "Brightness kembali normal saat jam ON (Subuh - X menit)",
                "Polling jadwal setiap 30 detik — real-time ikut waktu",
                "Tampil kartu 'Jadwal Hari Ini' di menu Daya & Booting",
                "Kartu menampilkan jam ON & OFF real-time sesuai jadwal sholat hari ini",
                "2 slider baru: 'Nyala Sebelum Subuh' & 'Redup Setelah Isya'",
                // ===== POPUP KONFIRMASI =====
                "Fitur BARU: AutoOffDialog — popup konfirmasi saat user tekan remote di jam OFF",
                "Popup tanya: 'Apakah Anda bersedia MEMATIKAN JADWAL ON/OFF?'",
                "Tombol YA (kiri, hijau) = matikan jadwal on/off → layar nyala terus",
                "Tombol TIDAK (kanan, abu-abu) = popup hilang, layar redup lagi",
                "Auto-dismiss 2 menit → dianggap TIDAK (default aman)",
                "Back button = TIDAK (langsung dismiss)",
                "Focus D-pad otomatis ke tombol TIDAK",
                "Toggle 'Aktifkan Popup Konfirmasi' di menu Daya & Booting",
                "File baru: AutoOffDialog.kt (popup + countdown + focus management)",
                "Temporary wake 2 menit setelah popup di-dismiss",
                // ===== FIX VIDEO STAGE =====
                "Fix CRITICAL: video tidak full ke kanan (gap hitam di sisi kanan panel)",
                "Fix: bug resizeMode shadowing di MasjidVideoPlayer → rename jadi aspectResizeMode",
                "Fix: contentScale diteruskan dari settings.videoFrameScale ke player",
                "Fix: 5 mode ukuran frame (POTONG/PAS/ZOOM/FULL/FIT) sekarang benar-benar beda",
                "Fitur BARU: parameter zoomFactor 1.15f untuk mode ZOOM (beda dari POTONG)",
                "Fix: struktur root Mode Video Box → Column, running text jadi bagian Column",
                "Fix: jadwal sholat di panel kiri TIDAK kepotong running text",
                "Fix: jadwal sholat pakai weight(1f) → isi penuh sampai tepat di atas kotak 'Menuju Subuh'",
                "Fix: tombol ⚙ Settings overlay di sudut kanan atas LAYAR (bukan di dalam panel kanan)",
                "Improve: panel pakai weight() bukan fillMaxWidth(fraction) → 100% presisi tanpa rounding gap",
                "Improve: wisdom card full sampai tepi kanan layar (76% panel kanan)",
                "Fix: AnimatedContent di WisdomCardCarousel pakai fillMaxWidth() → tidak wrap content",
                // ===== FIX AUTO-SWITCH =====
                "Fix CRITICAL: Auto-Switch Mode sekarang berfungsi dengan benar",
                "Fix: video pakai REPEAT_MODE_ALL → tidak pernah trigger STATE_ENDED",
                "Solusi: polling posisi video setiap 500ms deteksi loop",
                "Deteksi loop: posisi turun drastis dari >80% ke <20% durasi",
                "Rename callback onVideoEnded → onVideoLooped (semantik lebih tepat)",
                "Logic: tunggu video selesai 1x putaran → baru switch ke Mode Normal",
                // ===== FIX WISDOM CARD =====
                "Fix: wisdom card tulisan terpotong — auto-shrink font + multi-line dinamis",
                "Strategi auto-shrink: ≤60 char 14sp/1 baris, ≤100 13sp/2 baris, ≤150 12sp/2 baris, ≤200 11sp/3 baris, >200 10sp/3 baris",
                "Ganti TextOverflow.Ellipsis → TextOverflow.Clip (teks utuh tanpa '...')",
                "Padding wisdom card 12x4dp (lebih lega untuk teks panjang)",
                "Line height dinamis sesuai font size",
                // ===== SLIDE CLEAN VIEW =====
                "Improve: SlideManager CLEAN VIEW — hapus semua overlay navigasi",
                "Hapus tombol X (kanan atas)",
                "Hapus panah ◀ kiri & ▶ kanan (tengah sisi)",
                "Hapus indicator dot + label slide (bawah tengah)",
                "Hapus hint tombol remote (kiri bawah)",
                "Fungsi tombol remote TETAP JALAN: OK/BACK keluar, KIRI/KANAN pindah slide",
                // ===== FILE PICKER CUSTOM =====
                "Fitur BARU: VideoFilePickerDialog.kt — custom file picker dalam app (TV friendly)",
                "Scan rekursif /sdcard/ + semua subfolder (skip folder sistem)",
                "3 mode: VIDEO (hanya video) / IMAGE (hanya foto) / ALL (semua file)",
                "Navigasi D-pad remote TV (auto-focus, clickable semua item)",
                "Icon berbeda per tipe (video/foto/audio/dok/unknown)",
                "Tampilkan size + ekstensi file",
                "Fix: tombol GALERI + FILE di VideoSettingsPane sekarang benar-benar berfungsi",
                "Fix: file picker lama (ActivityResultContracts) diganti custom picker yang TV friendly",
                "Improve: copy dari path lokal (bukan URI) → lebih reliable",
                "File baru: VideoFilePickerDialog.kt (enum FilePickerMode + Composable dialog)",
                "File baru: data class PickerItem + helper scanFolder() + folderContainsMatchingFiles()",
                // ===== JADWAL SHOLAT ICON =====
                "Improve: icon jadwal sholat berbeda per waktu (bukan emoji)",
                "Icon: Subuh=WbTwilight, Syuruq=WbSunny, Dzuhur=LightMode",
                "Icon: Ashar=WbCloudy, Maghrib=NightsStay, Isya=Nightlight",
                // ===== CLEANUP FIELD LAMA =====
                "Cleanup: hapus field autoOnTime & autoOffTime (diganti otomatis dari jadwal sholat)",
                "Field baru: autoOffMinutesAfterIsya (Int, default 30)",
                "Field baru: autoOnMinutesBeforeSubuh (Int, default 15)",
                "Field baru: autoOffDialogEnabled (Boolean, default true)",
                // ===== FILE YANG DIUBAH =====
                "File diubah: AppSettings.kt (field baru + hapus field lama)",
                "File diubah: SettingsRepository.kt (load/save field baru)",
                "File diubah: MasjidVideoPlayer.kt (aspectResizeMode + zoomFactor + onVideoLooped + polling)",
                "File diubah: HomeScreen.kt (weight panel + icon per waktu + onVideoLooped)",
                "File diubah: WisdomCardCarousel.kt (auto-shrink + AnimatedContent full width)",
                "File diubah: SlideManager.kt (clean view — hapus semua overlay)",
                "File diubah: VideoSettingsPane.kt (pakai custom picker)",
                "File diubah: PowerSettingsPane.kt (jadwal on/off otomatis + 2 slider + toggle popup)",
                "File diubah: MainActivity.kt (brightness otomatis + popup trigger + deteksi remote)",
                "File baru: VideoFilePickerDialog.kt",
                "File baru: AutoOffDialog.kt"
            )
        ),
        UpdateEntry(
            version = "V1.04.422",
            date = "06 Oktober 2026",
            title = "Fix Mode Video Full-Frame + Auto-Switch + PIN Lock Options + Slide Navigasi",
            features = listOf(
                "Fix CRITICAL: video tidak full ke kanan (gap hitam di sisi kanan panel video)",
                "Fix CRITICAL: bug resizeMode shadowing di MasjidVideoPlayer → 5 mode ukuran frame sekarang benar-benar bekerja",
                "Fix CRITICAL: mode POTONG/PAS/ZOOM/FIT hasilnya sama saja sebelum perbaikan",
                "Fix: struktur root Mode Video berubah Box → Column, running text jadi bagian Column (bukan overlay)",
                "Fix: jadwal sholat di panel kiri TIDAK LAGI kepotong oleh running text",
                "Fix: tombol ⚙ Settings overlay sekarang di sudut kanan atas LAYAR (bukan di dalam panel kanan)",
                "Fix: Auto-Switch Mode sekarang berfungsi — tunggu video selesai 1x putaran sebelum switch",
                "Fix: tanpa media (tanpa video & tanpa foto) → otomatis Mode Normal permanen",
                "Improve: panel kiri Mode Video di-compact (spacing dirapatkan, kotak jam/tanggal dirapatkan)",
                "Improve: font jam di panel kiri 32sp → 26sp, font list sholat 11sp → 10sp",
                "Improve: kotak MENUJU SUBUH dikecilkan (padding 8x6dp)",
                "Improve: contentScale video/foto diteruskan dari settings.videoFrameScale ke MasjidVideoPlayer & PhotoSlideshow",
                "Improve: mode FULL (Fullscreen) panel kiri otomatis hilang (width 0%)",
                "Fitur BARU: SlideManager — tekan OK/BACK/ENTER/DPAD_CENTER untuk keluar slide kembali ke Home",
                "Fitur BARU: SlideManager — tombol KIRI/KANAN untuk pindah slide (Qris ↔ Laporan ↔ Kajian)",
                "Fitur BARU: panah navigasi kiri/kanan di sisi slide (visual hint)",
                "Fitur BARU: hint tombol remote di pojok kiri bawah slide",
                "Fitur BARU: PIN Lock Mode 3 opsi — IMMEDIATE / TIMEOUT_5MIN / UNTIL_EXIT",
                "Fitur BARU: section KEAMANAN di Settings → PIN LOCK (setelah PIN Saat Ini, sebelum Mode Kiosk)",
                "Fitur BARU: 3 radio opsi PIN Lock dengan ikon + judul + deskripsi",
                "Fitur BARU: sessionPinVerified + lastPinVerifiedTime di MainActivity",
                "Fitur BARU: shouldRequestPin() — cek mode + flag + waktu untuk decide minta PIN atau tidak",
                "Fitur BARU: Export summary sekarang menyertakan section KEAMANAN",
                "Fitur BARU: Cooldown slide 5 menit setelah user dismiss via OK/BACK",
                "Fitur BARU: enum PinLockMode di AppSettings.kt (IMMEDIATE/TIMEOUT_5MIN/UNTIL_EXIT)",
                "Fitur BARU: field pinLockMode di AppSettings (default UNTIL_EXIT = behavior lama)"
            )
        ),
        UpdateEntry(
            version = "V1.04.421",
            date = "04 Oktober 2026",
            title = "Alur Sholat Baru + Media Persistence + Fix Mode Fokus",
            features = listOf(
                "Fitur BARU: Alur sholat lengkap (Adzan → Himbauan HP → Niat Qobliyah → Mode Fokus)",
                "Overlay Adzan fullscreen: 'ADZAN [waktu]' + 'Selamat menunaikan ibadah sholat [waktu]'",
                "Overlay Himbauan HP fullscreen: 'HENINGKAN HP ANDA' + kata-kata bagus",
                "Overlay Niat Qobliyah fullscreen: niat arab + latin + arti (5 waktu sholat)",
                "Countdown Iqomah tampil di overlay Niat Qobliyah",
                "Mode Fokus sekarang 2 fase: Niat Fardhu (arab + latin + arti) + Dzikir",
                "Niat sholat fardhu standar NU (baku, sesuai ajaran mayoritas Indonesia)",
                "Fase ADZAN & QOBLIYAH lama dihapus dari Mode Fokus (dipindah ke AdzanSequenceOverlay)",
                "Tombol OK di Adzan Sequence = lanjut ke tahap berikutnya",
                "Tombol BACK di Adzan Sequence = skip ke Mode Fokus",
                "Tombol OK di Mode Fokus = skip ke fase berikutnya",
                "Tombol BACK di Mode Fokus = keluar (kalau focusModeAllowExitWithRemote = true)",
                "Semua durasi tahap bisa diatur user via Settings → Durasi & Hitungan Mundur",
                "Fix: file foto/video hilang setelah app tutup/update (media persistence)",
                "Media persistence: copy file ke filesDir/masjid_io/ (folder permanen)",
                "File baru: MediaPersistenceHelper.kt (helper copy file)",
                "File baru: AdzanSequenceOverlay.kt (3 tahap pembuka)",
                "5 file settings diupdate pakai MediaPersistenceHelper",
                "CustomBackgroundPane: copy background ke folder permanen",
                "IdentitySettingsPane: copy logo ke folder permanen",
                "QrisSettingsPane: copy QRIS ke folder permanen",
                "VideoSettingsPane: copy video + foto ke folder permanen",
                "WeeklyOfficersSettingsPane: copy semua foto petugas ke folder permanen",
                "Hapus 2 radio lama di VideoSettingsPane (Split Screen + Smart Fullscreen)",
                "Digantikan oleh Auto-Switch Mode + Ukuran Frame",
                "CountdownSettingsPane: tambah 5 slider durasi alur sholat baru",
                "Fix: Mode Fokus tidak lagi tampilkan slide QRIS/Laporan/Kajian",
                "Fix: FocusRequester di Mode Fokus terima tombol remote",
                "Perbaikan: import lengkap di HomeScreen (ClockAndDate, MosqueHeader, QRISFocusOverlay, SlideManager)"
            )
        ),
        UpdateEntry(
            version = "V1.04.420",
            date = "04 Oktober 2026",
            title = "Layout Opsi H + Auto-Switch Mode + Ukuran Frame",
            features = listOf(
                "Fitur BARU: Layout Opsi H untuk mode video/foto",
                "Panel kiri 24%: logo + kotak gabungan jam/tanggal + list sholat vertikal + progress bar",
                "Panel kanan 76%: video/foto dengan lock frame",
                "List sholat vertikal 6 baris (Subuh → Isya) dengan highlight NEXT (pulse emas)",
                "Kotak gabungan: jam 32sp ExtraBold + garis pemisah + hijriah + masehi",
                "Overlay wisdom card di bawah video (rotate: wisdom → ayat → hadits → asmaul husna)",
                "Tombol Settings jadi overlay mengambang sudut kanan atas",
                "Running text full width di bawah",
                "Layout NORMAL tetap lengkap seperti sebelumnya",
                "Fitur BARU: Auto-Switch Mode — bolak-balik Mode Video ↔ Mode Normal",
                "Tunggu video loop 1x selesai sebelum switch (video tidak terpotong)",
                "Pengaturan: toggle ON/OFF, interval video (1-60 menit), durasi normal (1-30 menit)",
                "Kalau belum ada video/foto → mode normal permanen",
                "Fitur BARU: Pengaturan Ukuran Frame — 5 mode",
                "POTONG (Crop) seperti Instagram Reels",
                "PAS (Fit/Letterbox) seperti Netflix",
                "ZOOM (Fill) seperti TikTok",
                "FULL (Fullscreen) panel kiri hilang",
                "FIT (Stretch) paksa video/foto sesuaikan frame",
                "Frame video/foto LOCK ukuran tetap (16:9, 9:16, 1:1 semua sama)",
                "Icon sholat pakai Material Icon (bukan emoji) — hemat tempat",
                "Auto-switch JEDA saat Mode Fokus Sholat / Slide Fullscreen / Ramadhan aktif"
            )
        ),
                UpdateEntry(
            version = "V1.04.418",
            date = "04 Oktober 2026",
            title = "Storage Permission Dialog + Fix Download Check",
            features = listOf(
                "Fitur BARU: StoragePermissionDialog muncul otomatis saat pertama buka",
                "Dialog berisi: penjelasan + 4 manfaat + cara aktivasi + tombol BERI IZIN / NANTI",
                "Tombol BERI IZIN buka halaman Manage All Files Access (izin kelola semua file)",
                "Auto-detect: kalau izin baru diberikan, dialog hilang otomatis (LifecycleObserver ON_RESUME)",
                "Fix: startDownload() cek permission dulu sebelum download update",
                "Kalau izin belum ada → tampilkan dialog + Toast peringatan",
                "Fix: Back button diblokir saat dialog izin aktif",
                "RunningTextMarquee: perlambat speed 1 dari 7.0x → 40.0x",
                "RemoteServer: hapus fonnteToken & fonnteGroupId dari web dashboard",
                "Tambah info-box di tab Sistem: token hanya bisa diatur via Opsi Developer"
            )
        ),
        UpdateEntry(
            version = "V1.30.7",
            date = "03 Oktober 2026",
            title = "Verifikasi Transfer + Konfirmasi Restart Manual + File Template .iO",
            features = listOf(
                "Fitur BARU: verifikasi transfer sebelum restart — pastikan semua file terkirim",
                "Fitur BARU: konfirmasi restart manual via tombol (bukan auto restart)",
                "Fitur BARU: file template .iO (bundle ZIP berisi settings + media)",
                "Fitur BARU: daftar file template di menu Tampilan & Background",
                "Tombol GUNAKAN untuk apply template .iO (settings + media)",
                "Tombol INFO untuk lihat log stack trace Kotlin lengkap",
                "Tombol HAPUS untuk buang file template",
                "Tombol SALIN LOG untuk copy log ke clipboard",
                "File template disimpan otomatis di /sdcard/masjid.io/Terima/",
                "Nama file otomatis: {Merk HP}-{dd-MM-yyyy HH.mm}.iO",
                "File .iO TIDAK dihapus otomatis (kecuali user hapus manual)",
                "Retry otomatis hanya file yang gagal (bukan semua)",
                "Error detail: setiap kegagalan simpan stack trace Kotlin asli",
                "Kalau user tidak klik restart → keluar-buka app → template aktif otomatis",
                "File baru: IoBundleHelper.kt (create/read/list/delete bundle)",
                "File baru: IoBundleListSection.kt (UI daftar file template)",
                "RemoteControlClient.kt: tambah MediaFileFailure + retryFailedMedia()",
                "IoControlScreen.kt: tambah phase VERIFYING + READY_TO_RESTART",
                "RemoteServer.kt: auto-bikin bundle saat transfer selesai",
                "RemoteServer.kt: 3 endpoint baru (list-bundles, delete-bundle, restore-bundle)",
                "CustomBackgroundPane.kt: tambah section file template .iO"
            )
        ),
        UpdateEntry(
            version = "V1.30.6",
            date = "03 Oktober 2026",
            title = "Fix Fokus D-pad TV + Fix Transfer Media iO Control",
            features = listOf(
                "Fix CRITICAL: foto & video tidak muncul di TV setelah transfer iO Control",
                "Update settings TV dengan path lokal setelah file media masuk",
                "Fungsi baru: applyMediaPathToSettings() di RemoteServer.kt",
                "Support update path: QRIS, Logo, Background, Kartu Sholat, Video, Foto Slideshow",
                "Fix fokus D-pad tidak jelas / hilang di layar Settings",
                "File baru: AutoFocusPane.kt — auto-fokus ke elemen pertama pane",
                "Fokus langsung ke elemen pertama pane (bukan tombol Kembali)",
                "Border fokus jadi INSTANT saat pindah, animasi scale menyusul smooth 150ms",
                "Kembalikan animasi border berputar di NeonFocusBorder",
                "Core border tipis (dim gold 20%) → glow berputar 2-kutub terlihat jelas",
                "Blur glow tetap dipertahankan sesuai permintaan",
                "TvFocusHelper: spring(800) diganti tween(150) — respons lebih cepat",
                "IoControlScreen: semua tombol pakai NeonFocusBorder",
                "TopBar: tombol Settings pakai NeonFocusBorder",
                "Fix icon celengan babi di slide Laporan Keuangan",
                "Ganti icon Icons.Default.Savings → Icons.Default.AccountBalanceWallet",
                "Icon dompet lebih cocok untuk konteks masjid",
                "Konsisten border berputar di seluruh halaman Settings, iO Control, HomeScreen"
            )
        ),
        UpdateEntry(
            version = "V1.30.5",
            date = "03 Oktober 2026",
            title = "Auto-Focus Pane & Smooth D-pad Navigation",
            features = listOf(
                "Fix UX: fokus D-pad sekarang langsung ke elemen PERTAMA pane (bukan tombol Kembali)",
                "Auto-focus instan saat pane baru dibuka — tidak perlu tekan D-pad dulu",
                "File baru: AutoFocusPane.kt — wrapper sekali pakai untuk 20 pane",
                "SettingsScreen: bungkus semua pane dengan AutoFocusPane",
                "Fokus pindah INSTANT — user langsung tahu elemen mana yang aktif",
                "Border tebal muncul INSTANT saat fokus pindah",
                "Scale smooth 150ms MENYUSUL setelah fokus (bukan bareng)",
                "Glow blur tetap ada, animasi menyusul (tidak menghilang)",
                "TvFocusHelper: spring(800) diganti tween(150) — lebih snappy",
                "4 modifier di TvFocusHelper: pro, simple, icon, card — semua konsisten",
                "NeonFocusBorder: border instant via drawInstantBorder()",
                "NeonFocusBorder: glow blur tetap, dot berputar smooth",
                "Boundary color: emas full opacity saat fokus (INSTANT)",
                "Hilangkan efek 'ayun dulu' dari spring lama",
                "Fix UX: tidak lagi kepencet tombol Kembali tidak sengaja saat buru-buru",
                "Fix UX: navigasi antar tombol terasa langsung nempel, tidak nyangkut",
                "Performa TV low-end lebih lancar karena animasi singkat",
                "Konsisten di semua pane: Location, Audio, QRIS, CCTV, iO Control, dll"
            )
        ),
        UpdateEntry(
            version = "V1.30.4",
            date = "02 Oktober 2026",
            title = "Transfer Media iO Control — Foto & Video Antar Device",
            features = listOf(
                "Fix CRITICAL: transfer foto & video via iO Control sekarang berhasil",
                "Transfer file media via chunk upload (1 MB per chunk)",
                "Support semua media: QRIS, Logo, Background, Kartu Sholat, Video, Foto Slideshow",
                "Kompres foto otomatis (max 1920px, quality 85%) sebelum kirim",
                "Kompres video otomatis via MediaMuxer (remux stream tanpa re-encode)",
                "Progress bar per-file + total keseluruhan (real-time)",
                "Status fase jelas: membaca → transfer → selesai → error",
                "Retry otomatis 3x kalau chunk gagal terkirim",
                "Notifikasi sukses/gagal/berjalan untuk setiap file",
                "Alur baru: kirim settings dulu → auto lanjut kirim media",
                "Server endpoint baru: receive-media-start, receive-media-chunk, receive-media-finish",
                "Endpoint /api/io/media-status untuk cek status transfer aktif",
                "Semua endpoint media PUBLIC (tidak perlu login)",
                "File baru: MediaTransferHelper.kt (helper chunk + kompres)",
                "File RemoteServer.kt: 4 endpoint media baru",
                "File RemoteControlClient.kt: sendMediaFilesChunked() + MediaTransferResult",
                "File SettingsTransferHelper.kt: uploadMediaFiles() wrapper",
                "File IoControlScreen.kt: MediaProgressView + enum SENDING_MEDIA",
                "Auto-save media di folder internal TV: filesDir/masjid_io/{qris|logo|background|video|slideshow|prayer_card}"
            )
        ),
        UpdateEntry(
            version = "V1.30.3",
            date = "02 Oktober 2026",
            title = "Responsive Auto-Scale — Tampilan Otomatis Semua Ukuran TV",
            features = listOf(
                "Fitur BARU: Auto-Scale tampilan untuk semua ukuran TV",
                "Deteksi otomatis resolusi TV (px, dp, aspect ratio, density)",
                "Deteksi tipe layar: Ultrawide 21:9, Standar 16:9, Klasik 4:3",
                "Base design 1920x1080 (FHD) — semua elemen auto-scale",
                "Scale factor clamp 0.6× - 2.5× supaya tidak ekstrem",
                "Safe Area Padding dinamis (0-10%) untuk hindari overscan bezel TV",
                "TV Ultrawide: konten otomatis di-center (max 88% lebar)",
                "TV 4:3 lama: spacing & font otomatis disesuaikan",
                "TV 4K: font & padding auto-membesar proporsional",
                "TV 720p: font & padding auto-mengecil proporsional",
                "Tombol Settings di TopBar tidak terpotong di TV apapun",
                "Menu baru di Pengaturan: 'Tampilan TV' (kategori ke-20)",
                "Info resolusi TV real-time tampil di panel pengaturan",
                "Toggle Auto-Scale ON/OFF (default ON)",
                "Slider Safe Area 0-10% (default 3%)",
                "4 Preset layout: AUTO / STANDAR / ULTRAWIDE / 4:3",
                "Tombol Test Safe Area untuk cek area aman dari bezel",
                "File baru: ResponsiveLayoutHelper.kt (helper deteksi & scale)",
                "File baru: TvDisplaySettingsPane.kt (panel pengaturan)",
                "AppSettings: 3 field baru — tvAutoScaleEnabled, tvSafeAreaPercent, tvLayoutPreset"
            )
        ),
        UpdateEntry(
            version = "V1.30.2",
            date = "02 Oktober 2026",
            title = "Tema Makkah Dinamis & Fix iO Control Transfer",
            features = listOf(
                "Fitur BARU: Tema Makkah Dinamis sebagai tema DEFAULT",
                "Langit Makkah bergerak real-time: matahari melengkung dari timur ke barat",
                "Bulan bergeser otomatis dari kiri bawah ke kanan atas sepanjang malam",
                "Fase bulan REAL: purnama/sabit mengikuti siklus lunar otomatis",
                "Awan bergerak kiri ke kanan dengan 6 lapis paralax",
                "Burung berterbangan V-formation (siang saja, tidak saat hujan)",
                "Bintang berkelip di langit malam (60 bintang dengan twinkle)",
                "Cuaca real-time: cerah, berawan, hujan, hujan petir",
                "Efek hujan: 140 tetes air dengan kecepatan bervariasi",
                "Efek petir: kilat putih muncul 10-15 detik sekali saat badai",
                "Kabut tipis muncul otomatis jam 04:00-06:00 (waktu subuh)",
                "Refleksi marmer di lantai Mataf (silau matahari)",
                "Siluet Masjidil Haram + Ka'bah + pita emas Hizam",
                "Lampu arcade menyala otomatis saat malam",
                "7 gradasi warna langit otomatis (subuh-malam)",
                "Fix CRITICAL: transfer iO Control antar device berhasil (fix error 401)",
                "Fix: /api/io/receive jadi public route",
                "Fix: APK tersimpan bisa di-tap untuk INSTALL",
                "Fix: tombol hapus APK dipisah dari area install",
                "File baru: SunMoonCalculator.kt + MakkahDynamicBackground.kt"
            )
        ),
        UpdateEntry(
            version = "V1.30.1",
            date = "30 September 2026",
            title = "iO Control — Kontrol & Transfer Pengaturan Antar Device via WiFi",
            features = listOf(
                "Fitur iO Control: HP sebagai remote tampilan TV via WiFi/Hotspot sama",
                "Auto-discovery device Masjid.io lain pakai UDP broadcast (port 45678)",
                "UI radar biru dengan animasi sweep 360° + pulse",
                "Setelah connect: 2 tombol besar KIRIM dan TERIMA",
                "Transfer semua pengaturan: tema, jadwal, ustadz, running text, PIN, dll",
                "Progress bar realtime 0-100% saat transfer",
                "Device penerima otomatis restart setelah 100% transfer",
                "Serialisasi AppSettings via JSON (aman lintas versi)",
                "RemoteSettingsPane: tombol BUKA iO CONTROL + SALIN URL otomatis",
                "Endpoint baru RemoteServer: POST /api/io/handshake + /api/io/receive",
                "Fix: SettingsRepository lengkap — load/save semua 100+ field",
                "Fix: build.gradle.kts timezone Asia/Jakarta",
                "Fix: build.yml tag release pakai versi asli via aapt"
            )
        ),
        UpdateEntry(
            version = "V1.29.3",
            date = "29 September 2026",
            title = "Fix Crash Scroll & Fade Animasi Manual",
            features = listOf(
                "Fix crash: 'Vertically scrollable component was measured with infinity maximum height'",
                "Ganti Crossfade ke Box + key + alpha fade manual (300ms)",
                "Update BackupManager: pertahankan method lama + fetch source code dari GitHub",
                "Fix: RunningTextMarquee guard text kosong + durasi minimal 1000ms",
                "Feat: MainActivity auto-show dialog crash log setelah force close"
            )
        ),
        UpdateEntry(
            version = "V1.28.4",
            date = "29 September 2026",
            title = "Slide Fullscreen, CCTV, Remote Control & Konten Rotasi",
            features = listOf(
                "Menambahkan Slide Fullscreen (QRIS Infaq)",
                "Menambahkan Slide Laporan Keuangan",
                "Menambahkan Slide Jadwal Kajian",
                "Menambahkan Konten Rotasi (Ayat/Hadits/Asmaul Husna)",
                "Menambahkan data DzikirStore + AyatStore + HaditsStore + AsmaulHusnaStore",
                "Menambahkan Mode Ramadhan Overlay",
                "Menambahkan CCTV Widget (RTSP + HTTP support)",
                "Menambahkan Remote Control (HTTP server)",
                "Menambahkan background foto di PrayerCard",
                "Menambahkan Phase 4 Dzikir di PrayerFocusOverlay"
            )
        ),
        UpdateEntry(
            version = "V1.28.3",
            date = "29 September 2026",
            title = "Fokus D-pad, Pane Lengkap & Restore Fitur",
            features = listOf(
                "Fokus D-pad lebih tebal (TvFocusHelper)",
                "TvSlider + TvToggle (remote-friendly)",
                "LocationSettingsPane lengkap (GPS + manual + Kemenag)",
                "IdentitySettingsPane lengkap (nama, alamat, logo, takmir)",
                "RunningTextSettingsPane lengkap",
                "Fix warning Locale deprecated",
                "Fix warning BorderStroke deprecated",
                "Restore fitur Location & Identity yang hilang"
            )
        ),
        UpdateEntry(
            version = "V1.28.2",
            date = "28 September 2026",
            title = "Versioning, Keystore & Fitur Inti",
            features = listOf(
                "Implementasi versioning otomatis",
                "Setup keystore permanen (update tanpa uninstall)",
                "Auto GitHub Release setiap build",
                "Fitur Backup Aman (TXT)",
                "Fitur Log Crash + Riwayat Crash",
                "Fitur Periksa Update + Riwayat Update",
                "Fitur ApkDownloader (download + install APK)",
                "Fitur WhatsApp Fonnte (crash → grup admin)",
                "Fitur Foto Slideshow",
                "Fitur Build History Fetcher",
                "Restore 4 pane: Ramadhan, Keamanan, Daya, Tentang"
            )
        ),
        UpdateEntry(
            version = "V1.28.1",
            date = "28 September 2026",
            title = "Perbaikan Tampilan & Petugas",
            features = listOf(
                "Upload foto per sesi petugas",
                "Foto profil kotak sudut tumpul",
                "HomeScreen: video diperbesar, jam diperkecil",
                "Kartu sholat digeser kiri saat video aktif",
                "Suara beep diperbaiki (nyaring, panjang, serius)",
                "Integrasi KioskManager & WatchdogService",
                "Deteksi crash di MainActivity"
            )
        ),
        UpdateEntry(
            version = "V1.28.0",
            date = "27 September 2026",
            title = "Versi Dasar",
            features = listOf(
                "Jadwal sholat 6 waktu",
                "Jam digital + tanggal Hijriah",
                "Kartu 6 waktu sholat sejajar",
                "Panel Imam & Muadzin auto-slide",
                "Running text pengumuman",
                "Mode fokus 30 menit",
                "Audio: Beep / Full Adzan / Silent",
                "Pengaturan lengkap dengan PIN",
                "Jadwal petugas mingguan",
                "Kiosk mode + auto-start",
                "Auto-build APK via GitHub Actions",
                "Launcher default Android TV",
                "Background 3 mode",
                "Mode Ramadhan (dasar)",
                "Donasi QRIS & Rekening",
                "Video kegiatan masjid",
                "Kartu Nasihat & Mutiara"
            )
        )
    )

    fun getEntry(version: String): UpdateEntry? =
        entries.find { it.version.equals(version, ignoreCase = true) }

    fun getLatestVersion(): String =
        entries.firstOrNull()?.version ?: "V1.0.0"

    fun getFullText(): String = buildString {
        appendLine("Total: ${entries.size} versi tercatat")
        appendLine()
        entries.forEach { entry ->
            appendLine("┌─ ${entry.version}  (${entry.date})")
            appendLine("│  ${entry.title}")
            appendLine("│")
            entry.features.forEach { f ->
                appendLine("│  • $f")
            }
            appendLine("└─")
            appendLine()
        }
    }

    fun getSummary(): String = buildString {
        entries.forEach { entry ->
            appendLine("${entry.version} (${entry.date})")
            entry.features.take(5).forEach { f ->
                appendLine("- $f")
            }
            if (entry.features.size > 5) {
                appendLine("- ... (${entry.features.size - 5} lagi)")
            }
            appendLine()
        }
    }
}
