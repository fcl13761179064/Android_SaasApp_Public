package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.OrderListView;
import com.ayla.hotelsaas.bean.RoomOrderBean;

import java.util.List;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface RoomOrderView extends OrderListView {

    void loadDataSuccess(List<RoomOrderBean> data);

    void loadDataFinish();

}
