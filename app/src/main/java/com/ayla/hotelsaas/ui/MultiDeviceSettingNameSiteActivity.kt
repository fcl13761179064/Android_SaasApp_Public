package com.ayla.hotelsaas.ui

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.base.ext.request
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.SelectRoomAdapter
import com.ayla.hotelsaas.api.CommonApi
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.events.RegionChangeEvent
import com.ayla.hotelsaas.widget.ItemPickerDialog
import com.ayla.hotelsaas.widget.MultiDevicePisiteDialog
import kotlinx.android.synthetic.main.activity_device_setting.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import rx.Observable
import rx.functions.Func2

/**
 * @ClassName:  DeviceSettingActivity
 * @Description:设置设备名字和位置
 * @Author: vi1zen
 * @CreateDate: 2020/10/9 10:25
 */
class MultiDeviceSettingNameSiteActivity : BasicActivity() {

    private val adapter = SelectRoomAdapter()
    private val api = RetrofitHelper.getRetrofit().create(CommonApi::class.java)
    private var deviceId = ""
    private lateinit var deviceSubBean: BuilderInference

    override fun onResume() {
        super.onResume()
        getRoomData()
    }

    override fun getLayoutId(): Int =R.layout.activity_device_setting

    override fun getLayoutView(): View? =null


    override fun initView() {
        deviceId = intent.getStringExtra(Keys.ID) ?: ""
        deviceSubBean = intent.getParcelableExtra(Keys.DATA)!!
        mdf_rv_content.layoutManager = LinearLayoutManager(this)
        mdf_rv_content.adapter = adapter
        adapter.setEmptyView(R.layout.new_empty_page_status_layout)
        mdf_btn_next.setOnClickListener {setNameOrPosition()}
    }

    private fun setNameOrPosition() {
        MultiDevicePisiteDialog.newInstance()
            .setSubTitle("请选择设备所属位置")
            .setTitle("设备位置")
            .setData(null)
            .setDefaultIndex(1)
            .setCallback(object : MultiDevicePisiteDialog.Callback<String?> {
                override fun onCallback(newLocationName: String?) {


                }
            })
            .show(supportFragmentManager, "dialog")
    }

    override fun initListener() {

    }





    private fun getRoomData() {
        api.getExistRoom().request(this, {

        }, { })
    }
}