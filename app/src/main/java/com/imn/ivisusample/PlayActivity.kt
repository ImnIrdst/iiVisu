package com.imn.ivisusample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.imn.ivisusample.databinding.ActivityPlayBinding
import com.imn.ivisusample.player.Player

class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = Player.getInstance(this)

        binding.playButton.setOnClickListener {
            player.play()
        }
    }

}