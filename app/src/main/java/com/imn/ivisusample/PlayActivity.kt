package com.imn.ivisusample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import com.imn.ivisusample.databinding.ActivityPlayBinding
import com.imn.ivisusample.recorder.Recorder
import java.io.File

class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.playButton.setOnClickListener {
            player = SimpleExoPlayer.Builder(this).build()
            player.prepare(Recorder.getInstance(this).recordFile.toMediaSource())
            player.playWhenReady = true
        }
    }

    private fun File.toMediaSource(): MediaSource =
        DataSpec(this.toUri())
            .let { FileDataSource().apply { open(it) } }
            .let { DataSource.Factory { it } }
            .let { ProgressiveMediaSource.Factory(it, DefaultExtractorsFactory()) }
            .createMediaSource(this.toUri())

}