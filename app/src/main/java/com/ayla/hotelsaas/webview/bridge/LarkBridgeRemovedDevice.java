package com.ayla.hotelsaas.webview.bridge;

public class LarkBridgeRemovedDevice {

    /**
     * 动作名称
     */
    private String dsn;

    public LarkBridgeRemovedDevice() {

    }

    public LarkBridgeRemovedDevice(String dsn) {
        this.dsn = dsn;
    }

    public String getDsn() {
        return dsn;
    }

    public void setDsn(String dsn) {
        this.dsn = dsn;
    }
}
