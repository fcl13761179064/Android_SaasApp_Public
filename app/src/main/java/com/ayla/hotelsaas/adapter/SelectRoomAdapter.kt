package com.ayla.hotelsaas.adapter

import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.bean.RoomBean
import com.ayla.hotelsaas.bean.SelectBean
import com.ayla.hotelsaas.protocol.MultiBindResp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @ClassName:  SetLocationAdapter
 * @Description:选择房间适配器
 * @Author: vi1zen
 * @CreateDate: 2020/10/13 14:41
 */
class SelectRoomAdapter(private val isMultiSelect:Boolean = false) : BaseQuickAdapter<MultiBindResp, BaseViewHolder>(
    R.layout.item_multi_device_found){
    override fun convert(holder: BaseViewHolder, item: MultiBindResp) {
        /*holder.itemView.iv_select.isSelected = item.isSelected
        holder.itemView.tv_name.text = item.data.roomName*/
        holder.itemView.setOnClickListener {
            if(isMultiSelect){
                item.isSelected = !item.isSelected
            }else{
                val tempSelected = item.isSelected
                data.find { it.isSelected }?.isSelected = false
                item.isSelected = !tempSelected
            }
            notifyDataSetChanged()
        }
    }
}