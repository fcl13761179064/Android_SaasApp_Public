package com.ayla.hotelsaas.ui;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.aliyun.alink.business.devicecenter.api.discovery.IOnDeviceTokenGetListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.GlideApp;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.GatewayAddGuideView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 鸿雁网关添加页面
 * 进入时必须带上dsn、cuId 、scopeId、deviceCategory。
 * 樊春雷
 */
public class HongyanGatewayAddActivity extends BaseMvpActivity<GatewayAddGuideView, GatewayAddGuidePresenter> implements GatewayAddGuideView {
    @BindView(R.id.iv_01)
    public ImageView mImageView;
    @BindView(R.id.tv_loading)
    public TextView mLoadingTextView;
    @BindView(R.id.tv_bind_progress)
    public TextView mBindProgressTextView;
    @BindView(R.id.bt_bind)
    public Button mFinishButton;
    private String TAG = HongyanGatewayAddActivity.class.getSimpleName();

    private int bindTag = 0;//0:绑定中 1:绑定成功 -1:绑定失败
    private String mIotId = "100";
    private boolean mIs_getway;
    private long mCuId, mScopeId;
    private String mDeviceName, mHongyanproductKey, mHongyandeviceName;

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
        mCuId = getIntent().getLongExtra("cuId", 1l);
        mDeviceName = getIntent().getStringExtra("deviceName");
        mScopeId = getIntent().getLongExtra("scopeId", 0l);
        mIs_getway = getIntent().getBooleanExtra("is_getway", false);

        if (mIs_getway) {
            getBindToken(mHongyanproductKey, mHongyandeviceName);
        } else {
            bindVirturalZigbeeToUser(mHongyanproductKey, mHongyandeviceName);
            Log.d("aliyun_key_log", "mHongyanproductKey=" + mHongyanproductKey + "mHongyanproductKey=" + mHongyandeviceName + "mDeviceName=" + mDeviceName + "productKey=" + getIntent().getStringExtra("deviceCategory"));
        }
    }

    @Override
    protected void initListener() {

    }


    public void getBindToken(String productKey, String deviceName) {
        //获取绑定设备的token
        LocalDeviceMgr.getInstance().getDeviceToken(productKey, deviceName, 60 * 1000, new IOnDeviceTokenGetListener() {
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
                    JSONObject data = (JSONObject) ioTResponse.getData();
                    try {
                        mIotId = (String) data.get("iotId");
                        startBind(mIotId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //基于时间窗口的方式绑定设备
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
                Log.d(TAG, "mHongyanproductKey=" + 1111111 + "hongyanDeviceName=" + "222222");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                try {
                    final int code = ioTResponse.getCode();
                    if (code == 200) {
                        JSONObject data = (JSONObject) ioTResponse.getData();
                        mIotId = (String) data.get("iotId");
                        startBind(mIotId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startBind(String data) {
        mPresenter.registerDeviceWithDSN(data, mCuId, mScopeId, getIntent().getStringExtra("deviceCategory"), mDeviceName);
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
            case 0:
                GlideApp.with(mImageView).load(R.drawable.ic_device_bind_loading).into(mImageView);
                mLoadingTextView.setVisibility(View.VISIBLE);
                mBindProgressTextView.setText("正在发现网关，最长可能需要1分钟。\n请确保网关与手机处于同一Wi-Fi下");
                mLoadingTextView.setText("绑定中...");
                mFinishButton.setVisibility(View.INVISIBLE);
                break;

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
                mLoadingTextView.setText("绑定失败...");
                mBindProgressTextView.setText("发现网关失败，请确保网关与手机处于同一Wi-Fi下");
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("重试");
                break;

        }
    }

}
