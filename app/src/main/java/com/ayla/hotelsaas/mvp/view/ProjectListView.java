package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.WorkOrderBean;

public interface ProjectListView extends BaseView {

    void showData(WorkOrderBean data);

    void onRequestFailed(Throwable throwable);
}
