package com.ayla.hotelsaas.widget.common_dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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

public class CustomBottomAlarmDialog extends DialogFragment {

    @Deprecated
    public static CustomBottomAlarmDialog newInstance(Callback doneCallback) {

        Bundle args = new Bundle();
        CustomBottomAlarmDialog fragment = new CustomBottomAlarmDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    public static CustomBottomAlarmDialog newInstance() {

        Bundle args = new Bundle();

        CustomBottomAlarmDialog fragment = new CustomBottomAlarmDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private Callback doneCallback;

    public CustomBottomAlarmDialog setDoneCallback(Callback doneCallback) {
        this.doneCallback = doneCallback;
        return this;
    }

    public enum Style {
        STYLE_NORMAL, STYLE_SINGLE_BUTTON,
    }

    public enum Location {
        LEFT, CENTER,RIGHT
    }
    private Style style = Style.STYLE_NORMAL;
    private Location location= Location.CENTER;

    public CustomBottomAlarmDialog setStyle(Style style) {
        this.style = style;
        return this;
    }

    public CustomBottomAlarmDialog setFontLocation(Location location) {
        this.location = location;
        return this;
    }

    private String title;

    public CustomBottomAlarmDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    private String content;

    public CustomBottomAlarmDialog setContent(String content) {
        this.content = content;
        return this;
    }

    private String cancelText;

    public CustomBottomAlarmDialog setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    private String ensureText;

    public CustomBottomAlarmDialog setEnsureText(String ensureText) {
        this.ensureText = ensureText;
        return this;
    }


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
        if (style == Style.STYLE_SINGLE_BUTTON) {
            return inflater.inflate(R.layout.dialog_custom_alarm_single_button, container, false);
        } else {
            return inflater.inflate(R.layout.dialog_huanyingyu_notice, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView ensureTextView = view.findViewById(R.id.v_done);
        ensureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onDone(CustomBottomAlarmDialog.this);
                }
            }
        });
        if (!TextUtils.isEmpty(ensureText)) {
            ensureTextView.setText(ensureText);
        }
        TextView cancelTextView = view.findViewById(R.id.v_cancel);
        if (cancelTextView != null) {
            cancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (doneCallback != null) {
                        doneCallback.onCancel(CustomBottomAlarmDialog.this);
                    }
                }
            });
            if (!TextUtils.isEmpty(cancelText)) {
                cancelTextView.setText(cancelText);
            }
        }
        TextView titleTextView = view.findViewById(R.id.tv_title);
        if (TextUtils.isEmpty(title)) {
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
        TextView contentTextView = view.findViewById(R.id.tv_content);
        if (location== Location.LEFT){
            contentTextView.setGravity(Gravity.LEFT);
        }
        contentTextView.setText(content);
    }

    public interface Callback {
        void onDone(CustomBottomAlarmDialog dialog);

        void onCancel(CustomBottomAlarmDialog dialog);
    }
}
