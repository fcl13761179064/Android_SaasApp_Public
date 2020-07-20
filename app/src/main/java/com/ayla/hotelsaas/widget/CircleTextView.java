package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

import com.ayla.hotelsaas.application.Constance;


/**
 * @描述 圆型背景TextView
 * @作者 fanchunlei
 * @时间 2020/7/18
 */
public class CircleTextView extends AppCompatTextView {
    private Paint mPaint;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float fx = getMeasuredWidth() / 2;
        float fy = getMeasuredHeight() / 2;
        float radius = Math.min(fx, fy);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, radius, mPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置状态
     * @param status
     * @param payMethod
     */
    public void setData(String status, String payMethod) {
        if (TextUtils.isEmpty(status)) {
            return;
        }
        switch (status) {
            case Constance.V_STATUS_online://在线
                mPaint.setColor(Color.parseColor("#579BE6"));
                setTextSize(16);
                setText("在线");
                break;
            case Constance.V_STATUS_offline://离线
                mPaint.setColor(Color.parseColor("#579BE6"));
                setTextSize(15);
                setText("离线");
                break;
        }
    }
}
