# iO Control — Kontrol & Transfer Pengaturan Antar Device via WiFi

## 📱 Fitur Utama

### ✨ iO Control adalah fitur baru di Masjid.io V1.31.0+ yang memungkinkan:

1. **Auto-discovery Device** — HP otomatis menemukan TV (dan sebaliknya) di WiFi/Hotspot yang sama
2. **Transfer Pengaturan Lengkap** — Mengirim semua konfigurasi (tema, jadwal, ustadz, running text, PIN, dll) dari satu device ke device lain
3. **Progress Real-time** — Lihat progress transfer 0-100% dengan status detail
4. **Auto-Restart** — Device penerima otomatis restart setelah transfer selesai
5. **Safe Transfer** — Menggunakan serialisasi JSON aman lintas versi
6. **Detailed Error Handling** — Pesan error spesifik jika terjadi masalah koneksi

---

## 🚀 Cara Menggunakan

### Langkah 1: Persiapan Jaringan
- Pastikan **HP dan TV terhubung ke WiFi/Hotspot yang sama**
- Jangan gunakan koneksi 4G/5G yang berbeda
- Pastikan WiFi **aktif** dan **stabil**

### Langkah 2: Buka iO Control
**Di TV (Pengirim/Penerima):**
- Masuk Settings → Remote Control → iO Control
- Tekan tombol biru **"BUKA iO CONTROL"**
- Sistem akan mulai scanning perangkat di sekitar...

**Di HP (Remote):**
- Buka aplikasi Masjid.io → Settings → Remote Control
- Tekan tombol "BUKA iO CONTROL"
- HP akan scanning mencari TV di jaringan yang sama

### Langkah 3: Pilih & Koneksi Device
- Tunggu 3-5 detik sampai perangkat lain terdeteksi
- Perangkat akan muncul di daftar dengan nama dan IP-nya
- Tekan perangkat yang ingin dikoneksi
- Sistem akan melakukan handshake dan menampilkan status "TERHUBUNG"

### Langkah 4: Kirim atau Terima Pengaturan

**Jika ingin KIRIM pengaturan dari device ini ke device lain:**
- Tekan tombol besar **"KIRIM"** (warna biru)
- Tunggu progress bar selesai sampai 100%
- Device penerima akan menerima semua pengaturan
- **Otomatis restart** setelah 100% selesai
- Pengaturan baru sudah aktif di device penerima

**Jika ingin TERIMA pengaturan dari device lain:**
- Tekan tombol besar **"TERIMA"** (warna hijau)
- Tunggu sampai device lain (pengirim) mengirimkan datanya
- Progress bar akan bertambah otomatis saat menerima
- Setelah 100%, **otomatis restart**
- Pengaturan baru dari device lain sudah aktif

---

## 📋 Apa yang Ditransfer?

Semua pengaturan berikut ditransfer **LENGKAP**:

### ✅ Identitas & Lokasi
- Nama Masjid, alamat, takmir
- Negara, provinsi, kota, distrik
- Koordinat GPS, metode perhitungan sholat

### ✅ Jadwal & Petugas
- Jadwal Imam & Muadzin semua waktu sholat
- Jadwal Khutbah Jumat
- Jadwal Kajian
- Foto petugas (jika disimpan sebagai URI)
- Jadwal petugas mingguan

### ✅ Tampilan & Running Text
- Running text, kecepatan, ukuran font
- Background (tema terang/gelap/custom)
- Animasi, efek visual
- Foto kartu sholat

### ✅ Audio & Adzan
- Mode audio, volume beep, jumlah beep
- File adzan, volume adzan
- Durasi wait (adzhan, iqamah, fokus sholat)

### ✅ QRIS & Donasi
- Foto QRIS, nama bank, no rekening
- Interval tampil QRIS, durasi display
- Slide QRIS setting

### ✅ Video & Foto
- Video kegiatan masjid
- Foto slideshow
- Mode fullscreen smart
- Durasi interval

### ✅ Konten Rotasi
- Ayat Al-Quran, Hadits, Asmaul Husna
- Interval tampil konten
- Kartu nasihat & mutiara

### ✅ Keamanan & PIN
- **PIN kode** (ikut ditransfer sesuai keputusan user)
- Kiosk mode, auto-start, auto-restart
- Lock task mode

### ✅ Power Management
- Keep screen on, auto on/off schedule
- Idle screen off, brightness control
- Battery save mode

### ✅ CCTV & Monitor
- CCTV enabled/disabled
- CCTV URL, posisi, ukuran

### ❌ TIDAK Ditransfer (Tetap Default Penerima)
- Remote server port (untuk menghindari bentrok)
- Remote auth token (keamanan)
- WhatsApp report settings (token Fonnte dianggap rahasia)

---

## ⚡ Troubleshooting

### Problem: "Tidak menemukan perangkat Masjid.io lain"
**Solusi:**
1. Cek WiFi/Hotspot kedua device sama
2. Pastikan WiFi aktif (bukan airplane mode)
3. Matikan VPN jika ada
4. Tekan tombol "🔄 SCAN ULANG"
5. Tunggu 3-5 detik
6. Jika masih tidak muncul, restart both device

### Problem: "Gagal terhubung ke [Device Name]"
**Solusi:**
1. Pastikan **Remote Server aktif** di perangkat target
   - Di TV: Settings → Remote Control → toggle ON
2. Pastikan **port tidak bentrok** dengan aplikasi lain
3. Cek signal WiFi stabil (ping target IP dari device lain)
4. Restart aplikasi Masjid.io di kedua device
5. Jika masih gagal, restart HP/TV

### Problem: "Server terdeteksi dipakai aplikasi lain"
**Solusi:**
1. **Force close aplikasi lain** yang memakai port yang sama (misal: aplikasi remote TV)
2. Restart HP/TV
3. Buka ulang Masjid.io
4. Coba koneksi lagi

Atau pilih **port berbeda** di Settings → Remote Control → ubah port

### Problem: "Gagal mengirim / Gagal menerima"
**Solusi:**
1. Cek koneksi WiFi masih aktif
2. Pastikan signal WiFi stabil (jangan weak signal)
3. Ulangi proses dari awal
4. Jika data besar, tunggu lebih lama
5. Jika tetap gagal, restart kedua device

### Problem: "Transfer berhenti di tengah jalan (stuck di % tertentu)"
**Solusi:**
1. Data mungkin putus karena signal WiFi lemah
2. Ulangi proses transfer
3. Pastikan signal WiFi kuat sebelum mulai
4. Jika masih gagal, cek WiFi router dalam kondisi baik

### Problem: "Aplikasi tidak otomatis restart setelah transfer selesai"
**Solusi:**
1. **Restart manual** aplikasi
   - Tutup aplikasi (tekan back / close)
   - Buka ulang aplikasi
2. Cek Settings → apakah pengaturan baru sudah masuk?
3. Jika perubahan tidak aktif, tunggu beberapa detik atau restart device

### Problem: "Koneksi lambat / Progress sangat lambat"
**Solusi:**
1. Cek kecepatan WiFi:
   - Jarakkan lebih dekat ke router
   - Pindah ke lokasi sinyal kuat
2. Kurangi jumlah device yang terhubung ke WiFi
3. Restart WiFi router
4. Gunakan WiFi 5GHz jika tersedia (lebih cepat dari 2.4GHz)

---

## 🔒 Keamanan

### Transfer Aman Karena:
- ✅ **Token Authentication** — Hanya device dalam jaringan yang sama bisa terhubung
- ✅ **JSON Serialization** — Data terenkripsi format JSON, aman lintas versi
- ✅ **Graceful Fallback** — Field tidak dikenal di penerima otomatis di-skip (tidak crash)
- ✅ **Local Network Only** — Transfer hanya via WiFi lokal, bukan cloud/internet
- ✅ **PIN Handling** — PIN diperlakukan sebagai data sensitif (user decide)

### Port & Token:
- Default port: **14039** (bisa diubah di Settings)
- Default token: **"masjid-io"** (aman untuk LAN)
- Remote server hanya dengarkan localhost + WiFi interface

---

## 🎯 Tips & Best Practices

1. **Sebelum Transfer Besar-besaran:**
   - Test koneksi dengan SCAN dulu
   - Pastikan signal WiFi kuat
   - Jangan ada aplikasi lain yang pakai port 14039

2. **Transfer Optimal:**
   - Gunakan WiFi 5GHz jika ada (lebih cepat)
   - Jarakkan device lebih dekat ke router
   - Jangan pakai VPN saat transfer

3. **Troubleshooting Cepat:**
   - Restart WiFi router
   - Restart aplikasi Masjid.io
   - Restart device jika perlu

4. **Jika Sering Gagal:**
   - Cek log di Logcat (tag: "RemoteServer", "DeviceDiscovery")
   - Update ke versi terbaru Masjid.io
   - Hubungi support dengan detail error message

---

## 📱 Kompatibilitas

- **iOS:** Belum tersedia (Android only)
- **Android:** Min SDK 24 (Android 7.0+)
- **Performa:** Teruji di Android TV & Smartphone

---

## 🐛 Melaporkan Bug

Jika menemukan masalah:
1. Catat **error message** yang muncul
2. Catat **device name, versi Masjid.io**
3. Catat **langkah-langkah untuk reproduce**
4. Kirim ke tim developer dengan detail di atas

---

## 📌 Versi & Update

- **Rilis:** Masjid.io V1.31.0+
- **Last Updated:** 1 Oktober 2026
- **Status:** ✅ Production Ready

---

**Selamat menggunakan iO Control! 🎉**
