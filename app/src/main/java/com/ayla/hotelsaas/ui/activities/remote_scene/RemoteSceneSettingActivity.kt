package com.ayla.hotelsaas.ui.activities.remote_scene

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter
import com.ayla.hotelsaas.adapter.remote_scene.ConditionOrActionType
import com.ayla.hotelsaas.adapter.remote_scene.RemoteSceneConditionOrActionAdapter
import com.ayla.hotelsaas.adapter.remote_scene.SceneConditionOrActionItem
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean
import com.ayla.hotelsaas.bean.DeviceTemplateBean.AttributesBean
import com.ayla.hotelsaas.bean.GroupItem
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean.*
import com.ayla.hotelsaas.bean.scene_bean.DeviceType
import com.ayla.hotelsaas.bean.scene_bean.RemoteSceneBean
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.events.OneKeyRulerBean
import com.ayla.hotelsaas.events.SceneChangedEvent
import com.ayla.hotelsaas.events.SceneItemEvent
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.interfaces.ChoosePropertyValueListener
import com.ayla.hotelsaas.mvp.present.RemoteSceneSettingPresenter
import com.ayla.hotelsaas.mvp.view.RemoteSceneSettingView
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet.*
import com.ayla.hotelsaas.ui.activities.OnekeylinkageListActivity
import com.ayla.hotelsaas.ui.activities.RuleEngineActionHotelWelcomeActivity
import com.ayla.hotelsaas.ui.activities.SceneMoreActivity
import com.ayla.hotelsaas.ui.activities.SceneSettingFunctionDatumSetActivity
import com.ayla.hotelsaas.ui.activities.set_scene.ChooseDevicePropertyValueUtil
import com.ayla.hotelsaas.ui.activities.set_scene.SceneSettingDeviceSelectActivity
import com.ayla.hotelsaas.ui.activities.set_scene.SetEffectiveTimeActivity
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.widget.HomeItemDragAndSwipeCallback
import com.ayla.hotelsaas.widget.ThreeStringEques
import com.ayla.hotelsaas.widget.common_dialog.*
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import com.blankj.utilcode.util.GsonUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemDragListener
import kotlinx.android.synthetic.main.activity_local_scene_setting.delete_scene
import kotlinx.android.synthetic.main.activity_local_scene_setting.layout_effective_time
import kotlinx.android.synthetic.main.activity_local_scene_setting.local_scene_add_action
import kotlinx.android.synthetic.main.activity_local_scene_setting.local_scene_add_action_top
import kotlinx.android.synthetic.main.activity_local_scene_setting.local_scene_add_condition
import kotlinx.android.synthetic.main.activity_local_scene_setting.local_scene_add_condition_top
import kotlinx.android.synthetic.main.activity_local_scene_setting.local_scene_choose_condition_way
import kotlinx.android.synthetic.main.activity_local_scene_setting.local_scene_effect_repeat
import kotlinx.android.synthetic.main.activity_local_scene_setting.local_scene_effect_time
import kotlinx.android.synthetic.main.activity_local_scene_setting.rv_action
import kotlinx.android.synthetic.main.activity_local_scene_setting.rv_condition
import kotlinx.android.synthetic.main.activity_local_scene_setting.save_scene
import kotlinx.android.synthetic.main.activity_remote_scene_setting.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.util.*

data class TTSPayloadBean(val text: String?)
class RemoteSceneSettingActivity :
    BaseMvpActivity<RemoteSceneSettingView, RemoteSceneSettingPresenter>(), RemoteSceneSettingView {
    private val conditionAdapter = RemoteSceneConditionOrActionAdapter()
    private var actionAdapter: SceneSettingActionItemAdapter? = null
    private var effectiveTime = EnableTime()
    private var mBtnPosition = 0
    private var editScene = false
    private val REQUEST_CODE_HOTEL_WELCOME_ACTION = 0X17//欢迎语
    private var scopeId: Long? = null
    private var createDelay = false
    private var startConditionPos = -1
    private var fromPos = 0
    private val sceneIconSelectDialog by lazy {
        RemoteSceneIconSelectDialog(this)
    }
    private val sceneSelectDelayDialog by lazy {
        SelectTimeDialog(this, false, true, true)
    }

    private var chooseDevicePropertyValueUtil: ChooseDevicePropertyValueUtil? = null
    private var mRuleEngineBean = RemoteSceneBean()

    override fun getLayoutId(): Int = R.layout.activity_remote_scene_setting

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
        scopeId = SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, 0)
        rv_condition.layoutManager = LinearLayoutManager(this)
        rv_condition.adapter = conditionAdapter
        //条件拖动排序
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


        actionAdapter = SceneSettingActionItemAdapter(null)
        actionAdapter?.bindToRecyclerView(rv_action)
        rv_action.layoutManager = LinearLayoutManager(this)
        rv_action.adapter = actionAdapter
        // 开启拖拽
        val itemDragAndSwipeCallback = HomeItemDragAndSwipeCallback(actionAdapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(rv_action)
        actionAdapter?.enableDragItem(itemTouchHelper)
        actionAdapter?.setOnItemDragListener(onItemDragListener)
        val remoteSceneBean = intent.getSerializableExtra(KEYS.REMOTESCENEBEAN)
        remoteSceneBean?.let {
            //编辑场景
            if (it is BaseSceneBean) {//这是编辑页面
                editScene = true
                conditionAdapter.setEdit(true)
                delete_scene.visibility = View.VISIBLE
                mRuleEngineBean = it as RemoteSceneBean
                effectiveTime = mRuleEngineBean.enableTime
                setEffectiveTime()
                remote_scene_app_bar.setCenterText(it.ruleName)
                remote_scene_app_bar.setRightText("更多")
                //设置条件类型
                val ruleSetMode = mRuleEngineBean.ruleSetMode
                local_scene_choose_condition_way.text =
                    if (ruleSetMode == RULE_SET_MODE.ALL) "当满足所有条件时" else "当满足任意条件时"
                loadScenePropertyDetail()
            } else
                mRuleEngineBean = RemoteSceneBean()
        }
        if (remoteSceneBean == null) {
            //添加云端场景
            val scopeId = SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, -1) ?: -1L
            mRuleEngineBean.ruleType = RULE_TYPE.AUTO
            mRuleEngineBean.siteType = SITE_TYPE.REMOTE
            mRuleEngineBean.ruleSetMode = RULE_SET_MODE.ANY
            mRuleEngineBean.scopeId = scopeId
            mRuleEngineBean.ruleDescription = "remote_rule"
            mRuleEngineBean.status = 1
            mRuleEngineBean.iconPath = getIconPathByIndex(1)
            val enableTime = EnableTime()
            mRuleEngineBean.enableTime = enableTime
            remote_scene_app_bar?.setRightText("")
            remote_scene_app_bar?.setCenterText("添加自动场景-云端")
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
                        this@RemoteSceneSettingActivity,
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


    private var onItemDragListener: OnItemDragListener = object : OnItemDragListener {
        override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            fromPos = pos
        }

        override fun onItemDragMoving(
            source: RecyclerView.ViewHolder,
            from: Int,
            target: RecyclerView.ViewHolder,
            to: Int
        ) {
        }

        override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            try {
                val action = mRuleEngineBean?.actions?.get(fromPos)
                mRuleEngineBean!!.actions.removeAt(fromPos)
                mRuleEngineBean!!.actions.add(pos, action)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        remote_scene_app_bar.rightTextView.singleClick {
            val intent = Intent(this, SceneMoreActivity::class.java)
            intent.putExtra(KEYS.SCENE_BASERESULT, mRuleEngineBean)
            intent.putExtra(KEYS.SCENELOCATION, 2)
            startActivityForResult(intent, 10011)
        }

        layout_effective_time.singleClick {
            val intent = Intent(this, SetEffectiveTimeActivity::class.java)
            intent.putExtra("enableTime", effectiveTime)
            startActivityForResult(intent, 999)
        }
        local_scene_add_condition_top.singleClick {
            val intent = Intent(
                this@RemoteSceneSettingActivity,
                SceneSettingDeviceSelectActivity::class.java
            )
            intent.putExtra(KEYS.SCENETYPE, 0)
            intent.putExtra(KEYS.REMOTE, "remote")
            startActivity(intent)
        }
        local_scene_add_condition.singleClick {
            val intent = Intent(
                this@RemoteSceneSettingActivity,
                SceneSettingDeviceSelectActivity::class.java
            )
            intent.putExtra(KEYS.SCENETYPE, 0)
            intent.putExtra(KEYS.REMOTE, "remote")
            startActivity(intent)
        }
        local_scene_add_action_top.singleClick {
            showAddActionDialog()
        }
        local_scene_add_action.singleClick {
            showAddActionDialog()
        }

        local_scene_choose_condition_way.singleClick {
            showConditionTypeDialog()
        }
        conditionAdapter.setOnItemChildClickListener { adapter, view, position ->
            mRuleEngineBean.conditions.removeAt(position)
            showData(0)
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
//                    jumpEditConditionDeviceItem(it.condition, position)
                } else Log.e(TAG, "不是设备Condition")
            }

        }
        actionAdapter?.setOnItemClickListener { adapter, view, position ->
            val action = mRuleEngineBean.actions?.get(position)
            if (action is DelayAction) {
                try {
                    mBtnPosition = position;
                    val rightValue = action.getRightValue()
                    val seconds = rightValue.toInt()
                    val hour = seconds / 3600
                    val min = (seconds - hour * 3600) / 60
                    val s = (seconds - hour * 3600) % 60
                    sceneSelectDelayDialog.show()
                    sceneSelectDelayDialog.setMinSecondValue(min, s)

                } catch (ignored: java.lang.Exception) {
                }
            } else if (action is DeviceAction) {
                if (action.getFunctionName() != null && action.getFunctionName().equals("已失效")) {
                    CustomToast.makeText(this, "已失效的动作不能再点击进入", R.drawable.ic_toast_warning)
                    return@setOnItemClickListener
                } else if (action.getFunctionName() != null && "已失效" != action.getFunctionName() && TextUtils.isEmpty(
                        action.getValueName()
                    )
                ) {
                    return@setOnItemClickListener
                }
                val devicesBean = MyApplication.getInstance().getDevicesBean(action.targetDeviceId)
                if (devicesBean != null && devicesBean.bindType == 0) {
                    chooseDevicePropertyValueUtil?.apply {
                        setCurrentPos(position)
                        sceneType = 1
                        setAction(action)
                        loadSingleProperty(action.targetDeviceId, action.leftValue, true)
                    }
                }
//                jumpEditActionDeviceItem(action, position)
            } else if (action is GroupAction) {
                chooseDevicePropertyValueUtil?.apply {
                    setCurrentPos(position)
                    sceneType = 1
                    setGroupAction(action)
                    loadGroupSingleProperty(action.targetDeviceId, action.leftValue)
                }
//                jumpEditActionGroupItem(action, position)
            } else if (action is TTSAction) {
                val devicesBean = MyApplication.getInstance().getDevicesBean(action.targetDeviceId)
                if (devicesBean != null && devicesBean.bindType == 0) {
                    if (action.getFunctionName() != null && action.getFunctionName().equals("已失效")
                    ) {
                        CustomToast.makeText(this, "已失效的动作不能再点击进入", R.drawable.ic_toast_warning)
                        return@setOnItemClickListener
                    } else if (action.getFunctionName() != null
                        && "已失效" != action.getFunctionName()
                        && TextUtils.isEmpty(action.getValueName())
                    ) {
                        return@setOnItemClickListener
                    }
                    showSetTTSTextDialog(action.valueName, position)
                }
            }
        }
        actionAdapter?.setOnItemChildClickListener(object :
            BaseQuickAdapter.OnItemChildClickListener {
            private fun doRemove(position: Int) {
                mRuleEngineBean.actions?.removeAt(position)
                showActionData()
            }

            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View,
                position: Int
            ) {
                val action = mRuleEngineBean.actions[position]
                if (action is WelcomeAction) {
                    CustomAlarmDialog.newInstance()
                        .setDoneCallback(object : CustomAlarmDialog.Callback {
                            override fun onDone(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                                doRemove(position)
                            }

                            override fun onCancel(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                            }
                        })
                        .setTitle("确认删除").setContent("删除后该房间下的音箱将不再播放欢迎语，是否删除？")
                        .show(supportFragmentManager, "delete")
                } else {
                    doRemove(position)
                }
            }
        })

        save_scene.singleClick {
            // 判断添加条件 添加动作 延时不能作为最后一个动作  延时后必须添加一个设备类型的动作
            //保存场景
            if (conditionAdapter.data.size == 0) {
                CustomToast.makeText(this, "请添加条件", R.drawable.ic_toast_warming)
                return@singleClick
            }
            if (actionAdapter?.data?.size == 0) {
                CustomToast.makeText(this, "请添加动作", R.drawable.ic_toast_warming)
                return@singleClick
            }
            var lastPotion = actionAdapter?.data?.size ?: -1
            if (lastPotion == -1) {
                return@singleClick
            }
            val lastAction = actionAdapter?.data?.get(lastPotion - 1)
            if (lastAction?.action is DelayAction) {
                CustomToast.makeText(this, "延时后必须添加一个设备类型的动作", R.drawable.ic_toast_warming)
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
            val delayAction = DelayAction()
            val seconds = min * 60 + second
            if (seconds == 0)
                CustomToast.makeText(
                    this,
                    "无效的延时设置",
                    R.drawable.ic_toast_warming
                )
            else {
                if (mRuleEngineBean is BaseSceneBean && createDelay == false) {
                    delayAction.rightValue = seconds.toString()
                    mRuleEngineBean?.actions?.removeAt(mBtnPosition)
                    mRuleEngineBean?.actions?.add(mBtnPosition, delayAction)
                    showActionData()
                } else {
                    createDelay = false
                    delayAction.rightValue = seconds.toString()
                    mRuleEngineBean?.actions?.add(delayAction)
                    showActionData()
                }

            }
        }
        //删除场景
        delete_scene.singleClick {
            showDeleteDialog()
        }
    }


    private fun jumpEditActionGroupItem(action: GroupAction, position: Int) {
        val devicesBean = MyApplication.getInstance().getGroupItem(action.targetDeviceId)
        if (devicesBean != null) {
            val intent = Intent(this, SceneSettingFunctionDatumSetActivity::class.java)
            intent.putExtra("editMode", true)
            intent.putExtra("action", action)
            intent.putExtra("editPosition", position)
            intent.putExtra("property", action.leftValue)
            intent.putExtra(KEYS.GROUPID, action.targetDeviceId)
            intent.putExtra("deviceName", action.functionName)
            intent.putExtra("type", 1)
            startActivityForResult(intent, 10013)
        }
    }

    fun showActionData() {
        val actionItems: MutableList<SceneSettingActionItemAdapter.ActionItem> = ArrayList()
        mRuleEngineBean?.actions?.forEach { action ->
            actionItems.add(SceneSettingActionItemAdapter.ActionItem(action))
        }
        actionAdapter?.setNewData(actionItems)
        if (mRuleEngineBean?.actions.isEmpty()) {
            local_scene_add_action.visibility = View.VISIBLE
        } else {
            local_scene_add_action.visibility = View.GONE
        }
    }


    private fun jumpEditActionDeviceItem(action: DeviceAction, position: Int) {
        val devicesBean = MyApplication.getInstance().getDevicesBean(action.targetDeviceId)
        if (devicesBean != null && devicesBean.bindType == 0) {
            val intent = addDeviceActionIntent()
            intent.setClass(applicationContext, SceneSettingFunctionDatumSetActivity::class.java)
            intent.putExtra("editMode", true)
            intent.putExtra("action", action)
            intent.putExtra("editPosition", position)
            intent.putExtra("property", action.leftValue)
            intent.putExtra("deviceId", action.targetDeviceId)
            intent.putExtra("deviceName", action.functionName)
            startActivityForResult(intent, 10013)
        }
    }

    private fun addDeviceActionIntent(): Intent {
        val mainActivity = Intent(this, SceneSettingDeviceSelectActivity::class.java)
        mainActivity.putExtra("scopeId", mRuleEngineBean!!.scopeId)
        mainActivity.putExtra("type", 1)
        val selectedDatum = ArrayList<String>()
        for (action in mRuleEngineBean!!.actions) {
            val targetDeviceId = action.targetDeviceId
            val leftValue = action.leftValue
            selectedDatum.add("$targetDeviceId $leftValue")
        }
        mainActivity.putStringArrayListExtra("selectedDatum", selectedDatum)
        return mainActivity
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
        TooltipBuilder().setTitle("移除场景").setContent("将移除云端场景联动，确认继续？")
            .setLeftButtonColor(Color.parseColor("#000000"))
            .setRightButtonColor(Color.parseColor("#D73B4B")).setRightButtonName("移除")
            .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                override fun onClickRight(dialog: AylaBaseDialog) {
                    mPresenter.deleteScene(mRuleEngineBean.ruleId)
                }

                override fun onClickLeft(dialog: AylaBaseDialog) {
                }

            }).show(supportFragmentManager, "move_scene")
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
                            R.drawable.ic_toast_warming
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
                        R.drawable.ic_toast_warming
                    )
                    return
                } else {
                    if (devicesBean.bindType == 1) {
                        CustomToast.makeText(baseContext, "请先绑定待添加的设备", R.drawable.ic_toast_warming)
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
                            R.drawable.ic_toast_warming
                        )
                        return
                    }
                } else {
                    if (devicesBean.bindType == 1) {
                        CustomToast.makeText(baseContext, "请先绑定待添加的设备", R.drawable.ic_toast_warming)
                        return
                    }
                }
            }
            if (action is GroupAction) {
                val groupItem = MyApplication.getInstance()
                    .getGroupItem(action.targetDeviceId)
                if (groupItem == null) {
                    if (!ThreeStringEques.mIsEques(action)) {
                        CustomToast.makeText(
                            baseContext,
                            "如想激活此联动，请先删除已移除的设备",
                            R.drawable.ic_toast_warning
                        )
                        return
                    }
                }

                if (action.getFunctionName() != null && action.getFunctionName().equals("已失效")) {
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
        for (action in mRuleEngineBean.actions) {
            if (ThreeStringEques.mIsEques(action)) {
                mRuleEngineBean.ruleType = RULE_TYPE.ACTION_ONE_RULE
            }
        }
        mPresenter.saveOrUpdateRuleEngine(mRuleEngineBean)
    }

    private fun showAddActionDialog() {
        val haveA6Gateway = MyApplication.getInstance().haveA6Gateway()
        val chooseItems = arrayListOf("设备控制", "延时", "酒店欢迎语", "一键执行")
        if (haveA6Gateway)
            chooseItems.add("语音播报文本")
        NpCustomSheet.Builder(this)
            .setChooseItems(chooseItems)
            .show(object : NpCustomSheet.CallBack {
                override fun callback(index: Int) {
                    when (index) {
                        0 -> {
                            val intent = Intent(
                                this@RemoteSceneSettingActivity,
                                SceneSettingDeviceSelectActivity::class.java
                            )
                            intent.putExtra(KEYS.SCENETYPE, 1)//这是动作
                            startActivity(intent)
                        }
                        1 -> {
                            //这里判断下是创建延迟
                            createDelay = true
                            handleDelaySelect()
                        }
                        2 -> {
                            CustomBottomAlarmDialog.newInstance()
                                .setDoneCallback(object : CustomBottomAlarmDialog.Callback {
                                    override fun onDone(dialog: CustomBottomAlarmDialog) {
                                        mPresenter.check(scopeId!!)
                                        dialog.dismissAllowingStateLoss()
                                    }

                                    override fun onCancel(dialog: CustomBottomAlarmDialog) {
                                        dialog.dismissAllowingStateLoss()
                                    }
                                }).setStyle(CustomBottomAlarmDialog.Style.STYLE_NORMAL)
                                .setTitle("添加酒店欢迎语")
                                .setCancelText("取消")
                                .setEnsureText("添加")
                                .setContent(resources.getString(R.string.hanyingyu))
                                .show(supportFragmentManager, "tip")
                        }
                        3 -> {
                            startActivity<OnekeylinkageListActivity>()
                        }
                        4 -> {
                            //语音播报
                            val a6GatewayData = MyApplication.getInstance().a6GatewayData()
                            if (a6GatewayData.size > 1) {
                                val intent = Intent(
                                    this@RemoteSceneSettingActivity,
                                    SelectA6GatewayActivity::class.java
                                )
                                startActivityForResult(intent, 666)
                            } else {
                                showSetTTSTextDialog("")
                            }
                        }
                    }
                }

                override fun onCancel() {
                }

            })
    }

    private fun showSetTTSTextDialog(content: String, position: Int = -1) {
        InputContentDialog().setTitle("语音播报文本")
            .setEditHint("请输入要播报的文本")
            .setEditValue(content)
            .setMaxLength(1024)
            .setInputFilter(
                arrayOf(CommonUtils.getInputLengthFilter(1024))
            )
            .setOperateListener(object : InputContentDialog.OperateListener {
                override fun confirm(content: String?) {
                    if (position != -1) {
                        val editItem = mRuleEngineBean.actions[position]
                        editItem.valueName = content
                        val payloadMap = hashMapOf<String, Any>()
                        payloadMap["version"] = "1.0"
                        payloadMap["timestamp"] = System.currentTimeMillis() / 1000
                        payloadMap["tts"] = TTSPayloadBean(content)
                        val ttsReqJson = GsonUtils.toJson(payloadMap)
                        editItem?.rightValue = ttsReqJson
                        actionAdapter?.notifyItemChanged(position)
                    } else {
                        val a6GatewayData = MyApplication.getInstance().a6GatewayData()
                        if (a6GatewayData.size == 1) {
                            val ttsAction = getTTSActionItem(
                                content ?: "",
                                a6GatewayData[0].deviceId,
                                a6GatewayData[0].cuId
                            )
                            mRuleEngineBean.actions.add(ttsAction)
                            showActionData()

                        }
                    }
                }

                override fun cancel() {
                }

                override fun contentOverMaxLength() {
                    CustomToast.makeText(
                        this@RemoteSceneSettingActivity,
                        "字符超出限制",
                        R.drawable.ic_warning
                    )
                }
            }).show(supportFragmentManager, "TTS")

    }

    private fun getTTSActionItem(content: String, deviceId: String?, cuId: Int): TTSAction {
        val payloadMap = hashMapOf<String, Any>()
        payloadMap["version"] = "1.0"
        payloadMap["timestamp"] = System.currentTimeMillis() / 1000
        payloadMap["tts"] = TTSPayloadBean(content)
        val ttsReqJson = GsonUtils.toJson(payloadMap)
        val actionItem = TTSAction()
        actionItem.targetDeviceId = deviceId ?: ""
        actionItem.targetDeviceType = cuId
        actionItem.leftValue = "user_tts"
        actionItem.rightValue = ttsReqJson
        actionItem.functionName = "语音播报"
        actionItem.valueName = content
        actionItem.operator = "=="
        actionItem.rightValueType = 1
        return actionItem
    }


    private fun handleDelaySelect() {
        mRuleEngineBean?.let {
            if (it.getActions().size != 0) {
                val lastAction = it.actions.get(it.actions.size - 1)
                if (lastAction is DelayAction) {
                    createDelay = false
                    CustomAlarmDialog.newInstance()
                        .setDoneCallback(object : CustomAlarmDialog.Callback {
                            override fun onDone(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                            }

                            override fun onCancel(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                            }
                        }).setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON).setTitle("提示")
                        .setContent("不能连续添加延时").show(supportFragmentManager, "tip")
                    return
                } else {
                    sceneSelectDelayDialog.show()
                }
            } else {
                sceneSelectDelayDialog.show()

            }
        }
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

    private val BOND = 1
    val handler: Handler = object : Handler() {
        override
        fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                BOND -> {
                    val inputMethodManager: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
        }
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
        handler.sendEmptyMessageDelayed(BOND, 200)
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
                if (actionItem is GroupAction) {
                    val devicesBean =
                        MyApplication.getInstance().getGroupItem(actionItem.targetDeviceId)
                    if (devicesBean != null)
                        mergeGroupActionItem(
                            actionItem,
                            devicesBean,
                            currentAttributesBean,
                            callBackBean
                        )
                }
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
                }
                actionAdapter?.notifyItemChanged(currentPos)
            }
        }

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
                        actionAdapter?.notifyItemChanged(position)
                    }
                }

            }
        } else if (requestCode == REQUEST_CODE_HOTEL_WELCOME_ACTION && resultCode == RESULT_OK) { //酒店欢迎语动作添加返回
            val welcomeAction = WelcomeAction()
            mRuleEngineBean?.actions.add(welcomeAction)
            showActionData()
        } else if (requestCode == 10013 && resultCode == RESULT_OK) {
            val mItemEvent = data?.getSerializableExtra("every_data") as SceneItemEvent
            val attributesBean = mItemEvent.attributesBean
            val datumBean = mItemEvent.callBackBean
            val position = mItemEvent.editPosition
            if (position >= 0 && datumBean != null) {
                val actionItem = mRuleEngineBean.actions?.get(position)
                if (actionItem is DeviceAction) {
                    val deviceBean = MyApplication.getInstance().getDevicesBean(mItemEvent.deviceId)
                    mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean)
                } else if (actionItem is GroupAction) {
                    val groupItem = MyApplication.getInstance().getGroupItem(mItemEvent.deviceId)
                    mergeGroupActionItem(actionItem, groupItem, attributesBean, datumBean)
                }
                actionAdapter?.notifyItemChanged(position)

            }
        }

        //更多页面返回
        if (requestCode == 10011 && resultCode == 10012) {
            val iconPath = data?.getStringExtra("iconPath")
            val sceneName = data?.getStringExtra("sceneName")
            mRuleEngineBean.iconPath = iconPath
            mRuleEngineBean.ruleName = sceneName
            remote_scene_app_bar.setCenterText(sceneName)
        }

        if (requestCode == 666 && resultCode == RESULT_OK) {
            //语音播报文本
            val ttsContent = data?.getStringExtra(KEYS.TTSCONTENT)
            val deviceId = data?.getStringExtra(KEYS.DEVICEID)
            val cuId = data?.getIntExtra(KEYS.CUID, 0)
            val ttsAction = getTTSActionItem(
                ttsContent ?: "",
                deviceId ?: "",
                cuId ?: 0
            )
            mRuleEngineBean.actions.add(ttsAction)
            showActionData()
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

    override fun initPresenter(): RemoteSceneSettingPresenter = RemoteSceneSettingPresenter()

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
            if (deviceBean != null && datumBean != null) {
                if (sceneItemEvent.condition) {
                    val conditionItem = DeviceCondition()
                    mergeDeviceConditionItem(conditionItem, deviceBean, attributesBean, datumBean)
                    mRuleEngineBean.conditions.add(conditionItem)
                    showData(0)
                } else {
                    if (sceneItemEvent.editMode) {
                        val position = sceneItemEvent.editPosition
                        if (position >= 0) {
                            val actionItem = mRuleEngineBean.actions[position] as DeviceAction
                            mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean)
                        }
                    } else {
                        val actionItem = DeviceAction()
                        mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean)
                        mRuleEngineBean.actions.add(actionItem)
                    }
                    showActionData()
                }
            } else {
                val groupItem =
                    MyApplication.getInstance().getGroupItem(sceneItemEvent.deviceId)
                if (groupItem != null && datumBean != null) {
                    val actionItem = GroupAction()
                    mergeGroupActionItem(
                        actionItem,
                        groupItem,
                        attributesBean,
                        datumBean
                    )//装数据
                    mRuleEngineBean?.getActions()?.add(actionItem)
                    showActionData()
                }
            }


        }
    }

    private fun mergeGroupActionItem(
        actionItem: GroupAction, groupItem: GroupItem,
        attributesBean: AttributesBean,
        callBackBean: CallBackBean
    ) {
        if (callBackBean is ValueCallBackBean) {
            actionItem.targetDeviceType = DeviceType.GROUP_ACTION
            actionItem.targetDeviceId = groupItem.groupId
            actionItem.rightValueType = 9
            actionItem.operator = callBackBean.getOperator()
            actionItem.leftValue = attributesBean.code
            actionItem.rightValue =
                callBackBean.valueBean.version + ";" + callBackBean.valueBean.value
            actionItem.functionName = attributesBean.displayName
            actionItem.valueName = callBackBean.valueBean.displayName
        }
        if (callBackBean is SetupCallBackBean) {
            actionItem.targetDeviceType = DeviceType.GROUP_ACTION
            actionItem.targetDeviceId = groupItem.groupId
            actionItem.rightValueType = 9
            actionItem.operator = callBackBean.getOperator()
            actionItem.leftValue = attributesBean.code
            actionItem.rightValue =
                callBackBean.version + ";" + "{" + "\"" + callBackBean.abilitySubCode + "\"" + ":" + callBackBean.targetValue + "}"
            actionItem.functionName = attributesBean.displayName
            actionItem.valueName =
                callBackBean.displayName + " " + callBackBean.targetValue + callBackBean.setupBean.unit
            actionItem.abilitySubCode = callBackBean.abilitySubCode
        }
    }

    /**
     * 0 条件 1 动作
     */
    fun showData(type: Int) {
        if (type == 0) {
            val conditionItems: MutableList<SceneConditionOrActionItem> =
                ArrayList()
            for (condition in mRuleEngineBean.conditions) {
                val item = SceneConditionOrActionItem(
                    ConditionOrActionType.CONDITION,
                    condition,
                    null
                )
                conditionItems.add(item)
            }
            local_scene_add_condition.visibility = View.GONE
            conditionAdapter.setNewData(conditionItems)
        }
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
            R.drawable.ic_toast_warming
        )
    }

    override fun getPropertySuccess() {
        local_scene_add_condition.visibility = View.GONE
        val conditionItems = arrayListOf<SceneConditionOrActionItem>()
        for (item in mRuleEngineBean.conditions) {
            conditionItems.add(
                SceneConditionOrActionItem(
                    ConditionOrActionType.CONDITION,
                    item,
                    null
                )
            )
        }
        conditionAdapter.setNewData(conditionItems)
        showActionData()

        val groupActions = arrayListOf<Action>()
        for (action in mRuleEngineBean.actions) {
            if (action is GroupAction) {
                groupActions.add(action)
            }
        }
        if (groupActions.size > 0) {
            mPresenter.loadGroupDetail(groupActions)
        }
    }

    override fun getPropertyFail(throwable: Throwable?) {
        throwable?.let {
            CustomToast.makeText(this, it.message, R.drawable.ic_toast_warming)
        }
    }

    override fun deleteScene(state: Boolean, throwable: Throwable?) {
        if (state) {
            EventBus.getDefault().post(SceneChangedEvent())
            finish()
        } else {
            throwable?.let {
                CustomToast.makeText(this, it.message, R.drawable.ic_toast_warming)
            }
        }
    }

    override fun getWelcomeYuSuccess(code: Int) {
        checkResult(code)
    }

    override fun getWelcomeYuFail(throwable: Throwable?) {
        TODO("Not yet implemented")
    }

    override fun getGroupActionDetail(action: Action) {
        actionAdapter?.let { adapter ->
            for ((index, item) in adapter.data.withIndex()) {
                if (item.action.targetDeviceId == action.targetDeviceId && action.leftValue == action.leftValue) {
                    adapter.notifyItemChanged(index)
                }
            }
        }
    }

    fun checkResult(result: Int) {
        //如果是编辑一个包含欢迎语动作的联动，就需要忽略这个错误
        var currentExitWelcomeAction = false
        for (action in mRuleEngineBean?.actions) {
            if (action is WelcomeAction) {
                currentExitWelcomeAction = true
                break
            }

        }
        if (result == 0) {
            if (!currentExitWelcomeAction) {//欢迎语页面
                startActivityForResult<RuleEngineActionHotelWelcomeActivity>(
                    REQUEST_CODE_HOTEL_WELCOME_ACTION
                )
                return
            } else {
                CustomToast.makeText(
                    baseContext,
                    "动作列表已存在欢迎语",
                    R.drawable.ic_toast_warming
                )
            }
        } else if (result == -1) { //没有音响
            CustomAlarmDialog.newInstance()
                .setDoneCallback(object : CustomAlarmDialog.Callback {
                    override fun onDone(dialog: CustomAlarmDialog) {
                        dialog.dismissAllowingStateLoss()
                    }

                    override fun onCancel(dialog: CustomAlarmDialog) {
                        dialog.dismissAllowingStateLoss()
                    }
                }).setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON).setTitle("提示")
                .setContent("未检测到该房间下有可设置的音箱，请确认是否已完成关联").show(supportFragmentManager, "tip")
            return
        } else if (result == -2) { //已经设置了欢迎语的联动
            CustomAlarmDialog.newInstance()
                .setDoneCallback(
                    object : CustomAlarmDialog.Callback {
                        override fun onDone(dialog: CustomAlarmDialog) {
                            dialog.dismissAllowingStateLoss()
                        }

                        override fun onCancel(dialog: CustomAlarmDialog) {
                            dialog.dismissAllowingStateLoss()
                        }
                    }).setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON).setTitle("提示")
                .setContent("当前房间已设置过酒店欢迎语，不可重复设置").show(supportFragmentManager, "tip")
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleDeviceRemote(event: OneKeyRulerBean) {
        val mOneKeyRuleBean = event.onekeyBean
        val action = AddOneKeyRuleList()
        action.valueName = mOneKeyRuleBean.ruleName
        action.leftValue = mOneKeyRuleBean.ruleId.toString() + ""
        action.rightValue = mOneKeyRuleBean.ruleId.toString() + ""
        action.operator = "=="
        action.rightValueType = 0
        action.targetDeviceId = mOneKeyRuleBean.ruleId.toString() + ""
        action.targetDeviceType = 7
        action.iconpath = mOneKeyRuleBean.iconPath
        mRuleEngineBean.actions.add(action)
        showActionData()

    }
}