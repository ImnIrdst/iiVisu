package com.imn.ivisusample.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.imn.ivisusample.utils.SingletonHolder
import com.imn.ivisusample.utils.recordFile
import com.imn.ivisusample.utils.toMediaSource

class Player private constructor(context: Context) {

    private val player: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build()
            .apply {
                prepare(context.recordFile.toMediaSource())
            }
    }

    fun play() {
        player.apply {
            seekTo(0)
            playWhenReady = true
        }
    }


    companion object : SingletonHolder<Player, Context>(::Player)
}
