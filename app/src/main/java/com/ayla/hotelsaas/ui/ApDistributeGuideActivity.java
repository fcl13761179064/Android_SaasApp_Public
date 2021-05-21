package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;

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
                if(!jd_cb_confirm.isChecked()){
                    CustomToast.makeText(ApDistributeGuideActivity.this, "请先阅读设备配网引导", R.drawable.ic_warning);
                }else {
                    Intent intent = new Intent(ApDistributeGuideActivity.this,ApWifiConnectToA2GagtewayActivity.class);
                    intent.putExtras(getIntent());
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    protected void initListener() {

    }
}
