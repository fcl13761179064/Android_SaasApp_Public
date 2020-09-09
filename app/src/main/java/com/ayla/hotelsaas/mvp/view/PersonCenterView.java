package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface PersonCenterView extends BaseView {

    //错误提示
    void getUserInfoFail(String code,String msg);

    //操作成功
    void getUserInfoFailSuccess(boolean success);
}
