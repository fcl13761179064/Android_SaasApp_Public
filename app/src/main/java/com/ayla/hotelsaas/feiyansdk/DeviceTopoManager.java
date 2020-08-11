package com.ayla.hotelsaas.feiyansdk;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.alink.dm.api.BaseInfo;
import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.dm.api.SignUtils;
import com.aliyun.alink.dm.model.ResponseModel;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linkkit.api.LinkKitInitParams;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceActionListener;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceChannel;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceConnectListener;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceRemoveListener;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aliyun.alink.linksdk.tools.ALog.LEVEL_DEBUG;

public class DeviceTopoManager extends AppCompatActivity {
    private static String regionId = "cn-shanghai";
    private static final String TAG = "TOPO";

    //网关设备
    private static String GWproductKey = "a1HNe4WZayw";
    private static String GWdeviceName = "test007";
    private static String GWdeviceSecret = "02d84deae8a01e35d5ef6e50854406f5";


    public static void main(String[] args) {
        /**
         * mqtt连接信息
         */
        DeviceTopoManager manager = new DeviceTopoManager();

        /**
         * 服务器端的java http客户端使用TSLv1.2。
         */
        System.setProperty("https.protocols", "TLSv2");

        manager.init();
    }

    public void init() {
        LinkKitInitParams params = new LinkKitInitParams();
        /**
         * 设置mqtt初始化参数。
         */
        IoTMqttClientConfig config = new IoTMqttClientConfig();
        config.productKey = GWproductKey;
        config.deviceName = GWdeviceName;
        config.deviceSecret = GWdeviceSecret;
        config.channelHost = GWproductKey + ".iot-as-mqtt." + regionId + ".aliyuncs.com:1883";
        /**
         * 是否接受离线消息。
         * 对应mqtt的cleanSession字段。
         */
        config.receiveOfflineMsg = false;
        params.mqttClientConfig = config;
        ALog.setLevel(LEVEL_DEBUG);
        ALog.i(TAG, "mqtt connetcion info=" + params);

        /**
         * 设置初始化，传入网关的设备证书信息。
         */
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.productKey = GWproductKey;
        deviceInfo.deviceName = GWdeviceName;
        deviceInfo.deviceSecret = GWdeviceSecret;
        params.deviceInfo = deviceInfo;

        /**建立连接**/
        LinkKit.getInstance().init(this, params, new ILinkKitConnectListener() {
            public void onError(AError aError) {
                ALog.e(TAG, "Init Error error=" + aError);
            }

            @Override
            public void onInitDone(Object o) {
                //获取网关下拓扑关系，查询网关与子设备是否已经存在拓扑关系。
                //如果已经存在，则直接上线子设备。
                getGWDeviceTopo();

                //子设备动态注册获取DeviceSecret，如果设备已知设备证书则忽略此步，直接添加拓扑关系。
                //在物联网平台上提前创建设备时，可以使用设备的MAC地址或SN序列号等作为DeviceName。
                gatewaySubDevicRegister();

                //待添加拓扑关系的子设备信息。
                gatewayAddSubDevice();
            }

        });
    }

    /**
     * 获取网关下拓扑关系，查询网关与子设备是否已经存在拓扑关系。
     */
    private void getGWDeviceTopo() {
        LinkKit.getInstance().getGateway().gatewayGetSubDevices(new IConnectSendListener() {

            @Override
            public void onResponse(ARequest request, AResponse aResponse) {
                ALog.i(TAG, "获取网关的topo关系成功 : " + JSONObject.toJSONString(aResponse));

                // 获取子设备列表结果。
                try {
                    ResponseModel<List<DeviceInfo>> response = JSONObject.parseObject(aResponse.data.toString(), new TypeReference<ResponseModel<List<DeviceInfo>>>() {
                    }.getType());
                    // TODO 根据实际应用场景处理。
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ARequest request, AError error) {
                ALog.i(TAG, "获取网关的topo关系失败 : " + JSONObject.toJSONString(error));
            }
        });
    }

    /**
     * 子设备动态注册获取设备deviceSecret，如果网关已获得子设备证书则忽略此步。
     * 在物联网平台上提前创建设备时，可以使用设备的MAC地址或SN序列号等作为DeviceName。
     */
    private void gatewaySubDevicRegister() {

        List<BaseInfo> subDevices = new ArrayList<>();
        BaseInfo baseInfo1 = new BaseInfo();
        baseInfo1.productKey = "a1j7SyR**********";
        baseInfo1.deviceName = "test123*********";
        subDevices.add(baseInfo1);

        LinkKit.getInstance().getGateway().gatewaySubDevicRegister(subDevices, new IConnectSendListener() {

            @Override
            public void onResponse(ARequest request, AResponse response) {
                ALog.i(TAG, "子设备注册成功 : " + JSONObject.toJSONString(response));
            }

            @Override
            public void onFailure(ARequest request, AError error) {
                ALog.i(TAG, "子设备注册失败 : " + JSONObject.toJSONString(error));
            }
        });
    }

    /**
     * 待添加拓扑关系的子设备信息。
     */
    private void gatewayAddSubDevice() {
        final BaseInfo baseInfo1 = new BaseInfo();
        baseInfo1.productKey = "a1HNe4WZayw";
        baseInfo1.deviceName = "test007";
        final String deviceSecret = "02d84deae8a01e35d5ef6e50854406f5";

        LinkKit.getInstance().getGateway().gatewayAddSubDevice(baseInfo1, new ISubDeviceConnectListener() {
            @Override
            public String getSignMethod() {
                // 使用的签名方法
                return "hmacsha1";
            }

            @Override
            public String getSignValue() {
                // 获取签名，用户使用DeviceSecret获得签名结果。
                Map<String, String> signMap = new HashMap<>();
                signMap.put("productKey", baseInfo1.productKey);
                signMap.put("deviceName", baseInfo1.deviceName);
//                signMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
                signMap.put("clientId", getClientId());
                return SignUtils.hmacSign(signMap, deviceSecret);
            }

            @Override
            public String getClientId() {
                // clientId可为任意值。
                return "id";
            }

            @Override
            public Map<String, Object> getSignExtraData() {
                return null;
            }

            @Override
            public void onConnectResult(boolean isSuccess, ISubDeviceChannel iSubDeviceChannel, AError aError) {


                // 添加结果
                if (isSuccess) {
                    // 子设备添加成功，接下来可以做子设备上线的逻辑。
                    ALog.i(TAG, "topo关系添加成功 : " + JSONObject.toJSONString(iSubDeviceChannel));

                    //子设备上线
                    gatewaySubDeviceLogin();

                } else {
                    ALog.i(TAG, "topo关系添加失败 : " + JSONObject.toJSONString(aError));
                }
            }

            @Override
            public void onDataPush(String s, AMessage aMessage) {

            }
        });
    }

    public void gatewayDeleteSubDevice() {
        BaseInfo baseInfo1 = new BaseInfo();
        baseInfo1.productKey = "a1j7S**************";
        baseInfo1.deviceName = "saf*********";

        LinkKit.getInstance().getGateway().gatewayDeleteSubDevice(baseInfo1, new ISubDeviceRemoveListener() {
            @Override
            public void onSuceess() {
                // 成功删除子设备。删除之前可先做下线操作。
            }

            @Override
            public void onFailed(AError aError) {
                // 删除子设备失败。
            }
        });
    }

    /**
     * 调用子设备上线之前，请确保已建立拓扑关系。网关发现子设备连接后，需要告知云端子设备上线。
     * 子设备上线之后可以执行子设备的订阅、发布等操作。
     */
    public void gatewaySubDeviceLogin() {

        BaseInfo baseInfo1 = new BaseInfo();
        baseInfo1.productKey = "a1j7SyR***********";
        baseInfo1.deviceName = "safa*********";

        LinkKit.getInstance().getGateway().gatewaySubDeviceLogin(baseInfo1, new ISubDeviceActionListener() {
            @Override
            public void onSuccess() {
                // 代理子设备上线成功。
                // 上线之后可订阅、发布消息，并可以删除和禁用子设备。
                // subDevDisable(null);
                // subDevDelete(null);
            }

            @Override
            public void onFailed(AError aError) {
                ALog.d(TAG, "onFailed() called with: aError = [" + aError + "]");
            }
        });

    }

}