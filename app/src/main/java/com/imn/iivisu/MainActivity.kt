package com.imn.iivisu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.squti.androidwaverecorder.WaveRecorder
import com.imn.iivisu.databinding.ActivityMainBinding
import com.imn.iivisu.utils.checkAudioPermission
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isRecording = false

    private lateinit var f: File
    private lateinit var r: WaveRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAudioPermission(AUDIO_PERMISSION_REQUEST_CODE)

        f = File(filesDir, "rec.wav")
        r = WaveRecorder(f.toString())

        binding.recordButton.setOnClickListener { toggleRecording() }
    }

    private fun toggleRecording() {
        if (!isRecording) {
            r.startRecording()
            binding.recordButton.text = getString(R.string.stop)
            isRecording = true
        } else {
            r.stopRecording()
            binding.recordButton.text = getString(R.string.record)
            isRecording = false
        }
    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1
    }
}