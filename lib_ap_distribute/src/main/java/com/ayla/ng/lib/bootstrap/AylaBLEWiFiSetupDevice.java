package com.ayla.ng.lib.bootstrap;

public class AylaBLEWiFiSetupDevice {
    final com.aylanetworks.aylasdk.setup.AylaBLEWiFiSetupDevice aylaBLEWiFiSetupDevice;

    public AylaBLEWiFiSetupDevice(com.aylanetworks.aylasdk.setup.AylaBLEWiFiSetupDevice aylaBLEWiFiSetupDevice) {
        this.aylaBLEWiFiSetupDevice = aylaBLEWiFiSetupDevice;
    }

    public String getDeviceName() {
        return aylaBLEWiFiSetupDevice.getDeviceName();
    }

    public String getDsn() {
        return aylaBLEWiFiSetupDevice.getDsn();
    }
}
