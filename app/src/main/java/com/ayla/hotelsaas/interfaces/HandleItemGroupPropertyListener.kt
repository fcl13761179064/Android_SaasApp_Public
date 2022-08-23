package com.ayla.hotelsaas.interfaces

import com.ayla.hotelsaas.bean.DeviceTemplateBean
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet

interface HandleItemGroupPropertyListener {
    fun onShowChooseGroupItemValueDialog(callBackBean: ISceneSettingFunctionDatumSet.SetupCallBackBean)
    fun onShowInputValueDialog(valueBean: DeviceTemplateBean.AttributesBean.ValueBean,targetValue:String)
    fun onUpdateValue(pos: Int, callBackBean: ISceneSettingFunctionDatumSet.CallBackBean)
}