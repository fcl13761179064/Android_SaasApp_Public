package com.ayla.hotelsaas.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 品类中心，品类详细描述
 * 包含oemModel 、支持的条件properties 和动作properties
 */
public class DeviceCategoryDetailBean {

    /**
     * "pid": "ZBGW0-A000001",
     * "deviceId": "AC000W013190349",
     * "nickName": "AZB-GW0-001(A)_0349",
     * "iconUrl": "http://aylasmht-test.oss-cn-shanghai.aliyuncs.com/ccpgspark/Simulant%20icon/01_luoma/001_luomazhinengwangguan.png",
     * "isPurposeDevice": 0,
     * "cuId": 0,
     * "conditionProperties": [],
     * "actionProperties": [],
     * "deviceName": null,
     * "oemModel": null
     */

    private int cuId;
    private String deviceId;
    private String pid;
    private List<String> conditionProperties;
    private List<String> actionProperties;

    public int getCuId() {
        return cuId;
    }

    public void setCuId(int cuId) {
        this.cuId = cuId;
    }

    public List<String> getConditionProperties() {
        return conditionProperties == null? new ArrayList<>():conditionProperties;
    }

    public List<String> getActionProperties() {
        return actionProperties == null ? new ArrayList<>():actionProperties;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPid() {
        return pid;
    }
}
