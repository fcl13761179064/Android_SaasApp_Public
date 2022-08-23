package com.ayla.hotelsaas.widget.common_dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.utils.CustomToast;

public class SceneRuleMoreRenameDialog extends DialogFragment {

    public static SceneRuleMoreRenameDialog newInstance(DoneCallback doneCallback) {

        Bundle args = new Bundle();
        SceneRuleMoreRenameDialog fragment = new SceneRuleMoreRenameDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private String editValue;

    public SceneRuleMoreRenameDialog setEditValue(String editValue) {
        this.editValue = editValue;
        return this;
    }

    private InputType inputType = InputType.text;

    public SceneRuleMoreRenameDialog setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    private String title;

    public SceneRuleMoreRenameDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    private int maxLength = 20;

    public SceneRuleMoreRenameDialog setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    private String editHint;

    public SceneRuleMoreRenameDialog setEditHint(String editHint) {
        this.editHint = editHint;
        return this;
    }

    private DoneCallback doneCallback;


    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        params.windowAnimations = R.style.main_menu_animstyle;
        params.width = (int) ((getResources().getDisplayMetrics().widthPixels) * 0.8);
        params.gravity = Gravity.CENTER;
        getDialog().getWindow().setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_scene_rule_more_rename, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText editText = view.findViewById(R.id.et_dsn);
        if (!TextUtils.isEmpty(editValue)) {
            editText.setText(editValue);
        }
        if (!TextUtils.isEmpty(editHint)) {
            editText.setHint(editHint);
        }
        editText.setInputType(inputType.value);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        TextView titleTextView = view.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            titleTextView.setText(title);
        }
        ImageView delete_desc = view.findViewById(R.id.delete_desc);
        delete_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        view.findViewById(R.id.rl_confire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = editText.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    CustomToast.makeText(getContext(), "名称不能为空", R.drawable.ic_toast_warning);
                    return;
                }
                if (doneCallback != null) {
                    doneCallback.onDone(SceneRuleMoreRenameDialog.this, msg);
                }
                dismissAllowingStateLoss();
            }
        });
        view.findViewById(R.id.rl_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onCancel(SceneRuleMoreRenameDialog.this);
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
        void onDone(DialogFragment dialog, String txt);

        void onCancel(DialogFragment dialog);
    }
}
