package com.ayla.hotelsaas.ui.activities

import android.content.Intent
import android.text.InputFilter
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.CanAddMarshallDeviceAdapter
import com.ayla.hotelsaas.adapter.MarshallEntryAdapter
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.*
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.constant.SwitchWorkMode
import com.ayla.hotelsaas.events.DeviceAddEvent
import com.ayla.hotelsaas.events.DeviceChangedEvent
import com.ayla.hotelsaas.events.GroupUpdateEvent
import com.ayla.hotelsaas.mvp.present.DeviceMarshallEntryPresenter
import com.ayla.hotelsaas.mvp.view.DeviceMarshallEntryView
import com.ayla.hotelsaas.protocol.*
import com.ayla.hotelsaas.ui.activities.set_switch.SwitchKeyConfigActivity
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.widget.AppBar
import com.ayla.hotelsaas.widget.common_dialog.InputContentDialog
import com.ayla.hotelsaas.widget.common_dialog.MarchShallRenameDialog
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import com.blankj.utilcode.util.GsonUtils
import kotlinx.android.synthetic.main.activity_marshall_entry.*
import kotlinx.android.synthetic.main.activity_project_list.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

class DeviceMarshallEntryActivity :
    BaseMvpActivity<DeviceMarshallEntryView, DeviceMarshallEntryPresenter>(),
    DeviceMarshallEntryView {

    private var gatewayId: String = ""
    private var deviceId: String = ""
    private var mScopeId: Long = 0L
    private var regionId: Long = 0L
    private var groupDetail: GroupDetail? = null
    private var switchGroup = false
    private var currentIndex = ""

    //???????????????????????????id
    private var switchDeviceId = ""
    private val alsoAddDeviceAdapter =
        object : MarshallEntryAdapter(R.layout.item_common_delect_device) {
            override fun removeGroupDevice(item: BaseDevice) {
                this@DeviceMarshallEntryActivity.removeGroupDevice(item)
            }
        }
    private val allCanList by lazy { mutableListOf<BaseDevice>() }
    private val alsoAddList by lazy { mutableListOf<BaseDevice>() }
    private var groupName = ""

    private val canAddDeviceAdapter = CanAddMarshallDeviceAdapter(R.layout.item_common_add_device)
    override fun getLayoutId(): Int {
        return R.layout.activity_marshall_entry
    }

    override fun initView() {
        //??????????????????????????????
        switchGroup = intent.getBooleanExtra(KEYS.SWITCH_GROUP, false)
        currentIndex = intent.getStringExtra(KEYS.CURRENTINDEX) ?: "1"
        if (switchGroup) {
            val appBarLayout = findViewById<AppBar>(R.id.appBar)
            appBarLayout.setCenterText("??????${currentIndex}")
        }
        deviceId = intent.getStringExtra("deviceId") ?: ""
        mScopeId = intent.getLongExtra("scopeId", 0)
        regionId = intent.getLongExtra("regionId", 0)
        rv_also_add.layoutManager = LinearLayoutManager(this)
        alsoAddDeviceAdapter.bindToRecyclerView(rv_also_add)
        rv_also_add.adapter = alsoAddDeviceAdapter
        alsoAddDeviceAdapter.setEmptyView(R.layout.activity_empty_marshall)

        ry_add_device.layoutManager = LinearLayoutManager(this)
        canAddDeviceAdapter.bindToRecyclerView(ry_add_device)
        ry_add_device.adapter = canAddDeviceAdapter
        canAddDeviceAdapter.setEmptyView(R.layout.empty_marshall_device)


        val serializableGroupDetail = intent.getSerializableExtra("group")
        serializableGroupDetail?.let {
            groupDetail = serializableGroupDetail as GroupDetail
        }
        val productLabel = intent.getStringExtra(KEYS.PRODUCTLABEL)
        mPresenter.getSupportCombineGroupDevice(
            deviceId,
            productLabel,
            mScopeId
        )
        groupDetail?.let {
            val iterator = it.groupDeviceList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                for (item in ConstantValue.FOUR_SWITCH_PID) {
                    if (next.pid == item) {
                        switchDeviceId = next.deviceId
                        break
                    }
                }
            }
            for (item in it.groupDeviceList) {
                if (item.deviceId == switchDeviceId)
                    continue
                val deviceItem = DeviceItem()
                deviceItem.cuId = item.cuId
                deviceItem.deviceCategory = item.deviceCategory ?: ""
                deviceItem.deviceId = item.deviceId
                deviceItem.deviceName = item.nickname
                deviceItem.deviceStatus = item.connectionStatus ?: ""
                deviceItem.deviceUseType = item.deviceUseType
                deviceItem.domain = item.domain ?: ""
                deviceItem.h5Url = item.h5Url ?: ""
                deviceItem.iconUrl = item.actualIcon ?: ""
                deviceItem.isPurposeDevice = item.isPurposeDevice
                deviceItem.nickname = item.nickname
                deviceItem.pid = item.pid
                deviceItem.regionId = item.regionId
                deviceItem.regionName = item.regionName ?: ""
                alsoAddList.add(deviceItem)
            }
        }
        tmp_01.text = "???????????????(${alsoAddList.size})"
        alsoAddDeviceAdapter.setNewData(alsoAddList)
    }

    override fun initListener() {
        canAddDeviceAdapter.setOnItemChildClickListener { adapter, view, position ->
            val data = canAddDeviceAdapter.data.get(position)
            alsoAddList.add(data)
            allCanList.remove(data)
            isCanNextAndRefresh()
        }

        all_add_device_btn.setOnClickListener {
            if (allCanList.size == 0) return@setOnClickListener
            alsoAddList.addAll(allCanList)
            allCanList.clear()
            isCanNextAndRefresh()
        }

        btn_save_next.setOnClickListener {
            if (groupDetail != null) {
                if (switchGroup)
                    renameDialog(it)
                else
                    updateGroup()
            } else
                renameDialog(it)
        }
    }

    private fun updateGroup() {
        if (canSaveOrUpdate().not())
            return

        groupDetail?.let {
            var switchDevice: GroupNewDeviceItem? = null
            val iterator = it.groupDeviceList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                for (item in ConstantValue.FOUR_SWITCH_PID) {
                    if (next.pid == item) {
                        switchDevice = next
                        break
                    }
                }
            }
            val groupDeviceList = arrayListOf<GroupNewDeviceItem>()
            for (device in alsoAddList) {
                var id = ""
                if (device is DeviceItem)
                    id = device.deviceId
                else if (device is GroupItem)
                    id = device.groupId
                val groupDeviceItem = GroupNewDeviceItem()
                groupDeviceItem.deviceId = id
                groupDeviceList.add(groupDeviceItem)
            }
            if (switchGroup) {
                val mainGroupDevice = GroupNewDeviceItem()
                mainGroupDevice.deviceId = deviceId
                mainGroupDevice.subDeviceKey = currentIndex
                groupDeviceList.add(mainGroupDevice)
            } else {
                //?????????????????????????????????????????????????????????
                if (switchDevice != null)
                    groupDeviceList.add(switchDevice)
            }
            it.groupDeviceList = groupDeviceList
            mPresenter.updateGroup(GsonUtils.toJson(it))
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private fun canSaveOrUpdate(): Boolean {
        //???????????????????????????????????????????????????????????????
        if (!switchGroup) {
            //???????????? ??????????????????????????????????????????????????????
            if (TextUtils.isEmpty(switchDeviceId) && alsoAddList.size < 2) {
                CustomToast.makeText(
                    this@DeviceMarshallEntryActivity,
                    "??????2????????????????????????",
                    R.drawable.ic_toast_warning
                )
                return false
            }
        }
        return true
    }

    private fun renameDialog(view: View) {
        if (canSaveOrUpdate().not())
            return

        var name = ""
        groupDetail?.let {
            name = it.groupName
        }
        InputContentDialog()
            .setEditHint("???????????????")
            .setEditValue(name)
            .setGravity(Gravity.BOTTOM)
            .setTitle("????????????")
            .setInputFilter(arrayOf(InputFilter.LengthFilter(20)))
            .setOperateListener(object : InputContentDialog.OperateListener {
                override fun confirm(content: String?) {
                    if (switchGroup) {
                        groupName = content ?: ""
                        showCreateSwitchGroupTips()
                    } else {
                        saveGroup(content ?: "")
                    }
                }

                override fun cancel() {
                }

                override fun contentOverMaxLength() {

                }

            }).show(supportFragmentManager, "")
    }

    private fun showCreateSwitchGroupTips() {
        TooltipBuilder()
            .setTitle("??????")
            .setShowLeftButton(false)
            .setRightButtonName("?????????")
            .setContent("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
            .setClickOutCancel(false)
            .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                override fun onClickRight(dialog: AylaBaseDialog) {
                    val purposeDeviceId = intent.getStringExtra(KEYS.PURPOSE_DEVICEID)
                    if (TextUtils.isEmpty(purposeDeviceId)) {
                        if (groupDetail != null) {
                            groupDetail?.groupName = groupName
                            updateGroup()
                        } else
                            saveGroup(groupName)
                    } else {
                        showProgress("?????????...")
                        mPresenter.removeUseDevice(purposeDeviceId, mScopeId)
                    }
                }

                override fun onClickLeft(dialog: AylaBaseDialog) {
                }

            }).show(supportFragmentManager, "")
    }

    private fun saveGroup(name: String) {
        val groupDeviceList = arrayListOf<GroupDeviceBean>()
        for (device in alsoAddList) {
            var id = ""
            if (device is DeviceItem)
                id = device.deviceId
            else if (device is GroupItem)
                id = device.groupId
            groupDeviceList.add(GroupDeviceBean(id, null))
        }
        if (switchGroup)
            groupDeviceList.add(GroupDeviceBean(deviceId, currentIndex))

        val bean = GroupRequestBean(
            groupDeviceList, name, emptyList(), mScopeId, gatewayId,
            GroupTypeEnum.DEVICE_GROUP.code, regionId
        )

        mPresenter.createGroup(bean, name)
    }

    private fun removeGroupDevice(item: BaseDevice) {
        var connectStatus = ""
        if (item is DeviceItem)
            connectStatus = item.deviceStatus
        else if (item is GroupItem)
            connectStatus = item.connectionStatus

        //??????
        if (!TextUtils.equals(connectStatus, "ONLINE")) {
            CustomToast.makeText(this, "??????????????????????????????", R.drawable.ic_toast_warming)
            return
        }

        alsoAddList.remove(item)
        allCanList.add(item)
        isCanNextAndRefresh()
    }

    private fun isCanNextAndRefresh() {
        alsoAddDeviceAdapter.setNewData(alsoAddList)
        canAddDeviceAdapter.setNewData(allCanList)
        btn_save_next.isEnabled = alsoAddDeviceAdapter.data.isNotEmpty()
        tmp_01.text = "???????????????(${alsoAddList.size})"
    }


    override fun initPresenter(): DeviceMarshallEntryPresenter {
        return DeviceMarshallEntryPresenter()
    }


    override fun DeviceMarshallEntrySuccess(
        marshEntrydata: MutableList<MarshallEntryBean>,
        mGatewayId: String
    ) {
//        this.gatewayId = mGatewayId
//        if (alsoAddList.size > 0) {
//            val newData = marshEntrydata.filter {
//                var result = true
//                for (item in alsoAddList) {
//                    if (item.deviceId == it.deviceId) {
//                        result = false
//                        break
//                    }
//
//                }
//                result
//            }
//            allCanList.addAll(newData)
//        } else
//            allCanList.addAll(marshEntrydata)
//        canAddDeviceAdapter.setNewData(allCanList)
    }

    override fun DeviceMarshallEntryFail(o: String?) {
    }


    override fun saveGroupSuccess(groupId: String, groupName: String) {
        if (switchGroup) {
            mPresenter.updateSwitchProperty(
                deviceId,
                currentIndex,
                SwitchWorkMode.WIRELESS_MODE.code
            )
        } else {
            hideProgress()
            CustomToast.makeText(
                this@DeviceMarshallEntryActivity,
                "????????????",
                R.drawable.ic_toast_warning
            )
            val intent = Intent(this, DeviceDetailH5Activity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupName", groupName)
            intent.putExtra("scopeId", mScopeId)
            intent.putExtra("group_url", ConstantValue.Group_H5_Url)
            startActivityForResult(intent, 1000)
        }
    }

    override fun saveGroupFail(throwable: Throwable?) {
        hideProgress()
        throwable?.let {
            CustomToast.makeText(
                this,
                TempUtils.getLocalErrorMsg(throwable),
                R.drawable.ic_toast_warming
            )
        }
    }

    override fun updateGroupResult(result: Boolean?, throwable: Throwable?) {
        hideProgress()
        result?.let {
            if (result) {
                //??????????????????????????? h5?????????????????????
                val groupUpdateEvent = GroupUpdateEvent()
                groupUpdateEvent.updateDeviceCount = true
                EventBus.getDefault().post(groupUpdateEvent)
                finish()
            } else {
                if (throwable != null) CustomToast.makeText(
                    this,
                    TempUtils.getLocalErrorMsg(throwable),
                    R.drawable.ic_toast_warning
                ) else CustomToast.makeText(this, "????????????", R.drawable.ic_toast_warning)
            }
        }
        if (result == null) CustomToast.makeText(this, "????????????", R.drawable.ic_toast_warning)
    }

    override fun getCombineDeviceGroupSuccess(
        gatewayId: String,
        devices: MutableList<BaseDevice>?
    ) {
        this.gatewayId = gatewayId
        devices?.let {
            if (alsoAddList.size > 0) {
                val newData = devices.filter {
                    var result = true
                    for (item in alsoAddList) {
                        if (CommonUtils.getGroupDeviceId(item) == CommonUtils.getGroupDeviceId(it)) {
                            result = false
                            break
                        }

                    }
                    result
                }
                allCanList.addAll(newData)
            } else
                allCanList.addAll(devices)
        }
        canAddDeviceAdapter.setNewData(allCanList)
    }

    override fun onCombineDeviceGroupFail(throwable: Throwable?) {
        throwable?.let {
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(it), R.drawable.ic_toast_warming)
        }
    }

    override fun updateSwitchPropertySuccess() {
        hideProgress()
        EventBus.getDefault().post(DeviceAddEvent())
        EventBus.getDefault().post(DeviceChangedEvent("", ""))
        val intent = Intent(this, SwitchKeyConfigActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun updateSwitchPropertyFail(throwable: Throwable?) {
        hideProgress()
        throwable?.let {
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(it), R.drawable.ic_toast_warming)
        }
    }

    override fun removeUseDeviceResult(result: Boolean, throwable: Throwable?) {
        if (result)
            saveGroup(groupName)
        else {
            hideProgress()
            throwable?.let {
                CustomToast.makeText(
                    this,
                    TempUtils.getLocalErrorMsg(it),
                    R.drawable.ic_toast_warming
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            EventBus.getDefault().post(DeviceAddEvent())
            startActivity<MainActivity>()
            finish()
        }
    }
}