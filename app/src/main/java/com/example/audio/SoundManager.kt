package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioManager
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
    private var isPlaying = false

    fun playPrayerAlert(
        mode: AudioMode,
        beepVolume: Int = 70,
        beepCount: Int = 3,
        adzanStyle: String = "Makkah",
        adzanVolume: Int = 85
    ) {
        stopAll()
        when (mode) {
            AudioMode.SILENT -> {
                // No sound
            }
            AudioMode.BEEP_ONLY -> {
                playBeeps(count = beepCount, volumePercent = beepVolume)
            }
            AudioMode.FULL_ADZAN -> {
                playAdzanAudio(style = adzanStyle, volumePercent = adzanVolume)
            }
        }
    }

    fun testBeep(count: Int, volumePercent: Int) {
        stopAll()
        playBeeps(count = count, volumePercent = volumePercent)
    }

    fun testAdzan(style: String, volumePercent: Int) {
        stopAll()
        playAdzanAudio(style = style, volumePercent = volumePercent)
    }

    fun stopAll() {
        activeJob?.cancel()
        activeJob = null
        isPlaying = false
    }

    private fun playBeeps(count: Int, volumePercent: Int) {
        activeJob = scope.launch {
            try {
                isPlaying = true
                val toneVolume = volumePercent.coerceIn(10, 100)
                val tg = ToneGenerator(AudioManager.STREAM_MUSIC, toneVolume)
                for (i in 1..count) {
                    tg.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 600)
                    delay(800)
                }
                delay(300)
                tg.release()
            } catch (e: Exception) {
                Log.e("SoundManager", "ToneGenerator error: ${e.message}")
            } finally {
                isPlaying = false
            }
        }
    }

    private fun playAdzanAudio(style: String, volumePercent: Int) {
        activeJob = scope.launch {
            try {
                isPlaying = true
                // Synthesizes a sacred harmonic azan melodic drone chant using PCM AudioTrack
                playAdzanHarmonicMelody(volumePercent, style)
            } catch (e: Exception) {
                Log.e("SoundManager", "Adzan audio error: ${e.message}")
            } finally {
                isPlaying = false
            }
        }
    }

    private suspend fun playAdzanHarmonicMelody(volumePercent: Int, style: String) {
        val sampleRate = 22050
        val baseFreq = when (style) {
            "Madinah" -> 220.0 // A3
            "Indonesia" -> 246.94 // B3
            else -> 196.0 // G3 (Makkah deep resonant)
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
                // Harmonic synthesis (fundamental + 2nd harmonic + 3rd harmonic for rich vocal timbre)
                val wave = 0.6 * sin(2.0 * Math.PI * freq * time) +
                        0.3 * sin(2.0 * Math.PI * (freq * 2) * time) +
                        0.1 * sin(2.0 * Math.PI * (freq * 3) * time)

                // Envelope attack and release
                val attackSamples = (sampleRate * 0.1).toInt()
                val releaseSamples = (sampleRate * 0.2).toInt()
                val envelope = when {
                    i < attackSamples -> i.toDouble() / attackSamples
                    i > numSamples - releaseSamples -> (numSamples - i).toDouble() / releaseSamples
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
