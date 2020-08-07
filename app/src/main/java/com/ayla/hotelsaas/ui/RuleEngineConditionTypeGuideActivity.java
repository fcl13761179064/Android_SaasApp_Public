package com.ayla.hotelsaas.ui;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import butterknife.OnClick;

/**
 * 场景设备，选择条件类型
 */
public class RuleEngineConditionTypeGuideActivity extends BaseMvpActivity {
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ruleengine_condition_type_guide;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.rl_device_changed)
    public void jumpDeviceSelect() {
        Intent mainActivity = new Intent(this, SceneSettingDeviceSelectActivity.class);
        mainActivity.putExtra("type",0);
        startActivityForResult(mainActivity, 0);
    }

    @OnClick(R.id.rl_type_one_key)
    public void handleOneKeySelect() {
        setResult(RESULT_OK);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
