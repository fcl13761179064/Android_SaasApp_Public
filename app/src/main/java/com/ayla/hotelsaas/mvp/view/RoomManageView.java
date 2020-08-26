package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RoomManageBean;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface RoomManageView extends BaseView {

    void loadDataSuccess(RoomManageBean data);

    void createRoomSuccess(String data);

    void createRoomFailed(String code);

    void loadDataFinish();

}
