package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RoomOrderBean;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface MainView extends BaseView {

    void getAuthCodeFail(String code,String msg);

    void getAuthCodeSuccess(String data);
}
