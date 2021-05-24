package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;

public interface APwifiToGateWayView extends BaseView {
    void onFailed(Throwable throwable);
    void onSuccess(AylaSetupDevice aylaSetupDevice);
}
