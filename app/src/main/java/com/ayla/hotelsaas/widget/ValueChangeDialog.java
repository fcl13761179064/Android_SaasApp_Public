package com.ayla.hotelsaas.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
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

public class ValueChangeDialog extends DialogFragment {

    public static ValueChangeDialog newInstance(DoneCallback doneCallback) {

        Bundle args = new Bundle();
        ValueChangeDialog fragment = new ValueChangeDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private String editValue;

    public ValueChangeDialog setEditValue(String editValue) {
        this.editValue = editValue;
        return this;
    }

    private InputType inputType = InputType.text;

    public ValueChangeDialog setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    private String title;

    public ValueChangeDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    private int maxLength = 20;

    public ValueChangeDialog setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    private String editHint;

    public ValueChangeDialog setEditHint(String editHint) {
        this.editHint = editHint;
        return this;
    }

    private DoneCallback doneCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_scene_set_name, container, false);
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
        titleTextView.setText(title);
        view.findViewById(R.id.v_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = editText.getText().toString();
                if (doneCallback != null) {
                    doneCallback.onDone(ValueChangeDialog.this, msg);
                }
            }
        });
        view.findViewById(R.id.v_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }

    public enum InputType {
        numberSigned(0x00001002), text(0x00000001);

        private final int value;

        InputType(int i) {
            value = i;
        }
    }

    public interface DoneCallback {
        void onDone(DialogFragment dialog, String txt);
    }
}
