package com.ayla.ng.lib.bootstrap.connectivity;

import android.content.Context;

import androidx.annotation.NonNull;

public class AylaConnectivityManagerPreAndroid10Imp extends AylaConnectivityManager {
    protected AylaConnectivityManagerPreAndroid10Imp(@NonNull Context context) {
        super(new com.aylanetworks.aylasdk.connectivity.AylaConnectivityManagerPreAndroid10Imp(context));
    }
}
