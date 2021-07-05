package com.ayla.ng.lib.bootstrap;

public class AylaSetupDevice {
    com.aylanetworks.aylasdk.setup.AylaSetupDevice aylaSetupDevice;

    public AylaSetupDevice(com.aylanetworks.aylasdk.setup.AylaSetupDevice aylaSetupDevice) {
        this.aylaSetupDevice = aylaSetupDevice;
    }

    public com.aylanetworks.aylasdk.setup.AylaSetupDevice getAylaSetupDevice() {
        return aylaSetupDevice;
    }

    public void setLanIp(String lanIp) {
        aylaSetupDevice.setLanIp(lanIp);
    }

    public String getDsn() {
        return aylaSetupDevice.getDsn();
    }
}
