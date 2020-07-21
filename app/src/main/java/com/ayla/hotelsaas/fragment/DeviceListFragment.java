package com.ayla.hotelsaas.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class DeviceListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_list_show, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        final SmartRefreshLayout mRefreshLayout = root.findViewById(R.id.device_refreshLayout);
        final RecyclerView recyclerview = root.findViewById(R.id.device_recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        DeviceListAdapter mAdapter = new DeviceListAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setEmptyView(R.layout.empty_device_order);
        mRefreshLayout.setEnableLoadMore(false);
    }
}
