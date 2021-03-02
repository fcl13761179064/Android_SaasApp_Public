package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;

import java.util.List;

public interface DeviceReplaceView extends BaseView {
    void canReplace(String gatewayId, List<DeviceCategoryBean> deviceCategoryBeans);

    void cannotReplace(Throwable throwable);
}
