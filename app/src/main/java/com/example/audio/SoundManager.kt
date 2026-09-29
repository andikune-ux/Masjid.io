package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import com.example.data.model.AudioMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var activeJob: Job? = null

    /**
     * Mainkan alert saat masuk waktu sholat.
     */
    fun playPrayerAlert(
        mode: AudioMode,
        beepVolume: Int = 100,
        beepCount: Int = 5,
        beepDurationMs: Int = 1500,
        beepIntervalMs: Int = 2000,
        adzanStyle: String = "Makkah",
        adzanVolume: Int = 85
    ) {
        stopAll()
        when (mode) {
            AudioMode.SILENT -> { /* tanpa suara */ }
            AudioMode.BEEP_ONLY -> {
                playBeeps(
                    count = beepCount,
                    volumePercent = beepVolume,
                    durationMs = beepDurationMs,
                    intervalMs = beepIntervalMs
                )
            }
            AudioMode.FULL_ADZAN -> {
                playAdzanAudio(style = adzanStyle, volumePercent = adzanVolume)
            }
        }
    }

    /**
     * Test beep — dipanggil dari tombol TEST SUARA di AudioSettingsPane.
     */
    fun testBeep(
        count: Int,
        volumePercent: Int,
        durationMs: Int = 1500,
        intervalMs: Int = 2000
    ) {
        stopAll()
        playBeeps(
            count = count,
            volumePercent = volumePercent,
            durationMs = durationMs,
            intervalMs = intervalMs
        )
    }

    /**
     * Test adzan — kalau ada (opsional).
     */
    fun testAdzan(style: String, volumePercent: Int) {
        stopAll()
        playAdzanAudio(style = style, volumePercent = volumePercent)
    }

    fun stopAll() {
        activeJob?.cancel()
        activeJob = null
    }

    // ============================================================
    // BEEP — pola bip...bip...bip dengan durasi & jeda dinamis
    // ============================================================
    private fun playBeeps(
        count: Int,
        volumePercent: Int,
        durationMs: Int,
        intervalMs: Int
    ) {
        activeJob = scope.launch {
            var toneGenerator: ToneGenerator? = null
            try {
                val safeVolume = volumePercent.coerceIn(10, 100)

                // Coba pakai STREAM_ALARM (volume maksimal menembus speaker TV)
                toneGenerator = try {
                    ToneGenerator(AudioManager.STREAM_ALARM, safeVolume)
                } catch (e: Exception) {
                    // Fallback ke STREAM_MUSIC
                    ToneGenerator(AudioManager.STREAM_MUSIC, safeVolume)
                }

                val safeDuration = durationMs.coerceIn(200, 3000)
                val safeInterval = intervalMs.coerceIn(200, 5000)

                repeat(count) { index ->
                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, safeDuration)
                    // Tunggu durasi bip selesai + jeda
                    delay(safeDuration.toLong() + safeInterval.toLong())
                }
            } catch (e: Exception) {
                Log.e("SoundManager", "Beep error: ${e.message}")
            } finally {
                try {
                    toneGenerator?.release()
                } catch (_: Exception) {}
            }
        }
    }

    // ============================================================
    // ADZAN — pakai harmonic melody (kalau tidak dipakai, biarkan)
    // ============================================================
    private fun playAdzanAudio(style: String, volumePercent: Int) {
        activeJob = scope.launch {
            try {
                playAdzanHarmonicMelody(volumePercent, style)
            } catch (e: Exception) {
                Log.e("SoundManager", "Adzan error: ${e.message}")
            }
        }
    }

    private suspend fun playAdzanHarmonicMelody(volumePercent: Int, style: String) {
        val sampleRate = 22050
        val baseFreq = when (style) {
            "Madinah" -> 220.0
            "Indonesia" -> 246.94
            else -> 196.0
        }
        val notes = listOf(
            Pair(baseFreq, 1800),
            Pair(baseFreq * 1.25, 1400),
            Pair(baseFreq * 1.5, 2000),
            Pair(baseFreq * 1.333, 1600),
            Pair(baseFreq, 2200)
        )
        val trackBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(trackBufferSize)
            .build()
        audioTrack.play()
        val vol = (volumePercent.coerceIn(10, 100) / 100f)
        for ((freq, durationMs) in notes) {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val wave = 0.6 * sin(2.0 * Math.PI * freq * time) +
                        0.3 * sin(2.0 * Math.PI * (freq * 2) * time) +
                        0.1 * sin(2.0 * Math.PI * (freq * 3) * time)
                val attackSamples = (sampleRate * 0.1).toInt()
                val releaseSamples = (sampleRate * 0.2).toInt()
                val envelope = when {
                    i < attackSamples -> i.toDouble() / attackSamples
                    i > numSamples - releaseSamples ->
                        (numSamples - i).toDouble() / releaseSamples
                    else -> 1.0
                }
                val sample = (wave * envelope * Short.MAX_VALUE * vol).toInt().toShort()
                buffer[i] = sample
            }
            audioTrack.write(buffer, 0, buffer.size)
            delay(durationMs.toLong() + 150)
        }
        audioTrack.stop()
        audioTrack.release()
    }
}
