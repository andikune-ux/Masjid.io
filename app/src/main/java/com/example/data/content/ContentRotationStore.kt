package com.example.data.content

import com.example.data.model.AppSettings

/**
 * Jenis konten yang bisa tampil bergantian di panel rotasi.
 */
enum class RotationType {
    AYAT,
    HADITS,
    ASMAUL_HUSNA,
    WISDOM_CARD
}

/**
 * Data konten rotasi yang siap ditampilkan.
 */
data class RotationContent(
    val type: RotationType,
    val title: String,
    val arabic: String? = null,
    val latin: String? = null,
    val translation: String? = null,
    val source: String? = null
)

object ContentRotationStore {

    /**
     * Kumpulkan daftar konten rotasi yang aktif berdasarkan setting.
     */
    fun buildRotationList(settings: AppSettings, currentIndex: Int): List<RotationContent> {
        val list = mutableListOf<RotationContent>()

        if (settings.contentRotationShowAyat) {
            val ayat = AyatStore.getAyat(currentIndex)
            list.add(
                RotationContent(
                    type = RotationType.AYAT,
                    title = "Ayat Al-Quran",
                    arabic = ayat.arabic,
                    latin = ayat.latin,
                    translation = ayat.translation,
                    source = "QS. ${ayat.surahName}: ${ayat.ayahNumber}"
                )
            )
        }

        if (settings.contentRotationShowHadits) {
            val hadits = HaditsStore.getHadits(currentIndex)
            list.add(
                RotationContent(
                    type = RotationType.HADITS,
                    title = "Hadits Pilihan",
                    arabic = hadits.arabic,
                    latin = null,
                    translation = hadits.translation,
                    source = hadits.source
                )
            )
        }

        if (settings.contentRotationShowAsmaulHusna) {
            val asmaul = AsmaulHusnaStore.getByIndex(currentIndex)
            list.add(
                RotationContent(
                    type = RotationType.ASMAUL_HUSNA,
                    title = "Asmaul Husna #${asmaul.number}",
                    arabic = asmaul.arabic,
                    latin = asmaul.latin,
                    translation = asmaul.translation,
                    source = "Asmaul Husna"
                )
            )
        }

        return list
    }

    /**
     * Ambil satu konten rotasi berdasarkan index global.
     * Cocok untuk rotasi otomatis.
     */
    fun getContent(settings: AppSettings, index: Int): RotationContent? {
        val list = buildRotationList(settings, index)
        if (list.isEmpty()) return null
        return list[index % list.size]
    }

    /**
     * Total konten yang aktif.
     */
    fun getTotalActive(settings: AppSettings): Int {
        var count = 0
        if (settings.contentRotationShowAyat) count++
        if (settings.contentRotationShowHadits) count++
        if (settings.contentRotationShowAsmaulHusna) count++
        return count
    }
}
