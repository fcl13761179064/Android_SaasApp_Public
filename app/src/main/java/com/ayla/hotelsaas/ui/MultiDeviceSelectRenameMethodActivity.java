package com.ayla.hotelsaas.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class MultiDeviceSelectRenameMethodActivity extends DialogFragment {

    public static MultiDeviceSelectRenameMethodActivity newInstance(DoneCallback doneCallback) {

        Bundle args = new Bundle();
        MultiDeviceSelectRenameMethodActivity fragment = new MultiDeviceSelectRenameMethodActivity();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private String editValue;

    public MultiDeviceSelectRenameMethodActivity setEditValue(String editValue) {
        this.editValue = editValue;
        return this;
    }

    private InputType inputType = InputType.text;

    public MultiDeviceSelectRenameMethodActivity setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    private String title;

    public MultiDeviceSelectRenameMethodActivity setTitle(String title) {
        this.title = title;
        return this;
    }

    private int maxLength = 20;

    public MultiDeviceSelectRenameMethodActivity setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    private String editHint;

    public MultiDeviceSelectRenameMethodActivity setEditHint(String editHint) {
        this.editHint = editHint;
        return this;
    }

    private DoneCallback doneCallback;


    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        params.windowAnimations = R.style.main_menu_animstyle;
        params.width = getResources().getDisplayMetrics().widthPixels;
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_multi_device_select_rename_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText editText = view.findViewById(R.id.et_dsn);
        editText.setText(editValue);
        editText.setHint(editHint);
        editText.setInputType(inputType.value);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        TextView titleTextView = view.findViewById(R.id.tv_title);
        TextView empty_notice = view.findViewById(R.id.empty_notice);
        titleTextView.setText(title);
        view.findViewById(R.id.v_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = editText.getText().toString();
                if (doneCallback != null) {
                    doneCallback.onDone(MultiDeviceSelectRenameMethodActivity.this, msg, empty_notice);
                }
            }
        });
        view.findViewById(R.id.v_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onCancel(MultiDeviceSelectRenameMethodActivity.this);
                }
                dismissAllowingStateLoss();
            }
        });
    }

    public enum InputType {
        numberSigned(0x00001002), text(0x00000001), numberSignedDecimal(0x00003002);

        private final int value;

        InputType(int i) {
            value = i;
        }
    }

    public interface DoneCallback {
        void onDone(DialogFragment dialog, String txt, TextView empty_notice);
        void onCancel(DialogFragment dialog);
    }
}
