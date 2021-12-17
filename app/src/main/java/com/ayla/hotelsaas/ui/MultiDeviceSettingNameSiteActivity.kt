package com.ayla.hotelsaas.ui

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.base.ext.request
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.SelectRoomAdapter
import com.ayla.hotelsaas.api.CommonApi
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceLocationBean
import com.ayla.hotelsaas.bean.PurposeCategoryBean
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.events.RegionChangeEvent
import com.ayla.hotelsaas.widget.ItemPickerDialog
import com.ayla.hotelsaas.widget.MultiDevicePisiteDialog
import com.ayla.hotelsaas.widget.MultiDeviceRenameOrPositeMethodDialog
import com.ayla.hotelsaas.widget.RuleNameDialog
import com.chad.library.adapter.base.BaseQuickAdapter
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
    private var deviceListBean: List<DeviceLocationBean>? = null
    override fun onResume() {
        super.onResume()
        getRoomData()
    }

    override fun getLayoutId(): Int = R.layout.activity_device_setting

    override fun getLayoutView(): View? = null


    override fun initView() {
        deviceId = intent.getStringExtra(Keys.ID) ?: ""
        deviceListBean = intent.getParcelableExtra(Keys.DATA) as List<DeviceLocationBean>?
        mdf_rv_content.layoutManager = LinearLayoutManager(this)
        mdf_rv_content.adapter = adapter
        adapter.setEmptyView(R.layout.new_empty_page_status_layout)
        mdf_btn_next.setOnClickListener { setNameOrPosition() }
        adapter.setOnItemChildClickListener(object : BaseQuickAdapter.OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View?,
                position: Int
            ) {
                val devicesBean = adapter.getItem(position) as (DeviceListBean.DevicesBean)
                MultiDeviceRenameOrPositeMethodDialog.newInstance(object :
                    MultiDeviceRenameOrPositeMethodDialog.DoneCallback {
                    override fun onNameDone() {
                        RuleNameDialog.newInstance(object :
                            RuleNameDialog.DoneCallback {
                            override fun onDone(
                                dialog: DialogFragment?,
                                txt: String?,
                                empty_notice: TextView?
                            ) {

                            }

                            override fun onCancel(dialog: DialogFragment?) {
                            }

                        }).setEditValue(devicesBean.deviceName).setTitle("填写名称")
                            .show(supportFragmentManager, "setting_name")
                    }

                    override fun onPositionDone() {
                        MultiDevicePisiteDialog.newInstance()
                            .setTitle("控制设备")
                            .setData(null)
                            .setDefaultIndex(1)
                            .setCallback(object :
                                MultiDevicePisiteDialog.Callback<PurposeCategoryBean> {
                                override fun onCallback(s: PurposeCategoryBean) {

                                }

                            }).show(supportFragmentManager, "positionDialog")
                    }

                }).show(supportFragmentManager, "setting_name_position")

            }

        })
    }

    private fun setNameOrPosition() {

    }

    override fun initListener() {

    }


    private fun getRoomData() {

    }
}