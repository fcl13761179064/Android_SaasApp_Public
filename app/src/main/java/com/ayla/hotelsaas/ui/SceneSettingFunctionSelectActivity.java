package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionSelectAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingFunctionSelectPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionSelectView;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 场景创建，选择功能菜单的页面
 * 进入时，必须带上:
 * 1.支持的条件或者功能的propertiesName集合 。{@link java.util.ArrayList<String>}    properties 。
 * 2.{@link String} oemModel
 * 3.{@link DeviceListBean.DevicesBean} deviceBean
 * 可选参数
 * 1.datums {@link ArrayList<com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter.DatumBean>} 已选择的栏目
 */
public class SceneSettingFunctionSelectActivity extends BaseMvpActivity<SceneSettingFunctionSelectView, SceneSettingFunctionSelectPresenter> implements SceneSettingFunctionSelectView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    @BindView(R.id.appBar)
    AppBar appBar;
    private SceneSettingFunctionSelectAdapter mAdapter;

    @Override
    protected SceneSettingFunctionSelectPresenter initPresenter() {
        return new SceneSettingFunctionSelectPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zigbee_add_select_gateway;
    }

    @Override
    protected void initView() {
        appBar.setCenterText("选择功能");
        mAdapter = new SceneSettingFunctionSelectAdapter(R.layout.item_scene_setting_function_select);
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
                DeviceListBean.DevicesBean deviceBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("deviceBean");
                DeviceTemplateBean.AttributesBean attributesBean = mAdapter.getItem(position);
                ArrayList<SceneSettingFunctionDatumSetAdapter.DatumBean> datums = getIntent().getParcelableArrayListExtra("datums");
                if (datums != null) {
                    for (SceneSettingFunctionDatumSetAdapter.DatumBean datumBean : datums) {
                        if (deviceBean.getDeviceId().equals(datumBean.getDeviceId())) {
                            if (attributesBean.getCode().equals(datumBean.getLeftValue())) {
                                CustomToast.makeText(getBaseContext(), "不可重复添加", R.drawable.ic_toast_warming).show();
                                return;
                            }
                        }
                    }
                }
                Intent mainActivity = new Intent(SceneSettingFunctionSelectActivity.this, SceneSettingFunctionDatumSetActivity.class);
                mainActivity.putExtra("deviceBean", deviceBean);
                mainActivity.putExtra("attributeBean", attributesBean);
                startActivityForResult(mainActivity, 0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String oemModel = getIntent().getStringExtra("oemModel");
        ArrayList<String> properties = getIntent().getStringArrayListExtra("properties");
        mPresenter.loadFunction(oemModel, properties);
    }

    @Override
    public void showFunctions(List<DeviceTemplateBean.AttributesBean> data) {
        mAdapter.setNewData(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
