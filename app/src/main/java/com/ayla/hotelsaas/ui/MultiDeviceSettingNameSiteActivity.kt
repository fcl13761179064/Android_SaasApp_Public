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
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceLocationBean
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.events.AllAddDeviceEvent
import com.ayla.hotelsaas.events.DeviceAddEvent
import com.ayla.hotelsaas.events.DeviceChangedEvent
import com.ayla.hotelsaas.mvp.present.MultiSignleRenamePresenter
import com.ayla.hotelsaas.mvp.view.MultiSinaleRenameView
import com.ayla.hotelsaas.page.ext.setInvisible
import com.ayla.hotelsaas.page.ext.setVisible
import com.ayla.hotelsaas.page.ext.singleClick
import com.ayla.hotelsaas.widget.MultiDevicePisiteDialog
import com.ayla.hotelsaas.widget.MultiDeviceRenameOrPositeMethodDialog
import com.ayla.hotelsaas.widget.RuleNameDialog
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_device_setting.*
import kotlinx.android.synthetic.main.new_empty_page_status_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * @ClassName:  DeviceSettingActivity
 * @Description:设置设备名字和位置
 * @Author: vi1zen
 * @CreateDate: 2020/10/9 10:25
 */
class MultiDeviceSettingNameSiteActivity :
    BaseMvpActivity<MultiSinaleRenameView, MultiSignleRenamePresenter>(), MultiSinaleRenameView {

    private val adapter = SelectRoomAdapter()
    private val api = RetrofitHelper.getApiService()
    private var deviceSuccessListBean: List<DeviceListBean.DevicesBean>? = null
    private var deviceFailListBean: List<DeviceListBean.DevicesBean>? = null
    private var scopeId: Long = -1L
    private lateinit var subNodeBean: Bundle
    override fun onResume() {
        super.onResume()
        getRoomData()
    }

    override fun getLayoutId(): Int = R.layout.activity_device_setting

    override fun getLayoutView(): View? = null

    override fun initView() {
        deviceSuccessListBean =  intent.getSerializableExtra(Keys.NODEDATA)?.let {
            it as List<DeviceListBean.DevicesBean>
        }
        deviceFailListBean =
            intent.getSerializableExtra(Keys.NODEFailDATA)?.let {
                it as List<DeviceListBean.DevicesBean>
            }
        subNodeBean = intent.getBundleExtra(Keys.DATA) ?: Bundle()
        scopeId = (subNodeBean.get("scopeId") ?: -1L) as Long
        mdf_rv_content.layoutManager = LinearLayoutManager(this)
        adapter.bindToRecyclerView(mdf_rv_content)
        mdf_rv_content.adapter = adapter
        adapter.setEmptyView(R.layout.new_empty_page_status_layout)
        show_success_or_fail.setText("添加成功 ${deviceSuccessListBean?.size}，失败 ${deviceFailListBean?.size} 设备。失败设备可稍后进行单独添加")
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
        deviceSuccessListBean?.let { adapter.addData(it) }

    }


    override fun initListener() {
        adapter.getEmptyView().cl_layout.setVisible(true)
        if (adapter.data.isEmpty()) {
            ll_next_layout.setVisible(false)
            show_success_or_fail.setInvisible(false)
        }
        adapter.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener {

            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
                val devicesBean = adapter.getItem(position) as (DeviceListBean.DevicesBean)
                MultiDeviceRenameOrPositeMethodDialog.newInstance(object :
                    MultiDeviceRenameOrPositeMethodDialog.DoneCallback {
                    override fun onNameDone(name: String) {
                        RuleNameDialog.newInstance(object : RuleNameDialog.DoneCallback {
                            override fun onDone(
                                dialog: DialogFragment?,
                                txt: String,
                                empty_notice: TextView?
                            ) {
                                devicesBean.deviceName = txt
                                mPresenter.deviceRenameMethod(
                                    deviceSuccessListBean?.get(position)?.deviceId, txt
                                )
                            }

                            override fun onCancel(dialog: DialogFragment?) {

                            }

                        }).setEditValue(devicesBean.deviceName).setTitle("填写名称")
                            .show(supportFragmentManager, "setting_name")
                    }

                    override fun onPositionDone(positionSite: DeviceListBean.DevicesBean) {
                        mPresenter.getAllDeviceLocation(scopeId, positionSite)
                    }

                }).setTitle(deviceSuccessListBean?.get(position)?.deviceName)
                    .setPositionSite(deviceSuccessListBean?.get(position))
                    .show(supportFragmentManager, "setting_name_position")
            }

        })

        mdf_btn_next.setOnClickListener(View.OnClickListener {
            startActivity<MainActivity>()
            EventBus.getDefault().post(DeviceAddEvent())
            finish()

        })
        adapter.getEmptyView().log_out.singleClick {
            startActivity<MainActivity>()
            EventBus.getDefault().post(DeviceAddEvent())
            finish()
        }
        adapter.getEmptyView().bt_resert_search.singleClick {
            startActivity<DeviceAddGuideActivity>()
            finish()
        }
    }


    private fun getRoomData() {

    }

    override fun initPresenter(): MultiSignleRenamePresenter {
        return MultiSignleRenamePresenter()
    }

    override fun renameSuccess(nickName: String?) {
        adapter.notifyDataSetChanged()
        CustomToast.makeText(this, "重命名成功", R.drawable.ic_success)
    }

    override fun renameFailed(throwable: Throwable?) {
        CustomToast.makeText(this, "重命名失败", R.drawable.ic_success)
    }

    override fun loadDeviceLocationSuccess(
        deviceListBean: MutableList<DeviceLocationBean>,
        deviceBean: DeviceListBean.DevicesBean
    ) {//位置
        var deviceId = (subNodeBean.get("deviceId")?:"") as String
        var defIndex = 0
        for (i in deviceListBean.indices) {
            if (TextUtils.equals(deviceListBean.get(i).regionName, deviceBean.regionName)) {
                defIndex = i
                break
            }
        }
        MultiDevicePisiteDialog.newInstance()
            .setTitle("设置位置")
            .setData(deviceListBean)
            .setDefaultIndex(defIndex)
            .setCallback {
                deviceBean.regionName=it.regionName
                mPresenter.updatePurpose(deviceId, it.regionId)
            } .show(supportFragmentManager, "positionDialog")
    }

    override fun updatePurposeSuccess() {
        CustomToast.makeText(this, "设置成功", R.drawable.ic_success)
        adapter.notifyDataSetChanged()
    }

    override fun updatePurposeFail(throwable: Throwable?) {
        CustomToast.makeText(this, "设置失败", R.drawable.ic_success)
    }
}