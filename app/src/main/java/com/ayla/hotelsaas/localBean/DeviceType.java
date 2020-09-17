package com.ayla.hotelsaas.localBean;

public enum DeviceType {
    AYLA(0), ALI(1);

    public int code;

    DeviceType(int code) {
        this.code = code;
    }

    public static DeviceType valueOf(int code) {
        DeviceType data = null;
        switch (code) {
            case 0:
                data = AYLA;
                break;
            case 1:
                data = ALI;
                break;
        }
        return data;
    }
}
