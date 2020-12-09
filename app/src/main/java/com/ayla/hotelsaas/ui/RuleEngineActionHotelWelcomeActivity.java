package com.ayla.hotelsaas.ui;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

/**
 * 场景设备，酒店欢迎语动作页面
 */
public class RuleEngineActionHotelWelcomeActivity extends BaseMvpActivity {
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ruleengine_action_hotel_welcome;
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
        setResult(RESULT_OK);
        finish();
    }
}
