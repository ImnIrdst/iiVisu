package com.imn.ivisusample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.imn.ivisusample.databinding.ActivityMainBinding
import com.imn.ivisusample.recorder.Recorder
import com.imn.ivisusample.utils.checkAudioPermission
import com.imn.ivisusample.utils.formatAsTime
import com.imn.ivisusample.utils.getDrawableCompat
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

        initUI()
    }

    override fun onStart() {
        super.onStart()
        listenOnRecorderStates()
    }

    override fun onStop() {
        recorder.release()
        super.onStop()
    }

    private fun initUI() = with(binding) {
        recordButton.setOnClickListener { recorder.toggleRecording() }
        visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }
    }

    private fun listenOnRecorderStates() = with(binding) {
        recorder.apply {
            onStart = { recordButton.icon = getDrawableCompat(R.drawable.ic_stop_24) }
            onStop = {
                visualizer.clear()
                recordButton.icon = getDrawableCompat(R.drawable.ic_record_24)
                startActivity(Intent(this@MainActivity, PlayActivity::class.java))
            }
            onAmpListener = {
                runOnUiThread {
                    timelineTextView.text = recorder.getCurrentTime().formatAsTime()
                    visualizer.addAmp(it, tickDuration)
                }
            }
        }

    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1
    }
}