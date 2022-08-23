package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.GroupDeviceItem;

import java.util.List;

public interface CheckMarshallSubsetView extends BaseView {
    void getSubsetSucess(List<GroupDeviceItem> o);
    void getSubsetFail(String o);
}
