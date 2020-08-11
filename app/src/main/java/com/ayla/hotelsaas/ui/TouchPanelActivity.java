package com.ayla.hotelsaas.ui;

import android.widget.GridView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.TouchPanelBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TouchPanelActivity extends BaseMvpActivity {
    private static final String SAVA_KEY = "sava_key";
    @BindView(R.id.gridView)
    GridView mGridView;
    private List<TouchPanelBean> dataList;
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.touch_panel_layout;
    }

    @Override
    protected void initView() {
        /*dataList = new ArrayList<>();
        dataList.add(new TouchPanelBean(R.drawable.ic_todo, "待办事项"));
        dataList.add(new TouchPanelBean(R.drawable.ic_sales, "项目分析"));
        dataList.add(new TouchPanelBean(R.drawable.ic_tickets, "预留预购"));
        dataList.add(new TouchPanelBean(R.drawable.ic_voucher, "凭证管理"));
        mAdapter = new MainAdapter();
        mGridView.setAdapter(mAdapter);*/

    }

    @Override
    protected void initListener() {

    }
}
