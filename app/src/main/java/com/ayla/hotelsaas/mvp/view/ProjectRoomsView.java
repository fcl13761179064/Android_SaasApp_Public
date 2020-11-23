package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.TreeListBean;

import java.util.List;

public interface ProjectRoomsView extends BaseView {
    void showData(List<TreeListBean> treeListBeans);

    void loadDataFailed(Throwable throwable);
}
