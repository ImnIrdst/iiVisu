package com.imn.ivisusample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.imn.ivisusample.databinding.ActivityPlayBinding
import com.imn.ivisusample.player.AudioPlayer

class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var player: AudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = AudioPlayer.getInstance(this).apply {
            onStart = { binding.playButton.text = getString(R.string.pause) }
            onStop = { binding.playButton.text = getString(R.string.play) }
            onPause = { binding.playButton.text = getString(R.string.resume) }
            onResume = { binding.playButton.text = getString(R.string.pause) }
            onProgress = { time, isPlaying ->
                binding.visualizer.updateTime(time.toInt(), isPlaying)
            }
        }
        binding.visualizer.apply {
            onStartSeeking = { player.pause() }
            onSeeking = {}
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

}