package com.ayla.hotelsaas.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.events.GroupActionDoneEvent;
import com.ayla.hotelsaas.events.SceneItemEvent;
import com.ayla.hotelsaas.widget.scene_dialog.GroupActionChoosePropertyValueDialog;
import com.ayla.hotelsaas.mvp.present.SceneSettingFunctionDatumSetPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;
import com.ayla.hotelsaas.constant.KEYS;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * 场景创建，选择执行功能点的页面
 * 进入时必须带入参数
 * 1.String property
 * 2.String deviceId
 * 3.type 选择的功能作为条件还是动作。0:条件
 * 4.{@link Boolean editMode} 是否为再编辑模式进入
 * 编辑模式时，必须传入 {@link Long scopeId}
 * 可选参数:
 * 1.{@link com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean.DeviceAction action}  编辑模式时，已选中的条件
 * 2.int editPosition 编辑的item下标
 */
public class SceneSettingFunctionDatumSetActivity extends BaseMvpActivity<SceneSettingFunctionDatumSetView, SceneSettingFunctionDatumSetPresenter> implements SceneSettingFunctionDatumSetView {

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.v_done)
    TextView v_done;
    @BindView(R.id.v_cancel)
    TextView v_cancel;
    private DeviceTemplateBean.AttributesBean attributesBean;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        params.windowAnimations = R.style.main_menu_animstyle;
        params.width = getResources().getDisplayMetrics().widthPixels;
        params.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(params);

        String deviceId = getIntent().getStringExtra("deviceId");
        String property = getIntent().getStringExtra("property");
        String deviceName = getIntent().getStringExtra("deviceName");
        tv_name.setText(deviceName);
        groupId = getIntent().getStringExtra(KEYS.GROUPID);
        attributesBean = (DeviceTemplateBean.AttributesBean) getIntent().getSerializableExtra(KEYS.GROUPACTION);
        if (TextUtils.isEmpty(groupId)) {
            mPresenter.loadFunction(deviceId, property);
        } else {
            if (getIntent().getBooleanExtra("editMode", false)) {
                mPresenter.loadGroupFunction(groupId, property);
            } else
                showGroupFunction();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected SceneSettingFunctionDatumSetPresenter initPresenter() {
        return new SceneSettingFunctionDatumSetPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_function_datum_set;
    }

    @Override
    protected void initView() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.5); //高度设置为屏幕的1.0
        p.alpha = 1.0f; //设置本身透明度
        p.dimAmount = 0.6f; //设置黑暗度
        getWindow().setAttributes(p);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupActionValue(GroupActionDoneEvent event) {
        int type = getIntent().getIntExtra("type", 0);//0:条件
        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        Intent intent = new Intent();
        SceneItemEvent sceneItemEvent = new SceneItemEvent(type == 0, groupId, attributesBean, event.setupCallBackBean);
        if (editMode) {
            sceneItemEvent.editMode = true;
            sceneItemEvent.editPosition = getIntent().getIntExtra("editPosition", -1);
        }
        intent.putExtra("every_data", sceneItemEvent);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void initListener() {
        v_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        v_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment contentFragment = getSupportFragmentManager().findFragmentByTag("content");
                if (contentFragment instanceof ISceneSettingFunctionDatumSet) {
                    String deviceId = getIntent().getStringExtra("deviceId");
                    int type = getIntent().getIntExtra("type", 0);//0:条件
                    boolean editMode = getIntent().getBooleanExtra("editMode", false);
                    Intent intent = new Intent();
                    SceneItemEvent sceneItemEvent = new SceneItemEvent(type == 0, deviceId, attributesBean, ((ISceneSettingFunctionDatumSet) contentFragment).getDatum());
                    if (editMode) {
                        sceneItemEvent.editMode = true;
                        sceneItemEvent.editPosition = getIntent().getIntExtra("editPosition", -1);
                    }
                    intent.putExtra("every_data", sceneItemEvent);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                if (contentFragment instanceof GroupActionChoosePropertyValueDialog) {
                    int type = getIntent().getIntExtra("type", 0);//0:条件
                    boolean editMode = getIntent().getBooleanExtra("editMode", false);
                    Intent intent = new Intent();
                    ISceneSettingFunctionDatumSet.ValueCallBackBean selectData = ((GroupActionChoosePropertyValueDialog) contentFragment).getSelectData();
                    //彩光效果 白光效果，点击确定不设置数据
                    if (attributesBean != null && (TextUtils.equals("light", attributesBean.getCode()) || TextUtils.equals("colorLight", attributesBean.getCode()))) {
                        selectData = null;
                    }
                    SceneItemEvent sceneItemEvent = new SceneItemEvent(type == 0, groupId, attributesBean, selectData);
                    if (editMode) {
                        sceneItemEvent.editMode = true;
                        sceneItemEvent.editPosition = getIntent().getIntExtra("editPosition", -1);
                    }
                    intent.putExtra("every_data", sceneItemEvent);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {//如果是再编辑模式，就进入到 SceneSettingFunctionSelectActivity

        }
    }

    @Override
    public void showFunctions(DeviceTemplateBean.AttributesBean attributesBean) {
        this.attributesBean = attributesBean;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (attributesBean != null && attributesBean.getValue() != null) {
            fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetSingleChooseFragment.newInstance(attributesBean), "content");
        } else if (attributesBean != null && attributesBean.getSetup() != null) {
            int type = getIntent().getIntExtra("type", 0);//0:条件1动作

            Double max = attributesBean.getSetup().getMax();
            Double min = attributesBean.getSetup().getMin();
            Double step = attributesBean.getSetup().getStep();
            String unit = attributesBean.getSetup().getUnit();

            int count = 0;
            for (double i = min; i <= max; i += step) {
                count++;
            }
            if (count > 20) {//大于20个选项范围，使用手动填写
                if (!TextUtils.isEmpty(unit) && ("k".equalsIgnoreCase(unit) || "lm".equalsIgnoreCase(unit) || "SAT".equalsIgnoreCase(unit))) {
                    fragmentTransaction.replace(R.id.fl_container, SceneSettingLmKSatSetRangeFragment.newInstance(attributesBean), "content");
                } else {
                    fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetBigValueSelectFragment.newInstance(type == 0, attributesBean), "content");
                }
            } else {
                if (type == 0) {
                    fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetRangeWithOptionFragment.newInstance(attributesBean), "content");
                } else {
                    fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetRangeFragment.newInstance(attributesBean), "content");
                }
            }
        } else if (attributesBean != null && attributesBean.getBitValue() != null) {
            fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetSingleChooseFragment.newInstance(attributesBean), "content");
        } else {
            fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionEventSingleChooseFragment.newInstance(attributesBean), "content");
        }
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    @Override
    public void getGroupFunctionsSuccess(DeviceTemplateBean.AttributesBean attributesBeans) {
        this.attributesBean = attributesBeans;
        if (attributesBeans != null) {
            List<DeviceTemplateBean.AttributesBean.ValueBean> values = attributesBeans.getValue();
            if (values != null && values.size() > 0) {
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fl_container, GroupActionFunctionFragment.Companion.newInstance(attributesBeans), "content");
//                fragmentTransaction.commitNowAllowingStateLoss();
            }
        }
    }

    private void showGroupFunction() {
        if (attributesBean != null) {
            List<DeviceTemplateBean.AttributesBean.ValueBean> values = attributesBean.getValue();
            if (values != null && values.size() > 0) {
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fl_container, GroupActionFunctionFragment.Companion.newInstance(attributesBean), "content");
//                fragmentTransaction.commitNowAllowingStateLoss();
            }
        }
    }
}
