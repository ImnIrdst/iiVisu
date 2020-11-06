package com.imn.ivisusample.player

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
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

    var onProgress: ((Long, Boolean) -> Unit)? = null
    var onStart: (() -> Unit)? = null
    var onStop: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onResume: (() -> Unit)? = null
    val tickDuration = Recorder.getInstance(context).tickDuration

    private var timer: CountDownTimer? = null
    private val recordFile = context.recordFile
    private val bufferSize = Recorder.getInstance(context).bufferSize
    private val player: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build()
            .apply {
                prepare(context.recordFile.toMediaSource())
                addListener(this@AudioPlayer)
            }
    }

    fun togglePlay() {
        player.apply {
            if (!playWhenReady) {
                playWhenReady = true
            } else {
                pause()
            }
        }
    }

    fun seekTo(time: Long) {
        player.seekTo(time)
    }

    fun resume() {
        player.playWhenReady = true
        updateProgress()
        onResume?.invoke()
    }

    fun pause() {
        timer?.cancel()
        player.playWhenReady = false
        updateProgress()
        onPause?.invoke()
    }

    private fun updateProgress(position: Long = player.currentPosition) {
        onProgress?.invoke(position, player.playWhenReady)
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
                if (player.playWhenReady) {
                    val timerDuration = player.duration - player.currentPosition
                    timer?.cancel()
                    timer = object : CountDownTimer(timerDuration, 20) {
                        override fun onTick(millisUntilFinished: Long) {
                            updateProgress()
                        }

                        override fun onFinish() {
                            updateProgress(player.duration)
                        }
                    }.start()
                    onStart?.invoke()
                }
            }
            Player.STATE_ENDED -> {
                seekTo(0)
                player.playWhenReady = false
                onStop?.invoke()
            }
            Player.STATE_BUFFERING -> Unit
            Player.STATE_IDLE -> Unit
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Log.e(TAG, error.toString())
    }

    fun release() {
        onProgress = null
        onStart = null
        onStop = null
        onPause = null
        onResume = null
    }

    companion object : SingletonHolder<AudioPlayer, Context>(::AudioPlayer) {
        val TAG = AudioPlayer::class.simpleName
    }
}
