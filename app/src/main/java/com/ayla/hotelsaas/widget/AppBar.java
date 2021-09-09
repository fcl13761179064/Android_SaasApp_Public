/*
 * create by cairurui on 1/24/19 8:43 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.blankj.utilcode.util.ToastUtils;


public class AppBar extends FrameLayout {
    private final String TAG = this.getClass().getSimpleName();
    private ImageView leftImageView;
    private ImageView rightImageView;
    private TextView leftTextView;
    public TextView rightTextView;
    private TextView titleTextView;
    private LinearLayout leftLinearLayout;
    private View bottom_line;
    private Boolean isSHowHidden = false;
    private Button btn_all, btn_bufen;

    public AppBar(@NonNull Context context) {
        this(context, null);
    }

    public AppBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private @DrawableRes
    int left_iv, right_iv;
    private String left_tv, right_tv, center_tv;
    private int right_tv_color;
    private boolean bottom_line_visibility;
    private LinearLayout ll_tab;

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(getContext(), R.layout.common_appbar_layout, this);

        leftImageView = findViewById(R.id.iv_left);
        leftTextView = findViewById(R.id.tv_left);
        rightImageView = findViewById(R.id.iv_right);
        rightTextView = findViewById(R.id.tv_right);
        titleTextView = findViewById(R.id.tv_title);
        leftLinearLayout = findViewById(R.id.left_ll);
        bottom_line = findViewById(R.id.bottom_line);
        btn_all = findViewById(R.id.btn_all);
        btn_bufen = findViewById(R.id.btn_bufen);
        ll_tab = findViewById(R.id.ll_tab);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AppBar, defStyleAttr, 0);
        left_iv = ta.getResourceId(R.styleable.AppBar_left_iv, 0);
        left_tv = ta.getString(R.styleable.AppBar_left_tv);
        right_iv = ta.getResourceId(R.styleable.AppBar_right_iv, 0);
        right_tv = ta.getString(R.styleable.AppBar_right_tv);
        center_tv = ta.getString(R.styleable.AppBar_center_tv);
        bottom_line_visibility = ta.getBoolean(R.styleable.AppBar_appbar_bottom_line, true);
        right_tv_color = ta.getColor(R.styleable.AppBar_right_tv_color, ContextCompat.getColor(getContext(), R.color.color_333333));
        ta.recycle();

        adjustContent();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width, height;

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 2) {
            final View appBarView = getChildAt(0);
            final View contentView = getChildAt(1);
            final ViewGroup centerContainer = appBarView.findViewById(R.id.appbar_center_container);
            removeView(contentView);
            centerContainer.removeAllViewsInLayout();
            centerContainer.addView(contentView);
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        width = getChildAt(0).getMeasuredWidth();
        height = getChildAt(0).getMeasuredHeight();
        setMeasuredDimension(measureWidthMode == MeasureSpec.EXACTLY ? measureWidth : width, measureHeightMode == MeasureSpec.EXACTLY ? measureHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout: ");
        super.onLayout(changed, left, top, right, bottom);
    }


    private void adjustContent() {
        Log.d(TAG, "adjustContent: ");
        if (left_iv == 0) {
            leftImageView.setVisibility(GONE);
        } else {
            leftImageView.setVisibility(VISIBLE);
            leftImageView.setImageResource(left_iv);
        }
        if (TextUtils.isEmpty(left_tv)) {
            leftTextView.setVisibility(INVISIBLE);
        } else {
            leftTextView.setVisibility(VISIBLE);
            leftTextView.setText(left_tv);
        }
        if (right_iv == 0) {
            rightImageView.setVisibility(GONE);
        } else {
            rightImageView.setVisibility(VISIBLE);
            rightImageView.setImageResource(right_iv);
        }
        if (TextUtils.isEmpty(right_tv)) {
            rightTextView.setVisibility(INVISIBLE);
        } else {
            rightTextView.setVisibility(VISIBLE);
            rightTextView.setText(right_tv);
            rightTextView.setTextColor(right_tv_color);
        }

        if (getBackground() == null) {
            setBackgroundColor(Color.WHITE);
        }
        if (!bottom_line_visibility) {
            bottom_line.setVisibility(GONE);
        }

        if (isSHowHidden) {
            ll_tab.setVisibility(VISIBLE);
            titleTextView.setVisibility(GONE);
        } else {
            titleTextView.setVisibility(VISIBLE);
            titleTextView.setText(center_tv);
            ll_tab.setVisibility(GONE);
        }
    }

    public void setLeftText(String text) {
        left_tv = text;
        adjustContent();
    }

    public void setRightText(String text) {
        right_tv = text;
        adjustContent();
    }

    public void setCenterText(String text) {
        center_tv = text;
        adjustContent();
    }


    public void setLeftImageView(int leftiv) {
        left_iv = leftiv;
        adjustContent();
    }

    public void setShowHiddenCenterTitle(Boolean b) {
        isSHowHidden = b;
    }


    public Button getAllBtn() {
        return btn_all;
    }

    public Button getBufenBtn() {
        return btn_bufen;
    }


}

