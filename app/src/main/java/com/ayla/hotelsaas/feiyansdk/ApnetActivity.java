package com.ayla.hotelsaas.feiyansdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.IOnDeviceTokenGetListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.ui.MainActivity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApnetActivity extends AppCompatActivity implements OnDeviceAddListener {

    private static final int ACCESS_COARSE_LOCATION1 = 1;
    private DeviceAddHandler deviceAddHandler;
    private String TAG = ApnetActivity.class.getSimpleName();
    private Bundle mBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ap_net);
        deviceAddHandler = new DeviceAddHandler(this);
        // deviceAddHandler.getSupportDeviceListFromSever();
        findViewById(R.id.bt_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceAddHandler.reset();
                //发现局域网内设备以及WiFi热点设备
                EnumSet<DiscoveryType> discoveryTypeEnumSet = EnumSet.allOf(DiscoveryType.class);
                LocalDeviceMgr.getInstance().startDiscovery(ApnetActivity.this, discoveryTypeEnumSet, null, new IDeviceDiscoveryListener() {
                    @Override
                    public void onDeviceFound(DiscoveryType discoveryType, List<DeviceInfo> list) {
                        Log.d("DeviceAddBusiness", "--发现设备--" + JSON.toJSONString(list));
                        List<FoundDeviceListItem> foundDeviceListItems = new ArrayList<>();
                        for (DeviceInfo deviceInfo : list) {
                            final FoundDeviceListItem deviceListItem = new FoundDeviceListItem();
                            if (discoveryType == DiscoveryType.LOCAL_ONLINE_DEVICE) {
                                deviceListItem.deviceStatus = FoundDeviceListItem.NEED_BIND;
                            } else if (discoveryType == DiscoveryType.CLOUD_ENROLLEE_DEVICE) {
                                deviceListItem.deviceStatus = FoundDeviceListItem.NEED_CONNECT;
                            }
                            deviceListItem.discoveryType = discoveryType;
                            deviceListItem.deviceInfo = deviceInfo;
                            deviceListItem.deviceName = deviceInfo.deviceName;
                            deviceListItem.productKey = deviceInfo.productKey;
                            deviceListItem.token = deviceInfo.token;
                            foundDeviceListItems.add(deviceListItem);
                        }
                        ApNet("a1gnkwYSKkj");
                        //
                        getBindToken(foundDeviceListItems.get(0).productKey, foundDeviceListItems.get(0).deviceName);

                        //deviceAddHandler.filterDevice(foundDeviceListItems);
                    }

                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }

    @Override
    public void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSupportDeviceSuccess(List<SupportDeviceListItem> mSupportDeviceListItems) {
        Log.e(TAG, "onSupportDeviceSsuccess: " + mSupportDeviceListItems);
        final String productKey = mSupportDeviceListItems.get(0).getProductKey();
        final String deviceName = mSupportDeviceListItems.get(0).getDeviceName();
        //去配网
        // ApNet(productKey);

    }


    public void ApNet(String productKey) {
        // 启动插件
        Bundle bundle = new Bundle();
        bundle.putString("productKey", productKey);
        Router.getInstance().toUrlForResult(ApnetActivity.this, "link://router/connectConfig", 1, bundle);

    }


    @Override
    public void onFilterComplete(List<FoundDeviceListItem> foundDeviceListItems) {
        //  addDeviceAdapter.addLocalDevice(foundDeviceListItems);
    }


    //基于网关的token方式的设备绑定 这是绑定到阿里云上
    private void bindVirturAlaliToUser(String pk, String dn, String token) {
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
                Log.d(TAG, "onResponse: " + e.getMessage());

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                Log.d(TAG, "onResponse: " + ioTResponse.getMessage());

            }
        });
    }


    //基于网关的token方式的设备绑定 这是绑定到ayla上
    private void bindVirturalToAylaUser(String pk, String dn, String token) {
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
                Log.d(TAG, "onResponse: " + e.getMessage());

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                Log.d(TAG, "onResponse: " + ioTResponse.getMessage());

            }
        });
    }

    public void getBindToken(final String productKey, final String deviceName) {
        //获取绑定设备的token
        LocalDeviceMgr.getInstance().getDeviceToken(productKey, deviceName, 60 * 1000, new IOnDeviceTokenGetListener() {
            @Override
            public void onSuccess(String token) {
                // 拿到绑定需要的token
                //TODO 用户根据具体业务场景调用
                bindVirturAlaliToUser(productKey, deviceName, token);
                //绑定到ayla
                bindVirturalToAylaUser(productKey, deviceName, token);
            }

            @Override
            public void onFail(String reason) {
                Log.d("DeviceAddBusiness", "--发现设备--");
            }
        });
    }

    // 接收配网结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            // 配网失败
            return;
        }
        String productKey = data.getStringExtra("productKey");
        String deviceName = data.getStringExtra("deviceName");
        // 配网成功
        getBindToken(productKey, deviceName);

    }
}