package com.ayla.hotelsaas.widget.common_dialog;

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
import com.ayla.hotelsaas.bean.DeviceTemplateBean;

public class SceneFuctionSelectAttributionDialog extends DialogFragment {

    private DeviceTemplateBean.AttributesBean attributesBean;
    private boolean autoJump;
    private int conditonTypeOrActionType;

    @Deprecated
    public static SceneFuctionSelectAttributionDialog newInstance(Callback doneCallback) {

        Bundle args = new Bundle();
        SceneFuctionSelectAttributionDialog fragment = new SceneFuctionSelectAttributionDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    public static SceneFuctionSelectAttributionDialog newInstance() {
        Bundle args = new Bundle();
        SceneFuctionSelectAttributionDialog fragment = new SceneFuctionSelectAttributionDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private Callback doneCallback;

    public SceneFuctionSelectAttributionDialog setDoneCallback(Callback doneCallback) {
        this.doneCallback = doneCallback;
        return this;
    }

    public enum Style {
        STYLE_NORMAL, STYLE_SINGLE_BUTTON,
    }

    public enum Location {
        LEFT, CENTER, RIGHT
    }

    private Style style = Style.STYLE_NORMAL;
    private Location location = Location.LEFT;

    public SceneFuctionSelectAttributionDialog setStyle(Style style) {
        this.style = style;
        return this;
    }

    private String title;

    public SceneFuctionSelectAttributionDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public SceneFuctionSelectAttributionDialog setDataAttribute(DeviceTemplateBean.AttributesBean attributesBean) {
        this.attributesBean = attributesBean;
        return this;
    }

    public SceneFuctionSelectAttributionDialog setAutoJump(boolean b) {
        this.autoJump = b;
        return this;
    }


    public SceneFuctionSelectAttributionDialog setConditonTypeOrActionType(int conditonTypeOrActionType) {
        this.conditonTypeOrActionType = conditonTypeOrActionType;
        return this;
    }

    private String cancelText;

    public SceneFuctionSelectAttributionDialog setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    private String ensureText;

    public SceneFuctionSelectAttributionDialog setEnsureText(String ensureText) {
        this.ensureText = ensureText;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.scene_function_select_attribute_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView ensureTextView = view.findViewById(R.id.v_done);
        ensureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onDone(SceneFuctionSelectAttributionDialog.this);
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
                        doneCallback.onCancel(SceneFuctionSelectAttributionDialog.this);
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
    }

    public interface Callback {
        void onDone(SceneFuctionSelectAttributionDialog dialog);

        void onCancel(SceneFuctionSelectAttributionDialog dialog);
    }
}
