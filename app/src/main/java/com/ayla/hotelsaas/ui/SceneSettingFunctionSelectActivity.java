package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
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
 * 3.{@link DeviceListBean.DevicesBean} deviceBean
 * 4.int type  ，0：condition  1：action
 * 可选参数
 * 1.selectedDatum {@link ArrayList<String>} 已选择的栏目
 * 2.ruleSetMode  ALL(2,"多条条件全部命中")   ANY(3,"多条条件任一命中")
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

                boolean nest = true;
                int type = getIntent().getIntExtra("type", 0);//选择的功能作为条件还是动作。
                if (type == 0) {//条件
                    int ruleSetMode = getIntent().getIntExtra("ruleSetMode", 2);//选择的功能作为条件还是动作。
                    if (ruleSetMode == 3) {//满足任意
                        nest = false;
                    }
                }

                if (nest) {
                    ArrayList<String> selectedDatum = getIntent().getStringArrayListExtra("selectedDatum");
                    if (selectedDatum != null) {
                        for (String s : selectedDatum) {
                            String[] split = s.split(" ");
                            String dsn = split[0];
                            String property = split[1];

                            if (TextUtils.equals(deviceBean.getDeviceId(), dsn)) {
                                if (TextUtils.equals(attributesBean.getCode(), property)) {
                                    CustomToast.makeText(getBaseContext(), "不可重复添加", R.drawable.ic_toast_warming).show();
                                    return;
                                }
                            }
                        }
                    }
                }

                Intent mainActivity = new Intent(SceneSettingFunctionSelectActivity.this, SceneSettingFunctionDatumSetActivity.class);
                mainActivity.putExtra("deviceBean", deviceBean);
                mainActivity.putExtra("attributeBean", attributesBean);
                mainActivity.putExtra("type", type);
                startActivityForResult(mainActivity, 0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> properties = getIntent().getStringArrayListExtra("properties");
        DeviceListBean.DevicesBean deviceBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("deviceBean");
        mPresenter.loadFunction(deviceBean.getCuId(), deviceBean.getDeviceId(), deviceBean.getDeviceCategory(), properties);
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
