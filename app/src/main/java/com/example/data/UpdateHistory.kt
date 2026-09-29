package com.example.data

/**
 * UpdateHistory — Riwayat update aplikasi MASJID.IO.
 *
 * Menyimpan semua entri versi + fitur baru.
 * Dipakai oleh:
 *   - RiwayatUpdateScreen (UI)
 *   - BackupManager (generateBackupContent)
 */
object UpdateHistory {

    // ============================================================
    // DATA CLASS
    // ============================================================
    data class UpdateEntry(
        val version: String,
        val date: String,
        val title: String,
        val features: List<String>
    )

    // ============================================================
    // DAFTAR ENTRI
    // ============================================================
    val entries: List<UpdateEntry> = listOf(
        UpdateEntry(
            version = "V1.28.4",
            date = "29 September 2026",
            title = "Slide Fullscreen, CCTV, Remote Control & Konten Rotasi",
            features = listOf(
                "Menambahkan Slide Fullscreen (QRIS Infaq)",
                "Menambahkan Slide Laporan Keuangan",
                "Menambahkan Slide Jadwal Kajian",
                "Menambahkan SlideManager (rotasi otomatis)",
                "Menambahkan Konten Rotasi (Ayat/Hadits/Asmaul Husna)",
                "Menambahkan data DzikirStore (8 dzikir)",
                "Menambahkan data AyatStore (10 ayat)",
                "Menambahkan data HaditsStore (10 hadits)",
                "Menambahkan data AsmaulHusnaStore (99 nama Allah)",
                "Menambahkan Mode Ramadhan Overlay (countdown Imsak/Iftar)",
                "Menambahkan CCTV Widget (RTSP + HTTP support)",
                "Menambahkan ExoPlayer RTSP (untuk CCTV RTSP)",
                "Menambahkan Remote Control (HTTP server)",
                "Menambahkan Remote Dashboard (info akses)",
                "Menambahkan background foto di PrayerCard",
                "Menambahkan Phase 4 Dzikir di PrayerFocusOverlay",
                "Menambahkan SlideSettingsPane",
                "Menambahkan CctvSettingsPane",
                "Menambahkan RemoteSettingsPane",
                "Update SettingsScreen (menu baru: Slide, CCTV, Remote)",
                "Update MainActivity (integrasi RemoteServer)"
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
                "Implementasi versioning otomatis (format V1.28.2)",
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

    // ============================================================
    // METHOD LAMA (tetap dipertahankan)
    // ============================================================
    fun getEntry(version: String): UpdateEntry? =
        entries.find { it.version.equals(version, ignoreCase = true) }

    fun getLatestVersion(): String =
        entries.firstOrNull()?.version ?: "V1.0.0"

    // ============================================================
    // METHOD BARU (untuk BackupManager)
    // ============================================================

    /** Format lengkap semua entri — dipakai BackupManager. */
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

    /** Format ringkas — untuk preview. */
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
