package com.imn.ivisusample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.imn.ivisusample.databinding.ActivityMainBinding
import com.imn.ivisusample.recorder.Recorder
import com.imn.ivisusample.utils.checkAudioPermission
import com.imn.ivisusample.utils.formatAsTime
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recorder: Recorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        checkAudioPermission(AUDIO_PERMISSION_REQUEST_CODE)

        recorder = Recorder.getInstance(applicationContext)
        binding.recordButton.setOnClickListener { recorder.toggleRecording() }
    }

    override fun onStart() {
        super.onStart()
        binding.visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }
        
        recorder.apply {
            onStart = { binding.recordButton.text = getString(R.string.stop) }
            onStop = {
                binding.visualizer.clear()
                binding.recordButton.text = getString(R.string.record)
                startActivity(Intent(this@MainActivity, PlayActivity::class.java))
            }
            onAmpListener = {
                runOnUiThread {
                    binding.timelineTextView.text = recorder.getCurrentTime().formatAsTime()

                    binding.visualizer.addAmp(it)
                }
            }
        }
    }

    override fun onStop() {
        recorder.release()
        super.onStop()
    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1
    }
}