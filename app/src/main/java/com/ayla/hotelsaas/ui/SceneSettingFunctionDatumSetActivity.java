package com.ayla.hotelsaas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.events.SceneItemEvent;
import com.ayla.hotelsaas.mvp.present.SceneSettingFunctionDatumSetPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;
import com.ayla.hotelsaas.widget.AppBar;

import org.greenrobot.eventbus.EventBus;

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
 * 1.{@link com.ayla.hotelsaas.localBean.BaseSceneBean.DeviceAction action}  编辑模式时，已选中的条件
 * 2.int editPosition 编辑的item下标
 */
public class SceneSettingFunctionDatumSetActivity extends BaseMvpActivity<SceneSettingFunctionDatumSetView, SceneSettingFunctionDatumSetPresenter> implements SceneSettingFunctionDatumSetView {
    @BindView(R.id.appBar)
    AppBar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String deviceId = getIntent().getStringExtra("deviceId");
        String property = getIntent().getStringExtra("property");
        mPresenter.loadFunction(deviceId, property);
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
        appBar.setCenterText("选择功能");
        appBar.setRightText("完成");
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void appBarRightTvClicked() {
        Fragment contentFragment = getSupportFragmentManager().findFragmentByTag("content");
        if (contentFragment instanceof ISceneSettingFunctionDatumSet) {
            String deviceId = getIntent().getStringExtra("deviceId");
            int type = getIntent().getIntExtra("type", 0);//0:条件
            boolean editMode = getIntent().getBooleanExtra("editMode", false);

            SceneItemEvent sceneItemEvent = new SceneItemEvent(type == 0, deviceId, attributesBean, ((ISceneSettingFunctionDatumSet) contentFragment).getDatum());
            if (editMode) {
                sceneItemEvent.editMode = true;
                sceneItemEvent.editPosition = getIntent().getIntExtra("editPosition", -1);
            }

            EventBus.getDefault().post(sceneItemEvent);

            setResult(Activity.RESULT_OK, null);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {//如果是再编辑模式，就进入到 SceneSettingFunctionSelectActivity
            Intent intent = new Intent(this, SceneSettingFunctionSelectActivity.class);
            intent.putExtras(getIntent());
            intent.removeExtra("action");
            intent.removeExtra("condition");//再编辑时，清空记录的已选项目
            startActivity(intent);
            finish();
        }
    }

    private DeviceTemplateBean.AttributesBean attributesBean;

    @Override
    public void showFunctions(DeviceTemplateBean.AttributesBean attributesBean) {
        this.attributesBean = attributesBean;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (attributesBean.getValue() != null) {
            fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetSingleChooseFragment.newInstance(attributesBean), "content");
        } else if (attributesBean.getSetup() != null) {
            int type = getIntent().getIntExtra("type", 0);//0:条件
            if (type == 0) {
                fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetRangeWithOptionFragment.newInstance(attributesBean), "content");
            } else {
                fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetRangeFragment.newInstance(attributesBean), "content");
            }
        } else if (attributesBean.getBitValue() != null) {
            fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetSingleChooseFragment.newInstance(attributesBean), "content");
        }
        fragmentTransaction.commitNowAllowingStateLoss();
    }
}
