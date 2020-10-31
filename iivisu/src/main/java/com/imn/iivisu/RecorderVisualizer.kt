package com.imn.iivisu

import android.content.Context
import android.util.AttributeSet

class RecorderVisualizer : BaseVisualizer {

    constructor(context: Context?) : super(context)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs)


    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    fun addAmp(amp: Int) {
        amps.add(amp)
        cursorPosition = (amps.size - 1).toFloat()
        invalidate()
    }

    fun clear() {
        amps.clear()
        cursorPosition = 0f
        invalidate()
    }
}