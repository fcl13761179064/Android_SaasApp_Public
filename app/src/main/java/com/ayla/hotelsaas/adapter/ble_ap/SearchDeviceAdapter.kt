package com.ayla.hotelsaas.adapter.ble_ap

import com.ayla.hotelsaas.R
import com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class SearchDeviceAdapter :
    BaseQuickAdapter<AylaBLEWiFiSetupDevice, BaseViewHolder>(R.layout.item_ble_search_device) {
    override fun convert(helper: BaseViewHolder?, item: AylaBLEWiFiSetupDevice?) {
        helper?.setText(R.id.item_ble_search_name, item?.deviceName)
    }
}