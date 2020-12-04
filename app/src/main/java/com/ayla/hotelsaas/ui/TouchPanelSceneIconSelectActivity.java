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

/**
 * 进入时带入参数int "index"，对应按钮的下标 ，从0开始。
 */
public class TouchPanelSceneIconSelectActivity extends BaseMvpActivity {
    @BindViews({R.id.cb_function_checked_01, R.id.cb_function_checked_02, R.id.cb_function_checked_03,
            R.id.cb_function_checked_04, R.id.cb_function_checked_05, R.id.cb_function_checked_06,
            R.id.cb_function_checked_07, R.id.cb_function_checked_08, R.id.cb_function_checked_09,
            R.id.cb_function_checked_10, R.id.cb_function_checked_11, R.id.cb_function_checked_12})
    List<CheckBox> checkBoxes;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_touchpanel_scene_icon_select;
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
        int index = getIntent().getIntExtra("index", 0);
        syncIconShow(index);
    }

    private void syncIconShow(int index) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (index == i) {
                checkBoxes.get(i).setChecked(true);
            } else {
                checkBoxes.get(i).setChecked(false);
            }
        }
    }

    @OnClick({R.id.rl_01, R.id.rl_02, R.id.rl_03,
            R.id.rl_04, R.id.rl_05, R.id.rl_06,
            R.id.rl_07, R.id.rl_08, R.id.rl_09,
            R.id.rl_10, R.id.rl_11, R.id.rl_12})
    void handleClicks(View view) {
        int index = 0;
        switch (view.getId()) {
            case R.id.rl_01:
                index = 0;
                break;
            case R.id.rl_02:
                index = 1;
                break;
            case R.id.rl_03:
                index = 2;
                break;
            case R.id.rl_04:
                index = 3;
                break;
            case R.id.rl_05:
                index = 4;
                break;
            case R.id.rl_06:
                index = 5;
                break;
            case R.id.rl_07:
                index = 6;
                break;
            case R.id.rl_08:
                index = 7;
                break;
            case R.id.rl_09:
                index = 8;
                break;
            case R.id.rl_10:
                index = 9;
                break;
            case R.id.rl_11:
                index = 10;
                break;
            case R.id.rl_12:
                index = 11;
                break;
        }
        syncIconShow(index);
        setResult(RESULT_OK, new Intent().putExtra("index", index));
        finish();
    }
}
