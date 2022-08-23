package com.ayla.hotelsaas.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.ayla.hotelsaas.R;
import com.blankj.utilcode.util.StringUtils;

import java.lang.ref.WeakReference;

public class CustomToast {
    private static final String TAG = "CustomToast";

    private static WeakReference<Toast> mToastWeakReference;
    private static WeakReference<Toast> mToastNoImgWeakReference;

    public static void makeText(Context context, CharSequence tex, @DrawableRes int dResId) {
        Log.d(TAG, "makeText: " + tex);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mToastWeakReference == null || mToastWeakReference.get() == null) {
                    Toast toast = Toast.makeText(context.getApplicationContext(), tex, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_custom, null);
                    toast.setView(view);
                    mToastWeakReference = new WeakReference<>(toast);
                }
                View view = mToastWeakReference.get().getView();
                ImageView imageView = view.findViewById(R.id.iv);
                imageView.setImageResource(dResId);
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(tex);
                mToastWeakReference.get().show();
            }
        });
    }

    public static void makeRotationText(Context context, CharSequence tex, @DrawableRes int dResId) {
        Log.d(TAG, "makeText: " + tex);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mToastWeakReference == null || mToastWeakReference.get() == null) {
                    Toast toast = Toast.makeText(context.getApplicationContext(), tex, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_custom, null);
                    toast.setView(view);
                    mToastWeakReference = new WeakReference<>(toast);
                }
                View view = mToastWeakReference.get().getView();
                ImageView imageView = view.findViewById(R.id.iv);
                imageView.setImageResource(dResId);
                loadAni(imageView,context);
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(tex);
                mToastWeakReference.get().show();
            }
        });
    }

    /*
     *
     * @Title: bitmapRotation
     * @Description: 图片旋转
     * @param bm
     * @param orientationDegree
     * @return Bitmap
     * @throws
     */
    public static void loadAni(ImageView imageView, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
        anim.setFillAfter(false);//设置旋转后停止
        imageView.startAnimation(anim);
    }

    public static void makeText(Context context, @StringRes int resId, @DrawableRes int dResId) {
        makeText(context, StringUtils.getString(resId), dResId);
    }

    public static void makeOnlyHaseText(Context context, CharSequence tex) {
        Log.d(TAG, "makeText: " + tex);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mToastNoImgWeakReference == null || mToastNoImgWeakReference.get() == null) {
                    Toast toast = Toast.makeText(context.getApplicationContext(), tex, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_no_img_custom, null);
                    toast.setView(view);
                    mToastNoImgWeakReference = new WeakReference<>(toast);
                }
                View view = mToastNoImgWeakReference.get().getView();
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(tex);
                mToastNoImgWeakReference.get().show();
            }
        });
    }
}
