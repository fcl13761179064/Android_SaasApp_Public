package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingFunctionDatumSetSingleChoosePresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 场景创建，选择执行功能点的页面，单选情况
 */
public class SceneSettingFunctionDatumSetSingleChooseFragment extends BaseMvpFragment<SceneSettingFunctionDatumSetView, SceneSettingFunctionDatumSetSingleChoosePresenter> implements SceneSettingFunctionDatumSetView, ISceneSettingFunctionDatumSet {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    private SceneSettingFunctionDatumSetAdapter mAdapter;

    public static SceneSettingFunctionDatumSetSingleChooseFragment newInstance(DeviceListBean.DevicesBean devicesBean, DeviceTemplateBean.AttributesBean attributesBean) {

        Bundle args = new Bundle();
        args.putSerializable("deviceBean", devicesBean);
        args.putSerializable("attributeBean", attributesBean);
        SceneSettingFunctionDatumSetSingleChooseFragment fragment = new SceneSettingFunctionDatumSetSingleChooseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SceneSettingFunctionDatumSetSingleChoosePresenter initPresenter() {
        return new SceneSettingFunctionDatumSetSingleChoosePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene_function_datum_set_single;
    }

    @Override
    protected void initView(View view) {
        mAdapter = new SceneSettingFunctionDatumSetAdapter(R.layout.item_scene_setting_function_datum_set);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .showLastDivider()
                .color(android.R.color.transparent).size(AutoSizeUtils.dp2px(getContext(), 1)).build());
        mRecyclerView.setAdapter(mAdapter);

        DeviceTemplateBean.AttributesBean attributesBean = (DeviceTemplateBean.AttributesBean) getArguments().getSerializable("attributeBean");
        DeviceListBean.DevicesBean deviceBean = (DeviceListBean.DevicesBean) getArguments().getSerializable("deviceBean");
        mPresenter.loadFunction(deviceBean, attributesBean);
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
    public void showFunctions(List<CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean>> data) {
        mAdapter.setNewData(data);
    }

    @Override
    public SceneSettingFunctionDatumSetAdapter.DatumBean getDatum() {
        for (CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean> datum : mAdapter.getData()) {
            if (datum.isChecked()) {
                return datum.getData();
            }
        }
        return null;
    }
}
