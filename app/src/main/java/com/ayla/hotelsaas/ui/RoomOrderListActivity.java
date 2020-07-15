package com.ayla.hotelsaas.ui;

import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.WorkOrderPresenter;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;

import java.util.ArrayList;

public class RoomOrderListActivity extends BaseMvpActivity<WorkOrderView, WorkOrderPresenter> implements WorkOrderView   {
    @Override
    protected WorkOrderPresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void loadDataSuccess(ArrayList<WorkOrderBean> data) {

    }

    @Override
    public void loadDataFinish() {

    }
}
