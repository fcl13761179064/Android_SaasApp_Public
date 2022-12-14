package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.RoomTypeShowBean;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface RoomPlanApplyView extends BaseView {
    void importPlanSuccess();

    void importPlanFailed(Throwable throwable);

    void showRoomTypeSuccess(RoomTypeShowBean i);
}
