package com.ayla.hotelsaas.events;

public final class DeviceChangedEvent {
    public String deviceId;
    public String newName;

    public DeviceChangedEvent(String deviceId, String newName) {
        this.deviceId = deviceId;
        this.newName = newName;
    }
}
