package com.ayla.hotelsaas.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

    public static void makeText(Context context, @StringRes int resId, @DrawableRes int dResId) {
        makeText(context, StringUtils.getString(resId), dResId);
    }
}
