package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;

/**
 * @author huangxin
 */
public class MaskProgress extends View {


    public CirculateUpdateThread circulateUpdateThread;
    private int mWidth;
    private int mHight;
    private int arrowResId;

    /**
     * 每次setProgress时进度条前进或者回退到所设的值时都会有一段动画。
     * 该接口用于监听动画的完成，你应该设置监听器监听到动画完成后，才再一次调用
     * setProgress方法
     */
    public interface AnimateListener {
        void onAnimateFinish();

        void NetError();

        void progressNum(float currentProgress);
    }

    private float totalTime = 5;//s


    private final static int REFRESH = 10;//mills

    private float step;

    private float max = 350;


    private float currentProgress;

    private float destProgress = 0;
    private float realProgress = 0;
    private float oldRealProgress = 0;
    private int backgroundResId;
    private int contentResId;

    private float startAngle = 0;

    private Bitmap bg;
    private Bitmap ct;
    private Bitmap ar;

    private Paint paint;

    private int radius;

    private int beginX;
    private int beginY;

    private int centerX;
    private int centerY;

    private RectF rectF;

    private PorterDuffXfermode srcIn;

    private double rate;

    public boolean initialing = false;

    AnimateListener animateListener;

    public MaskProgress(Context context) {
        this(context, null);
    }

    public MaskProgress(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.maskProgressStyle);
    }

    public MaskProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    public void setAnimateListener(AnimateListener animateListener) {
        this.animateListener = animateListener;
    }

    public void setProgress(float destProgress) {
        if (destProgress > max)
            try {
                throw new Exception("progress can biger than max");
            } catch (Exception e) {
                e.printStackTrace();
            }

        this.destProgress = destProgress;
        oldRealProgress = realProgress;
        realProgress = (float) (destProgress * rate);
    }

    public float getProgress() {
        return destProgress;
    }

    public void setTotaltime(float totalTime) {
        this.totalTime = totalTime;
        step = 360 / (totalTime * 1000 / REFRESH);
    }

    public static int getRefresh() {
        return REFRESH;
    }

    public void setMax(float max) {
        this.max = max;
        rate = 360 / max;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }


    public void setBackgroundResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
        bg = BitmapFactory.decodeResource(getResources(), backgroundResId);
    }

    public void setContentResId(int contentResId) {
        this.contentResId = contentResId;
        ct = BitmapFactory.decodeResource(getResources(), contentResId);
    }

    public void setArrowResId(int arrowResId) {
        this.arrowResId = arrowResId;
        ar = BitmapFactory.decodeResource(getResources(), arrowResId);
    }

    public void updateProgress() {
        invalidate();
    }

    /**
     * 初始化,第一次给MaskProgress设值时,从没有填充到,填充到给定的值时
     * 有一段动画
     */
    public void initial() {
        initialing = true;
        circulateUpdateThread = new CirculateUpdateThread();
        circulateUpdateThread.start();
    }

    public float getMax() {
        return max;
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.maskProgressBar, defStyle, 0);

        if (typedArray != null) {
            try {
                setMax(typedArray.getFloat(R.styleable.maskProgressBar_max, max));
                setProgress(typedArray.getFloat(R.styleable.maskProgressBar_progress, destProgress));
                setTotaltime(typedArray.getFloat(R.styleable.maskProgressBar_anim_time, totalTime));
                setStartAngle(typedArray.getFloat(R.styleable.maskProgressBar_start_angle, startAngle));
                setContentResId(typedArray.getResourceId(R.styleable.maskProgressBar_progress_content, R.mipmap.wifi_progress_green_bg));
                setBackgroundResId(typedArray.getResourceId(R.styleable.maskProgressBar_progress_background, R.mipmap.wifi_progress_gray_bg));
                setArrowResId(typedArray.getResourceId(R.styleable.maskProgressBar_progress_arrow, R.mipmap.wifi_info_arrow));
            } finally {
                typedArray.recycle();
            }
        }

        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);

        rate = 360 / max;
        currentProgress = 0;
        realProgress = (float) (destProgress * rate);
        srcIn = new PorterDuffXfermode(Mode.SRC_IN);
        step = 360 / (totalTime * 1000 / REFRESH);

        bg = BitmapFactory.decodeResource(getResources(), backgroundResId);
        ct = BitmapFactory.decodeResource(getResources(), contentResId);

        Log.w("init", "max: " + max + "\n" + "destProgress: " + destProgress + "\n" + "totalTime: " + totalTime + "\n" + "startAngle: " + startAngle);

        initialing = true;
        circulateUpdateThread = new CirculateUpdateThread();
        circulateUpdateThread.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bg, 0, (mHight-bg.getHeight())/2, paint);
        int rc = canvas.saveLayer(0, (mHight-bg.getHeight())/2, bg.getWidth(), (mHight + bg.getHeight()) / 2, null, Canvas.ALL_SAVE_FLAG);
        paint.setFilterBitmap(false);
        if (initialing) {
            canvas.drawArc(rectF, startAngle, currentProgress, true, paint);
        } else {
            canvas.drawArc(rectF, startAngle, realProgress, true, paint);
        }
        paint.setXfermode(srcIn);
        canvas.drawBitmap(ct, 0, (mHight - ct.getHeight()) / 2, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(rc);
        if (initialing) {
            if (realProgress>25 && realProgress<110){
                drawRotateBitmap(canvas, paint, ar, currentProgress/1.2f -26, (mWidth - ar.getWidth()) / 2.1f, mHight / 1.75f);
            }else if (realProgress>110 && realProgress<190){
                drawRotateBitmap(canvas, paint, ar, currentProgress/1.1f-26, (mWidth - ar.getWidth()) / 2.1f, mHight / 1.75f);
            }else if ( realProgress>190){
                drawRotateBitmap(canvas, paint, ar, currentProgress/1.1f-36, (mWidth - ar.getWidth()) / 2f, mHight / 1.75f);
            }
        } else {
            if (realProgress>25 && realProgress<110){
                drawRotateBitmap(canvas, paint, ar, realProgress/1.2f-26, (mWidth - ar.getWidth()) / 2.1f, mHight /  1.75f);
            }else if (realProgress>110 && realProgress<190){
                drawRotateBitmap(canvas, paint, ar, realProgress /1.1f-26, (mWidth - ar.getWidth()) / 2.1f, mHight /  1.75f);
            }else if (realProgress>190){
                drawRotateBitmap(canvas, paint, ar, realProgress/1.1f-36, (mWidth - ar.getWidth()) /2f, mHight /  1.75f);
            }
        }

    }

    /**
     * 绘制自旋转位图
     *
     * @param canvas
     * @param paint
     * @param bitmap   位图对象
     * @param rotation 旋转度数
     * @param posX     在canvas的位置坐标
     * @param posY
     */
    private void drawRotateBitmap(Canvas canvas, Paint paint, Bitmap bitmap, float rotation, float posX, float posY) {
        Matrix matrix = new Matrix();
        int offsetX = bitmap.getWidth() / 2;
        int offsetY = bitmap.getHeight() / 2;
        matrix.postTranslate(-offsetX, -offsetY);
        matrix.postRotate(rotation);
        matrix.postTranslate(posX + offsetX, posY + offsetY);
        canvas.save();
        canvas.drawBitmap(bitmap, matrix, paint);
        canvas.restore();
    }

    public int[] getRectPosition(int progress) {
        int[] rect = new int[4];

        rect[0] = beginX;
        rect[1] = beginY;
        rect[2] = (int) (centerX + radius * Math.cos(progress * Math.PI / 180));
        rect[3] = (int) (centerY + radius * Math.sin(progress * Math.PI / 180));

        Log.w("getRectPosition", "30: " + Math.sin(30 * Math.PI / 180));

        Log.w("getRectPosition", "X: " + rect[2] + " " + "Y: " + rect[3]);

        return rect;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHight = h;
        centerX = mWidth / 2;
        centerY = mHight / 2;

        Bitmap bg_ = resizeBitmap(bg, mWidth, mHight);
        Bitmap ct_ = resizeBitmap(ct, mWidth, mHight);
        rectF = new RectF(-2, (mHight-bg_.getHeight())/2, bg_.getWidth(), (mHight + bg_.getHeight()) / 2);
        bg.recycle();
        ct.recycle();

        bg = bg_;
        ct = ct_;
    }


    private Bitmap resizeBitmap(Bitmap src, int w, int h) {

        int width = src.getWidth();
        int height = src.getHeight();
        int scaleWidht = w / width;
        int scaleHeight = h / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidht, scaleHeight);

        Bitmap result = Bitmap.createScaledBitmap(src, w, h, true);
        src = null;

        return result;
    }

    class CirculateUpdateThread extends Thread {

        @Override
        public void run() {
            while (initialing) {
                Log.d("1111111", initialing + "");
                postInvalidate();
                if (currentProgress < realProgress) {
                    currentProgress += step * rate;
                    if (currentProgress > realProgress)
                        currentProgress = realProgress;
                    if (animateListener != null) {
                        animateListener.progressNum(currentProgress);
                    }
                    if (!WifiUtils.getInstance(getContext()).mIsopenWifi()) {//如果没有打开wifi
                        //progress背景图
                        animateListener.NetError();
                    }
                } else {
                    // new Thread(new Runnable() {
                    //
                    // @Override
                    // public void run() {
                    // while (true) {
                    // postInvalidate();
                    // if (currentProgress > 0) {
                    // currentProgress -= step * rate;
                    // } else {
                    // currentProgress = 0;
                    // new CirculateUpdateThread().start();
                    // break;
                    // }
                    // try {
                    // Thread.sleep(REFRESH);
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }
                    // }
                    // }
                    // }).start();
                    currentProgress = 0;
                    initialing = false;
                    if (animateListener != null)
                        animateListener.onAnimateFinish();
                }
                try {
                    Thread.sleep(REFRESH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
