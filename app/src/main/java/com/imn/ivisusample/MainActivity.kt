package com.imn.ivisusample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.imn.ivisusample.databinding.ActivityMainBinding
import com.imn.ivisusample.recorder.Recorder
import com.imn.ivisusample.utils.checkAudioPermission
import kotlin.math.sqrt

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
            onAmpListener = {
                binding.recorderVisualizer.addAmp(sqrt(it.toFloat()).toInt()) // TODO how to normalize
            }
        }

        binding.recordButton.setOnClickListener { recorder.toggleRecording() }
    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1
    }
}