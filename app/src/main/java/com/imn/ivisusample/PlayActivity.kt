package com.imn.ivisusample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.imn.ivisusample.databinding.ActivityPlayBinding
import com.imn.ivisusample.player.AudioPlayer
import com.imn.ivisusample.utils.formatAsTime
import com.imn.ivisusample.utils.getDrawableCompat
import kotlin.math.sqrt

class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var player: AudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = AudioPlayer.getInstance(applicationContext).init()

        initUI()
    }

    private fun initUI() = with(binding) {
        visualizer.apply {
            ampNormalizer = { sqrt(it.toFloat()).toInt() }
            onStartSeeking = {
                player.pause()
            }
            onSeeking = { binding.timelineTextView.text = it.formatAsTime() }
            onFinishedSeeking = { time, isPlayingBefore ->
                player.seekTo(time)
                if (isPlayingBefore) {
                    player.resume()
                }
            }
            onAnimateToPositionFinished = { time, isPlaying ->
                updateTime(time, isPlaying)
                player.seekTo(time)
            }
        }
        playButton.setOnClickListener { player.togglePlay() }
        seekForwardButton.setOnClickListener { visualizer.seekOver(SEEK_OVER_AMOUNT) }
        seekBackwardButton.setOnClickListener { visualizer.seekOver(-SEEK_OVER_AMOUNT) }

        lifecycleScope.launchWhenCreated {
            val amps = player.loadAmps()
            visualizer.setWaveForm(amps, player.tickDuration)
        }
    }

    override fun onStart() {
        super.onStart()

        listenOnPlayerStates()
    }

    override fun onStop() {
        player.release()
        super.onStop()
    }

    private fun listenOnPlayerStates() = with(binding) {
        player.apply {
            onStart = { playButton.icon = getDrawableCompat(R.drawable.ic_pause_24) }
            onStop = { playButton.icon = getDrawableCompat(R.drawable.ic_play_arrow_24) }
            onPause = { playButton.icon = getDrawableCompat(R.drawable.ic_play_arrow_24) }
            onResume = { playButton.icon = getDrawableCompat(R.drawable.ic_pause_24) }
            onProgress = { time, isPlaying -> updateTime(time, isPlaying) }
        }
    }

    private fun updateTime(time: Long, isPlaying: Boolean) = with(binding) {
        timelineTextView.text = time.formatAsTime()
        visualizer.updateTime(time, isPlaying)
    }

    companion object {
        const val SEEK_OVER_AMOUNT = 5000
    }
}