package com.ayla.hotelsaas.adapter.scene_dialog

import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.widget.scene_dialog.MenuItemsData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MenuItemAdapter :
    BaseQuickAdapter<MenuItemsData, BaseViewHolder>(R.layout.item_dialog_menu) {
    override fun convert(helper: BaseViewHolder?, items: MenuItemsData?) {
        helper?.setText(R.id.item_dialog_item_name, items?.name)
        helper?.setText(R.id.item_dialog_item_content, items?.content)
    }
}