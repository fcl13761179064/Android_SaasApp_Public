package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.Device;
import com.ayla.hotelsaas.mvp.view.GatewayAddGuideView;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddSelectGatewayView;
import com.ayla.hotelsaas.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ZigBeeAddSelectGatewayPresenter extends BasePresenter<ZigBeeAddSelectGatewayView> {

    public void loadGateway() {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Device device = new Device();
            device.setName("网关设备" + i);
            device.setOnlineStatus(i % 2 == 0 ? "online" : "offline");
            devices.add(device);
        }
        mView.showGateways(devices);
    }
}
