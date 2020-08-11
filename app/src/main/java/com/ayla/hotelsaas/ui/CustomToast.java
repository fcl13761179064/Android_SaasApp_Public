package com.ayla.hotelsaas.ui;

import android.content.Context;
import android.content.res.Resources;
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
    public static Toast makeText(Context context, @StringRes int resId, @DrawableRes int dResId)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), dResId);
    }

    public static Toast makeText(Context context, CharSequence tex, @DrawableRes int dResId) {
        Toast toast = Toast.makeText(context, tex, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_custom, null);
        ImageView imageView = view.findViewById(R.id.iv);
        imageView.setImageResource(dResId);
        TextView textView = view.findViewById(R.id.tv);
        textView.setText(tex);
        toast.setView(view);
        return toast;
    }
}
