package com.ayla.hotelsaas.widget.common_dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class SceneIconSelectDialog extends Dialog {
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private final List<View> rlLayouts = new ArrayList<>();
    int index = 0;

    public SceneIconSelectDialog(@NonNull Context context) {
        super(context);
    }

    private OnSelectSceneIconListener onSelectSceneIconListener;

    public void setOnSelectSceneIconListener(OnSelectSceneIconListener listener) {
        onSelectSceneIconListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_scene_icon_select, null);
        checkBoxes.add((CheckBox) view.findViewById(R.id.cb_function_checked_01));
        checkBoxes.add((CheckBox) view.findViewById(R.id.cb_function_checked_02));
        checkBoxes.add((CheckBox) view.findViewById(R.id.cb_function_checked_03));
        checkBoxes.add((CheckBox) view.findViewById(R.id.cb_function_checked_04));
        checkBoxes.add((CheckBox) view.findViewById(R.id.cb_function_checked_05));
        checkBoxes.add((CheckBox) view.findViewById(R.id.cb_function_checked_06));

        syncIconShow(0);
        View.OnClickListener layoutOnClickListener = v -> {
            String tag = (String) v.getTag();
            try {
                int tagInt = Integer.parseInt(tag);
                if (index == tagInt) return;
                index = tagInt;
                syncIconShow(index);
                setRlLayoutShow(index);
            } catch (NumberFormatException e) {
                Log.e("SceneIconSelectDialog", e.getMessage());
            }
        };
        rlLayouts.add(view.findViewById(R.id.rl_01));
        rlLayouts.add(view.findViewById(R.id.rl_02));
        rlLayouts.add(view.findViewById(R.id.rl_03));
        rlLayouts.add(view.findViewById(R.id.rl_04));
        rlLayouts.add(view.findViewById(R.id.rl_05));
        rlLayouts.add(view.findViewById(R.id.rl_06));
        for (int i = 0; i < rlLayouts.size(); i++) {
            if (i == 0) {
                rlLayouts.get(i).setSelected(true);
            }
            rlLayouts.get(i).setTag(String.valueOf(i));
            rlLayouts.get(i).setOnClickListener(layoutOnClickListener);
        }

        view.findViewById(R.id.rl_confirm).setOnClickListener(v -> {
            if (onSelectSceneIconListener != null) {
                onSelectSceneIconListener.onSelectIcon(index + 1);
            }
            dismiss();
        });
        view.findViewById(R.id.rl_cancel).setOnClickListener(v -> {
            dismiss();
        });
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            window.setBackgroundDrawable(null);
        }
    }

    private void setRlLayoutShow(int index) {
        for (int i = 0; i < rlLayouts.size(); i++) {
            rlLayouts.get(i).setSelected(index == i);
        }
    }

    public void syncIconShow(int index) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(index == i);
        }
    }

    public void initSelect(int index) {
        setRlLayoutShow(index);
        syncIconShow(index);
    }

    public interface OnSelectSceneIconListener {
        void onSelectIcon(int index);
    }
}
