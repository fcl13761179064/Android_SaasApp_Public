package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.aliyun.alink.business.devicecenter.api.discovery.IOnDeviceTokenGetListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.GatewayAddGuideView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 鸿雁网关添加页面
 * 进入时必须带上dsn、cuId 、scopeId。
 * 樊春雷
 */
public class HongyanGatewayAddActivity extends BaseMvpActivity<GatewayAddGuideView, GatewayAddGuidePresenter> implements GatewayAddGuideView {
    @BindView(R.id.iv_01)
    public LottieAnimationView mImageView;
    @BindView(R.id.tv_loading)
    public TextView mLoadingTextView;
    @BindView(R.id.tv_bind_progress)
    public TextView mBindProgressTextView;
    @BindView(R.id.bt_bind)
    public Button mFinishButton;

    private String TAG = MainActivity.class.getSimpleName();
    private String mHongyanproductKey;
    private String mHongyandeviceName;

    private int bindTag = 0;//0:绑定中 1:绑定成功 -1:绑定失败
    private String mIotId = "100";
    private boolean mIs_getway;

    @Override
    protected GatewayAddGuidePresenter initPresenter() {
        return new GatewayAddGuidePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hongyan_gateway_add;
    }

    @Override
    protected void initView() {
        bindTag = 0;
        refreshBindShow();
        mHongyanproductKey = getIntent().getStringExtra("HongyanproductKey");
        mHongyandeviceName = getIntent().getStringExtra("HongyandeviceName");
        mIs_getway = getIntent().getBooleanExtra("is_getway", false);
        Log.d(TAG, "mHongyanproductKey=" + mHongyanproductKey + "hongyanDeviceName=" + mHongyandeviceName);
        if (mIs_getway) {
            getBindToken(mHongyanproductKey, mHongyandeviceName);
        } else {
            startBind("CYwVQ3cFX7drc9Cz1Fex000000");
            //bindVirturalZigbeeToUser(mHongyanproductKey, mHongyandeviceName);
        }
    }

    @Override
    protected void initListener() {

    }


    public void getBindToken(final String productKey, final String deviceName) {
        //获取绑定设备的token
        LocalDeviceMgr.getInstance().getDeviceToken(this, productKey, deviceName, 60 * 1000, 5 * 1000, new IOnDeviceTokenGetListener() {
            @Override
            public void onSuccess(String token) {
                // 拿到绑定需要的token
                //TODO 用户根据具体业务场景调用
                Log.d(TAG, "hongyan_token=" + token);
                bindVirturalToUser(productKey, deviceName, token);
            }

            @Override
            public void onFail(String reason) {
                Log.d(TAG, "hongyan_token=" + reason.toString());

            }
        });
    }


    //基于网关的token方式的设备绑定
    private void bindVirturalToUser(String pk, String dn, String token) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        maps.put("token", token);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/awss/token/user/bind")
                .setApiVersion("1.0.3")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.d(TAG, "onResponse_HONGYAN_four: " + e.getMessage());

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                mIotId = (String) ioTResponse.getData();
                startBind(mIotId);
                Log.d(TAG, "onResponse_HONGYAN_one: " + "mIotId=: " + mIotId);


            }
        });
    }

    //基于网关的token方式的设备绑定
    private void bindVirturalZigbeeToUser(String pk, String dn) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/awss/time/window/user/bind")
                .setApiVersion("1.0.8")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.d(TAG, "onResponse_HONGYAN_four: " + e.getMessage());

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                final int code = ioTResponse.getCode();
                if (code == 200) {
                    mIotId = (String) ioTResponse.getData();
                    startBind(mIotId);
                    Log.d(TAG, "onResponse_HONGYAN_one: " + "mIotId=: " + mIotId);
                } else {
                    bindTag = -1;
                    refreshBindShow();
                }
            }
        });
    }

    private void startBind(String data) {
        mPresenter.registerDeviceWithDSN(data, 1, getIntent().getLongExtra("scopeId", 0), mHongyandeviceName);
    }

    @Override
    public void bindSuccess() {
        bindTag = 1;
        refreshBindShow();
        Log.d(TAG, "onResponse_HONGYAN_two: " + "成功");
    }

    @Override
    public void bindFailed() {
        bindTag = -1;
        refreshBindShow();
        Log.d(TAG, "onResponse_HONGYAN_three: " + "失败");
    }


    @OnClick(R.id.bt_bind)
    public void handleButton() {
        if (bindTag == 1) {
            setResult(RESULT_OK);
            finish();
        } else if (bindTag == -1) {
            bindTag = 0;
            refreshBindShow();
            startBind(mIotId);
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
                mBindProgressTextView.setText("设备绑定成功");
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("完成");
                break;
            case -1:
                mImageView.setImageResource(R.drawable.ic_device_bind_failed);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mBindProgressTextView.setText("设备绑定失败\n请再检查设备状态与设备ID号后重试");
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("重试");
                break;
            default:
                mImageView.setAnimation(R.raw.ic_device_bind_loading);
                mImageView.playAnimation();
                mLoadingTextView.setVisibility(View.VISIBLE);
                mBindProgressTextView.setText("最长可能需要1分钟，请耐心等待");
                mFinishButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

}
