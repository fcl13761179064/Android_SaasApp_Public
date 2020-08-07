package com.ayla.hotelsaas.feiyansdk;

import android.app.Application;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Env;
import com.aliyun.iot.ble.util.Log;


/**
 * Created by wuwang on 2017/10/30.
 */

public class DemoApplication extends Application {

    private static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化无线保镖
        try {
            SecurityInit.Initialize(getApplicationContext());
        } catch (JAQException ex) {
            Log.e(TAG, "security-sdk-initialize-failed");
        } catch (Exception ex) {
            Log.e(TAG, "security-sdk-initialize-failed");
        }

        // 初始化 IoTAPIClient
        IoTAPIClientImpl.InitializeConfig config = new IoTAPIClientImpl.InitializeConfig();
        // 国内环境
        config.host = "api.link.aliyun.com";
        // 海外环境，请参考如下设置
        //config.host = “api-iot.ap-southeast-1.aliyuncs.com”;
        config.apiEnv = Env.RELEASE; //只支持RELEASE
        //设置请求超时（可选）默认超时时间10s
        config.connectTimeout=10_000L;
        config.readTimeout=10_000L;
        config.writeTimeout=10_000L;
        IoTAPIClientImpl impl = IoTAPIClientImpl.getInstance();
        impl.init(getApplicationContext(), config);

    }
}