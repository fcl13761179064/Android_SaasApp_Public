package com.ayla.hotelsaas.mvp.present

import android.text.TextUtils
import android.util.Log
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BasePresenter
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceTemplateBean
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean
import com.ayla.hotelsaas.bean.scene_bean.DeviceType
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.mvp.view.RemoteSceneSettingView
import com.ayla.hotelsaas.utils.BeanObtainCompactUtil
import com.ayla.hotelsaas.utils.TempUtils
import com.blankj.utilcode.util.GsonUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*

class RemoteSceneSettingPresenter : BasePresenter<RemoteSceneSettingView>() {

    fun saveOrUpdateRuleEngine(mRuleEngineBean: BaseSceneBean) {
        val observable: Observable<Boolean>
        val ruleEngineBean = BeanObtainCompactUtil.obtainRuleEngineBean(mRuleEngineBean)
        observable = if (mRuleEngineBean.ruleId == 0L) {
            RequestModel.getInstance().saveRuleEngine(ruleEngineBean)
        } else {
            RequestModel.getInstance().updateRuleEngine(ruleEngineBean)
        }
        val subscribe = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mView?.showProgress("保存中") }
            .doFinally { mView?.hideProgress() }
            .subscribe({ mView?.saveSuccess() }
            ) { throwable -> mView?.saveFailed(throwable) }
        addSubscrebe(subscribe)
    }


    //一键执行场景，本地场景，自动化场景反选属性名称
    fun loadFunctionDetail(
        roomId: Long,
        conditionItems: List<BaseSceneBean.DeviceCondition>,
        actionItems: List<BaseSceneBean.Action>
    ) {
        val enableDevices: MutableSet<DeviceListBean.DevicesBean> = HashSet() //条件动作里面用到了的设备集合
        for (actionItem in actionItems) {
            val devicesBean = MyApplication.getInstance().getDevicesBean(actionItem.targetDeviceId)
            if (devicesBean != null) {
                enableDevices.add(devicesBean)
            }
        }
        for (conditionItem in conditionItems) {
            val devicesBean =
                MyApplication.getInstance().getDevicesBean(conditionItem.sourceDeviceId)
            if (devicesBean != null) {
                enableDevices.add(devicesBean)
            }
        }
        val observable: Observable<List<DeviceCategoryDetailBean>> = if (enableDevices.size == 0) {
            Observable.just(ArrayList())
        } else {
            RequestModel.getInstance().getDeviceCategoryDetail(roomId) //获取品类中心描述
        }
        val subscribe = observable
            .flatMap { deviceCategoryDetailBeans ->
                val tasks: MutableList<Observable<DeviceTemplateBean>> = ArrayList()
                for (enableDevice in enableDevices) {
                    if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(enableDevice)) { //如果是用途设备(红外遥控家电)，就直接套用物模型作为联动动作，不走品类中心过滤
                        tasks.add(RequestModel.getInstance()
                            .fetchDeviceTemplate(enableDevice.pid)
                            .map { deviceTemplateBeanBaseResult -> deviceTemplateBeanBaseResult.data }
                            .compose(
                                RequestModel.getInstance()
                                    .modifyTemplateDisplayName(enableDevice.deviceId)
                            )
                        )
                        continue
                    }
                    for (deviceCategoryDetailBean in deviceCategoryDetailBeans) {
                        if (TextUtils.equals(
                                deviceCategoryDetailBean.deviceId,
                                enableDevice.deviceId
                            )
                        ) {
                            tasks.add(
                                RequestModel.getInstance().fetchDeviceTemplate(enableDevice.pid)
                                    .map { deviceTemplateBeanBaseResult -> deviceTemplateBeanBaseResult.data }
                                    .compose(
                                        RequestModel.getInstance()
                                            .modifyTemplateDisplayName(enableDevice.deviceId)
                                    )
                            )
                            break
                        }
                    }
                }
                if (tasks.size == 0) {
                    Observable.just(arrayOf<DeviceTemplateBean>())
                } else {
                    Observable.zip(
                        tasks,
                        Function { objects ->
                            val data = arrayOfNulls<DeviceTemplateBean>(objects.size)
                            for (i in objects.indices) {
                                data[i] = objects[i] as DeviceTemplateBean?
                            }
                            return@Function data
                        })
                }
            } //查询出了所有动作、条件 要用到的物模板信息
            .doOnNext { deviceTemplateBeans ->
                for (actionItem in actionItems) {
                    val devicesBean =
                        MyApplication.getInstance().getDevicesBean(actionItem.targetDeviceId)
                    if (devicesBean != null) {
                        for (deviceTemplateBean in deviceTemplateBeans) {
                            for (attribute in deviceTemplateBean!!.attributes) {
                                if (TextUtils.equals(
                                        devicesBean.deviceId,
                                        deviceTemplateBean.deviceId
                                    )
                                ) { //找出了设备和物模型
                                    if (TextUtils.equals(attribute.code, actionItem.leftValue)) {
                                        actionItem.functionName = attribute.displayName
                                        val attributeValue = attribute.value
                                        val setupBean = attribute.setup
                                        if (attributeValue != null) {
                                            for (valueBean in attributeValue) {
                                                if (TextUtils.equals(
                                                        valueBean.value,
                                                        actionItem.rightValue
                                                    )
                                                ) {
                                                    actionItem.valueName = valueBean.displayName
                                                }
                                            }
                                        } else if (setupBean != null) {
                                            if (TextUtils.equals(
                                                    attribute.code,
                                                    ConstantValue.TTS_LEFT_VALUE
                                                )
                                            ) {
                                                val ttsBean: SceneSettingActionItemAdapter.TTSBean =
                                                    GsonUtils.fromJson(
                                                        actionItem.rightValue,
                                                        SceneSettingActionItemAdapter.TTSBean::class.java
                                                    )
                                                if (ttsBean.ttsText != null) {
                                                    actionItem.valueName = ttsBean.ttsText.text;
                                                }
                                            } else {
                                                val unit = setupBean.unit
                                                actionItem.valueName = String.format(
                                                    "%s%s",
                                                    actionItem.rightValue,
                                                    if (TextUtils.isEmpty(unit)) "" else unit
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        val rulename = MyApplication.getInstance()
                            .getmOneKeyRelueName(actionItem.targetDeviceId)
                        actionItem.valueName = String.format("%s", rulename)
                    }
                }
                for (conditionItem in conditionItems) {
                    val devicesBean =
                        MyApplication.getInstance().getDevicesBean(conditionItem.sourceDeviceId)
                    if (devicesBean != null) {
                        for (deviceTemplateBean in deviceTemplateBeans) {
                            for (attribute in deviceTemplateBean!!.attributes) {
                                if (TextUtils.equals(
                                        devicesBean.deviceId,
                                        deviceTemplateBean.deviceId
                                    )
                                ) { //找出了设备和物模型
                                    if (TextUtils.equals(attribute.code, conditionItem.leftValue)) {
                                        conditionItem.functionName = attribute.displayName
                                        val attributeValue = attribute.value
                                        val setupBean = attribute.setup
                                        val bitValue = attribute.bitValue
                                        if (attributeValue != null) {
                                            for (valueBean in attributeValue) {
                                                if (TextUtils.equals(
                                                        valueBean.value,
                                                        conditionItem.rightValue
                                                    )
                                                ) {
                                                    conditionItem.valueName = valueBean.displayName
                                                }
                                            }
                                            conditionItem.isRadioProperty = true
                                        } else if (setupBean != null) {
                                            val unit = setupBean.unit
                                            conditionItem.valueName = String.format(
                                                "%s%s",
                                                conditionItem.rightValue,
                                                if (TextUtils.isEmpty(unit)) "" else unit
                                            )
                                            conditionItem.isRadioProperty = false
                                        } else if (bitValue != null) {
                                            for (bitValueBean in bitValue) {
                                                if (bitValueBean.bit == conditionItem.bit && bitValueBean.compareValue == conditionItem.compareValue &&
                                                    TextUtils.equals(
                                                        conditionItem.rightValue,
                                                        bitValueBean.value.toString()
                                                    )
                                                ) {
                                                    conditionItem.valueName =
                                                        bitValueBean.displayName
                                                }
                                            }
                                            conditionItem.isRadioProperty = true
                                        }
                                    }
                                }
                            }
                            if (deviceTemplateBean.events != null) {
                                if (conditionItem.leftValue != ConstantValue.SCENE_TEMPLATE_CODE) { //event事件类型
                                    for (attribute in deviceTemplateBean.events) {
                                        if (TextUtils.equals(
                                                devicesBean.deviceId,
                                                deviceTemplateBean.deviceId
                                            )
                                        ) { //找出了设备和物模型
                                            if (conditionItem.leftValue.endsWith(".")) {
                                                val leftvalue =
                                                    conditionItem.leftValue.split("\\.".toRegex())
                                                        .toTypedArray()
                                                if (TextUtils.equals(
                                                        attribute.code,
                                                        leftvalue[0]
                                                    )
                                                ) {
                                                    conditionItem.functionName =
                                                        attribute.displayName
                                                    conditionItem.valueName = "event"
                                                }
                                            } else {
                                                //A.B
                                                val leftCode =
                                                    conditionItem.leftValue.split("\\.".toRegex())
                                                        .toTypedArray()
                                                if (TextUtils.equals(attribute.code, leftCode[0])) {
                                                    for (outparam in attribute.outParams) {
                                                        if (TextUtils.equals(
                                                                outparam.code,
                                                                leftCode[1]
                                                            )
                                                        ) {
                                                            val parentName = attribute.displayName
                                                            if (outparam.value != null) {
                                                                for (valueBean in outparam.value) {
                                                                    if (TextUtils.equals(
                                                                            valueBean.value,
                                                                            conditionItem.rightValue
                                                                        )
                                                                    ) {
                                                                        conditionItem.functionName =
                                                                            parentName + "-" + outparam.displayName
                                                                        conditionItem.valueName =
                                                                            valueBean.displayName
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mView?.showProgress() }
            .doFinally { mView?.hideProgress() }
            .subscribe({
                //设备反选属性名称和属性的数据DeviceTemplateBean
                mView?.getPropertySuccess()
            }, {
                mView?.getPropertyFail(it)
            })
        addSubscrebe(subscribe)
    }

    fun loadGroupDetail(actionItems: List<BaseSceneBean.Action>) {
        for (actionItem in actionItems) {
            if (actionItem.targetDeviceType == DeviceType.GROUP_ACTION) {
                val groupItem = MyApplication.getInstance().getGroupItem(actionItem.targetDeviceId)
                if (groupItem != null) {
                    val subscribe = RequestModel.getInstance().getGroupAbility(groupItem.groupId)
                        .flatMap { groupAbilities ->
                            for (ability in groupAbilities) {
                                if (ability.abilityCode == actionItem.leftValue) {
                                    val split = actionItem.rightValue.split(";")
                                    if (split.size == 2) {
                                        if (split[1].length > 1 && split[1].contains(":")) {
                                            val valueArray =
                                                split[1].substring(1, split[1].length - 1)
                                                    .split(":")
                                            if (valueArray.size == 2) {
                                                for (value in ability.abilityValues) {
                                                    if ("\"" + value.abilitySubCode + "\"" == valueArray[0]) {
                                                        actionItem.functionName =
                                                            ability.displayName
                                                        actionItem.valueName =
                                                            value.displayName + " " + valueArray[1] + value.setup?.unit
                                                        break
                                                    }
                                                }
                                            }

                                        } else {
                                            if (ability.version == split[0]) {
                                                for (value in ability.abilityValues) {
                                                    if (value.value == split[1]) {
                                                        actionItem.functionName =
                                                            ability.displayName
                                                        actionItem.valueName = value.displayName
                                                        break
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break
                                }
                            }
                            Observable.just(actionItem)
                        }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            mView?.getGroupActionDetail(it)
                        }, {
                            Log.e("GETGROUPACTION", it?.message ?: "")
                        })
                    addSubscrebe(subscribe)
                }
            }
        }

    }


    fun deleteScene(ruleId: Long) {
        val subscribe = RequestModel.getInstance()
            .deleteRuleEngine(ruleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mView?.showProgress("请稍后") }
            .doFinally { mView?.hideProgress() }
            .subscribe({ mView?.deleteScene(true) }
            ) { mView?.deleteScene(false, it) }
        addSubscrebe(subscribe)
    }


    //欢迎语
    fun check(scopeId: Long) {
        val it = RequestModel.getInstance().checkRadioExists(scopeId) //首先检查是否关联了音响
            .flatMap { aBoolean ->
                if (aBoolean) {
                    RequestModel.getInstance().checkVoiceRule(scopeId) //再检查是否已经创建了包含酒店欢迎语动作的联动
                        .map { aBoolean ->
                            if (aBoolean) {
                                -2
                            } else {
                                0
                            }
                        }
                } else {
                    Observable.just(-1)
                }
            } // -1:没有关联音响  -2:已创建了酒店欢迎语的联动  0:关联了音响，没有创建过酒店欢迎语动作。
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mView?.showProgress("请稍后") }
            .doFinally { mView?.hideProgress() }
            .subscribe({ integer ->
                mView?.getWelcomeYuSuccess(integer)
            })
            { throwable ->
                mView?.getWelcomeYuFail(throwable)
            }

        addSubscrebe(it)
    }

}