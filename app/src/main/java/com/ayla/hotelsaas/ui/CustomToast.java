package com.ayla.hotelsaas.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.ayla.hotelsaas.R;

public class CustomToast {
    public static void makeText(Context context, @StringRes int resId, @DrawableRes int dResId) {
        makeText(context, context.getResources().getText(resId), dResId);
    }

    public static void makeText(Context context, CharSequence tex, @DrawableRes int dResId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context.getApplicationContext(), tex, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_custom, null);
                ImageView imageView = view.findViewById(R.id.iv);
                imageView.setImageResource(dResId);
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(tex);
                toast.setView(view);
                toast.show();
            }
        });
    }
}
