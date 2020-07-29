/*
 * create by cairurui on 8/1/19 2:42 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.widget;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ayla.hotelsaas.R;
import com.blankj.utilcode.util.SizeUtils;


public class CustomSheet extends DialogFragment {
    public static final String EXTRA_DIMENABLED = "dimEnabled";
    public static final String EXTRA_TEXT_COLOR = "text_color";
    public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final boolean DEFAULT_DIMENABLED = true;
    private int textColor = DEFAULT_TEXT_COLOR;
    private boolean mDimEnabled = DEFAULT_DIMENABLED;
    private String[] mTexts = new String[]{};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textColor = getArguments().getInt(EXTRA_TEXT_COLOR, DEFAULT_TEXT_COLOR);
        mDimEnabled = getArguments().getBoolean(EXTRA_DIMENABLED, DEFAULT_DIMENABLED);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),
                mDimEnabled ? R.style.BottomDialogDim : R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(dialog);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }
        return dialog;
    }

    private void initView(Dialog dialog) {
        LinearLayout contentView = new LinearLayout(getContext());
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentView.setOrientation(LinearLayout.VERTICAL);
        int padding = SizeUtils.dp2px(16);
        contentView.setPadding(padding, padding, padding, padding);
        dialog.setContentView(contentView);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                Object tag = v.getTag();
                if (tag instanceof Integer && mCallBack != null) {
                    mCallBack.callback((Integer) tag);
                }
            }
        };

        for (int i = 0; i < mTexts.length; i++) {
            String text = mTexts[i];
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(16);
            textView.setText(text);
            textView.setTextColor(textColor);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(46));
            layoutParams1.gravity = Gravity.CENTER;
            textView.setLayoutParams(layoutParams1);
            if (mTexts.length == 1) {
                textView.setBackgroundResource(R.drawable.actionsheet_single_selector);
            } else if (i == 0) {
                textView.setBackgroundResource(R.drawable.actionsheet_top_selector);
            } else if (i == mTexts.length - 1) {
                textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
            } else {
                textView.setBackgroundResource(R.drawable.actionsheet_center_selector);
            }
            if (i > 0) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                layoutParams.topMargin = Math.max(SizeUtils.dp2px(0.1f), 1);
            }
            textView.setTag(i);
            textView.setOnClickListener(onClickListener);
            contentView.addView(textView);
        }

        TextView cancelTextView = new TextView(getContext());
        cancelTextView.setGravity(Gravity.CENTER);
        cancelTextView.setTextSize(16);
        cancelTextView.setText(R.string.cancel);
        cancelTextView.setTextColor(textColor);
        cancelTextView.setBackgroundResource(R.drawable.actionsheet_single_selector);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(46));
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = SizeUtils.dp2px(8);
        cancelTextView.setLayoutParams(layoutParams);
        contentView.addView(cancelTextView);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (null != mCallBack) {
                    mCallBack.onCancel();
                }
            }
        });
    }

    private CallBack mCallBack;

    public interface CallBack {
        void callback(int index);

        void onCancel();
    }

    public static class Builder {
        CustomSheet mCustomSheet;
        private FragmentActivity mActivity;

        public Builder(FragmentActivity activity) {
            mActivity = activity;
            mCustomSheet = new CustomSheet();
        }

        public Builder setTextColor(int color) {
            mCustomSheet.textColor = color;
            return this;
        }

        public Builder setText(String... texts) {
            mCustomSheet.mTexts = texts;
            return this;
        }

        public Builder dimEnabled(boolean enable) {
            mCustomSheet.mDimEnabled = enable;
            return this;
        }

        public CustomSheet show(CallBack callBack) {
            mCustomSheet.mCallBack = callBack;
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_DIMENABLED, mCustomSheet.mDimEnabled);
            bundle.putInt(EXTRA_TEXT_COLOR, mCustomSheet.textColor);
            mCustomSheet.setArguments(bundle);
            FragmentManager fm = mActivity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentByTag("CustomSheet");
            if (fragment != null) {
                ft.remove(fragment);
            }
            ft.addToBackStack(null);
            mCustomSheet.show(ft, "CustomSheet");
            return mCustomSheet;
        }

    }
}
