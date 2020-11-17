package com.ayla.hotelsaas.ui;

import android.content.Intent;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import butterknife.OnClick;

/**
 * 场景设备，选择动作类型
 */
public class RuleEngineActionTypeGuideActivity extends BaseMvpActivity {
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ruleengine_action_type_guide;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.rl_device_changed)
    public void jumpDeviceSelect() {
        setResult(RESULT_OK, new Intent().putExtra("delay", false));
        finish();
    }

    @OnClick(R.id.rl_type_one_key)
    public void handleOneKeySelect() {
        setResult(RESULT_OK, new Intent().putExtra("delay", true));
        finish();
    }
}
