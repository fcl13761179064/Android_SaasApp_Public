package com.ayla.hotelsaas.events;

public final class LocationChangedEvent {
    public String deviceId;
    public String newName;

    public LocationChangedEvent(String deviceId, String newName) {
        this.deviceId = deviceId;
        this.newName = newName;
    }
}
