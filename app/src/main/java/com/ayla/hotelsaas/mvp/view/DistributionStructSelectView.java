package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;

import java.util.List;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface DistributionStructSelectView extends BaseView {
    void showData(List<TreeListBean> treeListBeans);
}
