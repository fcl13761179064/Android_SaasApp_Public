package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RoomManageBean;

import java.util.List;


public interface DistributionView extends BaseView {
    void hotelLoadSuccess(List<RoomManageBean.RecordsBean> rooms);
}
