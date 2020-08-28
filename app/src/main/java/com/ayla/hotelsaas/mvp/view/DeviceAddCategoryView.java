package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;

import java.util.List;

public interface DeviceAddCategoryView extends BaseView {
    void showCategory(List<DeviceCategoryBean> deviceCategoryBeans);

    void getAuthCodeSuccess(String data);

    void getAuthCodeFail(String code, String msg);
}
