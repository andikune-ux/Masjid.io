# Rencana Perbaikan Build Masjid.io

- [x] Audit dan reproduksi kegagalan build pada commit terbaru.
- [x] Perbaiki struktur `DeveloperSettingsPane.kt` tanpa menghapus fitur yang sudah ada.
- [ ] Jalankan pemeriksaan kompilasi Kotlin dan build APK debug.
- [ ] Tinjau diff, status Git, dan siapkan perubahan untuk diekspor ke GitHub.
- [x] Audit dan perbaiki auto brightness 0% saat input D-pad/touchscreen.
- [x] Audit dan perbaiki popup jadwal OFF serta pengembalian brightness ke 0%.
- [x] Membuat seluruh popup/dialog dapat dipilih dan di-scroll pada layar HP.
- [x] Verifikasi tidak ada perubahan pada identitas aplikasi, icon, applicationId, keystore, atau signature.

## Perbaikan Update, Dialog HP, dan D-pad — 2026-10-17
- [ ] Audit dan perbaiki validasi/instalasi APK update agar tidak parse error.
- [ ] Kompakkan dialog PIN khusus HP tanpa mengubah kenyamanan TV.
- [ ] Terapkan focus animasi NeonFocusBorder pada tombol dialog yang belum jelas.
- [ ] Jalankan validasi diff dan build GitHub Actions.
- [ ] Push perubahan ke GitHub setelah verifikasi.

## Perbaikan Background Download APK — 2026-10-17
- [ ] Audit lifecycle download saat menu/activity/aplikasi berpindah.
- [ ] Pisahkan pekerjaan download dari lifecycle UI dan persist state selesai secara atomik.
- [ ] Cegah tombol install sebelum APK tervalidasi penuh setelah background download.
- [ ] Jalankan verifikasi terfokus dan build GitHub Actions.
- [ ] Push perubahan ke GitHub setelah build sukses.
