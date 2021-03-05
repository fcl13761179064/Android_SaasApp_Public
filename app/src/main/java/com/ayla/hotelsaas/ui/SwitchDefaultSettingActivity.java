package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.data.net.SelfMsgException;
import com.ayla.hotelsaas.databinding.ActivitySwitchDefaultSettingBinding;
import com.ayla.hotelsaas.mvp.present.SwitchDefaultSettingPresenter;
import com.ayla.hotelsaas.mvp.view.SwitchDefaultSettingView;

/**
 * 参数
 * String propertyCode
 * String deviceId
 */
public class SwitchDefaultSettingActivity extends BaseMvpActivity<SwitchDefaultSettingView, SwitchDefaultSettingPresenter> implements SwitchDefaultSettingView {
    private ActivitySwitchDefaultSettingBinding mBinding;

    @Override
    protected SwitchDefaultSettingPresenter initPresenter() {
        return new SwitchDefaultSettingPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivitySwitchDefaultSettingBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        mBinding.rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncShow(true);
            }
        });
        mBinding.rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncShow(false);
            }
        });
    }

    String deviceId, propertyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getStringExtra("deviceId");
        propertyCode = getIntent().getStringExtra("propertyCode");
        mPresenter.getPropertyDataPoint(deviceId, propertyCode);
    }

    @Override
    public void showData(String propertyValue) {
        if ("3".equals(propertyValue)) {//3：默认开启    4：默认关闭
            syncShow(true);
        } else {
            syncShow(false);
        }
    }

    @Override
    public void updateSuccess() {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
    }

    private void syncShow(boolean isOn) {
        if (isOn) {
            mBinding.cbFunctionChecked1.setChecked(true);
            mBinding.cbFunctionChecked2.setChecked(false);
        } else {
            mBinding.cbFunctionChecked1.setChecked(false);
            mBinding.cbFunctionChecked2.setChecked(true);
        }
    }

    @Override
    protected void appBarRightTvClicked() {
        if (mBinding.cbFunctionChecked1.isChecked()) {
            mPresenter.updateProperty(deviceId, propertyCode, "3");
            return;
        }

        if (mBinding.cbFunctionChecked2.isChecked()) {
            mPresenter.updateProperty(deviceId, propertyCode, "4");
            return;
        }
        showError(new SelfMsgException("必须选中一个", null));
    }
}
