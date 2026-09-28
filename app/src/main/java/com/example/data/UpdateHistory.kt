package com.example.data

object UpdateHistory {

    data class UpdateEntry(
        val version: String,
        val date: String,
        val title: String,
        val features: List<String>
    )

    /**
     * Daftar riwayat update MASJID.IO.
     * Urutan: terbaru di atas.
     * Setiap update WAJIB ditambahkan di sini (tidak bisa dihapus).
     */
    val entries: List<UpdateEntry> = listOf(
        UpdateEntry(
            version = "V1.28.2",
            date = "28 September 2026",
            title = "Perbaikan & Fitur Baru",
            features = listOf(
                "Memperbaiki Bug Backup Aman (izin penyimpanan)",
                "Menambahkan fitur Backup Aman ke file TXT",
                "Menambahkan fitur Log Crash otomatis",
                "Menambahkan fitur Riwayat Crash di Opsi Developer",
                "Menambahkan fitur Periksa Update dari GitHub",
                "Menambahkan fitur Riwayat Update dengan detail fitur",
                "Memulihkan panel Mode Ramadhan (jadwal tarawih, kultum)",
                "Memulihkan panel Keamanan (PIN, kiosk, auto-start)",
                "Memulihkan panel Daya & Booting (auto on/off, idle screen)",
                "Memulihkan panel Tentang Aplikasi + icon sosmed",
                "Implementasi versioning otomatis (format V1.28.2)",
                "Setup keystore permanen (update tanpa uninstall)",
                "Auto GitHub Release setiap build"
            )
        ),
        UpdateEntry(
            version = "V1.28.1",
            date = "28 September 2026",
            title = "Perbaikan Tampilan & Petugas",
            features = listOf(
                "Menambahkan fitur upload foto per sesi petugas",
                "Setiap imam/muadzin/khatib/ustadz bisa punya foto sendiri",
                "Foto profil kotak sudut tumpul (dari lingkaran)",
                "HomeScreen: video kegiatan masjid diperbesar",
                "HomeScreen: jam digital diperkecil",
                "Kartu sholat digeser ke kiri saat video aktif",
                "Memperbaiki suara beep (lebih nyaring, panjang, serius)",
                "Menambahkan integrasi KioskManager",
                "Menambahkan WatchdogService untuk auto-restart",
                "Memperbaiki detect crash di MainActivity"
            )
        ),
        UpdateEntry(
            version = "V1.28.0",
            date = "27 September 2026",
            title = "Versi Dasar",
            features = listOf(
                "Jadwal sholat 6 waktu (Subuh, Syuruq, Dzuhur, Ashar, Maghrib, Isya)",
                "Jam digital besar + tanggal Hijriah & Masehi",
                "Kartu 6 waktu sholat sejajar horizontal",
                "Panel Imam & Muadzin dengan auto-slide",
                "Running text pengumuman",
                "Mode fokus 30 menit (Adzan, Qobliyah, Fardhu)",
                "Audio: Beep Only / Full Adzan / Silent",
                "Pengaturan lengkap via ikon gerigi (dengan PIN)",
                "Jadwal petugas mingguan (imam, muadzin, khatib, ustadz)",
                "Kiosk mode + auto-start on boot",
                "Auto-build APK via GitHub Actions",
                "Bisa jadi launcher default Android TV",
                "Background 3 mode (Alam, Kakbah, Custom)",
                "Mode Ramadhan (dasar)",
                "Donasi QRIS & Rekening",
                "Video kegiatan masjid",
                "Kartu Nasihat & Mutiara"
            )
        )
    )

    /**
     * Ambil riwayat update untuk versi tertentu.
     */
    fun getEntry(version: String): UpdateEntry? {
        return entries.firstOrNull { it.version == version }
    }

    /**
     * Ambil versi terbaru.
     */
    fun getLatestVersion(): String {
        return entries.firstOrNull()?.version ?: "V1.0.0"
    }
}
