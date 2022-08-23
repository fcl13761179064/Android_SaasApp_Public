package com.ayla.hotelsaas.interfaces

import com.ayla.hotelsaas.bean.dialog.BaseDialogItemData

interface OnDialogItemClickListener<T : BaseDialogItemData> {
    fun onItemClick(item: T)
}