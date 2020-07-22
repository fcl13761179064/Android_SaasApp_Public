package com.ayla.hotelsaas.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class CustomAlarmDialog extends DialogFragment {
    public static CustomAlarmDialog newInstance(Callback doneCallback, String title, String content) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        CustomAlarmDialog fragment = new CustomAlarmDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private Callback doneCallback;

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
        view.findViewById(R.id.v_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onDone(CustomAlarmDialog.this);
                }
            }
        });
        view.findViewById(R.id.v_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onDone(CustomAlarmDialog.this);
                }
            }
        });
        TextView titleTextView = view.findViewById(R.id.tv_title);
        TextView contentTextView = view.findViewById(R.id.tv_content);
        titleTextView.setText(getArguments().getString("title"));
        contentTextView.setText(getArguments().getString("content"));
    }

    public interface Callback {
        void onDone(CustomAlarmDialog dialog);

        void onCancel(CustomAlarmDialog dialog);
    }
}
