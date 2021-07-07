package com.ayla.ng.lib.bootstrap;

public class AylaSetupDevice {
    com.aylanetworks.aylasdk.setup.AylaSetupDevice aylaSetupDevice;

    public AylaSetupDevice(com.aylanetworks.aylasdk.setup.AylaSetupDevice aylaSetupDevice) {
        this.aylaSetupDevice = aylaSetupDevice;
    }

    public String getDsn() {
        return aylaSetupDevice.getDsn();
    }
}
