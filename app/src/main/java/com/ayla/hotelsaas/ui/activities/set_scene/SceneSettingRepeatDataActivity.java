package com.ayla.hotelsaas.ui.activities.set_scene;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.utils.CustomToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 场景生效时间段 重复日期 选择页面
 * int[] enable_position  已选中的星期，从1 开始。
 */
public class SceneSettingRepeatDataActivity extends BasicActivity {
    @BindViews({R.id.cb_01, R.id.cb_02, R.id.cb_03, R.id.cb_04, R.id.cb_05, R.id.cb_06, R.id.cb_07})
    List<CheckBox> mCheckBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] enable_positions = getIntent().getIntArrayExtra("enable_position");
        if (enable_positions != null) {
            for (int enable_position : enable_positions) {
                int index = enable_position - 1;
                if (index >= 0 && index <= mCheckBoxes.size() - 1) {
                    mCheckBoxes.get(index).setChecked(true);
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_setting_repeat_data;
    }

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void appBarLeftIvClicked() {
        super.appBarLeftIvClicked();
        onBackPressed();
    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        List<Integer> enables = new ArrayList<>();
        for (int i = 0; i < mCheckBoxes.size(); i++) {
            if (mCheckBoxes.get(i).isChecked()) {
                enables.add(i + 1);
            }
        }
        if (enables.size() == 0) {
            CustomToast.makeText(this, "最少选择一天", R.drawable.ic_toast_warning);
            return;
        }
        int[] result = new int[enables.size()];
        for (int i = 0; i < enables.size(); i++) {
            result[i] = enables.get(i);
        }
        setResult(RESULT_OK, new Intent().putExtra("enable_position", result));
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
        CheckBox checkBox = mCheckBoxes.get(selected_position - 1);
        checkBox.setChecked(!checkBox.isChecked());
    }
}
