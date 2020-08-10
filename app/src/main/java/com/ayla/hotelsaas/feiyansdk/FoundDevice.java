package com.ayla.hotelsaas.feiyansdk;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;

import java.util.Objects;

public class FoundDevice {

    public String deviceName;
    public String productKey;
    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoundDevice)) return false;
        FoundDevice that = (FoundDevice) o;
        return Objects.equals(deviceName, that.deviceName) &&
                Objects.equals(productKey, that.productKey)
                && Objects.equals(productKey, that.productKey);
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
