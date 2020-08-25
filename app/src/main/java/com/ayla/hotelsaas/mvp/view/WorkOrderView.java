package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.WorkOrderBean;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface WorkOrderView extends BaseView {

    void loadDataSuccess(WorkOrderBean data);

    void loadDataFinish();

}
