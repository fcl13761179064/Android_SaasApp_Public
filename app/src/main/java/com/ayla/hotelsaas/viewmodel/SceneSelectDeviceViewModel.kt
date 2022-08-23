package com.ayla.hotelsaas.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter.TTSBean
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.bean.*
import com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean
import com.ayla.hotelsaas.bean.DeviceTemplateBean.AttributesBean.*
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean.DeviceCondition
import com.ayla.hotelsaas.bean.scene_bean.DeviceType
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.vm.AbsViewModel
import com.ayla.hotelsaas.utils.BeanObtainCompactUtil
import com.ayla.hotelsaas.utils.TempUtils
import com.blankj.utilcode.util.GsonUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*

class SceneSelectDeviceViewModel : AbsViewModel() {
    private val api by lazy { RequestModel.getInstance() }
    private val _saveOrUpdateRuleEngine = MutableLiveData<Boolean>()
    val saveOrUpdateRuleEngine: LiveData<Boolean?> = _saveOrUpdateRuleEngine

    private val _huanYingYuRuleEngine = MutableLiveData<Int>()
    val huanYingYuRuleEngine: LiveData<Int?> = _huanYingYuRuleEngine


    //条件或者动作的设备
    private val condtion_or_action_devices = MutableLiveData<MutableList<DevicesBean>>()
    val condtionOrActionDevices: MutableLiveData<MutableList<DevicesBean>> =
        condtion_or_action_devices

    val conditionActionDeviceGroup = MutableLiveData<List<BaseDevice>>()

    //删除成功
    private val delete_Success = MutableLiveData<Boolean>()
    val deleteSuccess: MutableLiveData<Boolean> = delete_Success

    //反选条件或者动作的设备
    val action_devices = MutableLiveData<Any?>()
    val action_devices_throw: MutableLiveData<Any?> = action_devices

    private val _deviceErrorLiveData = MutableLiveData<Throwable>()
    val deviceErrorLiveData: LiveData<Throwable> = _deviceErrorLiveData

    var observable: Observable<MutableList<DevicesBean>>? = null

    val groupAction = MutableLiveData<BaseSceneBean.Action>()

    /**
     * 选择设备条件还是动作的
     */
    fun getConditionOrActionDevices(roomId: Long, regionId: Long, ActionOrAction: Boolean) {
        if (regionId == -1L) {
            val devicesBean = MyApplication.getInstance().devicesBean.devices
            val bean =
                devicesBean?.filter { it.bindType == 0 && !it.pid.equals(ConstantValue.A6_GATEWAY_PID) } as MutableList<DevicesBean>
            loadConditionOrActionData(roomId, bean, ActionOrAction, -1)
        } else {
            val subscribe = api.getDeviceList(roomId, 1, 200, regionId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val devices = it.devices
                    val chooseDevice = mutableListOf<DevicesBean>()
                    for (devicesBean in devices) {
                        if (devicesBean.bindType == 0 && !TempUtils.isDeviceGateway(devicesBean)) {
                            chooseDevice.add(devicesBean)
                        }
                    }

                    loadConditionOrActionData(roomId, chooseDevice, ActionOrAction, regionId)

                }, {
                    this._deviceErrorLiveData.value = it
                })
            addDisposable(subscribe)
        }
    }


    /**
     * type 0 条件 1 动作
     */
    fun getLocalConditionOrActionDevices(
        roomId: Long,
        regionId: Long,
        gatewayId: String,
        type: Int
    ) {
        //获取网关下的子设备
        val observable = api.getGatewayNodes(gatewayId, roomId)
            .map<List<GatewayNodeBean>> { listBaseResult -> listBaseResult.data }
            .map<List<DevicesBean>> { gatewayNodeBeans ->
                val devicesBeans: MutableList<DevicesBean> = ArrayList()
                if (MyApplication.getInstance().devicesBean != null) {
                    for (devicesBean in MyApplication.getInstance().devicesBean.devices) {
                        for (gatewayNodeBean in gatewayNodeBeans) {
                            if (TextUtils.equals(
                                    devicesBean.deviceId,
                                    gatewayNodeBean.deviceId
                                ) && devicesBean.bindType == 0
                            ) {
                                devicesBeans.add(devicesBean)
                            }
                        }
                    }
                }
                val newDeviceBeans: MutableList<DevicesBean> = ArrayList()
                if (regionId == -1L)
                    newDeviceBeans.addAll(devicesBeans)
                else {
                    for (bean in devicesBeans) {
                        if (bean.regionId == regionId) newDeviceBeans.add(bean)
                    }
                }
                newDeviceBeans
            }

        val conditionOrActionObservable = api.getDeviceCategoryDetail(roomId) //查询出设备对条件、动作的支持情况
            .zipWith<List<DevicesBean>, List<BaseDevice>>(observable,
                { deviceCategoryDetailBeans, devicesBeans ->
                    val enableDevices: MutableList<BaseDevice> = ArrayList() //可以显示在列表里面的设备
                    for (devicesBean in devicesBeans) {
                        if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(devicesBean)) { //如果是用途设备(红外遥控家电)，就直接套用物模型作为联动动作，不走品类中心过滤
                            continue
                        }
                        for (categoryDetailBean in deviceCategoryDetailBeans) {
                            if (TextUtils.equals(
                                    categoryDetailBean.deviceId,
                                    devicesBean.deviceId
                                )
                            ) { //找到已绑定的设备的条件、动作描述信息
                                if (type == 0) {
                                    val conditionProperties = categoryDetailBean.conditionProperties
                                    if (conditionProperties != null && conditionProperties.size != 0) {
                                        enableDevices.add(TempUtils.getNewDeviceItem(devicesBean))
                                    }
                                } else if (type == 1) {
                                    val actionProperties = categoryDetailBean.actionProperties
                                    if (actionProperties != null && actionProperties.size != 0) {
                                        enableDevices.add(TempUtils.getNewDeviceItem(devicesBean))
                                    }
                                }
                                break
                            }
                        }
                    }
                    enableDevices
                })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    conditionActionDeviceGroup.value = it
                },
                {
                    this._deviceErrorLiveData.value = it
                })

        addDisposable(conditionOrActionObservable)
    }

    /**
     * @param gateway   网关id，如果为空 不用网关节点过滤
     * @param condition
     */
    private fun loadDevice(scopeId: Long, devies: MutableList<DevicesBean>, condition: Boolean) {
        val subscribe =
            RequestModel.getInstance().getDeviceCategoryDetail(scopeId) //查询出设备对条件、动作的支持情况
                .map {
                    val enableDevices: MutableList<DevicesBean> = mutableListOf() //可以显示在列表里面的设备
                    if (it.isNotEmpty() && devies.isNotEmpty()) {
                        for (devicesBean in devies) {
                            if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(devicesBean) && !condition) { //如果是用途设备(红外遥控家电)，就直接套用物模型作为联动动作，不走品类中心过滤
                                enableDevices.add(devicesBean)
                                continue
                            }
                            for (categoryDetailBean in it) {
                                if (TextUtils.equals(
                                        categoryDetailBean.deviceId,
                                        devicesBean.deviceId
                                    )
                                ) { //找到已绑定的设备的条件、动作描述信息
                                    if (condition) {
                                        val conditionProperties =
                                            categoryDetailBean.conditionProperties
                                        if (conditionProperties != null && conditionProperties.size != 0) {
                                            enableDevices.add(devicesBean)
                                        }
                                    } else {
                                        val actionProperties = categoryDetailBean.actionProperties
                                        if (actionProperties != null && actionProperties.size != 0) {
                                            enableDevices.add(devicesBean)
                                        }
                                    }
                                    break
                                }
                            }
                        }
                        return@map enableDevices
                    } else {
                        return@map enableDevices
                    }
                }

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    this.condtion_or_action_devices.value = it
                }, {
                    this._deviceErrorLiveData.value = it
                })
        addDisposable(subscribe)
    }


    /**
     * 获取可以作为条件或动作的设备、编组
     */
    private fun loadConditionOrActionData(
        scopeId: Long,
        devies: MutableList<DevicesBean>,
        condition: Boolean,
        regionId: Long
    ) {
        val subscribe =
            RequestModel.getInstance().getConditionOrActionDeviceAndGroup(
                scopeId,
                if (condition) 1 else 2
            ) //查询出设备对条件、动作的支持情况
                .map {
                    val enableDevices: MutableList<BaseDevice> = mutableListOf() //可以显示在列表里面的设备
                    if (it.groupList.size > 0) {
                        for (groupItem in it.groupList) {
                            val group =
                                MyApplication.getInstance().getGroupItem(groupItem.groupId)
                            group.actionAbilities = groupItem.actionAbilities
                            group.regionId = groupItem.regionId
                            if (group != null) {
                                if (regionId != -1L) {
                                    if (groupItem.regionId.toLong() == regionId)
                                        enableDevices.add(group)
                                } else {
                                    enableDevices.add(group)
                                }


                            }
                        }
                    }
                    if (it.deviceList.size > 0 || devies.size > 0) {
                        for (devicesBean in devies) {
                            if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(devicesBean) && !condition) { //如果是用途设备(红外遥控家电)，就直接套用物模型作为联动动作，不走品类中心过滤
                                enableDevices.add(TempUtils.getNewDeviceItem(devicesBean))
                                continue
                            }
                            for (deviceItem in it.deviceList) {
                                if (TextUtils.equals(
                                        deviceItem.deviceId,
                                        devicesBean.deviceId
                                    )
                                ) { //找到已绑定的设备的条件、动作描述信息
                                    if (condition) {
                                        val conditionProperties =
                                            deviceItem.conditionProperties
                                        if (conditionProperties != null && conditionProperties.size != 0) {
                                            enableDevices.add(TempUtils.getNewDeviceItem(devicesBean))
                                        }
                                    } else {
                                        val actionProperties = deviceItem.actionProperties
                                        if (actionProperties != null && actionProperties.size != 0) {
                                            enableDevices.add(TempUtils.getNewDeviceItem(devicesBean))
                                        }
                                    }
                                    break
                                }
                            }
                        }

                    }
                    return@map enableDevices
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    this.conditionActionDeviceGroup.value = it
                }, {
                    this._deviceErrorLiveData.value = it
                })
        addDisposable(subscribe)
    }

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
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                _saveOrUpdateRuleEngine.value = it
            }, {
                this._deviceErrorLiveData.value = it
            })
        addDisposable(subscribe)

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
            .doOnSubscribe {
                showLoadings(true)
            }.doFinally { hideLoadings() }
            .subscribe({ integer ->
                this._huanYingYuRuleEngine.value = integer
            })
            { throwable ->
                this._deviceErrorLiveData.value = throwable
            }

        addDisposable(it)
    }

    //一键执行场景，本地场景，自动化场景反选属性名称
    fun loadFunctionDetail(
        roomId: Long,
        conditionItems: List<DeviceCondition>,
        actionItems: List<BaseSceneBean.Action>
    ) {
        val enableDevices: MutableSet<DevicesBean> = HashSet() //条件动作里面用到了的设备集合
        for (actionItem in actionItems) {
            val devicesBean =
                MyApplication.getInstance().getDevicesBean(actionItem.targetDeviceId)
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
        val observable: Observable<List<DeviceCategoryDetailBean>> =
            if (enableDevices.size == 0) {
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
                    Observable.zip<DeviceTemplateBean, Array<DeviceTemplateBean?>>(
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
                                                val ttsBean: TTSBean = GsonUtils.fromJson(
                                                    actionItem.rightValue,
                                                    TTSBean::class.java
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
                                        } else if (setupBean != null) {
                                            val unit = setupBean.unit
                                            conditionItem.valueName = String.format(
                                                "%s%s",
                                                conditionItem.rightValue,
                                                if (TextUtils.isEmpty(unit)) "" else unit
                                            )
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
                                                    conditionItem.valueName = ""
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
            .doOnSubscribe {
                showLoadings(true)
            }.doFinally { hideLoadings() }
            .subscribe({
                //设备反选属性名称和属性的数据DeviceTemplateBean
                this.action_devices.value = it
            }, {
                //设备反选属性名称和属性的数据DeviceTemplateBean
                this._deviceErrorLiveData.value = it
            })

        addDisposable(subscribe)
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
                            groupAction.value = it
                        }, {
                            Log.e("GETGROUPACTION", it?.message ?: "")
                        })
                    addDisposable(subscribe)
                }
            }
        }

    }

    fun deleteScene(ruleId: Long) {
        val subscribe = RequestModel.getInstance()
            .deleteRuleEngine(ruleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadings(true)
            }.doFinally { hideLoadings() }
            .subscribe({
                this.deleteSuccess.value = it
            }, Consumer {
                //设备反选属性名称和属性的数据DeviceTemplateBean
                this._deviceErrorLiveData.value = Throwable("删除失败")
            })

        addDisposable(subscribe)
    }
}
