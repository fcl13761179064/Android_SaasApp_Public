package com.ayla.hotelsaas.ui;

import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.RoomOrderPresenter;
import com.ayla.hotelsaas.mvp.present.WorkOrderPresenter;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;

import java.util.ArrayList;

public class RoomOrderListActivity extends BaseMvpActivity<RoomOrderView, RoomOrderPresenter>   {
    @Override
    protected RoomOrderPresenter initPresenter() {
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

    
}
