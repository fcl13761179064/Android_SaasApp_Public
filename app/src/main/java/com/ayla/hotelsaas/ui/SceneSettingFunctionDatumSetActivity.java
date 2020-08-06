package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingFunctionDatumSetPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 场景创建，选择执行功能点的页面
 * 进入时必须带入参数
 * 1.{@link DeviceTemplateBean.AttributesBean} attributesBean
 * 2.{@link DeviceListBean.DevicesBean} deviceBean
 */
public class SceneSettingFunctionDatumSetActivity extends BaseMvpActivity<SceneSettingFunctionDatumSetView, SceneSettingFunctionDatumSetPresenter> implements SceneSettingFunctionDatumSetView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    @BindView(R.id.appBar)
    AppBar appBar;
    private SceneSettingFunctionDatumSetAdapter mAdapter;

    @Override
    protected SceneSettingFunctionDatumSetPresenter initPresenter() {
        return new SceneSettingFunctionDatumSetPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zigbee_add_select_gateway;
    }

    @Override
    protected void initView() {
        appBar.setCenterText("选择功能");
        appBar.setRightText("完成");
        mAdapter = new SceneSettingFunctionDatumSetAdapter(R.layout.item_scene_setting_function_datum_set);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .color(android.R.color.transparent).size(AutoSizeUtils.dp2px(this, 1)).build());
        mRecyclerView.setAdapter(mAdapter);
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
        appBar.rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                for (CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean> datum : mAdapter.getData()) {
                    if (datum.isChecked()) {
                        data.putExtra("result", datum.getData());
                        break;
                    }
                }

                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceTemplateBean.AttributesBean attributesBean = (DeviceTemplateBean.AttributesBean) getIntent().getSerializableExtra("attributesBean");
        DeviceListBean.DevicesBean deviceBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("deviceBean");
        mPresenter.loadFunction(deviceBean, attributesBean);
    }

    @Override
    public void showFunctions(List<CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean>> data) {
        mAdapter.setNewData(data);
    }
}
