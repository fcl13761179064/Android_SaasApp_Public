package com.ayla.ng.lib.bootstrap.connectivity;

import android.content.Context;

import androidx.annotation.NonNull;

public class AylaConnectivityManagerInteractiveImp extends AylaConnectivityManager {

    public AylaConnectivityManagerInteractiveImp(@NonNull Context context) {
        super(new com.aylanetworks.aylasdk.connectivity.AylaConnectivityManagerInteractiveImp(context));
    }
}
