package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingFunctionDatumSetSingleChoosePresenter extends BasePresenter<SceneSettingFunctionDatumSetView> {

    public void loadFunction(DeviceListBean.DevicesBean deviceBean, DeviceTemplateBean.AttributesBean attributesBean) {
        List<CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean>> devices = new ArrayList<>();

        List<DeviceTemplateBean.AttributesBean.ValueBean> attributesBeanValue = attributesBean.getValue();
        if (attributesBeanValue != null) {
            for (DeviceTemplateBean.AttributesBean.ValueBean valueBean : attributesBeanValue) {
                SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = new SceneSettingFunctionDatumSetAdapter.DatumBean();

                datumBean.setFunctionName(attributesBean.getDisplayName());
                datumBean.setValueName(valueBean.getDisplayName());
                datumBean.setLeftValue(attributesBean.getCode());
                datumBean.setOperator("==");
                datumBean.setRightValue(valueBean.getValue());
                datumBean.setRightValueType(valueBean.getDataType());
                {
                    if (deviceBean.getDeviceUseType() == 1 && deviceBean.getCuId() == 0) {
                        datumBean.setDeviceType(8);
                    } else {
                        datumBean.setDeviceType(deviceBean.getCuId());
                    }
                }
                datumBean.setDeviceId(deviceBean.getDeviceId());
                datumBean.setIconUrl(deviceBean.getIconUrl());
                CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean> bean = new CheckableSupport<>(datumBean);
                devices.add(bean);
            }
        }
        if (devices.size() != 0) {
            CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean> bean = devices.get(0);
            bean.setChecked(true);
        }
        mView.showFunctions(devices);
    }
}
