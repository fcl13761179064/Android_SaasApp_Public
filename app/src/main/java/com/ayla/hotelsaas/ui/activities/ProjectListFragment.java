package com.ayla.hotelsaas.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ProjectListAdapter;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.events.RefreshDataEvent;
import com.ayla.hotelsaas.events.RoomChangedEvent;
import com.ayla.hotelsaas.mvp.present.ProjectListPresenter;
import com.ayla.hotelsaas.mvp.view.ProjectListView;
import com.ayla.hotelsaas.widget.popmenu.PopupWindowUtil;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.blankj.utilcode.util.SizeUtils;
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
    private static final int USER_SEARCH = 0;
    private static final int USER_ADD = 1;

    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.bt_add)
    ImageButton bt_add;
    private ProjectListAdapter mAdapter;
    private String saas_saft_img;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ProjectListActivity projectListActivity = (ProjectListActivity)context;
        if (projectListActivity.change_center_title != null) {
            projectListActivity.change_center_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClick(v);
                }
            });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    //菜单按钮onClick事件
    public void menuClick(View view) {
        try {
            final List<String> items = new ArrayList<>();
            items.add("智慧酒店");
            items.add("地产行业");
            final PopupWindowUtil popupWindow = new PopupWindowUtil(getActivity(), items);
            popupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    popupWindow.dismiss();
                    String title_type = SharePreferenceUtils.getString(getContext(), ConstantValue.SP_SAAS, "1");
                    switch ((int) id) {
                        case USER_SEARCH:
                            if ("1".equals(title_type)) {
                                return;
                            }
                            SharePreferenceUtils.saveString(getActivity(), ConstantValue.SP_SAAS, "1");
                            mPresenter.refresh("1","1");
                            restartApp(getContext());
                            break;
                        case USER_ADD:
                            if ("2".equals(title_type)) {
                                return;
                            }
                            SharePreferenceUtils.saveString(getActivity(), ConstantValue.SP_SAAS, "2");
                            mPresenter.refresh("2","1");
                            restartApp(getContext());
                            break;
                    }

                }
            });
            //根据后面的数字 手动调节窗口的宽度
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            popupWindow.setOff(190,0);
            popupWindow.show(view, width/3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启应用
     *
     * @param context
     */
   /* public static void restartApp(Context context) {

        final Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.ayla.hotelsaas");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }*/
    public static void restartApp(Context context) {
        String title_type = SharePreferenceUtils.getString(context, ConstantValue.SP_SAAS, "1");
        if ("1".equalsIgnoreCase(title_type)) {
            CustomToast.makeText(context, "切换到智慧酒店", R.drawable.ic_toast_warning);
        } else {
            CustomToast.makeText(context, "切换到地产行业", R.drawable.ic_toast_warning);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 1000);// 1秒钟后重启应用

       /* // 获取启动的intent
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        // 设置杀死应用后2秒重启
        AlarmManager mgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis()+2000, restartIntent);
        android.os.Process.killProcess(android.os.Process.myPid());*/

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
        saas_saft_img = SharePreferenceUtils.getString(getActivity(), ConstantValue.SP_SAAS, "1");
        setRefreshData();
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

                setLoadData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                setRefreshData();
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
        CustomToast.makeText(getContext(), TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        if (mAdapter.getData().isEmpty()) {//如果是空的数据
            mAdapter.setEmptyView(R.layout.widget_empty_view);
            mAdapter.getEmptyView().findViewById(R.id.bt_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.setEmptyView(R.layout.layout_loading);
                    setRefreshData();
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

    public void setLoadData() {
        saas_saft_img = SharePreferenceUtils.getString(getActivity(), ConstantValue.SP_SAAS, "1");
        if ("1".equalsIgnoreCase(saas_saft_img)) {
            mPresenter.loadData("1","1");
        } else {
            mPresenter.loadData("2","1");
        }
    }

    public void setRefreshData() {
        saas_saft_img = SharePreferenceUtils.getString(getActivity(), ConstantValue.SP_SAAS, "1");
        if ("1".equalsIgnoreCase(saas_saft_img)) {
            mPresenter.refresh("1","1");
        } else {
            mPresenter.refresh("2","1");
        }
    }

    @OnClick(R.id.bt_add)
    void handleAdd() {
        Intent startActivity = new Intent(getContext(), CreateProjectActivity.class);
        startActivity.putExtra("project_type", saas_saft_img);
        startActivityForResult(startActivity, REQUEST_CODE_CREATE_PROJECT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(RefreshDataEvent event) {
        setRefreshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DeviceRemoved(DeviceRemovedEvent event) {
        setRefreshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RenameSuccess(RoomChangedEvent event) {
        setRefreshData();
    }

}
