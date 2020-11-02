package com.imn.iivisu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.imn.ivisu.R
import kotlin.math.max
import kotlin.math.min

open class BaseVisualizer : View {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
        loadAttribute(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
        loadAttribute(context, attrs)
    }

    protected var maxAmp = DEFAULT_MAX_AMP
    protected var amps = mutableListOf<Int>()
    protected var spaceBetweenBar = 128f
    protected var cursorPosition = 0f
    private var maxVisibleBars = 0
    protected var tickPerBar = 1
    protected var tickDuration = 1000
    protected var tickCount = 0
    protected var barDuration = 1000
    protected var barWidth = 2f
        set(value) {
            if (field > 0) {
                field = value
                this.backgroundBarPrimeColor.strokeWidth = value
                this.loadedBarPrimeColor.strokeWidth = value
            }
        }
    private lateinit var loadedBarPrimeColor: Paint
    private lateinit var backgroundBarPrimeColor: Paint


    private fun init() {
        backgroundBarPrimeColor = Paint()
        this.backgroundBarPrimeColor.color = context.getColorCompat(R.color.gray)
        this.backgroundBarPrimeColor.strokeCap = Paint.Cap.ROUND
        this.backgroundBarPrimeColor.strokeWidth = barWidth

        loadedBarPrimeColor = Paint()
        this.loadedBarPrimeColor.color = context.getColorCompat(R.color.orange)
        this.loadedBarPrimeColor.strokeCap = Paint.Cap.ROUND
        this.loadedBarPrimeColor.strokeWidth = barWidth
    }

    private fun loadAttribute(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.iiVisu, 0, 0
        )
        try {
            spaceBetweenBar = typedArray
                .getDimension(R.styleable.iiVisu_spaceBetweenBar, context.dpToPx(2f))
            barWidth = typedArray.getDimension(R.styleable.iiVisu_barWidth, 2f)
            barDuration = typedArray.getInt(R.styleable.iiVisu_barDuration, 1000)
            loadedBarPrimeColor.strokeWidth = barWidth
            backgroundBarPrimeColor.strokeWidth = barWidth
            loadedBarPrimeColor.color = typedArray.getColor(
                R.styleable.iiVisu_loadedBarPrimeColor,
                context.getColorCompat(R.color.orange)
            )
            backgroundBarPrimeColor.color = typedArray.getColor(
                R.styleable.iiVisu_backgroundBarPrimeColor,
                context.getColorCompat(R.color.gray)
            )
        } finally {
            typedArray.recycle()
        }
    }

    protected val currentDuration: Int
        get() = cursorPosition.toInt() * tickDuration


    override fun onDraw(canvas: Canvas) {
        if (amps.isNotEmpty()) {
            for (i in getStartBar() until getEndBar()) {
                val startX = width / 2 - (getBarPosition() - i) * (barWidth + spaceBetweenBar)
                drawStraightBar(canvas, startX, getBarHeightAt(i).toInt(), getBaseLine())
            }
        }
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxVisibleBars = (width / (barWidth + spaceBetweenBar)).toInt()
    }

    private fun drawStraightBar(canvas: Canvas, startX: Float, height: Int, baseLine: Int) {
        val startY = baseLine + (height / 2).toFloat()
        val stopY = startY - height
        if (startX < width / 2) {
            canvas.drawLine(startX, startY, startX, stopY, loadedBarPrimeColor)
        } else {
            canvas.drawLine(startX, startY, startX, stopY, backgroundBarPrimeColor)
        }
    }

    private fun getBaseLine() = height / 2
    private fun getStartBar() = max(0, getBarPosition().toInt() - maxVisibleBars / 2)
    private fun getEndBar() = min(amps.size, getStartBar() + maxVisibleBars)
    private fun getBarHeightAt(i: Int) = height * max(0.01f, min(amps[i] / maxAmp, 0.9f))
    private fun getBarPosition() = cursorPosition / tickPerBar.toFloat()

    companion object {
        var DEFAULT_MAX_AMP = 50f
        var DEFAULT_BAR_DURATION = 100
    }
}