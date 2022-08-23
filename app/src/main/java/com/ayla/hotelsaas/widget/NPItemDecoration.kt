package com.ayla.hotelsaas.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.ext.dp
import com.blankj.utilcode.util.ConvertUtils

/**
 * @ClassName:  NPItemDecoration
 * @Description: 列表分割线
 * @Author: vi1zen
 * @CreateDate: 2020/10/13 10:45
 */
class NPItemDecoration(
    private val leftMargin: Float = 44.dp().toFloat(),
    private val rightMargin: Float = 47.dp().toFloat()
) : RecyclerView.ItemDecoration() {

    private val firstLineColor = Color.parseColor("#0D000000")
    private val firstLineHeight = ConvertUtils.dp2px(1f).toFloat()
    private val mPaint = Paint().apply { style = Paint.Style.STROKE }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        for (index in 0 until parent.childCount - 1) {
            val childView = parent.getChildAt(index)
            mPaint.color = firstLineColor
            mPaint.strokeWidth = firstLineHeight
            c.drawLine(
                leftMargin,
                childView.bottom.toFloat(),
                parent.width - rightMargin,
                childView.bottom.toFloat(),
                mPaint
            )
        }
    }
}