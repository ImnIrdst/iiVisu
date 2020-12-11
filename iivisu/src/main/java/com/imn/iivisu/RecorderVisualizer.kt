package com.imn.iivisu

import android.content.Context
import android.util.AttributeSet

class RecorderVisualizer : BaseVisualizer {

    constructor(context: Context?) : super(context)

    constructor(
        context: Context,
        attrs: AttributeSet?,
    ) : super(context, attrs)


    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
    ) : super(context, attrs, defStyleAttr)

    private val tempAmps = mutableListOf<Int>()
    fun addAmp(amp: Int, tickDuration: Int) {
        this.tickDuration = tickDuration
        this.tickPerBar = approximateBarDuration / (this.tickDuration)
        this.barDuration = tickPerBar * this.tickDuration
        this.tempAmps.add(amp)
        if (tempAmps.size >= tickPerBar) {
            this.amps.add(ampNormalizer.invoke(tempAmps.average().toInt()))
            this.tempAmps.clear()
        }
        val lastBarPosition = if (tempAmps.size < tickPerBar) {
            tickPerBar.toFloat() / (tickPerBar - tempAmps.size)
        } else {
            tickPerBar.toFloat()
        }
        this.cursorPosition = ((amps.size - 1) * tickPerBar).toFloat() + lastBarPosition
        invalidate()
    }

    fun clear() {
        amps.clear()
        cursorPosition = 0f
        invalidate()
    }
}