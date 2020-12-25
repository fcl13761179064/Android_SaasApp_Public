package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface DeviceAddSuccessView extends BaseView {
    void renameSuccess(String nickName);

    void renameFailed(String code, String msg);
}
