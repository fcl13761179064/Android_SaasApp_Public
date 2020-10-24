package com.ayla.hotelsaas.localBean;

public enum DeviceType {
    AYLA_DEVICE_ID(0), ALI_DEVICE_ID(1),INFRARED_VIRTUAL_SUB_DEVICE(8);

    public int code;

    DeviceType(int code) {
        this.code = code;
    }

    public static DeviceType valueOf(int code) {
        DeviceType data = null;
        switch (code) {
            case 0:
                data = AYLA_DEVICE_ID;
                break;
            case 1:
                data = ALI_DEVICE_ID;
                break;
            case 8:
                data = INFRARED_VIRTUAL_SUB_DEVICE;
                break;
        }
        return data;
    }
}
