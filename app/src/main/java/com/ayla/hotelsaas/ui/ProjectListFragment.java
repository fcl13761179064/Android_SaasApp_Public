package com.ayla.hotelsaas.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ProjectListAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.events.RoomChangedEvent;
import com.ayla.hotelsaas.mvp.present.ProjectListPresenter;
import com.ayla.hotelsaas.mvp.view.ProjectListView;
import com.ayla.hotelsaas.popmenu.PopMenu;
import com.ayla.hotelsaas.popmenu.PopupWindowUtil;
import com.ayla.hotelsaas.popmenu.UserMenu;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.Programe_change_AppBar;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的项目页面
 */
public class ProjectListFragment extends BaseMvpFragment<ProjectListView, ProjectListPresenter> implements ProjectListView {
    private final int REQUEST_CODE_CREATE_PROJECT = 0x10;
    private Programe_change_AppBar appBar;
    private static final int USER_SEARCH = 0;
    private static final int USER_ADD = 1;
    private UserMenu mMenu;


    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;

    private ProjectListAdapter mAdapter;
    private List<String> roomBeans;
    private String saas_saft_img;

    public ProjectListFragment(Programe_change_AppBar appBar) {
        this.appBar = appBar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBar.getTitleLayoutView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });
        EventBus.getDefault().register(this);
    }


    private void initMenu(View view) {
        mMenu = new UserMenu(getContext());
        mMenu.addItem("地产行业", USER_SEARCH);
        mMenu.addItem("地产行业", USER_ADD);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenu.Item item, int position) {
                switch (item.id) {
                    case USER_SEARCH:
                        SharePreferenceUtils.saveString(getActivity(), Constance.SP_SAAS, "1");
                        mPresenter.refresh("2");
                        appBar.setCenterText("地产行业");
                        restartApp(getContext());
                        break;
                    case USER_ADD:
                        mPresenter.refresh("2");
                        appBar.setCenterText("地产行业");
                        restartApp(getContext());
                        break;
                }

            }
        });
        mMenu.showAsDropDown(view);
    }

    //菜单按钮onClick事件
    public void menuClick(View view) {
        final List<String> items = new ArrayList<>();
        items.add("智慧酒店");
        items.add("地产行业");
        final PopupWindowUtil popupWindow = new PopupWindowUtil(getActivity(), items);
        popupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                switch ((int) id) {
                    case USER_SEARCH:
                        SharePreferenceUtils.saveString(getActivity(), Constance.SP_SAAS, "1");
                        mPresenter.refresh("1");
                        appBar.setCenterText("智慧酒店");
                        restartApp(getContext());
                        break;
                    case USER_ADD:
                        SharePreferenceUtils.saveString(getActivity(), Constance.SP_SAAS, "2");
                        mPresenter.refresh("2");
                        appBar.setCenterText("地产行业");
                        restartApp(getContext());
                        break;
                }

            }
        });
        //根据后面的数字 手动调节窗口的宽度
        popupWindow.show(view, 2);
    }

    /**
     * 重启应用
     * @param context
     */
    public static void restartApp(Context context){
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.ayla.hotelsaas");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected ProjectListPresenter initPresenter() {
        return new ProjectListPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_project_fragment_list;
    }

    @Override
    protected void initView(View view) {
        saas_saft_img = SharePreferenceUtils.getString(getActivity(), Constance.SP_SAAS, "1");
        mPresenter.refresh("2");
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
        mAdapter = new ProjectListAdapter(R.layout.item_project_list);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.layout_loading);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setEnableRefresh(false);
    }

    @Override
    protected void initListener() {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                saas_saft_img = SharePreferenceUtils.getString(getActivity(), Constance.SP_SAAS, "1");
                mPresenter.loadData("2");
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                saas_saft_img = SharePreferenceUtils.getString(getActivity(), Constance.SP_SAAS, "1");
                mPresenter.refresh("2");
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                WorkOrderBean.ResultListBean item = mAdapter.getItem(position);
                startActivity(new Intent(getActivity(), ProjectMainActivity.class).putExtra("bean", item));
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void showData(WorkOrderBean data) {
        mAdapter.setEmptyView(R.layout.empty_project_list);
        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);

        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh();
            mAdapter.setNewData(data.getResultList());
        } else if (mSmartRefreshLayout.isLoading()) {
            mSmartRefreshLayout.finishLoadMore();
            mAdapter.addData(data.getResultList());
        } else {
            mAdapter.setNewData(data.getResultList());
        }

        boolean lastPage = data.getCurrentPage() >= data.getTotalPages();
        mSmartRefreshLayout.setNoMoreData(lastPage);
    }

    @Override
    public void onRequestFailed(Throwable throwable) {
        CustomToast.makeText(getContext(), TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
        if (mAdapter.getData().isEmpty()) {//如果是空的数据
            mAdapter.setEmptyView(R.layout.widget_empty_view);
            mAdapter.getEmptyView().findViewById(R.id.bt_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.setEmptyView(R.layout.layout_loading);
                    mPresenter.refresh("2");
                }
            });
        } else {
            mSmartRefreshLayout.setEnableRefresh(true);
            mAdapter.setEmptyView(R.layout.empty_project_list);
        }
        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh(false);
        } else if (mSmartRefreshLayout.isLoading()) {
            mSmartRefreshLayout.finishLoadMore(false);
        }
    }


    @OnClick(R.id.bt_add)
    void handleAdd() {
        startActivityForResult(new Intent(getContext(), CreateProjectActivity.class), REQUEST_CODE_CREATE_PROJECT);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RenameSuccess(RoomChangedEvent event) {
        mPresenter.refresh("2");
    }

}
