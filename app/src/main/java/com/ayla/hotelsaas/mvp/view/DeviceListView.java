package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.ArrayList;
import java.util.List;

public interface DeviceListView extends BaseView {

    void loadDataSuccess(DeviceListBean data);

    void loadDataFinish() ;

}
