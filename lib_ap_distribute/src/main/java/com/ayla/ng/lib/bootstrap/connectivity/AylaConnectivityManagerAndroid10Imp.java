package com.ayla.ng.lib.bootstrap.connectivity;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class AylaConnectivityManagerAndroid10Imp extends AylaConnectivityManager {
    protected AylaConnectivityManagerAndroid10Imp(@NonNull Context context) {
        super(new com.aylanetworks.aylasdk.connectivity.AylaConnectivityManagerAndroid10Imp(context));
    }
}
