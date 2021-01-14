package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.mvp.present.ProjectRoomsPresenter;
import com.ayla.hotelsaas.mvp.view.ProjectRoomsView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

/**
 * 酒店层级页面
 */
public class ProjectRoomsFragment extends BaseMvpFragment<ProjectRoomsView, ProjectRoomsPresenter> implements Observer, ProjectRoomsView {
    public static ProjectRoomsFragment newInstance(WorkOrderBean.ResultListBean bean) {
        Bundle args = new Bundle();
        args.putSerializable("bean", bean);
        ProjectRoomsFragment fragment = new ProjectRoomsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.fl_container_01)
    FrameLayout mFrameLayout;

    @Override
    protected ProjectRoomsPresenter initPresenter() {
        return new ProjectRoomsPresenter();
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

    private WorkOrderBean.ResultListBean bean;

    @Override
    protected void initData() {
        bean = (WorkOrderBean.ResultListBean) getArguments().getSerializable("bean");
        loadData();
    }

    private void loadData() {

        mFrameLayout.addView(LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null));
        mPresenter.loadData(bean.getId(),bean.getBusinessId());
    }

    TabLayout mTabLayout;

    @Override
    public void showData(List<TreeListBean> treeListBeans) {
        mFrameLayout.removeViewAt(mFrameLayout.getChildCount() - 1);

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
        TreeListBean treeListBean = new TreeListBean();
        treeListBean.setChildren(treeListBeans);
        treeListBean.setContentName(bean.getTitle());
        treeListBean.setId(bean.getBusinessId());
        handleSelect(treeListBean);
    }


    @Override
    public void loadDataFailed(Throwable throwable) {
        mFrameLayout.removeViewAt(mFrameLayout.getChildCount() - 1);

        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.widget_empty_view, null);
        mFrameLayout.addView(errorView);
        errorView.findViewById(R.id.bt_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrameLayout.removeViewAt(mFrameLayout.getChildCount() - 1);
                loadData();
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        TreeListBean treeListBean = (TreeListBean) arg;
        handleSelect(treeListBean);
    }

    private void handleSelect(TreeListBean treeListBean) {
        List<TreeListBean> children = treeListBean.getChildren();
        if (children == null || children.isEmpty()) {//如果没有叶子节点了，就进入主页面
            Intent intent = new Intent(getContext(), MainActivity.class);
            long roomId = Long.parseLong(treeListBean.getId());
            String roomName = treeListBean.getContentName();
            intent.putExtra("roomId", roomId);
            intent.putExtra("roomName", roomName);
            intent.putExtra("roomTypeId", treeListBean.getRoomTypeId());
            intent.putExtra("removeEnable", bean.getType() != 0);//展箱、展厅 说明是自己创建的。
            startActivity(intent);
        } else {
            String title = treeListBean.getContentName();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            //首先把现有的fragment全部hidden
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                fragmentTransaction.hide(fragment);
            }

            //创建新的fragment
            ProjectRoomBeanFragment fragment = ProjectRoomBeanFragment.newInstance(this, new ArrayList<>(children));
            fragmentTransaction.add(R.id.fl_container, fragment, title).commitNow();

            //创建新的tab
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_tab, null);
            TextView textView = view.findViewById(R.id.tv);
            textView.setText(fragment.getTag());
            TabLayout.Tab tab = mTabLayout.newTab().setCustomView(view);
            mTabLayout.addTab(tab, false);
            mTabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tab.select();
                }
            });
        }
    }
}
