package com.ayla.hotelsaas.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.ext.dp
import java.util.*
import kotlin.concurrent.schedule

class ScanView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val sideWidth = 2.dp().toFloat()
    private val sideLength = 18.dp().toFloat()
    private val frameLength = 210.dp().toFloat()

    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 200.dp().toFloat()
    private var y2 = y1 + frameLength

    private val mPaint = Paint()

    private val barPaint = Paint()
    private val barPath = Path()

    private val bgPaint = Paint()
    private lateinit var bgRectF: RectF

    private var barHeight = y1

    private var mTimer: Timer

    private var colorArray = intArrayOf(
        Color.parseColor("#0047C986"),
        Color.parseColor("#47C986"),
        Color.parseColor("#0047C986")
    )

    init {
//        setBackgroundColor(ContextCompat.getColor(context, R.color.common_black))
//        alpha = 0.6f

        mPaint.isAntiAlias = true
        mPaint.color = ContextCompat.getColor(context, R.color.common_theme_green)
        mPaint.strokeWidth = sideWidth

        bgPaint.color = Color.parseColor("#99000000")
        bgPaint.style = Paint.Style.FILL

        barPaint.isAntiAlias = true

        mTimer = Timer()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        x1 = (w / 2) - (frameLength / 2)
        x2 = (w / 2) + (frameLength / 2)

        bgRectF = RectF(0f, 0f, w.toFloat(), h.toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.save()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas?.clipOutRect(x1, y1, x2, y2)
        }
        canvas?.drawRect(bgRectF, bgPaint)
        canvas?.restore()

        canvas?.drawLine(x1, y1 + sideLength, x1, y1, mPaint)
        canvas?.drawLine(x1, y1, x1 + sideLength, y1, mPaint)

        canvas?.drawLine(x2 - sideLength, y1, x2, y1, mPaint)
        canvas?.drawLine(x2, y1, x2, y1 + sideLength, mPaint)

        canvas?.drawLine(x2, y2 - sideLength, x2, y2, mPaint)
        canvas?.drawLine(x2, y2, x2 - sideLength, y2, mPaint)

        canvas?.drawLine(x1 + sideLength, y2, x1, y2, mPaint)
        canvas?.drawLine(x1, y2, x1, y2 - sideLength, mPaint)

        barPath.reset()
        barPath.addRect(RectF(x1, barHeight, x2, barHeight + 3.dp()), Path.Direction.CW)
        val gradient = LinearGradient(x1, barHeight, x2, barHeight, colorArray, null, Shader.TileMode.CLAMP)
        barPaint.shader = gradient
        canvas?.drawPath(barPath, barPaint)
    }

    fun startScan() {
        mTimer.schedule(1000, 4) {
            ++barHeight
            if (barHeight >= y2) barHeight = y1
            invalidate()
        }
    }

    fun stopScan() {
        mTimer.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mTimer.cancel()
    }
}