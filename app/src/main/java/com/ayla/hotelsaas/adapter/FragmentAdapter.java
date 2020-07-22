package com.ayla.hotelsaas.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 功能：viewpager 添加　fragments 的适配器
 *
 * @author : fanchunlei
 * @created at : 20-7-20
 * @time : 下午7:14
 */
public final class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public FragmentAdapter(List<Fragment> fragments, FragmentManager fm) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {

        return mFragments.size();
    }
}

