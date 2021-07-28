package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionSelectAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingFunctionSelectPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionSelectView;
import com.ayla.hotelsaas.widget.AppBar;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 场景创建，选择功能菜单的页面
 * 进入时，必须带上:
 * 1.long scopeId
 * 2.int type  ，0：condition  1：action
 * 3.String deviceId
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

    private DeviceListBean.DevicesBean deviceBean;

    @Override
    protected void initView() {
        deviceBean = MyApplication.getInstance().getDevicesBean(getIntent().getStringExtra("deviceId"));
        appBar.setCenterText("选择功能");
        mAdapter = new SceneSettingFunctionSelectAdapter(R.layout.item_scene_setting_function_select);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceTemplateBean.AttributesBean attributesBean = mAdapter.getItem(position);
                jumpNext(false, attributesBean);
            }
        });
    }

    private void jumpNext(boolean autoJump, DeviceTemplateBean.AttributesBean attributesBean) {

       // boolean nest = true;
        int type = getIntent().getIntExtra("type", 0);//选择的功能作为条件还是动作。
        if (type == 0) {//条件
            int ruleSetMode = (int) getIntent().getIntExtra("ruleSetMode", BaseSceneBean.RULE_SET_MODE.ANY);//选择的功能作为条件还是动作。
            if (ruleSetMode == BaseSceneBean.RULE_SET_MODE.ANY) {//满足任意
               // nest = false;
            }
        }

        /*
        * 不可重复添加关闭
        */
     /*   if (nest) {//满足所有时，不可以重复添加
            ArrayList<String> selectedDatum = getIntent().getStringArrayListExtra("selectedDatum");
            String editProperty = getIntent().getStringExtra("property");//编辑时 ，原来的属性名。
            if (selectedDatum != null) {
                for (String s : selectedDatum) {
                    String[] split = s.split(" ");
                    String dsn = split[0];
                    String property = split[1];
                    if (deviceBean != null && deviceBean.getDeviceId() != null) {
                        if (TextUtils.equals(deviceBean.getDeviceId(), dsn)) {
                            if (TextUtils.equals(attributesBean.getCode(), property)) {
                                if (TextUtils.equals(editProperty, property)) {
                                    break;
                                }
                                CustomToast.makeText(getBaseContext(), "不可重复添加", R.drawable.ic_toast_warming);
                                return;
                            }
                        }
                    }
                }
            }
        }*/

        Intent mainActivity = new Intent(SceneSettingFunctionSelectActivity.this, SceneSettingFunctionDatumSetActivity.class);
        mainActivity.putExtras(getIntent());
        mainActivity.putExtra("autoJump", autoJump);
        if (deviceBean != null && deviceBean.getDeviceId() != null) {
            mainActivity.putExtra("deviceId", deviceBean.getDeviceId());
        } else {
            mainActivity.putExtra("deviceId", "0");
        }
        mainActivity.putExtra("type", type);
        mainActivity.putExtra("property", attributesBean.getCode());
        startActivityForResult(mainActivity, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra("type", 0);//选择的功能作为条件还是动作。
        if (deviceBean != null && deviceBean.getDeviceId() != null) {
            String deviceId = deviceBean.getDeviceId();
            mPresenter.loadFunction(type == 0, deviceId, deviceBean.getPid());
        } else {
            String deviceId = "0";
            String pid = "0";
            mPresenter.loadFunction(type == 0, deviceId, pid);
        }

    }

    @Override
    public void showFunctions(List<DeviceTemplateBean.AttributesBean> data) {
        mAdapter.setNewData(data);

        BaseSceneBean.DeviceAction deviceAction = (BaseSceneBean.DeviceAction) getIntent().getSerializableExtra("action");
        BaseSceneBean.DeviceCondition deviceCondition = (BaseSceneBean.DeviceCondition) getIntent().getSerializableExtra("condition");

        String propertyCode = null;
        if (deviceAction != null) {
            propertyCode = deviceAction.getLeftValue();
        } else if (deviceCondition != null) {
            propertyCode = deviceCondition.getLeftValue();
        }

        boolean autoJump = getIntent().getBooleanExtra("autoJump", false);
        if (autoJump) {
            if (!TextUtils.isEmpty(propertyCode)) {//如果是编辑，就要直接跳转进去
                if (data != null) {
                    for (int i = 0; i < data.size(); i++) {
                        DeviceTemplateBean.AttributesBean attributesBean = data.get(i);
                        if (TextUtils.equals(attributesBean.getCode(), propertyCode)) {
                            jumpNext(true, attributesBean);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {//如果是再编辑模式
            Intent intent = new Intent(this, SceneSettingDeviceSelectActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        }
    }
}
