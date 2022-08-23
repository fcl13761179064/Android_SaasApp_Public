package com.ayla.hotelsaas.adapter.common_dialog

import android.widget.ImageView
import android.widget.RelativeLayout
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.utils.ImageLoader
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

data class DialogChooseIconItem(val id: String, val iconUrl: String,val name:String, var check: Boolean)
class DialogChooseIconAdapter :
    BaseQuickAdapter<DialogChooseIconItem, BaseViewHolder>(R.layout.item_dialog_choose_icon) {
    override fun convert(helper: BaseViewHolder?, item: DialogChooseIconItem?) {
        helper?.apply {
            val iv = getView<ImageView>(R.id.item_dialog_choose_icon_iv)
            ImageLoader.loadImg(
                iv,
                item?.iconUrl,
                R.drawable.icon_switch_key,
                R.drawable.icon_switch_key
            )
            getView<ImageView>(R.id.item_dialog_choose_icon_check).isSelected = item?.check ?: false
            getView<RelativeLayout>(R.id.item_dialog_choose_icon_rl).isSelected =
                item?.check ?: false
        }
    }
}