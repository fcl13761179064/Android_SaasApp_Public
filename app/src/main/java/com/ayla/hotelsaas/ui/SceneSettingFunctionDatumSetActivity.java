package com.ayla.hotelsaas.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;

/**
 * 场景创建，选择执行功能点的页面
 * 进入时必须带入参数
 * 1.{@link DeviceTemplateBean.AttributesBean} attributeBean
 * 2.String deviceId
 * 3.type 选择的功能作为条件还是动作。0:条件
 * 返回：
 * result {@link ISceneSettingFunctionDatumSet.CallBackBean}
 */
public class SceneSettingFunctionDatumSetActivity extends BaseMvpActivity {
    @BindView(R.id.appBar)
    AppBar appBar;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_function_datum_set;
    }

    private DeviceListBean.DevicesBean deviceBean;

    @Override
    protected void initView() {
        appBar.setCenterText("选择功能");
        appBar.setRightText("完成");

        DeviceTemplateBean.AttributesBean attributesBean = (DeviceTemplateBean.AttributesBean) getIntent().getSerializableExtra("attributeBean");
        deviceBean = MyApplication.getInstance().getDevicesBean(getIntent().getStringExtra("deviceId"));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (attributesBean.getValue() != null) {
            fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetSingleChooseFragment.newInstance(deviceBean.getDeviceId(), attributesBean), "content");
        } else if (attributesBean.getSetup() != null) {
            int type = getIntent().getIntExtra("type", 0);//0:条件
            if (type == 0) {
                fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetRangeWithOptionFragment.newInstance(deviceBean.getDeviceId(), attributesBean), "content");
            } else {
                fragmentTransaction.replace(R.id.fl_container, SceneSettingFunctionDatumSetRangeFragment.newInstance(deviceBean.getDeviceId(), attributesBean), "content");
            }
        }
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    @Override
    protected void initListener() {
        appBar.rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment contentFragment = getSupportFragmentManager().findFragmentByTag("content");
                if (contentFragment instanceof ISceneSettingFunctionDatumSet) {
                    ISceneSettingFunctionDatumSet.CallBackBean datumBean = ((ISceneSettingFunctionDatumSet) contentFragment).getDatum();

                    Intent data = new Intent();
                    data.putExtra("result", datumBean);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }
}
