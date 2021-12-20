package com.ayla.hotelsaas.adapter

import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.utils.ImageLoader
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_multi_device_found.view.*

/**
 * @ClassName:  MultiDeviceFoundAdapter
 * @Description:
 * @Author: vi1zen
 * @CreateDate: 2021/11/22 16:42
 */
class MultiDeviceFoundAdapter : BaseCardListAdapter<DeviceListBean.DevicesBean, BaseViewHolder>(R.layout.item_multi_device_found){
    override fun convert(holder: BaseViewHolder, item: DeviceListBean.DevicesBean) {
        super.convert(holder, item)
        ImageLoader.loadImg(
            holder.itemView.device_left_iv,
            item.iconUrl,
            R.drawable.ic_empty_device,
            R.drawable.ic_empty_device
        )
        holder.itemView.tv_device_name.text = item.deviceName
        holder.itemView.tv_device_id.text = item.deviceId
        holder.itemView.cb_function_checked.isChecked = item.isSelectDevice
    }
}