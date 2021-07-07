package com.ayla.ng.lib.bootstrap;

import com.aylanetworks.aylasdk.setup.AylaWifiScanResults;

public class AylaWifiScanResult {
    private final AylaWifiScanResults.Result result;

    public AylaWifiScanResult(AylaWifiScanResults.Result result) {
        this.result = result;
    }

   public String getSsid(){
        return result.ssid;
   }
   public String getSecurity(){
        return result.security;
   }
}
