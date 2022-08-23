package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.widget.EditText;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.utils.CustomToast;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 网关添加DSN输入页面
 * 进入时必须带上cuId 、scopeId 、deviceName。
 */
public class GatewayAddDsnInputActivity extends BaseMvpActivity {
    @BindView(R.id.et_dsn)
    public EditText mDSNEditText;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gateway_add_dsn_input;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.bt_start_add)
    public void handleJump() {
        String dsn = mDSNEditText.getText().toString();
        if (dsn.length() == 0) {
            CustomToast.makeText(this, "设备ID号输入不能为空", R.drawable.ic_toast_warning);
        } else {
            setResult(RESULT_OK, new Intent().putExtra("result", dsn));
            finish();
        }
    }
}
