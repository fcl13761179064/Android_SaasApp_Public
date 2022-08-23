package com.ayla.hotelsaas.widget.common_dialog;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

import org.jetbrains.annotations.NotNull;

public class LoadingDialog extends DialogFragment {
    public static LoadingDialog newInstance(String msg) {

        Bundle args = new Bundle();
        args.putString("msg", msg);
        LoadingDialog fragment = new LoadingDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.requestFeature(Window.FEATURE_NO_TITLE);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
        return inflater.inflate(R.layout.dialog_laoding, null);
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView iv = view.findViewById(R.id.iv);
        loadAni(iv, getContext());
        if (getArguments() != null) {
            TextView tv = view.findViewById(R.id.tv);
            tv.setText(getArguments().getString("msg"));
        }
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    public static void loadAni(ImageView imageView, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
        anim.setFillAfter(true);//设置旋转后停止
        imageView.startAnimation(anim);
    }

}
