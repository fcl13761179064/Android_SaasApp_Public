package com.ayla.hotelsaas.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R

class MaxHeightRecyclerView : RecyclerView {
    private var maxHeight = 0f

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAttr(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        initAttr(attributeSet)
    }

    private fun initAttr(attributeSet: AttributeSet) {
        val obtainStyledAttributes =
            context.obtainStyledAttributes(attributeSet, R.styleable.MaxHeightRecyclerView)
        maxHeight =
            obtainStyledAttributes.getDimension(R.styleable.MaxHeightRecyclerView_maxHeight, 0f)
        obtainStyledAttributes.recycle()
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(
            widthSpec,
            if (maxHeight > 0) MeasureSpec.makeMeasureSpec(
                maxHeight.toInt(),
                MeasureSpec.AT_MOST
            ) else heightSpec
        )
    }


}