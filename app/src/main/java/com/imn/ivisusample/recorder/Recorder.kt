package com.imn.ivisusample.recorder

import android.content.Context
import com.github.squti.androidwaverecorder.WaveRecorder
import com.imn.ivisusample.utils.SingletonHolder
import java.io.File

class Recorder private constructor(context: Context) {

    var onStartRecording: (() -> Unit)? = null
    var onStopRecording: (() -> Unit)? = null
    var onAmpListener: ((Int) -> Unit)? = null
        set(value) {
            recorder.onAmplitudeListener = value
            field = value
        }

    private val recordFile = File(context.filesDir, "rec.wav")
    private val recorder = WaveRecorder(recordFile.toString())

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

    companion object : SingletonHolder<Recorder, Context>(::Recorder)
}