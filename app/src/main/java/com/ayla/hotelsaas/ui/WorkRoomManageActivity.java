package com.ayla.hotelsaas.ui;


import android.content.Intent;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.WorkRoomManagePagerAdapter;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.widget.SpecialAppBar;
import com.google.android.material.tabs.TabLayout;

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
        appBar.setShowPersionCenter();
        appBar.setRightText("分配");
        super.refreshUI();
    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        Intent intent = new Intent(this, DistributionHotelSelectActivity.class);
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
    protected void appBarShowPersonCenter() {
        super.appBarShowPersonCenter();
        Intent intent = new Intent(WorkRoomManageActivity.this, PersonCenterActivity.class);
        startActivity(intent);
    }
}