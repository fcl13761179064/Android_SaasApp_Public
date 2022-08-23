package com.ayla.hotelsaas.ui.activities.onekey

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseViewModelActivity
import com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean
import com.ayla.hotelsaas.bean.DeviceTemplateBean.AttributesBean
import com.ayla.hotelsaas.bean.GroupItem
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.databinding.ActivitySceneAddOnekeyBinding
import com.ayla.hotelsaas.events.SceneChangedEvent
import com.ayla.hotelsaas.events.SceneItemEvent
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean.*
import com.ayla.hotelsaas.bean.scene_bean.DeviceType
import com.ayla.hotelsaas.bean.scene_bean.LocalSceneBean
import com.ayla.hotelsaas.bean.scene_bean.RemoteSceneBean
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.interfaces.ChoosePropertyValueListener
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet.*
import com.ayla.hotelsaas.ui.activities.RuleEngineActionHotelWelcomeActivity
import com.ayla.hotelsaas.ui.activities.SceneIconSelectActivity
import com.ayla.hotelsaas.ui.activities.SceneMoreActivity
import com.ayla.hotelsaas.ui.activities.SceneSettingFunctionDatumSetActivity
import com.ayla.hotelsaas.ui.activities.remote_scene.SelectA6GatewayActivity
import com.ayla.hotelsaas.ui.activities.remote_scene.TTSPayloadBean
import com.ayla.hotelsaas.ui.activities.set_scene.ChooseDevicePropertyValueUtil
import com.ayla.hotelsaas.ui.activities.set_scene.SceneSettingDeviceSelectActivity
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.viewmodel.SceneSelectDeviceViewModel
import com.ayla.hotelsaas.widget.*
import com.ayla.hotelsaas.widget.common_dialog.*
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemDragListener
import kotlinx.android.synthetic.main.activity_scene_add_onekey.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivityForResult
import java.util.*
import kotlin.concurrent.schedule

class SceneAddOneKeyActivity : BaseViewModelActivity<ActivitySceneAddOnekeyBinding>() {

    private var mActionAdapter: SceneSettingActionItemAdapter? = null
    private var scopeId: Long? = null
    private var mViewModel: SceneSelectDeviceViewModel = SceneSelectDeviceViewModel()
    private val REQUEST_CODE_HOTEL_WELCOME_ACTION = 0X17//欢迎语
    private val REQUEST_CODE_SET_DELAY_ACTION = 0X18//延时
    private var siteType = 2
    private var mRuleEngineBean: BaseSceneBean? = null
    private var fromPos = 0
    private var mBtnPosition = 0
    private var createDelay = false
    private val showDelayDialog by lazy {
        SelectTimeDialog(this, false, true, true)
    }
    private var chooseDevicePropertyValueUtil: ChooseDevicePropertyValueUtil? = null

    override fun getViewBinding(): ActivitySceneAddOnekeyBinding? =
        ActivitySceneAddOnekeyBinding.inflate(layoutInflater)


    /**
     * 仅初始化一些与界面相关的操作
     */
    override fun init(savedInstanceState: Bundle?) {

        chooseDevicePropertyValueUtil =
            ChooseDevicePropertyValueUtil(1, supportFragmentManager, object :
                ChoosePropertyValueListener {
                override fun onUpdate(currentPos: Int, callBackBean: CallBackBean?) {
                    callBackBean?.let {
                        updateItemValue(currentPos, it)
                    }
                }

                override fun onToastContent(content: String?) {
                    CustomToast.makeText(
                        this@SceneAddOneKeyActivity,
                        content,
                        R.drawable.ic_toast_warming
                    )
                }

                override fun onProgress() {
                    showLoading()
                }

                override fun onFinish() {
                    hideLoadings()
                }

            })
        //判断是否是新建一键联动，还是编辑一键联动
        val sceneBean = intent.getSerializableExtra("sceneBean")
        scopeId = SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, 0)
        if (sceneBean is BaseSceneBean) {
            siteType = sceneBean.siteType
            mRuleEngineBean = sceneBean
            tv_delete.setVisibility(View.VISIBLE)
            syncSourceAndAdapter2()
            appBar?.setRightText("更多")
            appBar?.setCenterText(sceneBean.ruleName)
            appBar?.rightTextView?.singleClick {
                //通过icon的path获取下标数字
                startActivityForResult<SceneMoreActivity>(
                    10011,
                    KEYS.SCENE_BASERESULT to mRuleEngineBean
                )
            }
        } else {
            mRuleEngineBean = RemoteSceneBean()
            mRuleEngineBean?.ruleType = RULE_TYPE.ONE_KEY
            mRuleEngineBean?.conditions?.add(OneKeyCondition())
            mRuleEngineBean?.scopeId = scopeId as Long
            mRuleEngineBean?.ruleDescription = "one_key_rule"
            mRuleEngineBean?.status = 1
            mRuleEngineBean?.ruleSetMode = 3
            val enableTime = EnableTime()
            mRuleEngineBean?.enableTime = enableTime
            appBar?.setRightText("")
            appBar?.setCenterText("添加一键执行")
        }

        rv_action.setLayoutManager(LinearLayoutManager(this))
        rv_action.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val size = SizeUtils.dp2px(10f)
                val position = parent.getChildAdapterPosition(view)
                outRect[0, if (position == 0) size else 0, 0] = size
            }
        })

        mActionAdapter = SceneSettingActionItemAdapter(null)
        mActionAdapter?.bindToRecyclerView(rv_action)
        mActionAdapter?.setEmptyView(R.layout.item_scene_setting_action_empty)
        mActionAdapter?.emptyView?.findViewById<View>(R.id.tv_add)
            ?.singleClick {
                chooseTask()
            }
        v_add_action.singleClick {
            chooseTask()
        }

        submitBtn.singleClick {
            mRuleEngineBean?.let {
                if (it.actions.size == 0) {
                    CustomToast.makeText(this, "请添加动作", R.drawable.ic_toast_warning)
                } else if (it.ruleId != 0L && it is BaseSceneBean) {//这个是编辑保存
                    val lastAction =
                        mRuleEngineBean!!.actions[mRuleEngineBean!!.actions.size - 1]
                    if (lastAction is DelayAction) { //如果最后一个Action是延时，不允许
                        CustomToast.makeText(
                            baseContext,
                            "延时后必须添加一个设备类型的动作",
                            R.drawable.ic_toast_warning
                        )
                        return@let
                    }
                    for (action in it.actions) {
                        if (action is DeviceAction) {
                            val devicesBean = MyApplication.getInstance()
                                .getDevicesBean(action.targetDeviceId)
                            if (devicesBean == null) {
                                if (!ThreeStringEques.mIsEques(action)) {
                                    CustomToast.makeText(
                                        baseContext,
                                        "如想激活此联动，请先删除已移除的设备",
                                        R.drawable.ic_toast_warning
                                    )
                                    return@let
                                }
                            } else {
                                if (devicesBean.bindType == 1) {
                                    CustomToast.makeText(
                                        baseContext,
                                        "请先绑定待添加的设备",
                                        R.drawable.ic_toast_warning
                                    )
                                    return@let
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
                                    return@let
                                }
                            }
                        }
                        if (action.getFunctionName() != null && action.getFunctionName()
                                .equals("已失效")
                        ) {
                            CustomToast.makeText(this, "请删除失效动作后保存", R.drawable.ic_toast_warning)
                            return@let
                        }
                    }

                    if (it.getRuleType() == 2) { //一键执行
                        it.setStatus(1)
                    }
                    loadingDialog.showLoading()
                    mViewModel?.saveOrUpdateRuleEngine(it)
                } else {//这个是创建保存
                    startActivityForResult<SceneIconSelectActivity>(100)
                }
            }
        }
        mViewModel.saveOrUpdateRuleEngine.observe(this, {
            loadingDialog.hideLoading()

            Timer().schedule(500) { //执行的任务
                CustomToast.makeRotationText(
                    this@SceneAddOneKeyActivity,
                    "保存中",
                    R.mipmap.bg_cicle_domation
                )
            }
            EventBus.getDefault().post(SceneChangedEvent())
            setResult(RESULT_OK)
            finish()
        })

        mViewModel.deviceErrorLiveData.observe(this, {
            loadingDialog.hideLoading()
            CustomToast.makeText(this, it.message.toString(), R.drawable.ic_toast_warning)
        })

        mViewModel.huanYingYuRuleEngine.observe(this, {
            loadingDialog.hideLoading()
            checkResult(it!!)
        })

        mViewModel.action_devices.observe(this, {
            loadingDialog.hideLoading()
            showActionData()
            val groupActions = arrayListOf<Action>()
            for (action in mRuleEngineBean!!.actions) {
                if (action is GroupAction) {
                    groupActions.add(action)
                }
            }
            if (groupActions.size > 0) {
                mViewModel.loadGroupDetail(groupActions)
            }
        })

        mViewModel.groupAction.observe(this, {
            mActionAdapter?.let { adapter ->
                for ((index, item) in adapter.data.withIndex()) {
                    if (item.action.targetDeviceId == it.targetDeviceId && it.leftValue == it.leftValue) {
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        })

        mActionAdapter?.setOnItemChildClickListener(object :
            BaseQuickAdapter.OnItemChildClickListener {
            private fun doRemove(position: Int) {
                mRuleEngineBean?.actions?.removeAt(position)
                showActionData()
            }

            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View,
                position: Int
            ) {
                val action = mRuleEngineBean!!.actions[position]
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
        tv_delete.singleClick {
            handleDelete()
        }
        mViewModel.deleteSuccess.observe(this, {
            loadingDialog.hideLoading()
            deleteSuccess()
        })


        showDelayDialog.setOnConfirmSelectTimeListener { _, min, second ->
            val delayAction = DelayAction()
            val seconds = min * 60 + second
            if (seconds == 0)
                CustomToast.makeText(
                    this,
                    "无效的延时设置",
                    R.drawable.ic_toast_warning
                )
            else {
                if (createDelay == false) {
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


        // 开启拖拽

        val itemDragAndSwipeCallback = HomeItemDragAndSwipeCallback(mActionAdapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(rv_action)
        mActionAdapter?.enableDragItem(itemTouchHelper)
        mActionAdapter?.setOnItemDragListener(onItemDragListener)

        mActionAdapter?.setOnItemClickListener { adapter, view, position ->
            val action = mRuleEngineBean?.actions?.get(position)
            if (action is DelayAction && mRuleEngineBean is RemoteSceneBean) {
                try {
                    mBtnPosition = position;
                    val rightValue = action.getRightValue()
                    var seconds = rightValue.toInt()
                    var hour = seconds / 3600
                    var min = (seconds - hour * 3600) / 60
                    var s = (seconds - hour * 3600) % 60
                    showDelayDialog.show()
                    showDelayDialog.setMinSecondValue(min, s)

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
    }

    private fun updateItemValue(currentPos: Int, callBackBean: CallBackBean) {
        chooseDevicePropertyValueUtil?.apply {
            val actionItem = mRuleEngineBean?.actions?.get(currentPos)
            if (actionItem is GroupAction){
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
            mActionAdapter?.notifyItemChanged(currentPos)
        }

    }

    private fun jumpEditActionDeviceItem(action: DeviceAction, position: Int) {
        val devicesBean = MyApplication.getInstance().getDevicesBean(action.targetDeviceId)
        if (devicesBean != null && devicesBean.bindType == 0) {
            val intent = addDeviceActionIntent()
            intent.setClass(
                applicationContext,
                SceneSettingFunctionDatumSetActivity::class.java
            )
            intent.putExtra("editMode", true)
            intent.putExtra("action", action)
            intent.putExtra("editPosition", position)
            intent.putExtra("property", action.leftValue)
            intent.putExtra("deviceId", action.targetDeviceId)
            intent.putExtra("deviceName", action.functionName)
            startActivityForResult(intent, 10013)
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

    private fun addDeviceActionIntent(): Intent {
        val mainActivity = Intent(this, SceneSettingDeviceSelectActivity::class.java)
        mainActivity.putExtra("scopeId", mRuleEngineBean!!.scopeId)
        mainActivity.putExtra("type", 1)
        if (mRuleEngineBean is LocalSceneBean) {
            mainActivity.putExtra(
                "targetGateway",
                (mRuleEngineBean as LocalSceneBean).targetGateway
            )
        }
        val selectedDatum = ArrayList<String>()
        for (action in mRuleEngineBean!!.actions) {
            val targetDeviceId = action.targetDeviceId
            val leftValue = action.leftValue
            selectedDatum.add("$targetDeviceId $leftValue")
        }
        mainActivity.putStringArrayListExtra("selectedDatum", selectedDatum)
        return mainActivity
    }


    var onItemDragListener: OnItemDragListener = object : OnItemDragListener {
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

    /**
     * 通过图片url获取icon下标
     *
     * @param path
     * @return
     */
    private fun getIconIndexByPath(path: String?): Int {
        var i = 1
        if (null == path) return i
        val indexString =
            path.replace("http://cdn-smht.ayla.com.cn/minip/assets/public/scene/", "")
                .replace(".png", "")
        try {
            i = indexString.toInt()
        } catch (ignore: Exception) {
        }
        return i
    }

    fun handleDelete() {
        showDeleteDialog()
    }


    private fun showDeleteDialog() {
        TooltipBuilder().setTitle("移除一键执行").setContent("将移除一键执行，确认继续？")
            .setLeftButtonColor(Color.parseColor("#000000"))
            .setRightButtonColor(Color.parseColor("#D73B4B")).setRightButtonName("移除")
            .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                override fun onClickRight(dialog: AylaBaseDialog) {
                    loadingDialog.showLoading()
                    mViewModel.deleteScene(mRuleEngineBean!!.ruleId)
                }

                override fun onClickLeft(dialog: AylaBaseDialog) {
                }

            }).show(supportFragmentManager, "move_oneKey")
    }

    private fun syncSourceAndAdapter2() {
        val conditions: MutableList<DeviceCondition> = ArrayList()
        val actions: MutableList<Action> = ArrayList()
        for (action in mRuleEngineBean!!.actions) {
            actions.add(action)
        }
        loadingDialog.showLoading()
        mRuleEngineBean?.scopeId?.let {
            mViewModel.loadFunctionDetail(it, conditions, actions)
        }
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
                    showDelayDialog.show()
                    //跳转到延时页面
//                    startActivityForResult<SceneActionDelaySettingActivity>(
//                        REQUEST_CODE_SET_DELAY_ACTION
//                    )
                }
            } else {
                showDelayDialog.show()
                //跳转到延时页面
//                startActivityForResult<SceneActionDelaySettingActivity>(
//                    REQUEST_CODE_SET_DELAY_ACTION
//                )
            }
        }
    }


//    /**
//     * 处理设备类型的条件、动作 变化
//     *
//     * @param sceneItemEvent
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun handleSceneItemEvent(list: List<SceneItemEvent>) {
//        for (sceneItemEvent in list) {
//            val deviceBean = MyApplication.getInstance().getDevicesBean(sceneItemEvent.deviceId)
//            val attributesBean = sceneItemEvent.attributesBean
//            val datumBean = sceneItemEvent.callBackBean
//            if (sceneItemEvent.editMode) {
//                val actionItem = DeviceAction()
//                mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean)//装数据
//                mRuleEngineBean?.getActions()?.add(actionItem)
//                showActionData()
//            }
//        }
//
//    }

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

    fun showActionData() {
        val actionItems: MutableList<SceneSettingActionItemAdapter.ActionItem> = ArrayList()
        mRuleEngineBean?.actions?.forEach { action ->
            actionItems.add(SceneSettingActionItemAdapter.ActionItem(action))
        }
        mActionAdapter?.setNewData(actionItems)
    }

    override fun onDestroy() {
        super.onDestroy()
        chooseDevicePropertyValueUtil?.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //选择ICON返回结果
            val index = data?.getIntExtra("index", 1)
            mRuleEngineBean!!.iconPath = index?.let { getIconPathByIndex(it) }
            index?.let { renameMethod(it) }

        } else if (requestCode == REQUEST_CODE_HOTEL_WELCOME_ACTION && resultCode == RESULT_OK) { //酒店欢迎语动作添加返回
            val welcomeAction = WelcomeAction()
            mRuleEngineBean!!.actions.add(welcomeAction)
            showActionData()
        } else if (requestCode == REQUEST_CODE_SET_DELAY_ACTION && resultCode == RESULT_OK) { //延时动作添加返回
            val seconds = data!!.getIntExtra("seconds", 0)
            val delayAction = DelayAction()
            delayAction.rightValue = seconds.toString()
            mRuleEngineBean?.actions?.add(delayAction)
            showActionData()
        } else if (requestCode == 10011 && resultCode == 10012) {
            val iconPath = data?.getStringExtra("iconPath")
            val sceneName = data?.getStringExtra("sceneName")
            mRuleEngineBean?.iconPath = iconPath
            mRuleEngineBean?.ruleName = sceneName
            appBar?.setCenterText(sceneName)
        } else if (requestCode == 10013 && resultCode == RESULT_OK) {
            val mItemEvent = data?.getSerializableExtra("every_data") as SceneItemEvent
            val attributesBean = mItemEvent.attributesBean
            val datumBean = mItemEvent.callBackBean
            val position = mItemEvent.editPosition
            if (position >= 0 && datumBean != null) {
                val actionItem = mRuleEngineBean?.getActions()?.get(position)
                if (actionItem is DeviceAction) {
                    val deviceBean = MyApplication.getInstance().getDevicesBean(mItemEvent.deviceId)
                    mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean)
                } else if (actionItem is GroupAction) {
                    val groupItem = MyApplication.getInstance().getGroupItem(mItemEvent.deviceId)
                    mergeGroupActionItem(actionItem, groupItem, attributesBean, datumBean)
                }
                mActionAdapter?.notifyItemChanged(position)

            }
        } else if (requestCode == 10014 && resultCode == RESULT_OK) {
            val list = data?.getSerializableExtra("every_data") as List<SceneItemEvent>
            for (sceneItemEvent in list) {
                val deviceBean =
                    MyApplication.getInstance().getDevicesBean(sceneItemEvent.deviceId)
                val attributesBean = sceneItemEvent.attributesBean
                val datumBean = sceneItemEvent.callBackBean
                if (deviceBean != null) {
                    if (datumBean != null) {
                        val actionItem = DeviceAction()
                        mergeDeviceActionItem(
                            actionItem,
                            deviceBean,
                            attributesBean,
                            datumBean
                        )//装数据
                        mRuleEngineBean?.getActions()?.add(actionItem)
                        showActionData()
                    }
                }
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
            mRuleEngineBean?.actions?.add(ttsAction)
            showActionData()
        }
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


    fun renameMethod(index: Int) {
        RuleNameDialog.newInstance(object : RuleNameDialog.DoneCallback {
            override fun onDone(
                dialog: DialogFragment?,
                txt: String?
            ) {
                mRuleEngineBean?.ruleName = txt
                val lastAction = mRuleEngineBean!!.actions[mRuleEngineBean!!.actions.size - 1]
                if (lastAction is DelayAction) { //如果最后一个Action是延时，不允许
                    CustomToast.makeText(
                        baseContext,
                        "延时后必须添加一个设备类型的动作",
                        R.drawable.ic_toast_warning
                    )
                    return
                }
                mViewModel?.saveOrUpdateRuleEngine(mRuleEngineBean!!)
                dialog?.dismissAllowingStateLoss()
            }

            override fun onCancel(dialog: DialogFragment?) {
                startActivityForResult<SceneIconSelectActivity>(100, "index" to index)
            }

        }).setTitle("场景名称")
            .setEditHint("请输入场景名称")
            .setEditValue("")
            .setMaxLength(20)
            .show(supportFragmentManager, "scene_name")

        handler.sendEmptyMessageDelayed(BOND, 200)

    }

    /**
     * 通过icon下标获得图片url
     *
     * @param i
     * @return
     */
    private fun getIconPathByIndex(i: Int): String? {
        return String.format("http://cdn-smht.ayla.com.cn/minip/assets/public/scene/%s.png", i)
    }

    fun checkResult(result: Int) {
        //如果是编辑一个包含欢迎语动作的联动，就需要忽略这个错误
        var currentExitWelcomeAction = false
        for (action in mRuleEngineBean!!.actions) {
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

    fun deleteSuccess() {
        loadingDialog.hideLoading()
        CustomToast.makeText(this, "删除成功", R.drawable.ic_success)
        setResult(RESULT_OK)
        EventBus.getDefault().post(SceneChangedEvent())
        finish()
    }


    private fun chooseTask() {
        val haveA6Gateway = MyApplication.getInstance().haveA6Gateway()
        val chooseItems = arrayListOf("设备控制", "延时", "酒店欢迎语")
        if (haveA6Gateway)
            chooseItems.add("语音播报文本")
        NpCustomSheet.Builder(this)
            .setChooseItems(chooseItems)
            .show(object : NpCustomSheet.CallBack {
                override fun callback(index: Int) {
                    when (index) {
                        0 -> {
                            startActivityForResult<SceneSettingDeviceSelectActivity>(
                                10014,
                                KEYS.REMOTE to "one_key"
                            )
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
                                        mViewModel.check(scopeId!!)
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
                            //语音播报
                            val a6GatewayData = MyApplication.getInstance().a6GatewayData()
                            if (a6GatewayData.size > 1) {
                                val intent = Intent(
                                    this@SceneAddOneKeyActivity,
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
                        val editItem = mRuleEngineBean?.actions?.get(position)
                        editItem?.valueName = content
                        val payloadMap = hashMapOf<String, Any>()
                        payloadMap["version"] = "1.0"
                        payloadMap["timestamp"] = System.currentTimeMillis() / 1000
                        payloadMap["tts"] = TTSPayloadBean(content)
                        val ttsReqJson = GsonUtils.toJson(payloadMap)
                        editItem?.rightValue = ttsReqJson
                        mActionAdapter?.notifyItemChanged(position)
                    } else {
                        val a6GatewayData = MyApplication.getInstance().a6GatewayData()
                        if (a6GatewayData.size == 1) {
                            val ttsAction = getTTSActionItem(
                                content ?: "",
                                a6GatewayData[0].deviceId,
                                a6GatewayData[0].cuId
                            )
                            mRuleEngineBean?.actions?.add(ttsAction)
                            showActionData()

                        }
                    }
                }

                override fun cancel() {
                }

                override fun contentOverMaxLength() {
                    CustomToast.makeText(
                        this@SceneAddOneKeyActivity,
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
        actionItem.leftValue = ConstantValue.TTS_LEFT_VALUE
        actionItem.rightValue = ttsReqJson
        actionItem.functionName = "语音播报"
        actionItem.valueName = content
        actionItem.operator = "=="
        actionItem.rightValueType = 1
        return actionItem
    }
}