package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface GatewayAddGuideView extends BaseView {

    void bindSuccess(String deviceId, String deviceName);

    void bindFailed();

    void renameSuccess();

    void renameFailed();
}
