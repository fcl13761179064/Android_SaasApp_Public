package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;


public interface TourchPanelView extends BaseView {

    //错误提示
    void operateError(String msg);

    //操作成功
    void operateSuccess(Boolean is_rename);

}
