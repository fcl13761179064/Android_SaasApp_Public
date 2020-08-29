package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface HongyanGatewayAddGuideView extends BaseView {

    void bindSuccess();

    void bindFailed();

    void removeReleteSuccess(String data);

    void removeReleteFail(String code, String msg);
}
