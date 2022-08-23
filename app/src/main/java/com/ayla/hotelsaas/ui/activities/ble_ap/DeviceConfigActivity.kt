package com.ayla.hotelsaas.ui.activities.ble_ap

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.DialogFragment
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseNewViewModelActivity
import com.ayla.hotelsaas.databinding.ActivityDeviceConfigBinding
import com.ayla.hotelsaas.events.AllAddDeviceEvent
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.ui.activities.MainActivity
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.viewmodel.DeviceConfigVM
import com.ayla.hotelsaas.widget.common_dialog.ItemPickerDialog
import com.ayla.hotelsaas.widget.common_dialog.ValueChangeDialog
import kotlinx.android.synthetic.main.activity_device_config.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class DeviceConfigActivity :
    BaseNewViewModelActivity<ActivityDeviceConfigBinding, DeviceConfigVM>() {
    private var regionId = -1L
    override fun getViewBinding(): ActivityDeviceConfigBinding =
        ActivityDeviceConfigBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        val name = intent.getStringExtra(KEYS.PRODUCKTNAME)
        device_config_name.text = name
        device_config_appbar.rightTextView.singleClick {
            val deviceId = intent.getStringExtra(KEYS.DEVICEID) ?: ""
            viewModel.updateDeviceInfo(
                deviceId,
                device_config_name.text.toString(),
                regionId,
                device_config_room.text.toString()
            ).observe(this, { result ->
                result.onSuccess {
                    EventBus.getDefault().post(AllAddDeviceEvent())
                    startActivity(Intent(this, MainActivity::class.java))
                }
                result.onFailure {
                    showWarnToast(TempUtils.getLocalErrorMsg(it))
                }
            })

        }
        layout_name.singleClick {
            showChangeNameDialog()
        }
        layout_room.singleClick {
            showSetLocationDialog()
        }

    }

    private fun showChangeNameDialog() {
        ValueChangeDialog
            .newInstance { dialog: DialogFragment, newName: String ->
                if (TextUtils.isEmpty(newName) || newName.trim { it <= ' ' }
                        .isEmpty()) {
                    CustomToast.makeText(this, "修改名称不能为空", R.drawable.ic_toast_warning)
                    return@newInstance
                } else {
                    device_config_name.text = newName
                }
                dialog.dismissAllowingStateLoss()
            }
            .setEditValue(device_config_name.text.toString())
            .setTitle("修改名称")
            .setEditHint("请输入名称")
            .setMaxLength(20)
            .show(supportFragmentManager, "device_name")
    }

    private fun showSetLocationDialog() {
        val purposeCategoryBeans: MutableList<String> = ArrayList()
        val locationBean = MyApplication.getInstance().devicesLocationBean
        var selectIndex = -1
        for (i in locationBean.indices) {
            purposeCategoryBeans.add(locationBean[i].regionName)
            if (regionId == locationBean[i].regionId) selectIndex = i
        }
        ItemPickerDialog.newInstance()
            .setSubTitle("请选择设备所属位置")
            .setTitle("设备位置")
            .setLocationIconRes(R.mipmap.choose_location_icon, 1000)
            .setData(purposeCategoryBeans)
            .setDefaultIndex(selectIndex).setCallback {
                if (it is String) {
                    if (TextUtils.isEmpty(it) || it.trim().isEmpty()) {
                        CustomToast.makeText(this, "设备名称不能为空", R.drawable.ic_toast_warning);
                        return@setCallback
                    }
                    for (item in locationBean) {
                        if (TextUtils.equals(item.regionName, it)) {
                            regionId = item.regionId
                            device_config_room.text = item.regionName
                            break
                        }
                    }

                }
            }.show(supportFragmentManager, "set_device_location");
    }

}