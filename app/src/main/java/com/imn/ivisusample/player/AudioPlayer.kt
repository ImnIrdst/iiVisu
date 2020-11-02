package com.imn.ivisusample.player

import android.content.Context
import android.os.CountDownTimer
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
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

class AudioPlayer private constructor(context: Context) : Player.EventListener {

    val tickDuration = Recorder.getInstance(context).tickDuration
    var onProgress: ((Long) -> Unit)? = null

    private var timer: CountDownTimer? = null
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
            addListener(this@AudioPlayer)
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

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        when (playbackState) {
            Player.STATE_READY -> {
                println("imnimn STATE_READY")
                println("imnimn duration ${player.duration}")
                timer = object : CountDownTimer(player.duration, 20) {
                    override fun onTick(millisUntilFinished: Long) {
                        onProgress?.invoke(player.currentPosition)
                    }

                    override fun onFinish() {
                        onProgress?.invoke(player.duration)
                    }
                }.start()
            }
            Player.STATE_ENDED -> {
                println("imnimn STATE_ENDED")
            }
            Player.STATE_BUFFERING -> {
                println("imnimn STATE_BUFFERING")
            }
            Player.STATE_IDLE -> {
                println("imnimn STATE_IDLE")
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        println("imnimn $error")
    }

    companion object : SingletonHolder<AudioPlayer, Context>(::AudioPlayer)
}
