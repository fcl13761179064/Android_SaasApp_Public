package com.ayla.hotelsaas.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.blankj.utilcode.util.FragmentUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 酒店层级页面
 */
public class ProjectRoomsFragment extends BaseMvpFragment implements Observer {
    public static ProjectRoomsFragment newInstance() {
        Bundle args = new Bundle();

        ProjectRoomsFragment fragment = new ProjectRoomsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_project_rooms;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        showData();
    }

    TabLayout mTabLayout;

    public void showData() {
        ((ViewStub) getView().findViewById(R.id.vs_content)).inflate();
        mTabLayout = getView().findViewById(R.id.tl_tabs);

        mTabLayout.setTabTextColors(Color.parseColor("#333333"), ContextCompat.getColor(getContext(), R.color.colorAccent));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected: ");
                int position = tab.getPosition();
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                List<Fragment> fragments = getChildFragmentManager().getFragments();
                for (int i = position; i < fragments.size(); i++) {
                    if (i == position) {
                        fragmentTransaction.show(fragments.get(i));
                        continue;
                    }
                    if (i > position) {
                        fragmentTransaction.remove(fragments.get(i));
                        mTabLayout.removeTabAt(position + 1);
                    }
                }
                fragmentTransaction.commitNow();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        handleSelect();
    }

    @Override
    public void update(Observable o, Object arg) {
        handleSelect();
    }

    private void handleSelect() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (getChildFragmentManager().getFragments().size() != 0) {
            Fragment latestFragment = getChildFragmentManager().getFragments().get(getChildFragmentManager().getFragments().size() - 1);
            fragmentTransaction.hide(latestFragment);
        }

        ProjectRoomBeanFragment fragment = ProjectRoomBeanFragment.newInstance(this);
        fragmentTransaction.add(R.id.fl_container, fragment, "AAA").commitNow();

        mTabLayout.addTab(mTabLayout.newTab().setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), true);
    }
}
