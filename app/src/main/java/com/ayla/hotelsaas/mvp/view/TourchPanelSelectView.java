package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;

import java.util.List;

public interface TourchPanelSelectView extends BaseView {

    //错误提示
    void operateError(String msg);

    //操作成功
    void operateSuccess(List<TouchPanelDataBean> dataBeans);

}
