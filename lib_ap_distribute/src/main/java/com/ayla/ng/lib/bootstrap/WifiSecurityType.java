package com.ayla.ng.lib.bootstrap;

import com.aylanetworks.aylasdk.setup.AylaSetup;

public class WifiSecurityType {
    final AylaSetup.WifiSecurityType wifiSecurityType;

    public WifiSecurityType(String str) {
        AylaSetup.WifiSecurityType securityType = AylaSetup.WifiSecurityType.NONE;
        switch (str) {
            case "WPA3-Personal":
            case "WPA3 Personal Mixed":
                securityType = AylaSetup.WifiSecurityType.WPA3;
                break;
            case "WPA2-Personal":
            case "WPA2_Personal":
                securityType = AylaSetup.WifiSecurityType.WPA2;
                break;
            case "WPA":
                securityType = AylaSetup.WifiSecurityType.WPA;
                break;
            case "WEP":
                securityType = AylaSetup.WifiSecurityType.WEP;
                break;
            case "None":
            case "none":
                securityType = AylaSetup.WifiSecurityType.NONE;
                break;
        }
        this.wifiSecurityType = securityType;
    }
}
