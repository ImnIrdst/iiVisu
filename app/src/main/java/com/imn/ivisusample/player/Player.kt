package com.imn.ivisusample.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.imn.ivisusample.recorder.Recorder
import com.imn.ivisusample.utils.SingletonHolder
import com.imn.ivisusample.utils.WAVE_HEADER_SIZE
import com.imn.ivisusample.utils.recordFile
import com.imn.ivisusample.utils.toMediaSource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Player private constructor(context: Context) {

    private val recordFile = context.recordFile
    private val bufferSize = Recorder.getInstance(context).bufferSize
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

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun loadAmps(): List<Int> = withContext(IO) {
        val amps = mutableListOf<Int>()
        val buffer = ByteArray(bufferSize)
        File(recordFile.toString()).inputStream().use {
            it.skip(WAVE_HEADER_SIZE.toLong())

            var count = it.read(buffer)
            while (count > 0) {
                amps.add(buffer.calculateAmplitude())
                count = it.read(buffer)
            }
        }
        amps
    }

    private fun ByteArray.calculateAmplitude(): Int {
        return ShortArray(size / 2).let {
            ByteBuffer.wrap(this)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
                .get(it)
            it.maxOrNull()?.toInt() ?: 0
        }
    }

    companion object : SingletonHolder<Player, Context>(::Player)
}
