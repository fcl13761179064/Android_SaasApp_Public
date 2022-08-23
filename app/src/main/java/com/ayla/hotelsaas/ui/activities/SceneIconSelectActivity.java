package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class SceneIconSelectActivity extends BaseMvpActivity {
    @BindViews({R.id.cb_function_checked_01, R.id.cb_function_checked_02, R.id.cb_function_checked_03,
            R.id.cb_function_checked_04, R.id.cb_function_checked_05, R.id.cb_function_checked_06})
    List<CheckBox> checkBoxes;

    @BindView(R.id.v_cancel)
    TextView v_cancel;
    @BindView(R.id.v_done)
    TextView v_done;
    int index = 1;
    private final List<View> rlLayouts = new ArrayList<>();

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_icon_select;
    }

    @Override
    protected void initView() {
        overridePendingTransition( R.anim.photo_dialog_in_anim,R.anim.photo_dialog_out_anim);
    }

    @Override
    protected void initListener() {
        v_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("index", index));
                finish();
            }
        });

        v_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }
        int index = getIntent().getIntExtra("index", 1);
        rlLayouts.add(findViewById(R.id.rl_01));
        rlLayouts.add(findViewById(R.id.rl_02));
        rlLayouts.add(findViewById(R.id.rl_03));
        rlLayouts.add(findViewById(R.id.rl_04));
        rlLayouts.add(findViewById(R.id.rl_05));
        rlLayouts.add(findViewById(R.id.rl_06));
        syncIconShow(index);
        setRlLayoutShow(index - 1);
    }


    private void syncIconShow(int index) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (index == i + 1) {
                checkBoxes.get(i).setChecked(true);

            } else {
                checkBoxes.get(i).setChecked(false);
            }
        }
    }

    private void setRlLayoutShow(int index) {
        for (int i = 0; i < rlLayouts.size(); i++) {
            rlLayouts.get(i).setSelected(index == i);
        }
    }

    @OnClick({R.id.rl_01, R.id.rl_02, R.id.rl_03,
            R.id.rl_04, R.id.rl_05, R.id.rl_06})
    void handleClicks(View view) {

        switch (view.getId()) {
            case R.id.rl_01:
                index = 1;
                break;
            case R.id.rl_02:
                index = 2;
                break;
            case R.id.rl_03:
                index = 3;
                break;
            case R.id.rl_04:
                index = 4;
                break;
            case R.id.rl_05:
                index = 5;
                break;
            case R.id.rl_06:
                index = 6;
                break;
        }
        syncIconShow(index);
        setRlLayoutShow(index - 1);
    }
}
