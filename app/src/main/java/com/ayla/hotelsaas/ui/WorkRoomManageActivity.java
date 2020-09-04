package com.ayla.hotelsaas.ui;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.WorkRoomManagePagerAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.WorkOrderPresenter;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.CustomSheet;
import com.ayla.hotelsaas.widget.SpecialAppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class WorkRoomManageActivity extends BasicActivity {
    private final int REQUEST_CODE_DISTRIBUTION_ROOM = 0x10;
    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.appBar)
    SpecialAppBar appBar;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;
    private WorkRoomManagePagerAdapter mMAdapter;

    @Override
    public void refreshUI() {
        appBar.setLeftText("退出");
        appBar.setRightText("分配");
        super.refreshUI();
    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        Intent intent = new Intent(this, DistributionActivity.class);
        startActivityForResult(intent, REQUEST_CODE_DISTRIBUTION_ROOM);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.work_room_manage_activity;
    }

    @Override
    protected void initView() {
        mMAdapter = new WorkRoomManagePagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(mMAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }


    @Override
    protected void initListener() {

    }

    @Override
    protected void mExitApp() {

        CustomAlarmDialog
                .newInstance(new CustomAlarmDialog.Callback() {
                    @Override
                    public void onDone(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                        SharePreferenceUtils.remove(WorkRoomManageActivity.this, Constance.SP_Login_Token);
                        SharePreferenceUtils.remove(WorkRoomManageActivity.this, Constance.SP_Refresh_Token);
                        Intent intent = new Intent(WorkRoomManageActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .setContent(getResources().getString(R.string.sing_out))
                .show(getSupportFragmentManager(), "");
    }
}