package com.ayla.hotelsaas.widget.common_dialog;

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
import com.ayla.hotelsaas.utils.SoftInputUtil;

public class MarchShallRenameDialog extends DialogFragment {

    public static MarchShallRenameDialog newInstance(DoneCallback doneCallback) {

        Bundle args = new Bundle();
        MarchShallRenameDialog fragment = new MarchShallRenameDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private String editValue;

    public MarchShallRenameDialog setEditValue(String editValue) {
        this.editValue = editValue;
        return this;
    }

    private InputType inputType = InputType.text;

    public MarchShallRenameDialog setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    private String title;

    public MarchShallRenameDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    private int maxLength = 20;

    public MarchShallRenameDialog setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    private String editHint;

    public MarchShallRenameDialog setEditHint(String editHint) {
        this.editHint = editHint;
        return this;
    }

    private boolean showTips = true;

    public MarchShallRenameDialog setShowTips(boolean show) {
        this.showTips = show;
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
        return inflater.inflate(R.layout.dialog_marshall_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View tipsView = view.findViewById(R.id.tv_tips);
        if (showTips) tipsView.setVisibility(View.VISIBLE);
        else tipsView.setVisibility(View.GONE);
        EditText editText = view.findViewById(R.id.et_dsn);
        editText.setHint(editHint);
        editText.setText(editValue);
        editText.setInputType(inputType.value);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        TextView titleTextView = view.findViewById(R.id.tv_title);
        TextView empty_notice = view.findViewById(R.id.empty_notice);
        titleTextView.setText(title);
        view.findViewById(R.id.rl_confire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = editText.getText().toString();
                if (doneCallback != null) {
                    doneCallback.onDone(MarchShallRenameDialog.this, msg, empty_notice);
                }
            }
        });
        view.findViewById(R.id.rl_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onCancel(MarchShallRenameDialog.this);
                }
                dismissAllowingStateLoss();
            }
        });
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();
        SoftInputUtil.showSoftInput(editText);
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
