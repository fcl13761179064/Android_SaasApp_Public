package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;

import java.util.List;

public interface SwitchDefaultListView extends BaseView {
    void showData(List<String[]> result);
}
