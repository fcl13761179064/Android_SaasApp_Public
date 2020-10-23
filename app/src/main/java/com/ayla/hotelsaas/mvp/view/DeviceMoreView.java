package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface DeviceMoreView extends BaseView {

    //错误提示
    void renameFailed(String msg);

    //操作成功
    void renameSuccess(String newNickName);

    //移除成功
    void removeSuccess(Boolean is_rename);

    //失败成功
    void removeFailed(String code, String msg);
}
