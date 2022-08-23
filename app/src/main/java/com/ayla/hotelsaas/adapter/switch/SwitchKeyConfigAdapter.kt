package com.ayla.hotelsaas.adapter.switch

import android.widget.ImageView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.bean.BaseDevice
import com.ayla.hotelsaas.bean.DeviceItem
import com.ayla.hotelsaas.bean.GroupItem
import com.ayla.hotelsaas.utils.ImageLoader
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

data class SwitchKeyItem(val id: String, var value: BaseDevice?)
class SwitchKeyConfigAdapter :
    BaseQuickAdapter<SwitchKeyItem, BaseViewHolder>(R.layout.item_switch_key_config) {
    override fun convert(helper: BaseViewHolder?, item: SwitchKeyItem?) {
        helper?.apply {
            setText(R.id.item_switch_key_name, "开关${item?.id}")
            item?.value?.let {
                if (it is GroupItem) {
                    setImageResource(R.id.item_switch_key_icon, R.drawable.icon_group)
                    setText(R.id.item_switch_key_value, it.groupName)
                } else if (it is DeviceItem) {
                    setText(R.id.item_switch_key_value, it.nickname)
                    val iv = getView<ImageView>(R.id.item_switch_key_icon)
                    ImageLoader.loadImg(
                        iv,
                        it.iconUrl,
                        R.drawable.icon_switch_key,
                        R.drawable.icon_switch_key
                    )
                }
            }
        }
    }
}