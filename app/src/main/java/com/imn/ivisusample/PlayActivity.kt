package com.imn.ivisusample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.imn.ivisusample.databinding.ActivityPlayBinding
import com.imn.ivisusample.player.AudioPlayer
import com.imn.ivisusample.utils.formatAsTime
import kotlin.math.sqrt

class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var player: AudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = AudioPlayer.getInstance(applicationContext)
        binding.visualizer.apply {
            onStartSeeking = { player.pause() } // TODO remove excessive toLongs
            onSeeking = { binding.timelineTextView.text = it.toLong().formatAsTime() }
            onFinishedSeeking = { time, isPlayingBefore ->
                player.seekTo(time)
                if (isPlayingBefore) {
                    player.resume()
                }
            }
        }
        binding.playButton.setOnClickListener { player.togglePlay() }

        lifecycleScope.launchWhenCreated {
            val amps = player.loadAmps()
            binding.visualizer.setWaveForm(amps, player.tickDuration)
        }

    }

    override fun onStart() {
        super.onStart()
        player.apply {
            onStart = { binding.playButton.text = getString(R.string.pause) }
            onStop = { binding.playButton.text = getString(R.string.play) }
            onPause = { binding.playButton.text = getString(R.string.resume) }
            onResume = { binding.playButton.text = getString(R.string.pause) }
            onProgress = { time, isPlaying ->
                binding.timelineTextView.text = time.formatAsTime()
                binding.visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }
                binding.visualizer.updateTime(time.toInt(), isPlaying)
            }
        }
    }

    override fun onStop() {
        player.release()
        super.onStop()
    }
}