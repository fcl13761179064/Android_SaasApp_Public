package com.ayla.hotelsaas.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class CustomAlarmDialog extends DialogFragment {
    public static CustomAlarmDialog newInstance(Callback doneCallback) {

        Bundle args = new Bundle();
        CustomAlarmDialog fragment = new CustomAlarmDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private Callback doneCallback;

    private String title;

    public CustomAlarmDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    private String content;

    public CustomAlarmDialog setContent(String content) {
        this.content = content;
        return this;
    }

    private String cancelText;

    public CustomAlarmDialog setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    private String ensureText;

    public CustomAlarmDialog setEnsureText(String ensureText) {
        this.ensureText = ensureText;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_custom_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView ensureTextView = view.findViewById(R.id.v_done);
        ensureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onDone(CustomAlarmDialog.this);
                }
            }
        });
        if (!TextUtils.isEmpty(ensureText)) {
            ensureTextView.setText(ensureText);
        }
        TextView cancelTextView = view.findViewById(R.id.v_cancel);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onCancel(CustomAlarmDialog.this);
                }
            }
        });
        if (!TextUtils.isEmpty(cancelText)) {
            cancelTextView.setText(cancelText);
        }
        TextView titleTextView = view.findViewById(R.id.tv_title);
        TextView contentTextView = view.findViewById(R.id.tv_content);
        if (TextUtils.isEmpty(title)) {
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
        contentTextView.setText(content);
    }

    public interface Callback {
        void onDone(CustomAlarmDialog dialog);

        void onCancel(CustomAlarmDialog dialog);
    }
}
