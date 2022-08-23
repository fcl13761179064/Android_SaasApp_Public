package com.ayla.hotelsaas.adapter.local_scene

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.utils.ImageLoader
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder

data class LocalSceneConditionOrActionItem(
    val type: ConditionOrActionType,
    val condition: BaseSceneBean.Condition?,
    val action: BaseSceneBean.Action?
)

enum class ConditionOrActionType {
    CONDITION, ACTION, DELAY
}

class SceneConditionOrActionAdapter :
    BaseItemDraggableAdapter<LocalSceneConditionOrActionItem, BaseViewHolder>(
        R.layout.item_local_scene_item,
        null
    ) {

    private var edit = false

    fun setEdit(value: Boolean) {
        edit = value
    }

    override fun convert(helper: BaseViewHolder?, item: LocalSceneConditionOrActionItem?) {
        helper?.apply {
            addOnClickListener(R.id.item_local_scene_delete)
            item?.let {
                when (item.type) {
                    ConditionOrActionType.ACTION -> {
                        item.action?.let {
                            setValue(
                                it.targetDeviceId,
                                it.functionName,
                                it.valueName,
                                this,
                                item.action
                            )
                        }
                    }
                    ConditionOrActionType.CONDITION -> {
                        item.condition?.let {
                            if (it is BaseSceneBean.DeviceCondition) {
                                setValue(
                                    it.sourceDeviceId,
                                    it.functionName,
                                    it.valueName,
                                    this,
                                    item.condition
                                )
                            }
                        }
                    }
                    ConditionOrActionType.DELAY -> {
                        helper.setImageResource(
                            R.id.item_local_scene_icon,
                            R.drawable.icon_delay
                        )
                        helper.setGone(R.id.item_local_scene_delete_device_tips, false)
                        helper.setGone(R.id.item_local_scene_wait_add, false)
                        helper.setGone(R.id.item_local_scene_add_device_tips, false)
                        helper.setText(R.id.item_local_scene_function_name, "延时")
                        item.action?.let {
                            if (it is BaseSceneBean.DelayAction) {
                                val rightValue: String = it.getRightValue()
                                var seconds = 0
                                try {
                                    seconds = rightValue.toInt()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                val minute = seconds / 60
                                val second = seconds % 60
                                val _minute = if (minute < 10) "0$minute" else minute.toString()
                                val _second = if (second < 10) "0$second" else second.toString()

                                helper.setText(
                                    R.id.item_local_scene_name,
                                    String.format("%s分%s秒", _minute, _second)
                                )
                            }
                        }

                    }
                }
            }
        }
    }


    private fun setValue(
        deviceId: String,
        functionName: String?,
        value: String?,
        helper: BaseViewHolder,
        item: Any?
    ) {
        val devicesBean =
            MyApplication.getInstance().getDevicesBean(deviceId)
        if (devicesBean != null) {
            helper.setGone(R.id.item_local_scene_delete_device_tips, false)
            helper.setGone(R.id.item_local_scene_wait_add, devicesBean.bindType == 1)
            if (edit)
                helper.setGone(
                    R.id.item_local_scene_add_device_tips,
                    devicesBean.bindType == 1
                )
            else helper.setGone(R.id.item_local_scene_add_device_tips, false)
            ImageLoader.loadImg(
                helper.getView(R.id.item_local_scene_icon),
                devicesBean.iconUrl,
                R.drawable.ic_empty_device,
                R.drawable.ic_empty_device
            )
            if (functionName != null && !TextUtils.isEmpty(value)) {
                //事件类型
                if (TextUtils.equals("event", value)) {
                    helper.setText(R.id.item_local_scene_function_name, functionName)
                } else {
                    var operatorTxt = ""
                    item?.let {
                        if (it is BaseSceneBean.DeviceCondition) {
                            if (!it.isRadioProperty)
                                operatorTxt = CommonUtils.convertOperatorToTxt(it.operator)
                        }
                    }
                    helper.setText(
                        R.id.item_local_scene_function_name,
                        String.format(
                            "%s:%s%s",
                            functionName,
                            operatorTxt,
                            value
                        )
                    )
                }
            } else {
                if (functionName != null && "已失效" != functionName) {
                    val span = SpannableStringBuilder(
                        String.format(
                            "%s:%s",
                            functionName,
                            "对应按钮已删除"
                        )
                    )
                    span.setSpan(
                        ForegroundColorSpan(Color.parseColor("#D73B4B")),
                        functionName.length,
                        functionName.length + 8,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    helper.setText(R.id.item_local_scene_function_name, span)
                } else {
                    item?.let {
                        if (it is BaseSceneBean.Condition) {
                            it.functionName = "已失效"
                        }
                        if (it is BaseSceneBean.Action) {
                            it.functionName = "已失效"
                        }
                    }
                    val span = SpannableStringBuilder(String.format("%s", "已失效"))
                    span.setSpan(
                        ForegroundColorSpan(Color.parseColor("#D73B4B")),
                        0,
                        3,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    helper.setText(R.id.item_local_scene_function_name, span)
                }
            }
            helper.setText(
                R.id.item_local_scene_name,
                if (TextUtils.isEmpty(devicesBean.nickname)) devicesBean.deviceId else devicesBean.nickname
            )
        } else {
            helper.setImageResource(
                R.id.item_local_scene_icon,
                R.drawable.ic_scene_removed_device_item
            )
            helper.setText(R.id.item_local_scene_function_name, "无效设备")
            helper.setText(R.id.item_local_scene_name, "")
            helper.setGone(R.id.item_local_scene_delete_device_tips, true)
            helper.setGone(R.id.item_local_scene_wait_add, false)
            helper.setGone(R.id.item_local_scene_add_device_tips, false)
        }
    }
}