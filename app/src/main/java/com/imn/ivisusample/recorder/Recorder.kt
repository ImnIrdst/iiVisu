package com.imn.ivisusample.recorder

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import com.github.squti.androidwaverecorder.WaveConfig
import com.github.squti.androidwaverecorder.WaveRecorder
import com.imn.ivisusample.utils.SingletonHolder
import com.imn.ivisusample.utils.recordFile

class Recorder private constructor(context: Context) {

    var onStart: (() -> Unit)? = null
    var onStop: (() -> Unit)? = null
    var onAmpListener: ((Int) -> Unit)? = null
        set(value) {
            recorder.onAmplitudeListener = value
            field = value
        }

    private var startTime: Long = 0
    private val recordingConfig = WaveConfig()
    private val appContext = context.applicationContext
    private lateinit var recorder: WaveRecorder

    var isRecording = false
        private set

    fun init(): Recorder {
        recorder = WaveRecorder(appContext.recordFile.toString())
            .apply { waveConfig = recordingConfig }
        return this
    }

    fun toggleRecording() {
        isRecording = if (!isRecording) {
            startTime = System.currentTimeMillis()
            recorder.startRecording()
            onStart?.invoke()
            true
        } else {
            recorder.stopRecording()
            onStop?.invoke()
            false
        }
    }

    fun getCurrentTime() = System.currentTimeMillis() - startTime

    val bufferSize: Int
        get() = AudioRecord.getMinBufferSize(
            recordingConfig.sampleRate,
            recordingConfig.channels,
            recordingConfig.audioEncoding
        )

    val tickDuration: Int
        get() = (bufferSize.toDouble() * 1000 / byteRate).toInt()

    private val channelCount: Int
        get() = if (recordingConfig.channels == AudioFormat.CHANNEL_IN_MONO) 1 else 2

    private val byteRate: Long
        get() = (bitPerSample * recordingConfig.sampleRate * channelCount / 8).toLong()

    private val bitPerSample: Int
        get() = when (recordingConfig.audioEncoding) {
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            else -> 16
        }

    fun release() {
        onStart = null
        onStop = null
        recorder.onAmplitudeListener = null
    }

    companion object : SingletonHolder<Recorder, Context>(::Recorder)
}