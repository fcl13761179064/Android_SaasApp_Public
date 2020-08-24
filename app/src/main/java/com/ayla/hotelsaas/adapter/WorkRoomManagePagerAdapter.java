package com.ayla.hotelsaas.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ayla.hotelsaas.fragment.WorkOrderListFragment;

/**
 * 房间管理页面 ViewPager 的adapter
 * fanchunlei
 */
public class WorkRoomManagePagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;

    public WorkRoomManagePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragments = new Fragment[]{new WorkOrderListFragment(), new WorkOrderListFragment()};
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "房间管理";
        if (position == 1)
            return "项目工单";
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
