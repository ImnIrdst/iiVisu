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

        player = AudioPlayer.getInstance(this)

        binding.playButton.setOnClickListener {
            player.play()
        }

        lifecycleScope.launchWhenCreated {
            println("imnimn startLoading")
            val amps = player.loadAmps()
            binding.visualizer.setWaveForm(amps, player.tickDuration)
        }

        player.onProgress = { binding.visualizer.updateTime(it.toInt(), true) }
    }

}