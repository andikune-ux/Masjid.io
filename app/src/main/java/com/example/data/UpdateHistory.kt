package dev.andikune.masjidio.data

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
            version = "V1.04.426",
            date = "06 Oktober 2026",
            title = "Fix Transfer Media Gagal + Color Coding Template .iO + Namespace Consistency",
            features = listOf(
                // ===== FIX TRANSFER MEDIA (STREAMING UPLOAD) =====
                "Fix CRITICAL: transfer foto & video via iO Control yang selalu gagal",
                "Metode baru: STREAMING UPLOAD — 1 request HTTP per file (bukan chunk + Base64)",
                "TCP handle retransmisi otomatis — tidak perlu retry chunk manual",
                "Hemat bandwidth: tidak ada Base64 overhead (33% lebih efisien)",
                "Timeout diperpanjang: 30 detik → 10 menit per file (untuk video besar)",
                "Progress report real-time setiap ~256 KB",
                "File dibaca langsung dari disk → socket (tidak buffer semua di memori)",
                "Support path lokal & content URI (auto-copy ke cache untuk URI)",
                "Auto-hapus file temp setelah transfer selesai",
                // ===== FOREGROUND SERVICE + WIFILOCK =====
                "Fitur BARU: TransferForegroundService — jaga server tetap hidup saat transfer",
                "File baru: TransferForegroundService.kt (Foreground Service + WiFiLock + WakeLock)",
                "WiFiLock: WiFi tidak mati saat layar TV off (penyebab utama transfer putus)",
                "WakeLock: CPU tetap jalan saat layar TV off (max 30 menit safety)",
                "Notifikasi permanen di status bar TV saat transfer aktif",
                "Android tidak boleh bunuh proses ini selama transfer berlangsung",
                // ===== ENDPOINT BARU =====
                "Endpoint baru: POST /api/io/upload-stream (streaming upload)",
                "Endpoint baru: GET /api/io/upload-status (cek progress)",
                "Header baru: X-File-Id, X-Field-Key, X-File-Name, X-File-Type, X-Total-Bytes, X-Mime-Type",
                "Auto-save file ke folder sesuai fieldKey (qris, logo, video, dll)",
                "Backward compatible: endpoint chunk lama tetap ada",
                // ===== COLOR CODING TEMPLATE .IO =====
                "Fitur BARU: Color coding tombol template .iO berdasarkan persentase transfer",
                "HIJAU (100%) = semua file berhasil dikirim",
                "KUNING (50-99%) = sebagian besar berhasil",
                "MERAH (1-49%) = sebagian besar gagal",
                "HITAM (0%) = gagal total",
                "Badge persentase ditampilkan di kotak icon template",
                "Badge kategori ditampilkan di kanan atas (label teks)",
                "Ringkasan kategori ditampilkan di atas daftar (jumlah per warna)",
                "Border kartu template ikut warna kategori",
                "Dialog INFO/DELETE/RESTART juga pakai warna kategori",
                "File template yang gagal sebagian tetap bisa dipakai (file yang berhasil tetap masuk)",
                // ===== FOLDER PROTECTION =====
                "Auto-bikin file .nomedia di folder /sdcard/masjid.io/Terima/",
                "Folder Terima tidak dianggap cache oleh aplikasi cleaner",
                "File .iO tidak akan dihapus otomatis oleh Clean Master, dll",
                // ===== NAMESPACE CONSISTENCY =====
                "Fix: namespace konsisten dev.andikune.masjidio di semua file",
                "Ganti namespace dari com.example ke dev.andikune.masjidio",
                "Update import di semua file yang terdampak",
                "Folder fisik tetap com/example/ (Kotlin izinkan package beda dari folder)",
                // ===== INSTALL DIALOG FIX =====
                "Fix: InstallPermissionDialog tidak muncul berulang setelah user grant/skip",
                "Tambah state installDialogAlreadyShown (muncul sekali per sesi)",
                "Fix: tombol dialog izin (Storage & Install) height fixed 56dp → teks & ikon muncul",
                "Ganti Row dengan padding ke Box dengan height fixed + contentAlignment Center",
                // ===== TOMBOL GALERI =====
                "Fitur BARU: Tombol GALERI di samping FILE untuk semua picker media",
                "Video Kegiatan: GALERI + FILE (pilih via galeri HP atau custom picker)",
                "Logo Masjid: GALERI + FILE",
                "QRIS: GALERI + FILE",
                "Background Custom: GALERI + FILE",
                "Foto Petugas: menu 3 opsi (GALERI / FILE / HAPUS) saat tap foto",
                "GALERI pakai ActivityResultContracts.GetContent (galeri bawaan HP)",
                "FILE pakai VideoFilePickerDialog custom (TV friendly)",
                // ===== FILE YANG DIUBAH =====
                "File baru: TransferForegroundService.kt",
                "File diubah: AndroidManifest.xml (izin + service)",
                "File diubah: RemoteServer.kt (endpoint streaming + WiFiLock)",
                "File diubah: RemoteControlClient.kt (client streaming)",
                "File diubah: MediaTransferHelper.kt (namespace fix)",
                "File diubah: IoControlScreen.kt (trigger foreground service)",
                "File diubah: IoBundleHelper.kt (successPercentage + colorCategory + .nomedia)",
                "File diubah: IoBundleListSection.kt (color coding UI)",
                "File diubah: MainActivity.kt (dialog install fix + namespace)",
                "File diubah: BootReceiver.kt (namespace)",
                "File diubah: VideoSettingsPane.kt (namespace + tombol GALERI)",
                "File diubah: IdentitySettingsPane.kt (namespace + tombol GALERI)",
                "File diubah: QrisSettingsPane.kt (namespace + tombol GALERI)",
                "File diubah: CustomBackgroundPane.kt (namespace + tombol GALERI)",
                "File diubah: WeeklyOfficersSettingsPane.kt (namespace + menu GALERI/FILE/HAPUS)"
            )
        ),
        UpdateEntry(
            version = "V1.04.423",
            date = "06 Oktober 2026",
            title = "Jadwal On/Off Otomatis + File Picker Custom + Fix Video Stage + Wisdom Card Full",
            features = listOf(
                "Fitur BARU: Jadwal On/Off layar OTOMATIS dari jadwal sholat",
                "ON otomatis = Subuh - X menit (X bisa diatur 5-120 menit, default 15)",
                "OFF otomatis = Isya + Y menit (Y bisa diatur 5-120 menit, default 30)",
                "Brightness otomatis jadi 0 (layar redup total) saat jam OFF",
                "Polling jadwal setiap 30 detik",
                "Fitur BARU: AutoOffDialog — popup konfirmasi saat tekan remote di jam OFF",
                "Tombol YA = matikan jadwal on/off, Tombol TIDAK = layar redup lagi",
                "Auto-dismiss 2 menit = dianggap TIDAK",
                "Fix CRITICAL: video tidak full ke kanan (gap hitam)",
                "Fix: 5 mode frame (POTONG/PAS/ZOOM/FULL/FIT) benar-benar beda",
                "Fix: jadwal sholat di panel kiri TIDAK kepotong running text",
                "Fix: tombol Settings di sudut kanan atas LAYAR",
                "Fix: Auto-Switch dengan polling posisi video",
                "Fix: wisdom card full sampai tepi kanan (auto-shrink + multi-line)",
                "Improve: Slide CLEAN VIEW — hapus semua overlay navigasi",
                "Improve: icon sholat beda per waktu (WbTwilight/WbSunny/LightMode/WbCloudy/NightsStay/Nightlight)",
                "Fitur BARU: VideoFilePickerDialog.kt (custom file picker TV friendly)"
            )
        ),
        UpdateEntry(
            version = "V1.04.422",
            date = "06 Oktober 2026",
            title = "Fix Mode Video Full-Frame + Auto-Switch + PIN Lock Options + Slide Navigasi",
            features = listOf(
                "Fix CRITICAL: video tidak full ke kanan (gap hitam di sisi kanan panel video)",
                "Fix CRITICAL: bug resizeMode shadowing di MasjidVideoPlayer",
                "Fix CRITICAL: mode POTONG/PAS/ZOOM/FIT hasilnya sama saja sebelum perbaikan",
                "Fix: struktur root Mode Video berubah Box -> Column",
                "Fix: jadwal sholat di panel kiri TIDAK LAGI kepotong oleh running text",
                "Fix: tombol Settings overlay sekarang di sudut kanan atas LAYAR",
                "Fix: Auto-Switch Mode sekarang berfungsi",
                "Fitur BARU: SlideManager OK/BACK keluar + tombol KIRI/KANAN pindah slide",
                "Fitur BARU: PIN Lock Mode 3 opsi (IMMEDIATE / TIMEOUT_5MIN / UNTIL_EXIT)",
                "Improve: panel kiri Mode Video di-compact",
                "Improve: contentScale video/foto diteruskan dari settings"
            )
        ),
        UpdateEntry(
            version = "V1.04.421",
            date = "04 Oktober 2026",
            title = "Alur Sholat Baru + Media Persistence + Fix Mode Fokus",
            features = listOf(
                "Fitur BARU: Alur sholat lengkap (Adzan -> Himbauan HP -> Niat Qobliyah -> Mode Fokus)",
                "Overlay Adzan fullscreen: 'ADZAN [waktu]'",
                "Overlay Himbauan HP fullscreen: 'HENINGKAN HP ANDA'",
                "Overlay Niat Qobliyah fullscreen",
                "Mode Fokus sekarang 2 fase: Niat Fardhu + Dzikir",
                "Fix: file foto/video hilang setelah app tutup/update",
                "Media persistence: copy file ke filesDir/masjid_io/",
                "File baru: MediaPersistenceHelper.kt",
                "File baru: AdzanSequenceOverlay.kt"
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
                "Fitur BARU: Auto-Switch Mode Video <-> Mode Normal",
                "Fitur BARU: Pengaturan Ukuran Frame — 5 mode",
                "POTONG (Crop) seperti Instagram Reels",
                "PAS (Fit/Letterbox) seperti Netflix",
                "ZOOM (Fill) seperti TikTok",
                "FULL (Fullscreen) panel kiri hilang",
                "FIT (Stretch) paksa video/foto sesuaikan frame"
            )
        ),
        UpdateEntry(
            version = "V1.04.418",
            date = "04 Oktober 2026",
            title = "Storage Permission Dialog + Fix Download Check",
            features = listOf(
                "Fitur BARU: StoragePermissionDialog muncul otomatis saat pertama buka",
                "Tombol BERI IZIN buka halaman Manage All Files Access",
                "Auto-detect: kalau izin baru diberikan, dialog hilang otomatis",
                "Fix: startDownload() cek permission dulu sebelum download update",
                "RunningTextMarquee: perlambat speed 1 dari 7.0x -> 40.0x",
                "RemoteServer: hapus fonnteToken & fonnteGroupId dari web dashboard"
            )
        ),
        UpdateEntry(
            version = "V1.30.7",
            date = "03 Oktober 2026",
            title = "Verifikasi Transfer + Konfirmasi Restart Manual + File Template .iO",
            features = listOf(
                "Fitur BARU: verifikasi transfer sebelum restart",
                "Fitur BARU: konfirmasi restart manual via tombol",
                "Fitur BARU: file template .iO (bundle ZIP berisi settings + media)",
                "Fitur BARU: daftar file template di menu Tampilan & Background",
                "Tombol GUNAKAN / INFO / HAPUS untuk tiap file",
                "Retry otomatis hanya file yang gagal",
                "Error detail: stack trace Kotlin asli",
                "File baru: IoBundleHelper.kt + IoBundleListSection.kt"
            )
        ),
        UpdateEntry(
            version = "V1.30.6",
            date = "03 Oktober 2026",
            title = "Fix Fokus D-pad TV + Fix Transfer Media iO Control",
            features = listOf(
                "Fix CRITICAL: foto & video tidak muncul di TV setelah transfer iO Control",
                "Fungsi baru: applyMediaPathToSettings() di RemoteServer.kt",
                "File baru: AutoFocusPane.kt",
                "Border fokus berputar kembali di NeonFocusBorder",
                "Fix icon celengan babi di slide Laporan Keuangan"
            )
        ),
        UpdateEntry(
            version = "V1.30.5",
            date = "03 Oktober 2026",
            title = "Auto-Focus Pane & Smooth D-pad Navigation",
            features = listOf(
                "Fix UX: fokus D-pad sekarang langsung ke elemen PERTAMA pane",
                "Auto-focus instan saat pane baru dibuka",
                "File baru: AutoFocusPane.kt",
                "TvFocusHelper: spring(800) diganti tween(150)"
            )
        ),
        UpdateEntry(
            version = "V1.30.4",
            date = "02 Oktober 2026",
            title = "Transfer Media iO Control — Foto & Video Antar Device",
            features = listOf(
                "Fix CRITICAL: transfer foto & video via iO Control sekarang berhasil",
                "Transfer file media via chunk upload (1 MB per chunk)",
                "Kompres foto otomatis (max 1920px, quality 85%)",
                "Kompres video otomatis via MediaMuxer",
                "Progress bar per-file + total keseluruhan",
                "Retry otomatis 3x kalau chunk gagal terkirim",
                "File baru: MediaTransferHelper.kt"
            )
        ),
        UpdateEntry(
            version = "V1.30.3",
            date = "02 Oktober 2026",
            title = "Responsive Auto-Scale — Tampilan Otomatis Semua Ukuran TV",
            features = listOf(
                "Fitur BARU: Auto-Scale tampilan untuk semua ukuran TV",
                "Deteksi otomatis resolusi TV (px, dp, aspect ratio, density)",
                "Base design 1920x1080 (FHD) — semua elemen auto-scale",
                "Safe Area Padding dinamis (0-10%)",
                "Menu baru: Tampilan TV",
                "File baru: ResponsiveLayoutHelper.kt + TvDisplaySettingsPane.kt"
            )
        ),
        UpdateEntry(
            version = "V1.30.2",
            date = "02 Oktober 2026",
            title = "Tema Makkah Dinamis & Fix iO Control Transfer",
            features = listOf(
                "Fitur BARU: Tema Makkah Dinamis sebagai tema DEFAULT",
                "Langit Makkah bergerak real-time: matahari melengkung",
                "Bulan bergeser otomatis dengan fase real",
                "Cuaca real-time: cerah, berawan, hujan, hujan petir",
                "Siluet Masjidil Haram + Ka'bah + pita emas Hizam",
                "Fix CRITICAL: transfer iO Control antar device (fix error 401)",
                "File baru: SunMoonCalculator.kt + MakkahDynamicBackground.kt"
            )
        ),
        UpdateEntry(
            version = "V1.30.1",
            date = "30 September 2026",
            title = "iO Control — Kontrol & Transfer Pengaturan Antar Device via WiFi",
            features = listOf(
                "Fitur iO Control: HP sebagai remote tampilan TV via WiFi",
                "Auto-discovery device Masjid.io lain pakai UDP broadcast",
                "UI radar biru dengan animasi sweep 360°",
                "Setelah connect: 2 tombol besar KIRIM dan TERIMA",
                "Transfer semua pengaturan: tema, jadwal, ustadz, dll",
                "Device penerima otomatis restart setelah 100% transfer"
            )
        ),
        UpdateEntry(
            version = "V1.29.3",
            date = "29 September 2026",
            title = "Fix Crash Scroll & Fade Animasi Manual",
            features = listOf(
                "Fix crash: 'Vertically scrollable component was measured with infinity'",
                "Ganti Crossfade ke Box + key + alpha fade manual",
                "Fix: RunningTextMarquee guard text kosong",
                "Feat: MainActivity auto-show dialog crash log"
            )
        ),
        UpdateEntry(
            version = "V1.28.4",
            date = "29 September 2026",
            title = "Slide Fullscreen, CCTV, Remote Control & Konten Rotasi",
            features = listOf(
                "Menambahkan Slide Fullscreen (QRIS, Laporan, Kajian)",
                "Menambahkan Konten Rotasi",
                "Menambahkan data DzikirStore + AyatStore + HaditsStore",
                "Menambahkan Mode Ramadhan Overlay",
                "Menambahkan CCTV Widget (RTSP + HTTP)",
                "Menambahkan Remote Control (HTTP server)"
            )
        ),
        UpdateEntry(
            version = "V1.28.3",
            date = "29 September 2026",
            title = "Fokus D-pad, Pane Lengkap & Restore Fitur",
            features = listOf(
                "Fokus D-pad lebih tebal (TvFocusHelper)",
                "TvSlider + TvToggle (remote-friendly)",
                "LocationSettingsPane lengkap",
                "IdentitySettingsPane lengkap"
            )
        ),
        UpdateEntry(
            version = "V1.28.2",
            date = "28 September 2026",
            title = "Versioning, Keystore & Fitur Inti",
            features = listOf(
                "Implementasi versioning otomatis",
                "Setup keystore permanen",
                "Auto GitHub Release setiap build",
                "Fitur Backup Aman (TXT)",
                "Fitur Log Crash + Riwayat Crash",
                "Fitur WhatsApp Fonnte"
            )
        ),
        UpdateEntry(
            version = "V1.28.1",
            date = "28 September 2026",
            title = "Perbaikan Tampilan & Petugas",
            features = listOf(
                "Upload foto per sesi petugas",
                "HomeScreen: video diperbesar",
                "Suara beep diperbaiki",
                "Integrasi KioskManager & WatchdogService"
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
                "Kiosk mode + auto-start",
                "Auto-build APK via GitHub Actions"
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
