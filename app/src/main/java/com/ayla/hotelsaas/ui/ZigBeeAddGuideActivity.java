package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;
import com.ayla.hotelsaas.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 网关添加引导页面
 */
public class ZigBeeAddGuideActivity extends BaseMvpActivity {
    @Override
    protected GatewayAddGuidePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zigbee_add_guide;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }


    @OnClick(R.id.bt_start_add)
    public void handleJump() {
        Intent mainActivity = new Intent(this, ZigBeeAddActivity.class);
        startActivityForResult(mainActivity, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {//网关绑定页面关闭，告知绑定成功，本引导页面自动关闭。
            setResult(RESULT_OK);
            finish();
        }
    }
}
