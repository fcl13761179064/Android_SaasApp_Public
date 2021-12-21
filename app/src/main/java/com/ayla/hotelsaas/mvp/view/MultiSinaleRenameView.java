package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceLocationBean;

import java.util.List;

public interface MultiSinaleRenameView extends BaseView {
    void renameSuccess(String nickName);

    void renameFailed(Throwable throwable);

    void loadDeviceLocationSuccess(List<DeviceLocationBean> deviceListBean);

    void updatePurposeSuccess();
}
