package com.ayla.hotelsaas.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

/**
 * 酒店层级页面
 */
public class ProjectRoomBeanFragment extends BaseMvpFragment {
    @BindView(R.id.rv_condition)
    RecyclerView mRecyclerView;

    private Observer observer;

    public ProjectRoomBeanFragment(Observer observer) {
        this.observer = observer;
    }

    public static ProjectRoomBeanFragment newInstance(Observer observer) {

        Bundle args = new Bundle();
        ProjectRoomBeanFragment fragment = new ProjectRoomBeanFragment(observer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_project_room_bean;
    }

    BaseQuickAdapter<Object, BaseViewHolder> mAdapter;

    @Override
    protected void initView(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mAdapter = new BaseQuickAdapter<Object, BaseViewHolder>(R.layout.item_project_room_list) {
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                helper.setText(R.id.tv, item.toString());
            }
        };
        mAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                observer.update(null, 1);
            }
        });
    }

    @Override
    protected void initData() {
        ArrayList<Object> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(getTag() + " " + i);
        }
        mAdapter.setNewData(data);
    }
}
