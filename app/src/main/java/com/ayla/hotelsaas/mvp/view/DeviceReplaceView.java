package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface DeviceReplaceView extends BaseView {
    void canReplace(String gatewayId);

    void cannotReplace(Throwable throwable);
}
