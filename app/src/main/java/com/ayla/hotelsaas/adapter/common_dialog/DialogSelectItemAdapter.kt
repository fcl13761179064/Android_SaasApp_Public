package com.ayla.hotelsaas.adapter.common_dialog

import com.ayla.hotelsaas.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

data class DialogSelectItem(val id: String, var name: String)
class DialogSelectItemAdapter :
    BaseQuickAdapter<DialogSelectItem, BaseViewHolder>(R.layout.item_dialog_select_item) {
    override fun convert(helper: BaseViewHolder?, item: DialogSelectItem?) {
        helper?.apply {
            setText(R.id.item_dialog_select_item_name, item?.name)
        }
    }
}