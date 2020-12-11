package com.imn.iivisu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PlayerVisualizer : BaseVisualizer {

    constructor(context: Context?) : super(context)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs)


    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
    ) : super(context, attrs, defStyleAttr)


    var onStartSeeking: (() -> Unit)? = null
    var onSeeking: ((Long) -> Unit)? = null
    var onFinishedSeeking: ((Long, Boolean) -> Unit)? = null
    var onAnimateToPositionFinished: ((Long, Boolean) -> Unit)? = null

    private var initialTouchX = 0f
    private var firstTouchX = 0f

    private var isPlaying = false
    private var isPlayingWhenSeekingStarts = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.parent.requestDisallowInterceptTouchEvent(true)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPlayingWhenSeekingStarts = isPlaying
                onStartSeeking?.invoke()
                initialTouchX = event.x
                firstTouchX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                isPressed = true
                updateView(event)
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                detectSingleTap(event)
                onFinishedSeeking?.invoke(currentDuration, isPlayingWhenSeekingStarts)
                this.parent.requestDisallowInterceptTouchEvent(false)
                isPressed = false
            }
        }
        return true
    }

    private fun updateView(event: MotionEvent) {
        val secondTouch = event.x
        val distance = ((secondTouch - firstTouchX) * tickPerBar) / (barWidth + spaceBetweenBar)

        if (abs(distance) > 0) {
            firstTouchX = event.x
            cursorPosition = min(tickCount.toFloat(), max(0f, cursorPosition - distance))
            onSeeking?.invoke(currentDuration)
            invalidate()
        }
    }

    private fun detectSingleTap(event: MotionEvent) {
        val secondTouch = event.x
        val distance =
            ((secondTouch - initialTouchX) * tickPerBar) / (barWidth + spaceBetweenBar)

        if (distance.toInt() == 0) {
            val singleTapDist =
                ((width / 2 - secondTouch) * tickPerBar) / (barWidth + spaceBetweenBar)
            cursorPosition = min(tickCount.toFloat(), max(0f, cursorPosition - singleTapDist))
            invalidate()
        }
    }

    fun setWaveForm(amps: List<Int>, tickDuration: Int) {
        val normalizedAmps = amps.map(ampNormalizer)

        this.amps.clear()
        this.tickDuration = tickDuration

        // show five bars per second
        this.tickPerBar = approximateBarDuration / (this.tickDuration)
        this.barDuration = tickPerBar * this.tickDuration

        for (i in normalizedAmps.indices step tickPerBar) {
            val j = min(normalizedAmps.size - 1, i + tickPerBar)
            this.amps.add(normalizedAmps.subList(i, j).average().toInt())
        }

        this.tickCount = this.amps.size * this.tickPerBar
        this.cursorPosition = 0f

        this.amps.maxOrNull()?.let {
            this.maxAmp = max(it.toFloat(), maxAmp)
        }

        invalidate()
    }

    fun updateTime(currentTime: Long, isPlaying: Boolean) {
        this.isPlaying = isPlaying
        this.cursorPosition = calculateCursorPosition(currentTime)
        invalidate()
    }

    fun seekOver(amount: Int) {
        seekTo(currentDuration + amount)
    }

    fun seekTo(timeStamp: Long) {
        val finishCursorPosition = calculateCursorPosition(timeStamp)
        val startCursorPosition = cursorPosition
        val diff = finishCursorPosition - startCursorPosition
        val animateDuration = 500L

        ValueAnimator.ofInt(0, diff.toInt()).apply {
            duration = animateDuration
            addUpdateListener {
                cursorPosition = startCursorPosition + it.animatedValue as Int
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onAnimateToPositionFinished?.invoke(
                        getTimeStamp(finishCursorPosition), isPlaying
                    )
                }
            })
            start()
        }
    }

    override fun onDetachedFromWindow() {
        onStartSeeking = null
        onSeeking = null
        onFinishedSeeking = null
        onAnimateToPositionFinished = null
        super.onDetachedFromWindow()
    }
}