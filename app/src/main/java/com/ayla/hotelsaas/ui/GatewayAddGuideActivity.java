package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;

import butterknife.OnClick;

/**
 * 网关添加引导页面
 * 进入时必须带上cuId 、scopeId 、deviceName、deviceCategory。
 */
public class GatewayAddGuideActivity extends BaseMvpActivity {
    private final int REQUEST_CODE_FOR_ADD = 0X10;
    private final int REQUEST_CODE_FOR_DSN_INPUT = 0X11;
    private final int REQUEST_CODE_FOR_DSN_SCAN = 0X12;

    @Override
    protected GatewayAddGuidePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gateway_add_guide;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.bt_start_add)
    public void handleJump() {
        Intent mainActivity = new Intent(this, ScanActivity.class);
        startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_FOR_DSN_SCAN || requestCode == REQUEST_CODE_FOR_DSN_INPUT) && resultCode == RESULT_OK) {//获取到了DSN
            if (data != null) {
                String dsn = data.getStringExtra("result");
                if (!TextUtils.isEmpty(dsn)) {
                    if (dsn.startsWith("Lark_DSN:") && dsn.endsWith("##")) {
                        dsn = dsn.substring(9, dsn.length() - 2).trim();
                        Intent mainActivity = new Intent(this, GatewayAddActivity.class);
                        mainActivity.putExtra("dsn", dsn);
                        mainActivity.putExtras(getIntent());
                        startActivityForResult(mainActivity, REQUEST_CODE_FOR_ADD);
                        return;
                    }
                }
            }
            CustomToast.makeText(this, "无效的二维码", R.drawable.ic_toast_warming).show();
        } else if (requestCode == REQUEST_CODE_FOR_DSN_SCAN && resultCode == ScanActivity.RESULT_FOR_INPUT) {//扫码页面回退到手动输入页面
            Intent mainActivity = new Intent(this, GatewayAddDsnInputActivity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_INPUT);
        } else if (requestCode == REQUEST_CODE_FOR_ADD && resultCode == RESULT_OK) {//网关添加成功
            setResult(RESULT_OK);
            finish();
        }
    }
}
