package com.ayla.ng.lib.bootstrap;

import android.os.ParcelUuid;

import java.util.List;
import java.util.UUID;

public class BLEScanResult {
    private final List<ParcelUuid> _serviceUuids;
    private final String _localName;

    BLEScanResult(String localName, List<ParcelUuid> serviceUuids) {
        this._localName = localName;
        this._serviceUuids = serviceUuids;
    }

    public boolean containsService(UUID serviceId) {
        if (_serviceUuids == null) {
            return false;
        }
        for (ParcelUuid uuid : _serviceUuids) {
            if (uuid.getUuid().equals(serviceId)) {
                return true;
            }
        }
        return false;
    }


    public String getLocalName() {
        return _localName;
    }
}
