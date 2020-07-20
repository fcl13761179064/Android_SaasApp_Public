package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.OrderListView;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;

import java.util.List;

public interface DeviceAddCategoryView extends OrderListView {
    void showCategory(List<DeviceCategoryBean> deviceCategoryBeans);
}
