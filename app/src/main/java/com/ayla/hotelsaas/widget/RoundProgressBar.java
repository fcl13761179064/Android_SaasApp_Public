package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ayla.hotelsaas.R;

public class RoundProgressBar extends View {

    /**
     * 字体大小
     */
    private int mTextSize;
    /**
     * 字体颜色
     */
    private int mTextColor;
    /**
     * 渐变开始的颜色
     */
    private int mStartColor;
    /**
     * 渐变结束的颜色
     */
    private int mEndColor;

    /**
     * 进度条的宽
     */
    private int mProgressWidth;

    /**
     * 进度条的圆角大小
     */
    private int mRadius;

    /**
     * 默认进度条的颜色
     */
    private int mBgColor;

    /**
     * 进度条的当前进度
     */
    private float mCurrentProgress;
    private String mContent="0%";
    /**
     * 加载的速度
     */
    private int mLoadSpeed;
    private Paint mPaint;
    private Rect mBounds;
    private int mCircleWidth;
    private int mCurrentColor;

    /**
     * @param context
     */
    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundProgressBar, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = array.getIndex(i);
            switch (index) {
                case R.styleable.RoundProgressBar_textSizeRound:
                    /**
                     * 默认设置为16sp，TypeValue也可以把sp转化为px
                     */
                    mTextSize = array.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RoundProgressBar_textColorRound:
                    /**
                     * 默认设置为黑色
                     */
                    mTextColor = array.getColor(index, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_bgColorRound:
                    mBgColor = array.getColor(index, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_circleWidthRound:
                    mCircleWidth = array.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()
                    ));
                    break;
                case R.styleable.RoundProgressBar_currentColorRound:
                    mCurrentColor = array.getColor(index, Color.BLACK);
                    break;
                case R.styleable.RoundProgressBar_loadSpeedRound:
                    mLoadSpeed=array.getInt(index,10);
                    break;
            }
        }
        array.recycle();
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mBounds = new Rect();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mCurrentProgress < 360) {
                    mCurrentProgress = mCurrentProgress + 1;
                    mContent = Math.round((mCurrentProgress / 360) * 100) + "%";
                    try {
                        postInvalidate();
                        Thread.sleep(mLoadSpeed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 设置画笔的属性
         */
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);

        /**
         * 绘制圆环背景
         */
        int xPoint = getWidth() / 2;//获取圆心x的坐标
        int radius = xPoint - mCircleWidth;//获取圆心的半径
        // 指定图片绘制区域(左上角的四分之一)
        Bitmap mBitmap = BitmapFactory.decodeResource(getContext().getResources(),R.mipmap.wifi_progress_bg);
        // 指定图片绘制区域(左上角的四分之一)
        Rect src = new Rect(0,0,mBitmap.getWidth()/2,mBitmap.getHeight()/2);
        // 指定图片在屏幕上显示的区域(原图大小)
        Rect dst = new Rect(mBitmap.getWidth()+50,0,mBitmap.getWidth()+50+mBitmap.getWidth(),mBitmap.getHeight());

        // 画出原图像
        canvas.drawBitmap(mBitmap, new Matrix(),null);

        /**
         * 绘制圆环
         */
        mPaint.setColor(mCurrentColor);
        RectF oval = new RectF(xPoint - radius, xPoint - radius, radius + xPoint, radius + xPoint);
     //   canvas.drawArc(oval, -90, mCurrentProgress, false, mPaint);

        /**
         * 绘制当前进度文本
         */
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mContent, 0, mContent.length(), mBounds);
        canvas.drawText(mContent, xPoint - mBounds.width() / 2, xPoint + mBounds.height() / 2, mPaint);
    }
}
