package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface GatewayAddView extends BaseView {

    void bindSuccess(String deviceId, String deviceName);

    void bindFailed(String msg);

    void renameSuccess(String nickName);

    void renameFailed(String code, String msg);
}
