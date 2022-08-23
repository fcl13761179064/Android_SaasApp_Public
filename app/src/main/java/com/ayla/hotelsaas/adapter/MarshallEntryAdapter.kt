package com.ayla.hotelsaas.adapter

import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.bean.BaseDevice
import com.ayla.hotelsaas.bean.DeviceItem
import com.ayla.hotelsaas.bean.GroupItem
import com.ayla.hotelsaas.utils.ImageLoader
import com.ayla.hotelsaas.utils.TempUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_common_delect_device.view.*

/**
 * 场景功能动作选择，单选
 */
abstract class MarshallEntryAdapter(layoutResId: Int) :
    BaseQuickAdapter<BaseDevice, BaseViewHolder>(layoutResId) {
    override fun convert(helper: BaseViewHolder, item: BaseDevice) {
        if (item is DeviceItem) {
            setData(helper, item.nickname, item.regionName, item.deviceStatus, item.iconUrl)
        }
        if (item is GroupItem) {
            val roomName = TempUtils.getRoomName(item.regionId)
            setData(helper, item.groupName, roomName, item.connectionStatus, null)
        }
        helper.itemView.iv_delete_device.setOnClickListener {
            removeGroupDevice(item)
        }

    }

    private fun setData(
        helper: BaseViewHolder,
        name: String,
        regionName: String,
        connectionStatus: String,
        icon: String?
    ) {
        helper.setText(R.id.tv_device_name, name)
        helper.setText(R.id.tv_device_regeinName, regionName)
        helper.setBackgroundRes(R.id.device_left_iv, R.color.white)
        if ("OFFLINE" == connectionStatus) {
            helper.setVisible(R.id.rl_line_off, true)
            helper.setBackgroundRes(R.id.device_left_iv, R.drawable.round_gray_shape)
        } else {
            helper.setVisible(R.id.rl_line_off, false)
        }
        if (icon == null) {
            helper.setImageResource(R.id.device_left_iv, R.drawable.icon_group)
        } else {
            ImageLoader.loadImg(
                helper.getView(R.id.device_left_iv),
                icon,
                R.drawable.ic_empty_device,
                R.drawable.ic_empty_device
            )
        }

    }

    abstract fun removeGroupDevice(item: BaseDevice)
}