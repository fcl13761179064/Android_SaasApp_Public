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
                    doneCallback.onCancel(CustomAlarmDialog.this);
                }
            }
        });
        TextView titleTextView = view.findViewById(R.id.tv_title);
        TextView contentTextView = view.findViewById(R.id.tv_content);
        if(TextUtils.isEmpty(title)){
            titleTextView.setVisibility(View.GONE);
        }else{
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
