package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;
import java.util.List;

public interface DeviceListView extends BaseView {

    void loadDataSuccess(List<DeviceListBean> data);

    void loadDataFinish() ;

}
