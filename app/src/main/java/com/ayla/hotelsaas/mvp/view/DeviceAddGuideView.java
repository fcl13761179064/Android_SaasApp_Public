package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;

public interface DeviceAddGuideView extends BaseView {
    void getGuideInfoSuccess(NetworkConfigGuideBean o);

    void getGuideInfoFailed(Throwable throwable);
}
