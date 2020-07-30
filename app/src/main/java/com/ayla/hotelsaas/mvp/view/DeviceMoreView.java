package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.User;

import java.util.List;

public interface DeviceMoreView extends BaseView {

    //错误提示
    void errorShark(int type, int CycleTimes);

    //操作成功
    void operateSuccess(User data);
}
