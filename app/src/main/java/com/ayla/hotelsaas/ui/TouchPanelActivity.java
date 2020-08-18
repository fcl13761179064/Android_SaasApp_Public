package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.TouchPanelAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.bean.TouchPanelBean;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.ayla.hotelsaas.widget.AppBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TouchPanelActivity extends BasicActivity {
    @BindView(R.id.gridView)
    GridView mGridView;

    @BindView(R.id.appBar)
    AppBar appBar;
    private List<TouchPanelBean> dataList;
    private TouchPanelAdapter mAdapter;

    @Override
    public void refreshUI() {
        appBar.setCenterText("触控面板");
        super.refreshUI();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.touch_panel_layout;
    }

    @Override
    protected void initView() {
        dataList = new ArrayList<>();
        dataList.add(new TouchPanelBean(R.mipmap.go_home, "场景", 0, 0,0));
        dataList.add(new TouchPanelBean(R.mipmap.back_home, "地暖", 0, 0,0));
        dataList.add(new TouchPanelBean(R.mipmap.sleep_model, "空调", 0, 0,0));
        dataList.add(new TouchPanelBean(R.mipmap.storm_model, "新风", 0, 0,0));
        mAdapter = new TouchPanelAdapter(dataList);
        mGridView.setAdapter(mAdapter);

    }

    @Override
    protected void initListener() {
        appBar.rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TouchPanelActivity.this, DeviceMoreActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MyApplication.getContext(), TouchPanelSelectActivity.class);
                    intent.putExtras(getIntent());
                    startActivity(intent);
                } else {
                    ToastUtils.showShortToast("该功能未开发");
                }
            }
        });
    }
}
