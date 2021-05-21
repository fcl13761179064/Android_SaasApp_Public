package com.ayla.hotelsaas.viewmodel;

import com.blankj.utilcode.util.NetworkUtils;

import io.reactivex.Observable;


public class APDistributeView {
    String gatewayIp = NetworkUtils.getGatewayByWifi();

}
