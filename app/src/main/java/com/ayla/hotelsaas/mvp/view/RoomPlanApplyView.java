package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface RoomPlanApplyView extends BaseView {
    void importPlanSuccess();

    void importPlanFailed(Throwable throwable);
}
