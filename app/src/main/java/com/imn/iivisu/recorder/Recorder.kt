package com.imn.iivisu.recorder

import android.content.Context
import com.github.squti.androidwaverecorder.WaveRecorder
import com.imn.iivisu.utils.SingletonHolder
import java.io.File

class Recorder private constructor(context: Context) {

    var onStartRecording: (() -> Unit)? = null
    var onStopRecording: (() -> Unit)? = null
    
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