package com.imn.iivisu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.imn.iivisu.databinding.ActivityMainBinding
import com.imn.iivisu.recorder.Recorder
import com.imn.iivisu.utils.checkAudioPermission

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recorder: Recorder
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAudioPermission(AUDIO_PERMISSION_REQUEST_CODE)

        recorder = Recorder.getInstance(this).apply {
            onStartRecording = { binding.recordButton.text = getString(R.string.stop) }
            onStopRecording = { binding.recordButton.text = getString(R.string.record) }
        }

        binding.recordButton.setOnClickListener { recorder.toggleRecording() }
    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1
    }
}