package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural 8-bit retro sound synthesizer and haptic engine for authentic
 * Undertale/Deltarune style audio blips, barks, hits, and fanfare without external sound files.
 */
class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private fun playTone(
        frequencies: FloatArray,
        durationsMs: IntArray,
        waveType: WaveType = WaveType.SQUARE,
        volume: Float = 0.5f
    ) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val sampleRate = 22050
                var totalSamples = 0
                for (d in durationsMs) {
                    totalSamples += (sampleRate * (d / 1000f)).toInt()
                }
                if (totalSamples <= 0) return@launch

                val buffer = ShortArray(totalSamples)
                var bufferIndex = 0

                for (segment in frequencies.indices) {
                    val freq = frequencies[segment]
                    val duration = durationsMs[segment]
                    val segmentSamples = (sampleRate * (duration / 1000f)).toInt()

                    for (i in 0 until segmentSamples) {
                        if (bufferIndex >= buffer.size) break
                        val t = i.toFloat() / sampleRate
                        val raw = when (waveType) {
                            WaveType.SQUARE -> {
                                if (sin(2 * PI * freq * t) > 0) 1.0f else -1.0f
                            }
                            WaveType.TRIANGLE -> {
                                val period = 1.0f / freq
                                val phase = (t % period) / period
                                if (phase < 0.5f) 4f * phase - 1f else 3f - 4f * phase
                            }
                            WaveType.SINE -> {
                                sin(2 * PI * freq * t).toFloat()
                            }
                        }
                        // Simple linear fade out envelope at the end of each segment
                        val envelope = if (i > segmentSamples - 200) {
                            (segmentSamples - i).toFloat() / 200f
                        } else 1.0f

                        val sampleValue = (raw * envelope * volume * Short.MAX_VALUE).toInt()
                        buffer[bufferIndex++] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Auto release after sound finishes
                scope.launch {
                    kotlinx.coroutines.delay((totalSamples * 1000L / sampleRate) + 100)
                    audioTrack.release()
                }
            } catch (_: Exception) {
                // Ignore audio playback exceptions in sandbox
            }
        }
    }

    private fun triggerVibration(durationMs: Long, amplitude: Int = 120) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs.coerceAtLeast(10),
                        amplitude.coerceIn(1, 255)
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs.coerceAtLeast(10))
            }
        } catch (_: Exception) {
            // Ignore vibration errors
        }
    }

    fun playTextBlip() {
        playTone(
            frequencies = floatArrayOf(620f),
            durationsMs = intArrayOf(25),
            waveType = WaveType.SQUARE,
            volume = 0.25f
        )
    }

    fun playDogBark() {
        triggerVibration(50, 180)
        playTone(
            frequencies = floatArrayOf(380f, 650f, 520f),
            durationsMs = intArrayOf(40, 70, 50),
            waveType = WaveType.TRIANGLE,
            volume = 0.6f
        )
    }

    fun playHit() {
        triggerVibration(40, 220)
        playTone(
            frequencies = floatArrayOf(180f, 110f),
            durationsMs = intArrayOf(30, 40),
            waveType = WaveType.SQUARE,
            volume = 0.5f
        )
    }

    fun playCrit() {
        triggerVibration(90, 255)
        playTone(
            frequencies = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f),
            durationsMs = intArrayOf(40, 40, 50, 90),
            waveType = WaveType.SQUARE,
            volume = 0.55f
        )
    }

    fun playSoulHurt() {
        triggerVibration(140, 255)
        playTone(
            frequencies = floatArrayOf(150f, 90f, 60f),
            durationsMs = intArrayOf(70, 80, 120),
            waveType = WaveType.SQUARE,
            volume = 0.7f
        )
    }

    fun playFanfare() {
        triggerVibration(100, 200)
        playTone(
            frequencies = floatArrayOf(440f, 554f, 659f, 880f, 880f),
            durationsMs = intArrayOf(60, 60, 60, 100, 150),
            waveType = WaveType.TRIANGLE,
            volume = 0.6f
        )
    }

    enum class WaveType {
        SQUARE, TRIANGLE, SINE
    }
}
