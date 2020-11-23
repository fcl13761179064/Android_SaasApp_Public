package com.ayla.hotelsaas.events;

public final class RoomChangedEvent {
    public long roomId;
    public String newName;

    public RoomChangedEvent(long roomId, String newName) {
        this.roomId = roomId;
        this.newName = newName;
    }
}
