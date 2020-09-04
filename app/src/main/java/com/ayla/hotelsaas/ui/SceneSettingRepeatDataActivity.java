package com.ayla.hotelsaas.ui;

import android.view.View;
import android.widget.CheckBox;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import java.util.List;

import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 场景生效时间段 重复日期 选择页面
 */
public class SceneSettingRepeatDataActivity extends BaseMvpActivity {
    @BindViews({R.id.cb_01, R.id.cb_02, R.id.cb_03, R.id.cb_04, R.id.cb_05, R.id.cb_06, R.id.cb_07})
    List<CheckBox> mCheckBoxes;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_setting_repeat_data;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void appBarRightIvClicked() {
        super.appBarRightIvClicked();
        finish();
    }

    @OnClick({R.id.rl_01, R.id.rl_02, R.id.rl_03, R.id.rl_04, R.id.rl_05, R.id.rl_06, R.id.rl_07})
    void handleSelect(View view) {
        int selected_position = 1;
        switch (view.getId()) {
            case R.id.rl_01:
                selected_position = 1;
                break;
            case R.id.rl_02:
                selected_position = 2;
                break;
            case R.id.rl_03:
                selected_position = 3;
                break;
            case R.id.rl_04:
                selected_position = 4;
                break;
            case R.id.rl_05:
                selected_position = 5;
                break;
            case R.id.rl_06:
                selected_position = 6;
                break;
            case R.id.rl_07:
                selected_position = 7;
                break;
        }
        syncCheckBox(selected_position);
    }

    private void syncCheckBox(int selected_position) {
        for (int i = 0; i < mCheckBoxes.size(); i++) {
            CheckBox checkBox = mCheckBoxes.get(i);
            if (i+1 == selected_position) {
                checkBox.setChecked(true);
            }else{
                checkBox.setChecked(false);
            }
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
