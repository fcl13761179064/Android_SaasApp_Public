package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.MarshallEntryBean;

import java.util.List;

public interface DeviceMarshallEntryView extends BaseView {
    void DeviceMarshallEntrySuccess(List<MarshallEntryBean> o, String mGatewayId);

    void DeviceMarshallEntryFail(String o);

    void saveGroupSuccess(String groupId, String groupName);

    void saveGroupFail(Throwable throwable);

    void updateGroupResult(Boolean result, Throwable throwable);

    void getCombineDeviceGroupSuccess(String gatewayId, List<BaseDevice> devices);

    void onCombineDeviceGroupFail(Throwable throwable);

    void updateSwitchPropertySuccess();

    void updateSwitchPropertyFail(Throwable throwable);

    void removeUseDeviceResult(boolean result, Throwable throwable);
}
