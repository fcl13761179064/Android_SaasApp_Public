package com.ayla.hotelsaas.adapter

import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.RoomBean
import com.ayla.hotelsaas.bean.SelectBean
import com.ayla.hotelsaas.page.ext.setVisible
import com.ayla.hotelsaas.protocol.MultiBindResp
import com.ayla.hotelsaas.utils.ImageLoader
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_device_add_guide.*
import kotlinx.android.synthetic.main.item_multi_device_found.view.*

/**
 * @ClassName:  SetLocationAdapter
 * @Description:选择房间适配器
 * @Author: vi1zen
 * @CreateDate: 2020/10/13 14:41
 */
class SelectRoomAdapter(private val isMultiSelect:Boolean = false) : BaseQuickAdapter<DeviceListBean.DevicesBean, BaseViewHolder>(
    R.layout.item_multi_device_found){
    override fun convert(holder: BaseViewHolder, item: DeviceListBean.DevicesBean) {
        holder.itemView.iv_arrow.setVisible(true)
        holder.itemView.tv_device_name.text = item.deviceName
        holder.itemView.tv_device_id.text = item.deviceId
        ImageLoader.loadImg(  holder.itemView.device_left_iv, item.iconUrl, R.drawable.ic_empty_device, R.drawable.ic_empty_device)
    }
}