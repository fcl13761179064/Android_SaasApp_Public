package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DistributionStructAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.mvp.present.DistributionStructSelectPresenter;
import com.ayla.hotelsaas.mvp.view.DistributionStructSelectView;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;

/**
 * 分配房间,楼层选择页面
 * 进入时传入参数
 * 1.{@link String hotelId}
 * 1.{@link String hotelName}
 */
public class DistributionStructSelectActivity extends BaseMvpActivity<DistributionStructSelectView, DistributionStructSelectPresenter> implements DistributionStructSelectView {
    @BindView(R.id.rv_rooms)
    RecyclerView mRecyclerView;

    private DistributionStructAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_distribution_struct_select;
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DistributionStructAdapter(null);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
    }

    private Stack<List<TreeListBean>> stackMap = new Stack<>();//维护调
    private Stack<TreeListBean> historyMap = new Stack<>();

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TreeListBean treeListBean = mAdapter.getItem(position);
                historyMap.push(treeListBean);
                List<TreeListBean> children = treeListBean.getChildren();
                if (children != null && !children.isEmpty()) {
                    stackMap.push(mAdapter.getData());
                    mAdapter.setNewData(children);
                } else {//下面没有节点了，就要判断是否是房间类型
                    if (structIsRoom(treeListBean.getId())) {
                        Intent intent = new Intent(DistributionStructSelectActivity.this, DistributionActivity.class);
                        intent.putExtra("names", getHistoryRote());
                        intent.putExtra("isRoom", true);
                        intent.putExtras(getIntent());
                        startActivityForResult(intent, 0);
                        historyMap.pop();
                    } else {
                        jumpEmpty();
                    }
                }
            }
        });
    }

    @Override
    protected DistributionStructSelectPresenter initPresenter() {
        return new DistributionStructSelectPresenter();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String hotelId = getIntent().getStringExtra("hotelId");
        mPresenter.fetchTransferTreeList(hotelId);
    }

    List<TransferRoomListBean.RecordsBean> roomListBean;

    @Override
    public void showData(List<TreeListBean> treeListBeans, List<TransferRoomListBean.RecordsBean> recordsBeans) {
        this.roomListBean = recordsBeans;
        if (treeListBeans != null && !treeListBeans.isEmpty()) {
            mAdapter.setNewData(treeListBeans);
        } else {
            jumpEmpty();
        }
    }

    @Override
    protected void onBackPress() {
        if (!historyMap.empty()) {
            historyMap.pop();
        }
        if (!stackMap.empty()) {
            List<TreeListBean> pop = stackMap.pop();
            mAdapter.setNewData(pop);
        } else {
            super.onBackPress();
        }
    }

    private void jumpEmpty() {
        mAdapter.setNewData(null);
        mAdapter.setEmptyView(R.layout.empty_distruction_struct_select);
        mAdapter.getEmptyView().findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DistributionStructSelectActivity.this, DistributionActivity.class);
                intent.putExtras(getIntent());
                intent.putExtra("names", getHistoryRote());
                startActivityForResult(intent, 0);
            }
        });
    }

    private String getHistoryRote() {
        StringBuilder sb = new StringBuilder();//层级名称
        ArrayList<TreeListBean> treeListBeans = new ArrayList<>(historyMap);
        Iterator<TreeListBean> iterator = treeListBeans.iterator();
        while (iterator.hasNext()) {
            TreeListBean next = iterator.next();
            sb.append(next.getContentName());
            if (iterator.hasNext()) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    private boolean structIsRoom(String structId) {
        boolean isRoom = false;
        if (roomListBean != null) {
            for (TransferRoomListBean.RecordsBean recordsBean : roomListBean) {
                if (TextUtils.equals(recordsBean.getId(), structId)) {
                    isRoom = true;
                    break;
                }
            }
        }
        return isRoom;
    }
}
