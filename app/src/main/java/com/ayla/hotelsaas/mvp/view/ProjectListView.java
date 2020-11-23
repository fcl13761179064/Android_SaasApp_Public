package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.ConstructionBillListBean;

public interface ProjectListView extends BaseView {

    void showData(ConstructionBillListBean data);

    void onRequestFailed(Throwable throwable);
}
