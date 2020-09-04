package com.ayla.hotelsaas.ui;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.widget.TimePickerDialog;

import butterknife.OnClick;

/**
 * 场景设置生效时间段
 */
public class SceneSettingEnableTimeActivity extends BaseMvpActivity {
    private final int REQUEST_CODE_REPEAT_DATA = 0X10;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_setting_enable_time;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        finish();
    }

    @OnClick(R.id.ll_repeat_data)
    void handleJumpRepeatData() {
        Intent intent = new Intent(this, SceneSettingRepeatDataActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REPEAT_DATA);
    }

    @OnClick(R.id.ll_start_time)
    void handleStartTime() {
        TimePickerDialog.newInstance().show(getSupportFragmentManager(), "time");
    }

    @OnClick(R.id.ll_end_time)
    void handleEndTime() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
