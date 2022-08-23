package com.ayla.hotelsaas.adapter.scene_dialog

import android.view.View
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.widget.scene_dialog.RadioItemData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class RadioItemAdapter :
    BaseQuickAdapter<RadioItemData, BaseViewHolder>(R.layout.item_dialog_radio_item) {
    override fun convert(helper: BaseViewHolder?, item: RadioItemData?) {
        helper?.setText(R.id.item_dialog_item_name, item?.name)
        helper?.getView<View>(R.id.item_dialog_item_iv)?.isSelected = item?.check == true
    }
}