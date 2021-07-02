package com.ayla.hotelsaas.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.events.RoomChangedEvent;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import butterknife.BindView;

/**
 * 酒店层级页面
 */
public class ProjectRoomBeanFragment extends BaseMvpFragment {
    @BindView(R.id.rv_condition)
    RecyclerView mRecyclerView;

    private Observer observer;

    public ProjectRoomBeanFragment() {
    }

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

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
                if (observer == null) {
                    return;
                }
                observer.update(null, item);
            }
        });
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            ArrayList<TreeListBean> treeListBeans = (ArrayList<TreeListBean>) getArguments().getSerializable("treeListBeans");
            mAdapter.setNewData(treeListBeans);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRoomChangedEvent(RoomChangedEvent event) {
        long roomId = event.roomId;
        String newName = event.newName;
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            TreeListBean treeListBean = mAdapter.getItem(i);
            if (treeListBean != null) {
                if (TextUtils.equals(treeListBean.getId(), String.valueOf(roomId))) {
                    treeListBean.setContentName(newName);
                    mAdapter.notifyItemChanged(i);
                    break;
                } else {
                    modifyChild(treeListBean.getChildren(), roomId, newName);
                }
            }
        }
    }

    public void modifyChild(List<TreeListBean> children, long roomId, String newName) {
        if (children != null) {
            for (TreeListBean treeListBean : children) {
                if (treeListBean != null) {
                    if (TextUtils.equals(treeListBean.getId(), String.valueOf(roomId))) {
                        treeListBean.setContentName(newName);
                        return;
                    } else {
                        modifyChild(treeListBean.getChildren(), roomId, newName);
                    }
                }
            }
        }
    }
}
