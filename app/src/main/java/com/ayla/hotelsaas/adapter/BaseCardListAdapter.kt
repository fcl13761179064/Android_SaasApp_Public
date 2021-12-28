package com.ayla.hotelsaas.adapter

import com.ayla.base.ext.dp
import com.ayla.hotelsaas.R
import com.blankj.utilcode.util.ResourceUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @ClassName:  BaseCardListAdapter
 * @Description:通用卡片背景列表适配器
 * @Author: vi1zen
 * @CreateDate: 2021/8/6 15:04
 */
abstract class BaseCardListAdapter<T,VH: BaseViewHolder>(layoutResId:Int) : BaseQuickAdapter<T,VH>(layoutResId){
    private val firstBg by lazy { ResourceUtils.getDrawable(R.drawable.bg_device_item_top) }
    private val lastBg by lazy { ResourceUtils.getDrawable(R.drawable.bg_device_item_bottom) }
    private val singleBg by lazy { ResourceUtils.getDrawable(R.drawable.room_num_shape) }
    private val normalBg by lazy { ResourceUtils.getDrawable(R.drawable.location_bg_shape) }

    override fun convert(holder: VH, item: T) {
        when (holder.adapterPosition) {
            0 -> {
                if (itemCount == 1) {
                    holder.itemView.setPadding(0, 0, 0, 0)
                } else {
                    holder.itemView.setPadding(0, 10.dp(), 0, 0)
                }
                holder.itemView.background = if (itemCount == 1) singleBg else firstBg
            }


            itemCount - 1 -> {
                holder.itemView.setPadding(0, 0, 0, 10.dp())
                holder.itemView.background = lastBg
            }else -> {
                holder.itemView.setPadding(0, 0, 0, 0)
                holder.itemView.background = normalBg
            }


        }
    }
}