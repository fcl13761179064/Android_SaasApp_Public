package com.ayla.hotelsaas.events;

public final class DeviceRemovedEvent {
    public String deviceId;

    public DeviceRemovedEvent(String deviceId) {
        this.deviceId = deviceId;
    }
}
