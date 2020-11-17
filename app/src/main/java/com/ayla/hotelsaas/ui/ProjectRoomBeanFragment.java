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
import com.ayla.hotelsaas.bean.TreeListBean;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
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

    public static ProjectRoomBeanFragment newInstance(Observer observer, ArrayList<TreeListBean> treeListBeans) {

        Bundle args = new Bundle();
        args.putSerializable("treeListBeans", treeListBeans);
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

    BaseQuickAdapter<TreeListBean, BaseViewHolder> mAdapter;

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
        mAdapter = new BaseQuickAdapter<TreeListBean, BaseViewHolder>(R.layout.item_project_room_list) {
            @Override
            protected void convert(BaseViewHolder helper, TreeListBean item) {
                helper.setText(R.id.tv, item.getContentName());
            }
        };
        mAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TreeListBean item = mAdapter.getItem(position);
                observer.update(null, item);
            }
        });
    }

    @Override
    protected void initData() {
        ArrayList<TreeListBean> treeListBeans = (ArrayList<TreeListBean>) getArguments().getSerializable("treeListBeans");
        mAdapter.setNewData(treeListBeans);
    }
}
