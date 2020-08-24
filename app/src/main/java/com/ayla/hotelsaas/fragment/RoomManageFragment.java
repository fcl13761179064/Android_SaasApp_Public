package com.ayla.hotelsaas.fragment;

import android.view.View;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.mvp.present.RoomOrderPresenter;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;

public class RoomManageFragment extends BaseMvpFragment<RoomOrderView, RoomOrderPresenter> implements RoomOrderView  {


    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }


    @Override
    public void loadDataSuccess(RoomOrderBean data) {

    }

    @Override
    public void loadDataFinish() {

    }

    @Override
    public void getAuthCodeSuccess(String  data) {

    }

    @Override
    protected RoomOrderPresenter initPresenter() {
        return null;
    }
}
