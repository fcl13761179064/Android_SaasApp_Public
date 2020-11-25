package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;

public interface DeviceListView extends BaseView {

    void loadDataSuccess(DeviceListBean data);

    void loadDataFinish(Throwable throwable) ;

}
