package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RoomOrderBean;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface RoomOrderView extends BaseView {

    void loadDataSuccess(RoomOrderBean data);

    void loadDataFinish();

    void getAuthCodeSuccess(String data);
}
