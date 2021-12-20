package com.ayla.hotelsaas.ui

import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.SelectRoomAdapter
import com.ayla.hotelsaas.api.CommonApi
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceLocationBean
import com.ayla.hotelsaas.bean.PurposeCategoryBean
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.protocol.MultiBindResp
import com.ayla.hotelsaas.widget.MultiDevicePisiteDialog
import com.ayla.hotelsaas.widget.MultiDeviceRenameOrPositeMethodDialog
import com.ayla.hotelsaas.widget.RuleNameDialog
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_device_setting.*

/**
 * @ClassName:  DeviceSettingActivity
 * @Description:设置设备名字和位置
 * @Author: vi1zen
 * @CreateDate: 2020/10/9 10:25
 */
class MultiDeviceSettingNameSiteActivity : BasicActivity() {

    private val adapter = SelectRoomAdapter()
    private val api = RetrofitHelper.getRetrofit().create(CommonApi::class.java)
    private var deviceListBean: MultiBindResp? = null
    private lateinit var subNodeBean: Bundle
    override fun onResume() {
        super.onResume()
        getRoomData()
    }

    override fun getLayoutId(): Int = R.layout.activity_device_setting

    override fun getLayoutView(): View? = null


    override fun initView() {
        deviceListBean = intent.getParcelableArrayExtra(Keys.DATA) as MultiBindResp?
        subNodeBean = intent.getBundleExtra(Keys.ADDINFO) ?: Bundle()
        mdf_rv_content.layoutManager = LinearLayoutManager(this)
        mdf_rv_content.adapter = adapter
        adapter.bindToRecyclerView(mdf_rv_content)
        mdf_rv_content.setLayoutManager(LinearLayoutManager(this))
        mdf_rv_content.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val size = SizeUtils.dp2px(10f)
                val position = parent.getChildAdapterPosition(view)
                outRect[0, if (position == 0) size else 0, 0] = size
            }
        })
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