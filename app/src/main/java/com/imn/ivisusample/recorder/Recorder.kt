package com.imn.ivisusample.recorder

import android.content.Context
import android.media.AudioRecord
import com.github.squti.androidwaverecorder.WaveConfig
import com.github.squti.androidwaverecorder.WaveRecorder
import com.imn.ivisusample.utils.SingletonHolder
import com.imn.ivisusample.utils.recordFile

class Recorder private constructor(context: Context) {

    var onStartRecording: (() -> Unit)? = null
    var onStopRecording: (() -> Unit)? = null
    var onAmpListener: ((Int) -> Unit)? = null
        set(value) {
            recorder.onAmplitudeListener = value
            field = value
        }

    private val recordingConfig = WaveConfig()
    private val recorder = WaveRecorder(context.recordFile.toString())
        .apply { waveConfig = recordingConfig }

    private var isRecording = false

    fun toggleRecording() {
        isRecording = if (!isRecording) {
            recorder.startRecording()
            onStartRecording?.invoke()
            true
        } else {
            recorder.stopRecording()
            onStopRecording?.invoke()
            false
        }
    }

    val bufferSize: Int
        get() = AudioRecord.getMinBufferSize(
            recordingConfig.sampleRate,
            recordingConfig.channels,
            recordingConfig.audioEncoding
        )

    companion object : SingletonHolder<Recorder, Context>(::Recorder)
}