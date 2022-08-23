package com.ayla.hotelsaas.ui.activities.set_scene

import android.text.TextUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ayla.hotelsaas.bean.DeviceTemplateBean
import com.ayla.hotelsaas.interfaces.ChoosePropertyValueListener
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet
import com.ayla.hotelsaas.widget.scene_dialog.GroupActionChoosePropertyValueDialog
import com.ayla.hotelsaas.widget.scene_dialog.InputValueDialog

object GroupInputValueUtil {
    fun showSetNumberValueDialog(
        targetValue: String,
        valueBean: DeviceTemplateBean.AttributesBean.ValueBean,
        currentPos: Int,
        fm: FragmentManager,
        choosePropertyValueListener: ChoosePropertyValueListener,
        groupActionChoosePropertyValueDialog: GroupActionChoosePropertyValueDialog
    ) {
        val max = valueBean.setupBean.max
        val min = valueBean.setupBean.min
        InputValueDialog
            .newInstance(object : InputValueDialog.ButtonClickListener {
                override fun onDone(dialog: DialogFragment?, txt: String?) {
                    if (TextUtils.isEmpty(txt)) {
                        choosePropertyValueListener.onToastContent("输入不能为空")
                        return
                    }
                    val value: Long
                    var newValueTxt = txt
                    if (txt?.startsWith("+") == true)
                        newValueTxt = txt.substring(1)
                    try {
                        value = newValueTxt?.toLong()?:0
                    } catch (e: Exception) {
                        choosePropertyValueListener.onToastContent("输入不合法")
                        return
                    }

                    if (value < min || value > max) {
                        choosePropertyValueListener.onToastContent("输入不在范围之内")
                    } else {
                        val setupCallBackBean = ISceneSettingFunctionDatumSet.SetupCallBackBean(
                            "==",
                            null,
                            valueBean.setupBean,
                            valueBean.displayName + ":" + value + valueBean.setupBean.unit
                        )
                        setupCallBackBean.targetValue = value.toString()
                        setupCallBackBean.displayName = valueBean.displayName
                        setupCallBackBean.abilitySubCode = valueBean.abilitySubCode
                        setupCallBackBean.version = valueBean.version
                        choosePropertyValueListener.onUpdate(currentPos, setupCallBackBean)
                        dialog?.dismissAllowingStateLoss()
                        groupActionChoosePropertyValueDialog.dismissAllowingStateLoss()
                    }
                }

                override fun cancel() {

                }

            }).setTitle("输入目标值").setEditHint("输入目标值")
            .setValueRange("取值范围：${min.toInt()}${valueBean.setupBean.unit}-${max.toInt()}${valueBean.setupBean.unit}")
            .setMaxLength(max.toInt().toString().length)
            .setEditValue(
                if (TextUtils.isEmpty(targetValue)) min.toInt().toString() else targetValue
            )
            .setInputType(InputValueDialog.InputType.numberSigned)
            .show(fm, "dialog")
    }
}