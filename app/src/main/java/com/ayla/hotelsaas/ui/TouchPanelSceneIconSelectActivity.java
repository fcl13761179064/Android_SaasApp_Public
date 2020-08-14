package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import java.util.List;

import butterknife.BindViews;
import butterknife.OnClick;

public class TouchPanelSceneIconSelectActivity extends BaseMvpActivity {
    @BindViews({R.id.cb_function_checked_01, R.id.cb_function_checked_02, R.id.cb_function_checked_03,
            R.id.cb_function_checked_04, R.id.cb_function_checked_05, R.id.cb_function_checked_06})
    List<CheckBox> checkBoxes;

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

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = getIntent().getIntExtra("index", 1);
        syncIconShow(index);
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

    @OnClick({R.id.rl_01, R.id.rl_02, R.id.rl_03,
            R.id.rl_04, R.id.rl_05, R.id.rl_06})
    void handleClicks(View view) {
        int index = 1;
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
        setResult(RESULT_OK, new Intent().putExtra("index", index));
        finish();
    }
}
