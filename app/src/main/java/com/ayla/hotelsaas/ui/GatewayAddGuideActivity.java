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

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 网关添加引导页面
 * 进入时必须带上cuId 、scopeId 、deviceName。
 */
public class GatewayAddGuideActivity extends BaseMvpActivity {
    @BindView(R.id.iv_01)
    public ImageView mImageView;
    @BindView(R.id.step_1_view)
    public View mStep1View;
    @BindView(R.id.step_2_view)
    public View mStep2View;
    @BindView(R.id.bt_start_add)
    public Button mButton;
    @BindView(R.id.et_dsn)
    public EditText mDSNEditText;

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

    private boolean step2;

    @OnClick(R.id.bt_start_add)
    public void handleJump() {
        if (!step2) {
            step2 = true;
            mStep1View.setVisibility(View.GONE);
            mStep2View.setVisibility(View.VISIBLE);
            mImageView.setImageResource(R.drawable.ic_device_add_start_2);
            mButton.setText("下一步");
        } else {
            String dsn = mDSNEditText.getText().toString();
            if (dsn.length() == 0) {
                CustomToast.makeText(this, "DSN输入不能为空", R.drawable.ic_toast_warming).show();
            } else {
                Intent mainActivity = new Intent(this, GatewayAddActivity.class);
                mainActivity.putExtra("dsn", dsn);
                mainActivity.putExtras(getIntent());
                startActivityForResult(mainActivity, 0);
            }
        }
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
