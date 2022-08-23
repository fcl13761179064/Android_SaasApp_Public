package com.ayla.hotelsaas.widget.scene_dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class InputValueDialog extends DialogFragment {

    private String rangeValue;

    public static InputValueDialog newInstance(ButtonClickListener doneCallback) {

        Bundle args = new Bundle();
        InputValueDialog fragment = new InputValueDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private String editValue;

    public InputValueDialog setEditValue(String editValue) {
        this.editValue = editValue;
        return this;
    }

    private InputType inputType = InputType.text;

    public InputValueDialog setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    private String title;

    public InputValueDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    private int maxLength = 20;

    public InputValueDialog setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    private String editHint;

    public InputValueDialog setEditHint(String editHint) {
        this.editHint = editHint;
        return this;
    }

    public InputValueDialog setValueRange(String range) {
        this.rangeValue = range;
        return this;
    }

    private ButtonClickListener doneCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_input_value, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TextUtils.isEmpty(rangeValue)) {
            TextView valueView = view.findViewById(R.id.value_range);
            valueView.setVisibility(View.VISIBLE);
            valueView.setText(rangeValue);
        }
        EditText editText = view.findViewById(R.id.et_value);
        editText.setText(editValue);
        editText.setHint(editHint);
        editText.setInputType(inputType.value);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        TextView titleTextView = view.findViewById(R.id.tv_title);
        titleTextView.setText(title);
        view.findViewById(R.id.v_done).setOnClickListener(v -> {
            String msg = editText.getText().toString();
            if (doneCallback != null) {
                doneCallback.onDone(InputValueDialog.this, msg);
            }
        });
        view.findViewById(R.id.v_cancel).setOnClickListener(v -> {
            dismissAllowingStateLoss();
            if (doneCallback != null) {
                doneCallback.cancel();
            }
        });
        setCancelable(false);
    }

    public enum InputType {
        numberSigned(0x00001002), text(0x00000001), numberDecimal(0x00002002);
        private final int value;

        InputType(int i) {
            value = i;
        }
    }

    public interface ButtonClickListener {
        void onDone(DialogFragment dialog, String txt);

        void cancel();
    }
}
