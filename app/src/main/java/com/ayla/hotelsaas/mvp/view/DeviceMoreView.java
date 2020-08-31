package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface DeviceMoreView extends BaseView {

    //错误提示
    void operateError(String msg);

    //操作成功
    void operateSuccess(Boolean is_rename);

    //移除成功
    void operateRemoveSuccess(Boolean is_rename);

    //失败成功
    void operateMoveFailSuccess(String code,String msg);
}
