package dev.andikune.data

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
            version = "V1.04.432",
            date = "20 Oktober 2026",
            title = "Fix Total D-pad & Fokus, Hotspot HP, Auto-Save & Performa TV",
            features = listOf(
                "Fix Hotspot HP: Transfer via iO Control sekarang berfungsi meski HP jadi Hotspot",
                "Fix Hotspot HP: Tulisan 'WiFi Nonaktif' tidak muncul lagi jika ada IP valid",
                "Fix Fokus: Fokus D-pad tidak macet/lompat ke tombol Simpan saat scroll mentok atas",
                "Fix Fokus: Fokus bisa kembali ke menu Sidebar Kiri dari Pane Kanan",
                "Fix Fokus: Tombol pertama di semua Pop-up (Update, PIN, Jadwal Off) langsung menyala",
                "Fix Fokus: D-pad di menu Galeri (VideoSettingsPane) tidak stuck lagi",
                "Fix Input: Pop-up PIN sekarang mendukung ketikan angka dari Remote TV fisik",
                "Fix Input: Fokus PIN kembali ke awal jika PIN salah",
                "Fix Slider: Mencegah double toggle (OK & Klik) dan melepas aktif jika D-pad UP/DOWN",
                "Fix Auto-Save: Data tersimpan paksa saat menekan tombol BACK (tidak hilang)",
                "Fix Auto-Save: Mencegah tabrakan antara Auto-Save (500ms) dan tombol Simpan Manual",
                "Fix Galeri: Mencegah file ter-copy dobel saat tombol ditekan cepat (Loading State)",
                "Fix Galeri: Mencegah file video/foto aktif terhapus otomatis oleh sistem cleanup",
                "Fix File Picker: Thumbnail video dimuat di background (TV tidak nge-lag)",
                "Fix File Picker: Hanya 1 ExoPlayer global (mencegah crash saat scroll cepat)",
                "Fix File Picker: Dialog otomatis refresh saat kembali dari menu izin Android",
                "Fix File Picker: Cek eksistensi file sebelum dipilih (mencegah crash file hilang)",
                "Fix Timer AutoOff: Timer 2 menit di-reset jika user menekan tombol remote",
                "Fix Remote: Polling IP 5 detik dihapus (IP di-lock, mengurangi beban CPU)",
                "Perbaikan NetworkHelper: Mengenali WiFi, Hotspot, dan LAN dengan lebih akurat",
                "Optimasi Fokus: Retry requestFocus dibatasi agar tidak crash jika dialog ditutup cepat",
                "Catatan AI: NeonFocusBorder TIDAK diubah agar animasi glow tetap terlihat jelas"
            )
        ),
        UpdateEntry(
            version = "V1.04.431",
            date = "19 Oktober 2026",
            title = "Galeri Kegiatan Masjid + Smart Switch & Fix D-Pad",
            features = listOf(
                "Fitur BARU: Galeri Kegiatan Masjid (menggantikan menu Video Kegiatan)",
                "Tambah Master Switch (Aktifkan Galeri) - meredupkan menu jika OFF",
                "Tambah Smart Switch (Rotasi Video -> Foto 1 putaran -> Layar Default)",
                "Tambah fitur hitung total durasi putar foto (Contoh: 2 Menit 30 Detik)",
                "Tambah pratinjau popup video (hover D-pad) & thumbnail di File Picker",
                "Tambah deteksi file hilang (Missing File Warning)",
                "Fix TvSlider D-pad kanan tidak berfungsi (onPreviewKeyEvent)",
                "Fix NeonFocusBorder (animasi Canvas putih-emas dikembalikan)",
                "Fix Fokus awal dialog (Storage & Install) langsung menyala",
                "Fix Auto-Switch stuck (batas maksimal tunggu video 5 menit)",
                "Catatan AI: SettingsScreen.kt dibagi 2 bagian, file lain full 1 bubble"
            )
        ),
        UpdateEntry(
            version = "V1.04.426",
            date = "06 Oktober 2026",
            title = "Fix Transfer Media Gagal + Color Coding Template .iO + Namespace Consistency",
            features = listOf(
                "Fix CRITICAL: transfer foto & video via iO Control yang selalu gagal",
                "Metode baru: STREAMING UPLOAD — 1 request HTTP per file (bukan chunk + Base64)",
                "TCP handle retransmisi otomatis — tidak perlu retry chunk manual",
                "Hemat bandwidth: tidak ada Base64 overhead (33% lebih efisien)",
                "Timeout diperpanjang: 30 detik → 10 menit per file (untuk video besar)",
                "Progress report real-time setiap ~256 KB",
                "File dibaca langsung dari disk → socket (tidak buffer semua di memori)",
                "Support path lokal & content URI (auto-copy ke cache untuk URI)",
                "Auto-hapus file temp setelah transfer selesai",
                "Fitur BARU: TransferForegroundService — jaga server tetap hidup saat transfer",
                "File baru: TransferForegroundService.kt (Foreground Service + WiFiLock + WakeLock)",
                "WiFiLock: WiFi tidak mati saat layar TV off (penyebab utama transfer putus)",
                "WakeLock: CPU tetap jalan saat layar TV off (max 30 menit safety)",
                "Notifikasi permanen di status bar TV saat transfer aktif",
                "Android tidak boleh bunuh proses ini selama transfer berlangsung",
                "Endpoint baru: POST /api/io/upload-stream (streaming upload)",
                "Endpoint baru: GET /api/io/upload-status (cek progress)",
                "Header baru: X-File-Id, X-Field-Key, X-File-Name, X-File-Type, X-Total-Bytes, X-Mime-Type",
                "Auto-save file ke folder sesuai fieldKey (qris, logo, video, dll)",
                "Backward compatible: endpoint chunk lama tetap ada",
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
                "Auto-bikin file .nomedia di folder /sdcard/masjid.io/Terima/",
                "Folder Terima tidak dianggap cache oleh aplikasi cleaner",
                "File .iO tidak akan dihapus otomatis oleh Clean Master, dll",
                "Fix: namespace konsisten dev.andikune.masjidio di semua file",
                "Ganti namespace dari dev.andikune ke dev.andikune.masjidio",
                "Update import di semua file yang terdampak",
                "Folder fisik tetap dev/andikune/ (Kotlin izinkan package beda dari folder)",
                "Fix: InstallPermissionDialog tidak muncul berulang setelah user grant/skip",
                "Tambah state installDialogAlreadyShown (muncul sekali per sesi)",
                "Fix: tombol dialog izin (Storage & Install) height fixed 56dp → teks & ikon muncul",
                "Ganti Row dengan padding ke Box dengan height fixed + contentAlignment Center",
                "Fitur BARU: Tombol GALERI di samping FILE untuk semua picker media",
                "Video Kegiatan: GALERI + FILE (pilih via galeri HP atau custom picker)",
                "Logo Masjid: GALERI + FILE",
                "QRIS: GALERI + FILE",
                "Background Custom: GALERI + FILE",
                "Foto Petugas: menu 3 opsi (GALERI / FILE / HAPUS) saat tap foto",
                "GALERI pakai ActivityResultContracts.GetContent (galeri bawaan HP)",
                "FILE pakai VideoFilePickerDialog custom (TV friendly)",
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
