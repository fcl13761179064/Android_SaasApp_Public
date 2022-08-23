package com.ayla.hotelsaas.ui.activities.local_scene

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter
import com.ayla.hotelsaas.adapter.local_scene.ConditionOrActionType
import com.ayla.hotelsaas.adapter.local_scene.LocalSceneConditionOrActionItem
import com.ayla.hotelsaas.adapter.local_scene.SceneConditionOrActionAdapter
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean
import com.ayla.hotelsaas.bean.DeviceTemplateBean.AttributesBean
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.events.SceneChangedEvent
import com.ayla.hotelsaas.events.SceneItemEvent
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean.*
import com.ayla.hotelsaas.bean.scene_bean.DeviceType
import com.ayla.hotelsaas.bean.scene_bean.LocalSceneBean
import com.ayla.hotelsaas.mvp.present.LocalSceneSettingPresenter
import com.ayla.hotelsaas.mvp.view.LocalSceneSettingView
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.interfaces.ChoosePropertyValueListener
import com.ayla.hotelsaas.widget.common_dialog.NpCustomSheet
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet.*
import com.ayla.hotelsaas.ui.activities.SceneMoreActivity
import com.ayla.hotelsaas.ui.activities.set_scene.SceneSettingDeviceSelectActivity
import com.ayla.hotelsaas.ui.activities.SceneSettingFunctionDatumSetActivity
import com.ayla.hotelsaas.ui.activities.set_scene.ChooseDevicePropertyValueUtil
import com.ayla.hotelsaas.ui.activities.set_scene.SetEffectiveTimeActivity
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.widget.*
import com.ayla.hotelsaas.widget.common_dialog.RuleNameDialog
import com.ayla.hotelsaas.widget.common_dialog.SceneIconSelectDialog
import com.ayla.hotelsaas.widget.common_dialog.SelectTimeDialog
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import com.chad.library.adapter.base.listener.OnItemDragListener
import kotlinx.android.synthetic.main.activity_local_scene_setting.*
import kotlinx.android.synthetic.main.activity_local_scene_setting.rv_action
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.*

class LocalSceneSettingActivity :
    BaseMvpActivity<LocalSceneSettingView, LocalSceneSettingPresenter>(), LocalSceneSettingView {
    private val conditionAdapter = SceneConditionOrActionAdapter()
    private val actionAdapter = SceneConditionOrActionAdapter()
    private var effectiveTime = EnableTime()
    private var gatewayId: String? = null
    private var editActionPosition = -1
    private var editScene = false
    private var startConditionPos = -1
    private var startActionPos = -1
    private val sceneIconSelectDialog by lazy {
        SceneIconSelectDialog(this)
    }
    private val sceneSelectDelayDialog by lazy {
        SelectTimeDialog(this, false, true, true)
    }
    private var mRuleEngineBean = LocalSceneBean()
    private var chooseDevicePropertyValueUtil: ChooseDevicePropertyValueUtil? = null
    override fun getLayoutId(): Int = R.layout.activity_local_scene_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        chooseDevicePropertyValueUtil?.clear()
    }


    override fun initView() {
        gatewayId = intent.getStringExtra("targetGateway")
        rv_condition.layoutManager = LinearLayoutManager(this)
        rv_condition.adapter = conditionAdapter
        //拖动排序
        val conditionItemDragAndSwipeCallback = HomeItemDragAndSwipeCallback(conditionAdapter)
        val conditionItemTouchHelper = ItemTouchHelper(conditionItemDragAndSwipeCallback)
        conditionItemTouchHelper.attachToRecyclerView(rv_condition)
        conditionAdapter.enableDragItem(conditionItemTouchHelper)
        conditionAdapter.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(p0: RecyclerView.ViewHolder?, p1: Int) {
                startConditionPos = p1
            }

            override fun onItemDragMoving(
                p0: RecyclerView.ViewHolder?,
                p1: Int,
                p2: RecyclerView.ViewHolder?,
                p3: Int
            ) {
            }

            override fun onItemDragEnd(p0: RecyclerView.ViewHolder?, p1: Int) {
                val condition = mRuleEngineBean.conditions?.get(startConditionPos)
                mRuleEngineBean.conditions.removeAt(startConditionPos)
                mRuleEngineBean.conditions.add(p1, condition)
            }
        })

        rv_action.layoutManager = LinearLayoutManager(this)
        rv_action.adapter = actionAdapter
        //拖动排序
        val actionItemDragAndSwipeCallback = HomeItemDragAndSwipeCallback(actionAdapter)
        val actionItemTouchHelper = ItemTouchHelper(actionItemDragAndSwipeCallback)
        actionItemTouchHelper.attachToRecyclerView(rv_action)
        actionAdapter.enableDragItem(actionItemTouchHelper)
        actionAdapter.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(p0: RecyclerView.ViewHolder?, p1: Int) {
                startActionPos = p1
            }

            override fun onItemDragMoving(
                p0: RecyclerView.ViewHolder?,
                p1: Int,
                p2: RecyclerView.ViewHolder?,
                p3: Int
            ) {
            }

            override fun onItemDragEnd(p0: RecyclerView.ViewHolder?, p1: Int) {
                val action = mRuleEngineBean.actions?.get(startActionPos)
                mRuleEngineBean.actions.removeAt(startActionPos)
                mRuleEngineBean.actions.add(p1, action)
            }
        })

        val localSceneBean = intent.getSerializableExtra(KEYS.LOCALSCENEBEAN)
        localSceneBean?.let {
            //编辑场景
            if (it is LocalSceneBean) {
                editScene = true
                conditionAdapter.setEdit(true)
                actionAdapter.setEdit(true)
                delete_scene.visibility = View.VISIBLE
                mRuleEngineBean = it
                effectiveTime = mRuleEngineBean.enableTime
                setEffectiveTime()
                local_scene_app_bar.setCenterText(it.ruleName)
                local_scene_app_bar.setRightText("更多")
                //设置条件类型
                val ruleSetMode = mRuleEngineBean.ruleSetMode
                local_scene_choose_condition_way.text =
                    if (ruleSetMode == RULE_SET_MODE.ALL) "当满足所有条件时" else "当满足任意条件时"
                gatewayId = mRuleEngineBean.targetGateway
                gatewayId?.let { id ->
                    if (id.contains("@")) {
                        val newGatewayId = getGatewayId(id)
                        if (!TextUtils.isEmpty(newGatewayId)) {
                            gatewayId = newGatewayId
                        }
                    }
                }
                loadScenePropertyDetail()
                local_scene_app_bar.rightTextView.singleClick {
                    val intent = Intent(this, SceneMoreActivity::class.java)
                    intent.putExtra(KEYS.SCENE_BASERESULT, mRuleEngineBean)
                    intent.putExtra(KEYS.SCENELOCATION, 1)
                    startActivityForResult(intent, 10011)
                }
            } else
                Log.e(TAG, "initView: 非本地场景数据")
        }
        if (localSceneBean == null) {
            //添加场景
            val gatewayDevice = MyApplication.getInstance().getDevicesBean(gatewayId)
            if (gatewayDevice != null) {
                val scopeId =
                    SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, -1) ?: -1L
                mRuleEngineBean.targetGateway = gatewayId
                mRuleEngineBean.targetGatewayType = gatewayDevice.cuId
                mRuleEngineBean.ruleType = RULE_TYPE.AUTO
                mRuleEngineBean.ruleSetMode = RULE_SET_MODE.ANY
                mRuleEngineBean.scopeId = scopeId
                mRuleEngineBean.ruleDescription = "test"
                mRuleEngineBean.status = 1
                mRuleEngineBean.iconPath = getIconPathByIndex(1)
                val enableTime = EnableTime()
                mRuleEngineBean.enableTime = enableTime
            }
        }
        chooseDevicePropertyValueUtil =
            ChooseDevicePropertyValueUtil(0, supportFragmentManager, object :
                ChoosePropertyValueListener {
                override fun onUpdate(currentPos: Int, callBackBean: CallBackBean?) {
                    callBackBean?.let {
                        updateItemValue(currentPos, it)
                    }
                }

                override fun onToastContent(content: String?) {
                    CustomToast.makeText(
                        this@LocalSceneSettingActivity,
                        content,
                        R.drawable.ic_toast_warming
                    )
                }

                override fun onProgress() {
                    showProgress("加载中...")
                }

                override fun onFinish() {
                    hideProgress()
                }

            })
    }

    private fun updateItemValue(currentPos: Int, callBackBean: CallBackBean) {
        chooseDevicePropertyValueUtil?.apply {
            if (sceneType == 0) {
                val conditionItem = mRuleEngineBean.conditions?.get(currentPos)
                if (conditionItem is DeviceCondition) {
                    val devicesBean =
                        MyApplication.getInstance().getDevicesBean(conditionItem.sourceDeviceId)
                    if (devicesBean != null)
                        mergeDeviceConditionItem(
                            conditionItem,
                            devicesBean,
                            currentAttributesBean,
                            callBackBean
                        )
                    conditionAdapter.notifyItemChanged(currentPos)
                }
            } else {
                val actionItem = mRuleEngineBean.actions?.get(currentPos)
                if (actionItem is DeviceAction) {
                    val devicesBean =
                        MyApplication.getInstance().getDevicesBean(actionItem.targetDeviceId)
                    if (devicesBean != null)
                        mergeDeviceActionItem(
                            actionItem,
                            devicesBean,
                            currentAttributesBean,
                            callBackBean
                        )
                    actionAdapter.notifyItemChanged(currentPos)
                }
            }
        }

    }

    private fun getGatewayId(id: String): String {
        val devicesBean = MyApplication.getInstance().devicesBean
        if (devicesBean != null && devicesBean.devices != null && devicesBean.devices.size > 0) {
            for (item in devicesBean.devices) {
                if ((item.pointName + "@" + item.pid) == id) {
                    return item.deviceId
                }
            }
        }
        return ""
    }

    private fun loadScenePropertyDetail() {
        val conditions: MutableList<DeviceCondition> = ArrayList()
        val actions: MutableList<Action> = ArrayList()
        for (condition in mRuleEngineBean.conditions) {
            if (condition is DeviceCondition) {
                conditions.add(condition)
            }
        }
        for (action in mRuleEngineBean.actions) {
            actions.add(action)
        }
        mPresenter.loadFunctionDetail(mRuleEngineBean.scopeId, conditions, actions)
    }

    override fun initListener() {
        layout_effective_time.singleClick {
            val intent = Intent(this, SetEffectiveTimeActivity::class.java)
            intent.putExtra("enableTime", effectiveTime)
            startActivityForResult(intent, 999)
        }
        local_scene_add_condition_top.singleClick {
            val intent = Intent(
                this@LocalSceneSettingActivity,
                SceneSettingDeviceSelectActivity::class.java
            )
            intent.putExtra(KEYS.LOCAL, true)
            intent.putExtra(KEYS.SCENETYPE, 0)
            intent.putExtra(KEYS.GATEWAYID, gatewayId)
            startActivity(intent)
        }
        local_scene_add_condition.singleClick {
            val intent = Intent(
                this@LocalSceneSettingActivity,
                SceneSettingDeviceSelectActivity::class.java
            )
            intent.putExtra(KEYS.LOCAL, true)
            intent.putExtra(KEYS.SCENETYPE, 0)
            intent.putExtra(KEYS.GATEWAYID, gatewayId)
            startActivity(intent)
        }
        local_scene_add_action_top.singleClick {
            //米兰网关 无法设置延时 直接进入设备控制
            if (TempUtils.getDeviceSourceFromDeviceType(mRuleEngineBean.targetGatewayType) == 1) {
                val intent = Intent(
                    this@LocalSceneSettingActivity,
                    SceneSettingDeviceSelectActivity::class.java
                )
                intent.putExtra(KEYS.LOCAL, true)
                intent.putExtra(KEYS.SCENETYPE, 1)
                intent.putExtra(KEYS.GATEWAYID, gatewayId)
                startActivity(intent)
            } else
                showAddActionDialog()
        }
        local_scene_add_action.singleClick {
            //米兰网关 无法设置延时 直接进入设备控制
            if (TempUtils.getDeviceSourceFromDeviceType(mRuleEngineBean.targetGatewayType) == 1) {
                val intent = Intent(
                    this@LocalSceneSettingActivity,
                    SceneSettingDeviceSelectActivity::class.java
                )
                intent.putExtra(KEYS.LOCAL, true)
                intent.putExtra(KEYS.SCENETYPE, 1)
                intent.putExtra(KEYS.GATEWAYID, gatewayId)
                startActivity(intent)
            } else
                showAddActionDialog()
        }
        local_scene_choose_condition_way.singleClick {
            showConditionTypeDialog()
        }
        conditionAdapter.setOnItemChildClickListener { adapter, view, position ->
            mRuleEngineBean.conditions.removeAt(position)
            adapter.remove(position)
            if (adapter.data.size == 0)
                local_scene_add_condition.visibility = View.VISIBLE
        }
        conditionAdapter.setOnItemClickListener { adapter, view, position ->
            val item = conditionAdapter.getItem(position)
            item?.let {
                if (it.condition is DeviceCondition) {
                    if (it.condition.functionName != null && it.condition.functionName == "已失效") {
                        CustomToast.makeText(this, "已失效的条件不能再点击进入", R.drawable.ic_toast_warning)
                        return@let
                    }
                    val devicesBean =
                        MyApplication.getInstance().getDevicesBean(it.condition.sourceDeviceId)
                    if (devicesBean != null && devicesBean.bindType == 0) {
                        chooseDevicePropertyValueUtil?.apply {
                            setCurrentPos(position)
                            sceneType = 0
                            setCondition(it.condition)
                            loadSingleProperty(
                                it.condition.sourceDeviceId,
                                it.condition.leftValue,
                                true
                            )
                        }
                    }
                }
//                    jumpEditConditionDeviceItem(it.condition, position)
                else Log.e(TAG, "不是设备Condition")
            }

        }
        actionAdapter.setOnItemClickListener { adapter, view, position ->
            val item = actionAdapter.getItem(position)
            item?.let { actionItem ->
                if (actionItem.type == ConditionOrActionType.DELAY) {
                    editActionPosition = position
                    actionItem.action?.let {
                        if (it is DelayAction) {
                            try {
                                val seconds = it.rightValue.toInt()
                                sceneSelectDelayDialog.show()
                                sceneSelectDelayDialog.setMinSecondValue(seconds / 60, seconds % 60)
                            } catch (e: Exception) {
                                Log.e(TAG, "延时错误")
                            }
                        }
                    }

                } else if (actionItem.type == ConditionOrActionType.ACTION) {
                    if (actionItem.action is DeviceAction) {
                        if (actionItem.action.getFunctionName() != null && actionItem.action.getFunctionName()
                                .equals("已失效")
                        ) {
                            CustomToast.makeText(this, "已失效的动作不能再点击进入", R.drawable.ic_toast_warning)
                            return@setOnItemClickListener
                        } else if (actionItem.action.getFunctionName() != null && "已失效" != actionItem.action.getFunctionName() && TextUtils.isEmpty(
                                actionItem.action.getValueName()
                            )
                        ) {
                            return@setOnItemClickListener
                        }
                        val devicesBean =
                            MyApplication.getInstance()
                                .getDevicesBean(actionItem.action.targetDeviceId)
                        if (devicesBean != null && devicesBean.bindType == 0) {
                            chooseDevicePropertyValueUtil?.apply {
                                setCurrentPos(position)
                                sceneType = 1
                                setAction(actionItem.action)
                                loadSingleProperty(
                                    actionItem.action.targetDeviceId,
                                    actionItem.action.leftValue,
                                    true
                                )
                            }
                        }
                    }
//                        jumpEditActionDeviceItem(actionItem.action, position)
                    else Log.e(TAG, "不是设备Action")
                }
            }
        }
        actionAdapter.setOnItemChildClickListener { adapter, view, position ->
            mRuleEngineBean.actions.removeAt(position)
            adapter.remove(position)
            if (adapter.data.size == 0)
                local_scene_add_action.visibility = View.VISIBLE
        }
        save_scene.singleClick {
            // 判断添加条件 添加动作 延时不能作为最后一个动作  延时后必须添加一个设备类型的动作
            //保存场景
            if (conditionAdapter.data.size == 0) {
                CustomToast.makeText(this, "请添加条件", R.drawable.ic_toast_warning)
                return@singleClick
            }
            if (actionAdapter.data.size == 0) {
                CustomToast.makeText(this, "请添加动作", R.drawable.ic_toast_warning)
                return@singleClick
            }
            val lastAction = actionAdapter.data[actionAdapter.data.size - 1]
            if (lastAction.action is DelayAction) {
                CustomToast.makeText(this, "延时后必须添加一个设备类型的动作", R.drawable.ic_toast_warning)
                return@singleClick
            }

            if (editScene)
                saveScene()
            else {
                sceneIconSelectDialog.show()
                sceneIconSelectDialog.initSelect(0)
            }
        }
        sceneIconSelectDialog.setOnSelectSceneIconListener {
            mRuleEngineBean.iconPath = getIconPathByIndex(it)
            showNameDialog("")
        }
        sceneSelectDelayDialog.setOnConfirmSelectTimeListener { _, min, second ->
            if (editActionPosition == -1) {
                //添加
                //延时动作添加返回
                val seconds = min * 60 + second
                if (seconds == 0)
                    CustomToast.makeText(
                        this,
                        "无效的延时设置",
                        R.drawable.ic_toast_warning
                    )
                else {
                    val delayAction = DelayAction()
                    delayAction.rightValue = seconds.toString()
                    mRuleEngineBean.actions.add(delayAction)
                    addDelayItem(delayAction)
                }
            } else {
                //编辑
                val seconds = min * 60 + second
                if (seconds == 0)
                    CustomToast.makeText(
                        this,
                        "无效的延时设置",
                        R.drawable.ic_toast_warning
                    )
                else {
                    val item = actionAdapter.getItem(editActionPosition)
                    item?.let {
                        it.action?.rightValue = seconds.toString()
                        actionAdapter.notifyItemChanged(editActionPosition)
                    }
                }
            }


        }
        //删除场景
        delete_scene.singleClick {
            showDeleteDialog()
        }
    }


    private fun jumpEditActionDeviceItem(action: DeviceAction, position: Int) {
        if (action.functionName != null && action.functionName == "已失效") {
            CustomToast.makeText(this, "已失效的动作不能再点击进入", R.drawable.ic_toast_warning)
            return
        }
        val devicesBean = MyApplication.getInstance().getDevicesBean(action.targetDeviceId)
        if (devicesBean != null && devicesBean.bindType == 0) {
            val intent = Intent(this, SceneSettingFunctionDatumSetActivity::class.java)
            intent.putExtra("editMode", true)
            intent.putExtra("type", 1)
            intent.putExtra("action", action);
            intent.putExtra("editPosition", position)
            intent.putExtra("property", action.leftValue)
            intent.putExtra("deviceId", action.targetDeviceId)
            intent.putExtra("deviceName", action.functionName)
            startActivityForResult(intent, 1000)
        }
    }


    private fun jumpEditConditionDeviceItem(condition: DeviceCondition, position: Int) {
        if (condition.functionName != null && condition.functionName == "已失效") {
            CustomToast.makeText(this, "已失效的条件不能再点击进入", R.drawable.ic_toast_warning)
            return
        }

        val devicesBean = MyApplication.getInstance().getDevicesBean(condition.sourceDeviceId)
        if (devicesBean != null && devicesBean.bindType == 0) {
            val intent = Intent(this, SceneSettingFunctionDatumSetActivity::class.java)
            intent.putExtra("editMode", true)
            intent.putExtra("type", 0)
            intent.putExtra("condition", condition)
            intent.putExtra("editPosition", position)
            intent.putExtra("property", condition.leftValue)
            intent.putExtra("deviceId", condition.sourceDeviceId)
            intent.putExtra("deviceName", condition.functionName)
            startActivityForResult(intent, 1000)
        }
    }

    private fun showDeleteDialog() {
        TooltipBuilder().setTitle("移除场景").setContent("将移除本地场景联动，确认继续？")
            .setLeftButtonColor(Color.parseColor("#000000"))
            .setRightButtonColor(Color.parseColor("#D73B4B")).setRightButtonName("移除")
            .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                override fun onClickRight(dialog: AylaBaseDialog) {
                    mPresenter.deleteScene(mRuleEngineBean.ruleId)
                }

                override fun onClickLeft(dialog: AylaBaseDialog) {
                }

            }).show(supportFragmentManager, "remove_scene")
    }


    private fun saveScene() {
        if (mRuleEngineBean.ruleSetMode == RULE_SET_MODE.ALL) { //满足所有条件时
            val exist: MutableList<String> = ArrayList()
            for (condition in mRuleEngineBean.conditions) {
                if (condition is DeviceCondition) {
                    val deviceId = condition.sourceDeviceId
                    val leftValue = condition.leftValue
                    val value = deviceId + leftValue
                    if (exist.contains(value)) {
                        CustomToast.makeText(
                            baseContext,
                            "选择满足所有条件时，条件中不可以添加多个同一设备的同一功能",
                            R.drawable.ic_toast_warning
                        )
                        return
                    } else {
                        exist.add(value)
                    }
                }
            }
        }
        for (condition in mRuleEngineBean.conditions) {
            if (condition is DeviceCondition) {
                val devicesBean =
                    MyApplication.getInstance().getDevicesBean(condition.sourceDeviceId)
                if (devicesBean == null) {
                    CustomToast.makeText(
                        baseContext,
                        "如想激活此联动，请先删除已移除的设备",
                        R.drawable.ic_toast_warning
                    )
                    return
                } else {
                    if (devicesBean.bindType == 1) {
                        CustomToast.makeText(baseContext, "请先绑定待添加的设备", R.drawable.ic_toast_warning)
                        return
                    }
                }
                if (condition.functionName != null && condition.functionName == "已失效") {
                    CustomToast.makeText(this, "请删除失效条件后保存", R.drawable.ic_toast_warning)
                    return
                }
            }
        }
        for (action in mRuleEngineBean.actions) {
            if (action is DeviceAction) {
                val devicesBean = MyApplication.getInstance().getDevicesBean(action.targetDeviceId)
                if (devicesBean == null) {
                    if (!ThreeStringEques.mIsEques(action)) {
                        CustomToast.makeText(
                            baseContext,
                            "如想激活此联动，请先删除已移除的设备",
                            R.drawable.ic_toast_warning
                        )
                        return
                    }
                } else {
                    if (devicesBean.bindType == 1) {
                        CustomToast.makeText(baseContext, "请先绑定待添加的设备", R.drawable.ic_toast_warning)
                        return
                    }
                }
                if (action.functionName != null && action.functionName == "已失效") {
                    CustomToast.makeText(this, "请删除失效动作后保存", R.drawable.ic_toast_warning)
                    return
                }
            }
        }
        //自动化
        if (mRuleEngineBean.status == 2) { //如果是异常状态，就要更改为 不可用的正常状态
            mRuleEngineBean.status = 0
        } else {
            mRuleEngineBean.status = 1
        }
        mRuleEngineBean.ruleType = RULE_TYPE.AUTO

        mPresenter.saveOrUpdateRuleEngine(mRuleEngineBean)
    }

    private fun showAddActionDialog() {
        NpCustomSheet.Builder(this)
            .setText("设备控制", "延时")
            ?.show(object : NpCustomSheet.CallBack {
                override fun callback(index: Int) {
                    when (index) {
                        0 -> {
                            val intent = Intent(
                                this@LocalSceneSettingActivity,
                                SceneSettingDeviceSelectActivity::class.java
                            )
                            intent.putExtra(KEYS.LOCAL, true)
                            intent.putExtra(KEYS.SCENETYPE, 1)
                            intent.putExtra(KEYS.GATEWAYID, gatewayId)
                            startActivity(intent)
                        }
                        1 -> {
                            val size = actionAdapter.data.size
                            if (size > 0 && (actionAdapter.getItem(size - 1)?.action is DelayAction)) {
                                showTipsDialog("不能连续添加延时")
                            } else {
                                //设置延时为添加状态
                                editActionPosition = -1
                                sceneSelectDelayDialog.show()
                                sceneSelectDelayDialog.setMinSecondValue(0, 0)
                            }

                        }
                    }
                }

                override fun onCancel() {
                }

            })
    }

    private fun showTipsDialog(content: String) {
        TooltipBuilder().setTitle("提示").setContent(content).setShowLeftButton(false)
            .show(supportFragmentManager, "tips")
    }

    private fun showConditionTypeDialog() {
        NpCustomSheet.Builder(this)
            .setText("当满足任意条件时", "当满足所有条件时")
            ?.show(object : NpCustomSheet.CallBack {
                override fun callback(index: Int) {
                    when (index) {
                        0 -> {
                            local_scene_choose_condition_way.text = "当满足任意条件时"
                            mRuleEngineBean.ruleSetMode = RULE_SET_MODE.ANY
                        }
                        1 -> {
                            local_scene_choose_condition_way.text = "当满足所有条件时"
                            mRuleEngineBean.ruleSetMode = RULE_SET_MODE.ALL
                        }
                    }
                }

                override fun onCancel() {
                }

            })
    }

    /**
     * 通过icon下标获得图片url
     *
     * @param i
     * @return
     */
    private fun getIconPathByIndex(i: Int): String {
        return String.format("http://cdn-smht.ayla.com.cn/minip/assets/public/scene/%s.png", i)
    }


    private fun showNameDialog(name: String) {
        RuleNameDialog.newInstance(object : RuleNameDialog.DoneCallback {
            override fun onDone(
                dialog: DialogFragment?,
                txt: String?
            ) {
                mRuleEngineBean.ruleName = txt
                saveScene()
                dialog?.dismissAllowingStateLoss()
            }

            override fun onCancel(dialog: DialogFragment?) {
                sceneIconSelectDialog.show()
                dialog?.dismissAllowingStateLoss()
            }

        }).setEditValue(name)
            .setMaxLength(20)
            .show(supportFragmentManager, "scene_name")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == RESULT_OK) {
            data?.let { intent ->
                val enableTimeFromNext =
                    intent.getSerializableExtra("enable_position") as EnableTime?
                enableTimeFromNext?.let {
                    effectiveTime = it
                    mRuleEngineBean.enableTime = effectiveTime;
                    setEffectiveTime()
                }
            }
        }
        //编辑场景
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            val mItemEvent = data?.getSerializableExtra("every_data") as SceneItemEvent
            val deviceBean = MyApplication.getInstance().getDevicesBean(mItemEvent.deviceId)
            val attributesBean = mItemEvent.attributesBean
            val datumBean = mItemEvent.callBackBean
            val position = mItemEvent.editPosition
            if (position >= 0) {
                if (mItemEvent.condition) {
                    val conditionItem = mRuleEngineBean.conditions?.get(position)
                    if (conditionItem is DeviceCondition) {
                        mergeDeviceConditionItem(
                            conditionItem,
                            deviceBean,
                            attributesBean,
                            datumBean
                        )
                        conditionAdapter.notifyItemChanged(position)
                    }
                } else {
                    val actionItem = mRuleEngineBean.actions?.get(position)
                    if (actionItem is DeviceAction) {
                        mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean)
                        actionAdapter.notifyItemChanged(position)
                    }
                }

            }
        }
        //更多页面返回
        if (requestCode == 10011 && resultCode == 10012) {
            val iconPath = data?.getStringExtra("iconPath")
            val sceneName = data?.getStringExtra("sceneName")
            mRuleEngineBean.iconPath = iconPath
            mRuleEngineBean.ruleName = sceneName
            local_scene_app_bar.setCenterText(sceneName)
        }

    }

    /**
     * 为有效时间段设置值
     */
    private fun setEffectiveTime() {
        if (effectiveTime.isAllDay)
            local_scene_effect_time.text = "全天生效"
        else {
            val formatTime = String.format(
                Locale.CHINA,
                "%02d:%02d~%02d:%02d",
                effectiveTime.startHour,
                effectiveTime.startMinute,
                effectiveTime.endHour,
                effectiveTime.endMinute
            )
            local_scene_effect_time.text = formatTime
        }
        local_scene_effect_repeat.text = formatRepeatDay(effectiveTime.enableWeekDay)
    }

    override fun initPresenter(): LocalSceneSettingPresenter = LocalSceneSettingPresenter()

    private fun formatRepeatDay(days: IntArray): String {
        val sb = StringBuilder()
        if (days.size == 7) {
            sb.append("每天")
        } else {
            sb.append("周")
            for (i in days.indices) {
                when (days[i]) {
                    1 -> sb.append("一")
                    2 -> sb.append("二")
                    3 -> sb.append("三")
                    4 -> sb.append("四")
                    5 -> sb.append("五")
                    6 -> sb.append("六")
                    7 -> sb.append("日")
                }
                if (i < days.size - 1) {
                    sb.append(" ")
                }
            }
            if (TextUtils.equals("周一 二 三 四 五", sb.toString()))
                return "工作日"
        }
        return sb.toString()
    }

    /**
     * 处理设备类型的条件、动作 变化
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleSceneItemEvent(list: List<SceneItemEvent>) {
        for (sceneItemEvent in list) {
            val deviceBean = MyApplication.getInstance().getDevicesBean(sceneItemEvent.deviceId)
            val attributesBean = sceneItemEvent.attributesBean
            val datumBean = sceneItemEvent.callBackBean
            if (datumBean != null) {
                if (sceneItemEvent.condition) {
                    val conditionItem = DeviceCondition()
                    mergeDeviceConditionItem(conditionItem, deviceBean, attributesBean, datumBean)
                    mRuleEngineBean.conditions.add(conditionItem)
                    showData(0)
                } else {
                    val actionItem = DeviceAction()
                    mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean)
                    mRuleEngineBean.actions.add(actionItem)
                    showData(1)
                }
            }

        }
    }

    /**
     * 0 条件 1 动作
     */
    fun showData(type: Int) {
        if (type == 0) {
            val conditionItems: MutableList<LocalSceneConditionOrActionItem> =
                ArrayList()
            for (condition in mRuleEngineBean.conditions) {
                val item = LocalSceneConditionOrActionItem(
                    ConditionOrActionType.CONDITION,
                    condition,
                    null
                )
                conditionItems.add(item)
            }
            local_scene_add_condition.visibility = View.GONE
            conditionAdapter.setNewData(conditionItems)
            val actionItems: MutableList<SceneSettingActionItemAdapter.ActionItem> = ArrayList()
            for (action in mRuleEngineBean.actions) {
                actionItems.add(SceneSettingActionItemAdapter.ActionItem(action))
            }
        } else if (type == 1) {
            val actionItems: MutableList<LocalSceneConditionOrActionItem> = ArrayList()
            for (action in mRuleEngineBean.actions) {

                val item =
                    LocalSceneConditionOrActionItem(
                        if (action is DelayAction) ConditionOrActionType.DELAY else ConditionOrActionType.ACTION,
                        null,
                        action
                    )
                actionItems.add(item)
            }
            local_scene_add_action.visibility = View.GONE
            actionAdapter.setNewData(actionItems)
        }
    }

    private fun addDelayItem(delayAction: DelayAction) {
        val item =
            LocalSceneConditionOrActionItem(ConditionOrActionType.DELAY, null, delayAction)
        local_scene_add_action.visibility = View.GONE
        rv_action.visibility = View.VISIBLE
        actionAdapter.addData(item)
    }

    private fun mergeDeviceConditionItem(
        conditionItem: DeviceCondition, deviceBean: DevicesBean,
        attributesBean: AttributesBean,
        datumBean: CallBackBean
    ) {
        if (datumBean is ValueCallBackBean) {
            conditionItem.sourceDeviceId = deviceBean.deviceId
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                conditionItem.sourceDeviceType = DeviceType.SWITCH_PURPOSE_SUB_DEVICE
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                conditionItem.sourceDeviceType = DeviceType.INFRARED_VIRTUAL_SUB_DEVICE
            } else if (deviceBean.cuId == 0) {
                conditionItem.sourceDeviceType = DeviceType.AYLA_DEVICE_ID
            } else if (deviceBean.cuId == 1) {
                conditionItem.sourceDeviceType = DeviceType.ALI_DEVICE_ID
            }
            conditionItem.rightValue = datumBean.valueBean.value
            conditionItem.leftValue = attributesBean.code
            conditionItem.operator = datumBean.getOperator()
            conditionItem.functionName = attributesBean.displayName
            conditionItem.valueName = datumBean.valueBean.displayName
            conditionItem.isRadioProperty = true
        }
        if (datumBean is BitValueCallBackBean) {
            conditionItem.sourceDeviceId = deviceBean.deviceId
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                conditionItem.sourceDeviceType = DeviceType.SWITCH_PURPOSE_SUB_DEVICE
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                conditionItem.sourceDeviceType = DeviceType.INFRARED_VIRTUAL_SUB_DEVICE
            } else if (deviceBean.cuId == 0) {
                conditionItem.sourceDeviceType = DeviceType.AYLA_DEVICE_ID
            } else if (deviceBean.cuId == 1) {
                conditionItem.sourceDeviceType = DeviceType.ALI_DEVICE_ID
            }
            conditionItem.rightValue = datumBean.bitValueBean.value.toString()
            conditionItem.leftValue = attributesBean.code
            conditionItem.operator = datumBean.getOperator()
            conditionItem.functionName = attributesBean.displayName
            conditionItem.valueName = datumBean.bitValueBean.displayName
            conditionItem.bit = datumBean.bitValueBean.bit
            conditionItem.compareValue = datumBean.bitValueBean.compareValue
            conditionItem.isRadioProperty = true
        }
        if (datumBean is SetupCallBackBean) {
            conditionItem.sourceDeviceId = deviceBean.deviceId
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                conditionItem.sourceDeviceType = DeviceType.SWITCH_PURPOSE_SUB_DEVICE
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                conditionItem.sourceDeviceType = DeviceType.INFRARED_VIRTUAL_SUB_DEVICE
            } else if (deviceBean.cuId == 0) {
                conditionItem.sourceDeviceType = DeviceType.AYLA_DEVICE_ID
            } else if (deviceBean.cuId == 1) {
                conditionItem.sourceDeviceType = DeviceType.ALI_DEVICE_ID
            }
            conditionItem.rightValue = datumBean.targetValue
            conditionItem.leftValue = attributesBean.code
            conditionItem.operator = datumBean.getOperator()
            conditionItem.functionName = attributesBean.displayName
            conditionItem.valueName = datumBean.targetValue + datumBean.setupBean.unit
            conditionItem.isRadioProperty = false
        }
        if (datumBean is EventCallBackBean) {
            conditionItem.sourceDeviceId = deviceBean.deviceId
            if (attributesBean.code.endsWith(".")) { //event事件类型，A.无value情况，此时跳过选择value层级
                if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                    conditionItem.sourceDeviceType = DeviceType.SWITCH_PURPOSE_SUB_DEVICE
                } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                    conditionItem.sourceDeviceType = DeviceType.INFRARED_VIRTUAL_SUB_DEVICE
                } else if (deviceBean.cuId == 0) {
                    conditionItem.sourceDeviceType = DeviceType.AYLA_DEVICE_ID
                } else if (deviceBean.cuId == 1) {
                    conditionItem.sourceDeviceType = DeviceType.ALI_DEVICE_ID
                }
                conditionItem.rightValue = null
                conditionItem.leftValue = attributesBean.code
                conditionItem.operator = ""
                conditionItem.functionName = attributesBean.displayName
                conditionItem.valueName = "event"
            }
        }
    }

    private fun mergeDeviceActionItem(
        actionItem: DeviceAction, deviceBean: DevicesBean,
        attributesBean: AttributesBean,
        callBackBean: CallBackBean
    ) {
        if (callBackBean is ValueCallBackBean) {
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                actionItem.targetDeviceType = DeviceType.SWITCH_PURPOSE_SUB_DEVICE
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                actionItem.targetDeviceType = DeviceType.INFRARED_VIRTUAL_SUB_DEVICE
            } else if (deviceBean.cuId == 0) {
                actionItem.targetDeviceType = DeviceType.AYLA_DEVICE_ID
            } else if (deviceBean.cuId == 1) {
                actionItem.targetDeviceType = DeviceType.ALI_DEVICE_ID
            }
            actionItem.targetDeviceId = deviceBean.deviceId
            actionItem.rightValueType = callBackBean.valueBean.dataType
            actionItem.operator = callBackBean.getOperator()
            actionItem.leftValue = attributesBean.code
            actionItem.rightValue = callBackBean.valueBean.value
            actionItem.functionName = attributesBean.displayName
            actionItem.valueName = callBackBean.valueBean.displayName
        }
        if (callBackBean is SetupCallBackBean) {
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                actionItem.targetDeviceType = DeviceType.SWITCH_PURPOSE_SUB_DEVICE
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                actionItem.targetDeviceType = DeviceType.INFRARED_VIRTUAL_SUB_DEVICE
            } else if (deviceBean.cuId == 0) {
                actionItem.targetDeviceType = DeviceType.AYLA_DEVICE_ID
            } else if (deviceBean.cuId == 1) {
                actionItem.targetDeviceType = DeviceType.ALI_DEVICE_ID
            }
            actionItem.targetDeviceId = deviceBean.deviceId
            actionItem.rightValueType = attributesBean.dataType
            actionItem.operator = callBackBean.getOperator()
            actionItem.leftValue = attributesBean.code
            actionItem.rightValue = callBackBean.targetValue
            actionItem.functionName = attributesBean.displayName
            actionItem.valueName = callBackBean.targetValue + callBackBean.setupBean.unit
        }
    }

    override fun saveSuccess() {
        CustomToast.makeText(this, if (editScene) "保存成功" else "创建成功", R.drawable.ic_success)
        setResult(RESULT_OK)
        EventBus.getDefault().post(SceneChangedEvent())
        finish()
    }

    override fun saveFailed(throwable: Throwable?) {
        CustomToast.makeText(
            this,
            TempUtils.getLocalErrorMsg(throwable),
            R.drawable.ic_toast_warning
        )
    }

    override fun getPropertySuccess() {
        local_scene_add_action.visibility = View.GONE
        local_scene_add_condition.visibility = View.GONE
        val conditionItems = arrayListOf<LocalSceneConditionOrActionItem>()
        for (item in mRuleEngineBean.conditions) {
            conditionItems.add(
                LocalSceneConditionOrActionItem(
                    ConditionOrActionType.CONDITION,
                    item,
                    null
                )
            )
        }
        val actionItems = arrayListOf<LocalSceneConditionOrActionItem>()
        for (item in mRuleEngineBean.actions) {
            if (item is DelayAction)
                actionItems.add(
                    LocalSceneConditionOrActionItem(
                        ConditionOrActionType.DELAY,
                        null,
                        item
                    )
                )
            else if (item is DeviceAction)
                actionItems.add(
                    LocalSceneConditionOrActionItem(
                        ConditionOrActionType.ACTION,
                        null,
                        item
                    )
                )
        }
        conditionAdapter.setNewData(conditionItems)
        actionAdapter.setNewData(actionItems)
    }

    override fun getPropertyFail(throwable: Throwable?) {
        throwable?.let {
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(it), R.drawable.ic_toast_warning)
        }
    }

    override fun deleteScene(state: Boolean, throwable: Throwable?) {
        if (state) {
            EventBus.getDefault().post(SceneChangedEvent())
            finish()
        } else {
            throwable?.let {
                CustomToast.makeText(
                    this,
                    TempUtils.getLocalErrorMsg(it),
                    R.drawable.ic_toast_warning
                )
            }
        }
    }

//    override fun appBarRightTvClicked() {
//        super.appBarRightTvClicked()
//        val intent = Intent(this, SceneMoreActivity::class.java)
//        intent.putExtra(KEYS.SCENE_BASERESULT, mRuleEngineBean)
//        intent.putExtra(KEYS.SCENELOCATION, 1)
//        startActivityForResult(intent, 10011)
//
//    }

}