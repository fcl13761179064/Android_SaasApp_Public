package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface APwifiToGateWayView extends BaseView {
    void onFailed(Throwable throwable);

    void onSuccess();
}
