package com.ayla.hotelsaas.ui.activities.set_switch

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.Gravity
import androidx.fragment.app.DialogFragment
import com.ayla.hotelsaas.adapter.common_dialog.DialogChooseIconItem
import com.ayla.hotelsaas.adapter.common_dialog.DialogSelectItem
import com.ayla.hotelsaas.adapter.switch.SwitchKeyConfigAdapter
import com.ayla.hotelsaas.adapter.switch.SwitchKeyItem
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseNewViewModelActivity
import com.ayla.hotelsaas.bean.*
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.constant.DeviceSourceType
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.constant.SwitchWorkMode
import com.ayla.hotelsaas.databinding.ActivitySwitchKeyConfigBinding
import com.ayla.hotelsaas.events.DeviceAddEvent
import com.ayla.hotelsaas.events.DeviceChangedEvent
import com.ayla.hotelsaas.ui.activities.DeviceMarshallEntryActivity
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.viewmodel.SwitchKeyConfigVM
import com.ayla.hotelsaas.widget.NPItemDecoration
import com.ayla.hotelsaas.widget.common_dialog.ChooseIconDialog
import com.ayla.hotelsaas.widget.common_dialog.InputContentDialog
import com.ayla.hotelsaas.widget.common_dialog.RuleNameDialog
import com.ayla.hotelsaas.widget.common_dialog.SelectItemDialog
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SizeUtils
import kotlinx.android.synthetic.main.activity_switch_key_config.*
import org.greenrobot.eventbus.EventBus

class SwitchKeyConfigActivity :
    BaseNewViewModelActivity<ActivitySwitchKeyConfigBinding, SwitchKeyConfigVM>() {
    private var purPoseInfoBean: PurPoseInfoBean? = null
    private val switchKeyConfigAdapter = SwitchKeyConfigAdapter()
    private val bindTypeData = mutableListOf<DialogSelectItem>()
    private var currentIndex = "1"
    private var deviceId = ""
    private var commonDeviceData = mutableListOf<DialogChooseIconItem>()
    private var selectPos = 0
    private var deviceBean: DeviceListBean.DevicesBean? = null

    override fun getViewBinding(): ActivitySwitchKeyConfigBinding =
        ActivitySwitchKeyConfigBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        bindTypeData.add(DialogSelectItem(ConstantValue.COMMON_ID, "绑定普通灯"))
        bindTypeData.add(DialogSelectItem(ConstantValue.INTELLIGENT_ID, "绑定智能灯"))
        deviceId = intent.getStringExtra(KEYS.DEVICEID) ?: ""
        switch_key_rv.adapter = switchKeyConfigAdapter
        switch_key_rv.addItemDecoration(
            NPItemDecoration(
                SizeUtils.dp2px(12f).toFloat(),
                SizeUtils.dp2px(12f).toFloat()
            )
        )
        switchKeyConfigAdapter.setOnItemClickListener { adapter, view, position ->
            currentIndex = switchKeyConfigAdapter.getItem(position)?.id ?: "1"
            val value = switchKeyConfigAdapter.getItem(position)?.value
            showSelectBindTypeDialog(value)
        }
        deviceBean = MyApplication.getInstance().getDevicesBean(deviceId)
        deviceBean?.let {
            val switchKeyCount = CommonUtils.getSwitchKeyCount(it)
            val data = mutableListOf<SwitchKeyItem>()
            for (index in 0 until switchKeyCount) {
                data.add(SwitchKeyItem((index + 1).toString(), null))
            }
            switchKeyConfigAdapter.setNewData(data)
        }
    }

    private fun getGroupDetail(groupId: String) {
        showProgress()
        viewModel.getGroupDetail(groupId).observe(this) { result ->
            hideProgress()
            result.onSuccess {
                if (it is GroupDetail) {
                    val intent = Intent(this, DeviceMarshallEntryActivity::class.java)
                    intent.putExtra("group", it)
                    intent.putExtra(
                        "deviceId",
                        deviceId
                    )
                    intent.putExtra(KEYS.SWITCH_GROUP, true)
                    intent.putExtra(
                        "scopeId",
                        getIntent().getLongExtra(KEYS.SCOPE_ID, 0)
                    )
                    intent.putExtra(
                        KEYS.PRODUCTLABEL,
                        getIntent().getStringExtra(KEYS.PRODUCTLABEL)
                    )
                    intent.putExtra(KEYS.CURRENTINDEX, currentIndex)
                    startActivity(intent)
                }
            }
            result.onFailure {
                showWarnToast(TempUtils.getLocalErrorMsg(it))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getBindData()
    }

    private fun getBindData() {
        viewModel.getPurposeComboList(deviceId).observe(this) { result ->
            result.onFailure {
                showWarnToast(TempUtils.getLocalErrorMsg(it))
            }
            result.onSuccess {
                if (it is MutableList<*>) {
                    for (item in it) {
                        if (item is PurposeComboBean) {
                            if (TextUtils.isEmpty(item.groupId)) {
                                for ((index, keyItem) in switchKeyConfigAdapter.data.withIndex()) {
                                    val first = item.propertyCode.first().toString()
                                    if (keyItem.id == first) {
                                        val deviceItem = DeviceItem()
                                        deviceItem.deviceId = item.deviceId
                                        deviceItem.nickname = item.nickname
                                        deviceItem.iconUrl = item.iconUrl
                                        deviceItem.purposeId = item.purposeId
                                        keyItem.value = deviceItem
                                        switchKeyConfigAdapter.notifyItemChanged(index)
                                        break
                                    }
                                }
                            } else {
                                for ((index, keyItem) in switchKeyConfigAdapter.data.withIndex()) {
                                    if (keyItem.id == item.propertyCode) {
                                        val groupItem = GroupItem()
                                        groupItem.groupId = item.groupId
                                        groupItem.groupName = item.groupName
                                        keyItem.value = groupItem
                                        switchKeyConfigAdapter.notifyItemChanged(index)
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showSelectBindTypeDialog(value: BaseDevice?) {
        initBindTypeData(value)
        SelectItemDialog().setData(bindTypeData)
            .setOnSelectItemListener(object : SelectItemDialog.OnSelectItemListener {
                override fun onSelectItem(item: DialogSelectItem?) {
                    item?.let {
                        if (it.id == ConstantValue.INTELLIGENT_ID) {
                            if (TempUtils.isDeviceOnline(deviceBean).not()) {
                                showWarnToast("设备已离线，请上线后再试")
                                return@let
                            }
                            if (value == null) {
                                intent.setClass(
                                    this@SwitchKeyConfigActivity,
                                    DeviceMarshallEntryActivity::class.java
                                )
                                intent.putExtra(KEYS.CURRENTINDEX, currentIndex)
                                intent.putExtra(KEYS.SWITCH_GROUP, true)
                                startActivity(intent)
                            } else {
                                if (value is GroupItem)
                                    getGroupDetail(value.groupId)
                                if (value is DeviceItem) {
                                    //进入新增绑定智能灯，并且保存时需要移除原先的普通灯
                                    intent.setClass(
                                        this@SwitchKeyConfigActivity,
                                        DeviceMarshallEntryActivity::class.java
                                    )
                                    intent.putExtra(KEYS.CURRENTINDEX, currentIndex)
                                    intent.putExtra(KEYS.SWITCH_GROUP, true)
                                    intent.putExtra(KEYS.PURPOSE_DEVICEID, value.deviceId)
                                    startActivity(intent)
                                }
                            }
                        } else if (it.id == ConstantValue.COMMON_ID) {
                            if (TempUtils.isDeviceOnline(deviceBean).not()) {
                                showWarnToast("设备已离线，请上线后再试")
                                return@let
                            }
                            getAllCommonDevice(value)
                        }
                    }
                }
            }).show(supportFragmentManager, "")
    }

    private fun initBindTypeData(value: BaseDevice?) {
        bindTypeData[0].name = "绑定普通灯"
        bindTypeData[1].name = "绑定智能灯"
        value?.let {
            if (it is GroupItem)
                bindTypeData[1].name = "绑定智能灯（当前配置）"
            if (it is DeviceItem)
                bindTypeData[0].name = "绑定普通灯（当前配置）"
        }
    }

    private fun getAllCommonDevice(value: BaseDevice?) {
        showProgress()
        viewModel.getPurposeCategory().observe(this) { result ->
            hideProgress()
            result.onSuccess {
                val data = mutableListOf<DialogChooseIconItem>()
                if (it is MutableList<*>) {
                    for (item in it) {
                        if (item is PurposeCategoryBean) {
                            data.add(
                                DialogChooseIconItem(
                                    item.id.toString(),
                                    item.iconUrl,
                                    item.purposeName,
                                    false
                                )
                            )
                        }
                    }
                }
                if (data.size > 0) {
                    var defaultSelectPos = 0
                    if (value != null && value is DeviceItem) {
                        for ((index, iconItem) in data.withIndex()) {
                            if (value.purposeId == iconItem.id) {
                                defaultSelectPos = index
                                iconItem.check = true
                                break
                            }
                        }
                    } else
                        data[0].check = true
                    commonDeviceData.clear()
                    commonDeviceData.addAll(data)
                    showSelectCommonDeviceDialog(data, defaultSelectPos, value)
                }
            }
            result.onFailure {
                showWarnToast(TempUtils.getLocalErrorMsg(it))
            }
        }
    }

    private fun showSelectCommonDeviceDialog(
        data: MutableList<DialogChooseIconItem>,
        pos: Int,
        value: BaseDevice?
    ) {
        ChooseIconDialog().setTitle("选择图标").setData(data, pos)
            .setOnDialogSelectIconListener(object : ChooseIconDialog.OnDialogSelectIconListener {
                override fun onConfirm(pos: Int, item: DialogChooseIconItem) {
                    selectPos = pos
                    showNameDialog(item, value)
                }

                override fun onCancel() {
                }


            }).show(supportFragmentManager, "")
    }

    private fun showNameDialog(item: DialogChooseIconItem, value: BaseDevice?) {
        var name = ""
        value?.let {
            if (value is DeviceItem)
                name = value.nickname
        }

        InputContentDialog()
            .setEditHint("请输入名称")
            .setEditValue(name)
            .setGravity(Gravity.BOTTOM)
            .setTitle("填写名称")
            .setInputFilter(arrayOf(InputFilter.LengthFilter(20)))
            .setLeftBack(true)
            .setOperateListener(object : InputContentDialog.OperateListener {
                override fun confirm(content: String?) {
                    startSave(content ?: "", item, value)
                }

                override fun cancel() {
                    showSelectCommonDeviceDialog(commonDeviceData, selectPos, value)
                }

                override fun contentOverMaxLength() {

                }

            }).show(supportFragmentManager, "")
    }

    private fun startSave(name: String, item: DialogChooseIconItem, value: BaseDevice?) {
        showProgress()
        viewModel.getTemplate(deviceBean?.pid ?: "").observe(this) { result ->
            result.onFailure {
                hideProgress()
                showWarnToast(TempUtils.getLocalErrorMsg(it))
            }
            result.onSuccess {
                if (it is DeviceTemplateBean) {
                    val templateCode = getTemplateCode(it)
                    purPoseInfoBean = PurPoseInfoBean()
                    purPoseInfoBean?.purPoseInfoList?.add(
                        PurposeItemBean(
                            intent.getLongExtra(KEYS.SCOPE_ID, 0),
                            item.id,
                            templateCode,
                            name,
                            deviceBean?.deviceId ?: ""
                        )
                    )
                    purPoseInfoBean?.deviceLocation?.add(
                        UseDeviceLocationBean(
                            templateCode,
                            deviceBean?.regionId,
                            name
                        )
                    )
                    if (value == null)
                        saveUseDevice(GsonUtils.toJson(purPoseInfoBean))
                    else {
                        removeUseDevice(value, GsonUtils.toJson(purPoseInfoBean))
                    }
                }
            }

        }
    }

    private fun removeUseDevice(value: BaseDevice?, json: String) {
        if (value is DeviceItem) {
            viewModel.removeUseDevice(value.deviceId, intent.getLongExtra(KEYS.SCOPE_ID, 0))
                .observe(this) { result ->
                    result.onSuccess {
                        saveUseDevice(json)
                    }
                    result.onFailure {
                        hideProgress()
                        showWarnToast(TempUtils.getLocalErrorMsg(it))
                    }
                }
        }
        if (value is GroupItem) {
            viewModel.removeGroup(value.groupId).observe(this) { result ->
                result.onFailure {
                    hideProgress()
                    showWarnToast(TempUtils.getLocalErrorMsg(it))
                }
                result.onSuccess {
                    saveUseDevice(json)
                }
            }
        }
    }

    private fun saveUseDevice(json: String) {
        viewModel.saveCommonUseDevice(json).observe(this) { result ->
            result.onSuccess {
                updateDeviceProperty()
            }
            result.onFailure {
                hideProgress()
                showWarnToast(TempUtils.getLocalErrorMsg(it))
            }
        }
    }

    private fun updateDeviceProperty() {
        viewModel.updateDeviceProperty(deviceId, currentIndex, SwitchWorkMode.POWER_MODE.code)
            .observe(this) {
                it.onFailure {
                    hideProgress()
                    showWarnToast(TempUtils.getLocalErrorMsg(it))
                }
                it.onSuccess {
                    hideProgress()
                    EventBus.getDefault().post(DeviceAddEvent())
                    EventBus.getDefault().post(DeviceChangedEvent("", ""))
                    getBindData()
                }
            }
    }


    //获取设备物模板code
    private fun getTemplateCode(templateBean: DeviceTemplateBean): String {
        return if (deviceBean?.cuId == DeviceSourceType.AYLA.code) {//艾拉设备
            if (deviceBean?.pid == ConstantValue.A6_GATEWAY_PID) {
                "gw_switch_${currentIndex}"
            } else {
                templateBean.attributes?.find {
                    it.code.endsWith("Onoff") && it.code.startsWith("${currentIndex}:")
                }?.code ?: "PowerSwitch_${currentIndex}"
            }
        } else {//鸿雁设备
            "PowerSwitch_${currentIndex}"
        }
    }

}