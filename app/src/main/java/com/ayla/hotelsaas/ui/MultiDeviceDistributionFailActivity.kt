package com.ayla.hotelsaas.ui

import android.text.TextUtils
import android.view.View
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.events.DeviceAddEvent
import com.ayla.hotelsaas.page.ext.setVisible
import kotlinx.android.synthetic.main.new_empty_page_status_layout.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

class MultiDeviceDistributionFailActivity : BasicActivity() {
    override fun getLayoutId(): Int {
        return R.layout.new_empty_page_status_layout
    }

    override fun getLayoutView(): View? {
        return null
    }

    override fun initView() {
    }

    override fun initListener() {
        val desc = intent.getStringExtra(Keys.MULTIDISTRIBUREFAIL) ?: ""
        if (TextUtils.isEmpty(desc)){
            appBar.setVisible(false)
        }else{
            appBar.setVisible(true)
        }
        cl_layout.setVisible(true)
        cl_layout.setVisible(true)
        bt_resert_search.setOnClickListener(View.OnClickListener {
            startActivity<DeviceAddGuideActivity>()
        })

        log_out.setOnClickListener(View.OnClickListener {
            EventBus.getDefault().post(DeviceAddEvent())
            startActivity<MainActivity>()
        })
    }
}