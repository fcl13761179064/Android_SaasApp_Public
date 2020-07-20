package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.OrderListView;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import java.util.List;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface WorkOrderView extends OrderListView {

    void loadDataSuccess(List<WorkOrderBean> data);

    void loadDataFinish();

}
