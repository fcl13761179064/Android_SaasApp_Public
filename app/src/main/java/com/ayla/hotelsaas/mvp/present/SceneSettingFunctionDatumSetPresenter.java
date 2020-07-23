package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingFunctionDatumSetPresenter extends BasePresenter<SceneSettingFunctionDatumSetView> {

    public void loadFunction(String dsn) {
        List<CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean>> devices = new ArrayList<>();
        {
            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = new SceneSettingFunctionDatumSetAdapter.DatumBean();
            datumBean.setFunctionName("开关");
            datumBean.setValueName("开启");
            datumBean.setLeftValue("Switch_Control");
            datumBean.setOperator("==");
            datumBean.setRightValue(1);
            datumBean.setRightValueType(1);
            datumBean.setTargetDeviceType(1);
            datumBean.setTargetDeviceId(dsn);
            CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean> bean = new CheckableSupport<>(datumBean);
            bean.setChecked(true);
            devices.add(bean);
        }
        {
            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = new SceneSettingFunctionDatumSetAdapter.DatumBean();
            datumBean.setFunctionName("开关");
            datumBean.setValueName("关闭");
            datumBean.setLeftValue("Switch_Control");
            datumBean.setOperator("==");
            datumBean.setRightValue(0);
            datumBean.setRightValueType(1);
            datumBean.setTargetDeviceType(1);
            datumBean.setTargetDeviceId(dsn);
            CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean> bean = new CheckableSupport<>(datumBean);
            bean.setChecked(false);
            devices.add(bean);
        }
        mView.showFunctions(devices);
    }
}
