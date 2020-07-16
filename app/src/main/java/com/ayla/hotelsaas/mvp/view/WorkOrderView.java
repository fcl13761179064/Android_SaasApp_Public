package com.ayla.hotelsaas.mvp.view;


import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.WorkOrderBean;

import java.util.ArrayList;


/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public interface WorkOrderView extends BaseView {

    void loadDataSuccess(ArrayList<WorkOrderBean> data);

    void loadDataFinish();

}
