package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.List;

public interface DeviceAddCategoryView extends BaseView {
    void showCategory(List<DeviceCategoryBean> deviceCategoryBeans);

    void categoryLoadFail(Throwable throwable);


    void loadAllDeviceDataSuccess(DeviceListBean data);

    void loadAllDeviceDataFail(Throwable throwable);
}
