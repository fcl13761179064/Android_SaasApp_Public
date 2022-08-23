package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.A2BindInfoBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;

public interface A2DeviceBindView extends BaseView {
    void getGuideInfoSuccess(NetworkConfigGuideBean o);
    void getBindInfoSuccess(A2BindInfoBean o);
    void getBindInfoFail(String o);
    void getGuideInfoFailed(Throwable throwable);
}
