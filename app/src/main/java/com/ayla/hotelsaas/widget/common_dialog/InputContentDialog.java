package com.ayla.hotelsaas.widget.common_dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.blankj.utilcode.util.SizeUtils;

import java.nio.charset.Charset;

public class InputContentDialog extends DialogFragment {
    private String editValue;
    private String title;
    private String editHint;
    private int gravity = Gravity.BOTTOM;
    private InputFilter[] inputFilter;
    private EditText contentET;
    private View confirmView;
    //取消按钮显示为返回
    private boolean leftBack;
    private TextView emptyView;

    public InputContentDialog setLeftBack(boolean leftBack) {
        this.leftBack = leftBack;
        return this;
    }

    //字节
    private int maxLength = 1024;

    public InputContentDialog setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public InputContentDialog setInputFilter(InputFilter[] inputFilter) {
        this.inputFilter = inputFilter;
        return this;
    }

    public InputContentDialog setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public InputContentDialog setEditValue(String editValue) {
        this.editValue = editValue;
        return this;
    }


    public InputContentDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public InputContentDialog setEditHint(String editHint) {
        this.editHint = editHint;
        return this;
    }

    private OperateListener operateListener;

    public InputContentDialog setOperateListener(OperateListener operateListener) {
        this.operateListener = operateListener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setWindowAnimations(R.style.FromBottomShow);
        }
        return inflater.inflate(R.layout.dialog_input_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        confirmView = view.findViewById(R.id.tv_confirm);
        contentET = view.findViewById(R.id.input_content);
        emptyView = view.findViewById(R.id.empty_tips);
        contentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0)
                    emptyView.setVisibility(View.INVISIBLE);
//                confirmView.setEnabled(s.toString().trim().length() != 0);
                if (s.toString().getBytes(Charset.forName("GBK")).length >= maxLength) {
                    if (operateListener != null) {
                        operateListener.contentOverMaxLength();
                    }
                }
            }
        });
        contentET.requestFocus();
        if (!TextUtils.isEmpty(editValue)) {
            contentET.setText(editValue);
            contentET.setSelection(editValue.length());
        }
        if (!TextUtils.isEmpty(editHint)) {
            contentET.setHint(editHint);
        }
        if (inputFilter != null) {
            contentET.setFilters(inputFilter);
        }
        TextView titleTextView = view.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            titleTextView.setText(title);
        }
        confirmView.setOnClickListener(v -> {
            if (TextUtils.isEmpty(contentET.getText().toString().trim())) {
                emptyView.setVisibility(View.VISIBLE);
                return;
            }
            hideSoftInput();
            if (operateListener != null) {
                operateListener.confirm(contentET.getText().toString());
            }
            dismissAllowingStateLoss();
        });
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        if (leftBack) {
            tvCancel.setText("返回");
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable drawable = getResources().getDrawable(R.drawable.icon_back_white, null);
            drawable.setBounds(0, 0, SizeUtils.dp2px(24), SizeUtils.dp2px(24));
            tvCancel.setCompoundDrawables(drawable, null, null, null);
        }
        tvCancel.setOnClickListener(v -> {
            hideSoftInput();
            if (operateListener != null) {
                operateListener.cancel();
            }
            dismissAllowingStateLoss();
        });

        view.findViewById(R.id.iv_clear).setOnClickListener(v -> {
            contentET.setText("");
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog() != null) {
            WindowManager.LayoutParams attributes = getDialog().getWindow().getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.gravity = gravity;
            getDialog().getWindow().setAttributes(attributes);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        contentET.postDelayed(this::showSoftInput, 50);
    }

    private void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) MyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            contentET.requestFocus();
            imm.showSoftInput(contentET, 0);
        }
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) MyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(contentET.getWindowToken(), 0);
        }
    }


    public interface OperateListener {
        void confirm(String content);

        void cancel();

        void contentOverMaxLength();
    }
}
