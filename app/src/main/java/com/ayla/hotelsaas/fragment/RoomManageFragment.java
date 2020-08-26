package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.RoomManageAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.mvp.present.RoomManagePresenter;
import com.ayla.hotelsaas.mvp.view.RoomManageView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.MainActivity;
import com.ayla.hotelsaas.ui.RoomOrderListActivity;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.RecycleViewDivider;
import com.ayla.hotelsaas.widget.ValueChangeDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class RoomManageFragment extends BaseMvpFragment<RoomManageView, RoomManagePresenter> implements RoomManageView {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private RoomManageAdapter mAdapter;
    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;

    @Override
    protected int getLayoutId() {
        return R.layout.room_manage_fragment;
    }

    @Override
    protected void initView(View view) {
        //是否在刷新的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenRefresh(true);
        //是否在加载的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenLoading(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, 3, R.color.all_bg_color));
        mAdapter = new RoomManageAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
    }

    @Override
    protected void initListener() {
        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FastClickUtils.isDoubleClick()) {
                    ValueChangeDialog
                            .newInstance(new ValueChangeDialog.DoneCallback() {
                                @Override
                                public void onDone(DialogFragment dialog, String txt) {
                                    if (TextUtils.isEmpty(txt)) {
                                        CustomToast.makeText(getContext(), "修改设备名称不能为空", R.drawable.ic_toast_warming).show();
                                    } else {
                                        mPresenter.createRoomNum(txt);
                                    }
                                    dialog.dismissAllowingStateLoss();
                                }
                            })
                            .setEditValue("")
                            .setTitle("创建新房间")
                            .setEditHint("请输入房间名称")
                            .setMaxLength(20)
                            .show(getFragmentManager(), "room_num");
                }

            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!FastClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    RoomManageBean.RecordsBean recordsBean = mAdapter.getData().get(position);
                    String roomId = recordsBean.getId();
                    long long_room_id = Long.parseLong(roomId);
                    String roomName = recordsBean.getContentName();
                    intent.putExtra("roomId", long_room_id);
                    intent.putExtra("roomName", roomName);
                    startActivity(intent);
                }
            }
        });


        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (null != mAdapter.getData()) {
                    mAdapter.removeAllFooterView();
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                }
                if (mPresenter != null) {
                    mPresenter.loadFistPage();
                }
                mRefreshLayout.setEnableLoadMore(true);

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadNextPage();
                }
            }
        });
        mRefreshLayout.autoRefresh();//自动刷新
    }

    @Override
    protected void initData() {

    }

    @Override
    public void loadDataSuccess(RoomManageBean data) {
        final List<RoomManageBean.RecordsBean> records = data.getRecords();
        if (records != null) {
            if (records.isEmpty()) {
                if (mAdapter.getData().isEmpty()) {
                    mAdapter.setEmptyView(R.layout.empty_work_order);
                }
                final View inflate = LayoutInflater.from(getContext()).inflate(R.layout.room_root_view, null);
                mAdapter.setFooterView(inflate);
                mRefreshLayout.setEnableLoadMore(false);
            } else {
                mAdapter.addData(records);
            }
        } else {
            final View inflate = LayoutInflater.from(getContext()).inflate(R.layout.room_root_view, null);
            mAdapter.setFooterView(inflate);
            mRefreshLayout.setEnableLoadMore(false);
        }
        loadDataFinish();
    }

    @Override
    public void createRoomSuccess(String data) {
        if (null != mAdapter.getData()) {
            mAdapter.removeAllFooterView();
            mAdapter.getData().clear();
            mAdapter.notifyDataSetChanged();
        }
        if (mPresenter != null) {
            mPresenter.loadFistPage();
        }
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

    @Override
    protected RoomManagePresenter initPresenter() {
        return new RoomManagePresenter();
    }
}
