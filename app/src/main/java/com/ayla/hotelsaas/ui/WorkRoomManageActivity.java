package com.ayla.hotelsaas.ui;


import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.WorkRoomManagePagerAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.fragment.RoomManageFragment;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
@Deprecated
public class WorkRoomManageActivity extends BaseMvpActivity {
    private final int REQUEST_CODE_DISTRIBUTION_ROOM = 0x10;
    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;
    private WorkRoomManagePagerAdapter mMAdapter;

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
    protected void appBarLeftIvClicked() {
        Intent intent = new Intent(WorkRoomManageActivity.this, PersonCenterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DISTRIBUTION_ROOM && resultCode == RESULT_OK) {
            String[] rooms = data.getStringArrayExtra("rooms");
            Fragment fragment = mMAdapter.getItem(0);
            if (fragment instanceof RoomManageFragment) {
                ((RoomManageFragment) fragment).initData();
            }
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}