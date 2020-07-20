package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.OrderListView;
import com.ayla.hotelsaas.bean.DeviceListBean;
import java.util.List;

public interface DeviceListView extends OrderListView {

    void loadDataSuccess(List<DeviceListBean> data);

    void loadDataFinish() ;

}
