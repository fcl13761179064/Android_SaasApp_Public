package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.utils.CustomToast;

import butterknife.BindView;

public class ApDistributeGuideActivity extends BasicActivity {

    @BindView(R.id.jd_btn_next)
    public Button jd_btn_next;

    @BindView(R.id.jd_cb_confirm)
    public CheckBox jd_cb_confirm;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ap_distribute_guide;
    }

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected void initView() {

        jd_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!jd_cb_confirm.isChecked()) {
                    CustomToast.makeText(ApDistributeGuideActivity.this, "请先阅读设备配网引导", R.drawable.ic_toast_warning);
                } else {
                    Intent intent = new Intent(ApDistributeGuideActivity.this, ApWifiConnectToA2GagtewayActivity.class);
                    intent.putExtras(getIntent());
                    startActivityForResult(intent, 1100);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1100 && resultCode == RESULT_OK)
            finish();
    }

    @Override
    protected void initListener() {

    }
}
