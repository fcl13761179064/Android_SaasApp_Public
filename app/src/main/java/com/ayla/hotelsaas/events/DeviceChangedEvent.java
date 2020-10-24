package com.ayla.hotelsaas.events;

public final class DeviceChangedEvent {
    public String deviceId;

    public DeviceChangedEvent(String deviceId) {
        this.deviceId = deviceId;
    }
}
