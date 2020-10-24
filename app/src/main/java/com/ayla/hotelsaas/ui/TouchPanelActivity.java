package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.TouchPanelAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.TouchPanelBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.ayla.hotelsaas.widget.AppBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 触控面板页面
 */
public class TouchPanelActivity extends BaseMvpActivity {
    @BindView(R.id.gridView)
    GridView mGridView;

    @BindView(R.id.appBar)
    AppBar appBar;
    private List<TouchPanelBean> dataList;
    private TouchPanelAdapter mAdapter;
    @BindView(R.id.iv_touch_panel)
    ImageView mImageView;

    @Override
    protected int getLayoutId() {
        return R.layout.touch_panel_layout;
    }

    @Override
    protected void initView() {
        appBar.setCenterText("触控面板");

        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(getIntent().getStringExtra("deviceId"));
        ImageLoader.loadImg(mImageView, devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);

        dataList = new ArrayList<>();
        dataList.add(new TouchPanelBean(R.mipmap.go_home, "场景", 1));
        dataList.add(new TouchPanelBean(R.mipmap.back_home, "地暖", 2));
        dataList.add(new TouchPanelBean(R.mipmap.sleep_model, "空调", 3));
        dataList.add(new TouchPanelBean(R.mipmap.storm_model, "新风", 4));
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
                startActivityForResult(intent, 1001);
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MyApplication.getContext(), TouchPanelSelectActivity.class);
                    intent.putExtras(getIntent());
                    intent.putExtra("pannel_type", 1);
                    startActivity(intent);
                } else {
                    ToastUtils.showShortToast("该功能未开发");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
