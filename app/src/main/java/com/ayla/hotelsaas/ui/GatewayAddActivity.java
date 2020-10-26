package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.GlideApp;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddPresenter;
import com.ayla.hotelsaas.mvp.view.GatewayAddView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 网关添加页面
 * 参数 int networkType 1、鸿雁-插网线网关配网2、顺舟-插网线网关配网3、艾拉zigbee配网 4、鸿雁节点 5、艾拉wifi设备
 * <p>
 * Ayla网关添加时，
 * 进入时必须带上dsn、cuId 、scopeId、deviceName、deviceCategory。
 * <p>
 * 鸿雁网关添加时，
 * 进入时必须带上cuId 、scopeId、deviceName、deviceCategory、HYproductKey、HYdeviceName。
 */
public class GatewayAddActivity extends BaseMvpActivity<GatewayAddView, GatewayAddPresenter> implements GatewayAddView {
    @BindView(R.id.iv_01)
    public ImageView mImageView;
    @BindView(R.id.tv_loading)
    public TextView mLoadingTextView;
    @BindView(R.id.tv_bind_progress)
    public TextView mBindProgressTextView;
    @BindView(R.id.bt_bind)
    public Button mFinishButton;
    @BindView(R.id.ll_success_name_input)
    View ll_success_name_input;
    @BindView(R.id.et_input)
    EditText mEditText;

    @Override
    protected GatewayAddPresenter initPresenter() {
        return new GatewayAddPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gateway_add;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }


    private int bindTag = 0;//0:绑定中 1:绑定成功 -1:绑定失败

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBind();
    }

    private void startBind() {
        bindTag = 0;
        refreshBindShow();
        int networkType = getIntent().getIntExtra("networkType", 0);
        if (networkType == 1) {//鸿雁网关
            mPresenter.bindHongYanGateway((AApplication) getApplication(),
                    getIntent().getIntExtra("cuId", 0),
                    getIntent().getLongExtra("scopeId", 0),
                    getIntent().getStringExtra("deviceCategory"),
                    getIntent().getStringExtra("deviceName"),
                    getIntent().getStringExtra("HYproductKey"),
                    getIntent().getStringExtra("HYdeviceName"));
        } else if (networkType == 2) {//顺舟网关
            mPresenter.bindAylaGateway(
                    getIntent().getStringExtra("dsn"),
                    getIntent().getIntExtra("cuId", 0),
                    getIntent().getLongExtra("scopeId", 0),
                    getIntent().getStringExtra("deviceCategory"),
                    getIntent().getStringExtra("deviceName"));
        }
    }

    private String bindedDeviceName;
    private String bindedDeviceId;

    @Override
    public void bindSuccess(String deviceId, String deviceName) {
        bindedDeviceId = deviceId;
        bindedDeviceName = deviceName;
        mEditText.setText(deviceName);
        setResult(RESULT_OK);
        bindTag = 1;
        refreshBindShow();
    }

    private String errorMsg;

    @Override
    public void bindFailed(String msg) {
        errorMsg = msg;
        bindTag = -1;
        refreshBindShow();
    }

    @Override
    public void renameSuccess(String nickName) {
        finish();
    }

    @Override
    public void renameFailed(String code, String msg) {
        if ("140000".equals(code)) {
            CustomToast.makeText(this, "该名称不能重复使用", R.drawable.ic_toast_warming).show();
        } else {
            CustomToast.makeText(MyApplication.getContext(), "修改失败", R.drawable.ic_toast_warming).show();
        }
    }

    @OnClick(R.id.bt_bind)
    public void handleButton() {
        if (bindTag == 1) {
            String newName = mEditText.getText().toString();
            if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
                CustomToast.makeText(MyApplication.getContext(), "设备名称不能为空", R.drawable.ic_toast_warming).show();
                return;
            }
            if (TextUtils.equals(newName, bindedDeviceName)) {
                finish();
                return;
            }
            mPresenter.deviceRenameMethod(bindedDeviceId, newName);
        } else if (bindTag == -1) {
            startBind();
        }
    }

    /**
     * 根据bindTag刷新UI
     */
    private void refreshBindShow() {
        switch (bindTag) {
            case 1:
                mImageView.setImageResource(R.drawable.ic_device_bind_success);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mBindProgressTextView.setVisibility(View.INVISIBLE);
                ll_success_name_input.setVisibility(View.VISIBLE);
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("完成");
                break;
            case -1:
                mImageView.setImageResource(R.drawable.ic_device_bind_failed);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mBindProgressTextView.setVisibility(View.VISIBLE);
                ll_success_name_input.setVisibility(View.INVISIBLE);
                mBindProgressTextView.setText(TextUtils.isEmpty(errorMsg) ? "设备绑定失败\n请再检查设备状态后重试" : errorMsg);
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("重试");
                break;
            default:
                GlideApp.with(mImageView).load(R.drawable.ic_device_bind_loading).into(mImageView);
                mLoadingTextView.setVisibility(View.VISIBLE);
                ll_success_name_input.setVisibility(View.INVISIBLE);
                mBindProgressTextView.setVisibility(View.VISIBLE);
                mBindProgressTextView.setText("最长可能需要1分钟，请耐心等待");
                mFinishButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

}
