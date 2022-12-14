package com.ayla.hotelsaas.ui.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionEventSetAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 场景创建，选择执行功能点的页面，单选情况
 */
public class SceneSettingFunctionEventSingleChooseFragment extends BaseMvpFragment implements ISceneSettingFunctionDatumSet {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    private SceneSettingFunctionEventSetAdapter mAdapter;

    public static SceneSettingFunctionEventSingleChooseFragment newInstance(DeviceTemplateBean.AttributesBean attributesBean) {

        Bundle args = new Bundle();
        args.putSerializable("attributeBean", attributesBean);
        SceneSettingFunctionEventSingleChooseFragment fragment = new SceneSettingFunctionEventSingleChooseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene_function_datum_set_single;
    }

    private DeviceTemplateBean.AttributesBean attributesBean;


    private int beanType;//物模板的类型  0：valueBean 1：bitValueBean

    @Override
    protected void initView(View view) {
        mAdapter = new SceneSettingFunctionEventSetAdapter(R.layout.item_scene_setting_function_datum_set);
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
        mRecyclerView.setAdapter(mAdapter);

        attributesBean = (DeviceTemplateBean.AttributesBean) getArguments().getSerializable("attributeBean");

        BaseSceneBean.Action action = (BaseSceneBean.Action) getActivity().getIntent().getSerializableExtra("action");
        BaseSceneBean.DeviceCondition condition = (BaseSceneBean.DeviceCondition) getActivity().getIntent().getSerializableExtra("condition");

        boolean editMode = getActivity().getIntent().getBooleanExtra("editMode", false);

        List<CheckableSupport<DeviceTemplateBean.EventbutesBean>> data = new ArrayList<>();

        boolean isEditChecked = false;

        CheckableSupport<DeviceTemplateBean.EventbutesBean> checkableSupport = new CheckableSupport(attributesBean);
        data.add(checkableSupport);
        if (data.size() > 0 && !isEditChecked) {
            data.get(0).setChecked(true);
        }
        mAdapter.setNewData(data);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (int i = 0; i < adapter.getData().size(); i++) {
                    CheckableSupport bean = (CheckableSupport) adapter.getItem(i);
                    bean.setChecked(i == position);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public CallBackBean getDatum() {
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            CheckableSupport<DeviceTemplateBean.EventbutesBean> item = mAdapter.getItem(i);
            if (item.isChecked()) {
                return new EventCallBackBean(attributesBean);
            }
        }
        return null;
    }
}
