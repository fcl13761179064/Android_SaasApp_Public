package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.adapter.SwitchDefaultListAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.databinding.ActivitySwitchDefaultListBinding;
import com.ayla.hotelsaas.mvp.present.SwitchDefaultListPresenter;
import com.ayla.hotelsaas.mvp.view.SwitchDefaultListView;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数
 * String deviceId
 */
public class SwitchDefaultListActivity extends BaseMvpActivity<SwitchDefaultListView, SwitchDefaultListPresenter> implements SwitchDefaultListView {
    private ActivitySwitchDefaultListBinding mBinding;
    private SwitchDefaultListAdapter mAdapter;

    @Override
    protected SwitchDefaultListPresenter initPresenter() {
        return new SwitchDefaultListPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivitySwitchDefaultListBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initView() {
        mBinding.rl.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rl.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mAdapter = new SwitchDefaultListAdapter();
        mAdapter.bindToRecyclerView(mBinding.rl);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SwitchDefaultListAdapter.Bean item = mAdapter.getItem(position);
                String propertyCode = item.getPropertyCode();
                Intent intent = new Intent(SwitchDefaultListActivity.this, SwitchDefaultSettingActivity.class);
                intent.putExtra("propertyCode", propertyCode).putExtra("deviceId", deviceId);
                startActivity(intent);
            }
        });
    }

    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getStringExtra("deviceId");
        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        if (devicesBean != null) {
            mPresenter.getSwitchDefaultSetting(devicesBean.getDeviceId(), devicesBean.getPid());
        }
    }

    @Override
    public void showData(List<String[]> result) {
        List<SwitchDefaultListAdapter.Bean> data = new ArrayList<>();
        for (String[] attributesBeans : result) {
            SwitchDefaultListAdapter.Bean bean = new SwitchDefaultListAdapter.Bean();
            bean.setPropertyCode(attributesBeans[0]);
            bean.setPropertyName(attributesBeans[1]);
            data.add(bean);
        }
        mAdapter.setNewData(data);
    }
}
