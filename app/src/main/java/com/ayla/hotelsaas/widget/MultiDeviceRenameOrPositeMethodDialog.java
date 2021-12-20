package com.ayla.hotelsaas.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

public class MultiDeviceRenameOrPositeMethodDialog extends DialogFragment {
    private DoneCallback doneCallback;
    public static MultiDeviceRenameOrPositeMethodDialog newInstance(DoneCallback doneCallback) {
        Bundle args = new Bundle();
        MultiDeviceRenameOrPositeMethodDialog fragment = new MultiDeviceRenameOrPositeMethodDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }


    private String positionSite;
    private String tv_rename_name;

    public MultiDeviceRenameOrPositeMethodDialog setPositionSite(String positionSite) {
        this.positionSite = positionSite;
        return this;
    }
    public MultiDeviceRenameOrPositeMethodDialog setTitle(String tv_rename_name) {
        this.tv_rename_name = tv_rename_name;
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
        return inflater.inflate(R.layout.dialog_multi_device_select_rename_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       TextView tv_name= view.findViewById(R.id.tv_rename_name);
       TextView tv_position= view.findViewById(R.id.tv_position_name);
       tv_name.setText(tv_rename_name);
        tv_position.setText(positionSite);

        view.findViewById(R.id.rl_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onNameDone(tv_name.getText().toString());
                    dismissAllowingStateLoss();
                }
            }
        });

        view.findViewById(R.id.rl_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onPositionDone(tv_position.getText().toString());
                    dismissAllowingStateLoss();
                }
            }
        });
        view.findViewById(R.id.rl_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }

    public interface DoneCallback {
        void onNameDone(String name);
        void onPositionDone(String postion);
    }
}
