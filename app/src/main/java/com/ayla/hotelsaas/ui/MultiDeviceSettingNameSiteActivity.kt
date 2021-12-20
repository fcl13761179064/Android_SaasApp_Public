package com.ayla.hotelsaas.ui

import android.graphics.Rect
import android.os.Bundle
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
import com.ayla.hotelsaas.bean.PurposeCategoryBean
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.mvp.present.DeviceAddSuccessPresenter
import com.ayla.hotelsaas.mvp.present.MultiSignleRenamePresenter
import com.ayla.hotelsaas.mvp.view.DeviceAddSuccessView
import com.ayla.hotelsaas.mvp.view.MultiSinaleRenameView
import com.ayla.hotelsaas.widget.MultiDevicePisiteDialog
import com.ayla.hotelsaas.widget.MultiDeviceRenameOrPositeMethodDialog
import com.ayla.hotelsaas.widget.RuleNameDialog
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_device_setting.*
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
    private var deviceListBean: List<DeviceListBean.DevicesBean>? = null
    private var scopeId: Long = -1L
    private lateinit var subNodeBean: Bundle
    override fun onResume() {
        super.onResume()
        getRoomData()
    }

    override fun getLayoutId(): Int = R.layout.activity_device_setting

    override fun getLayoutView(): View? = null


    override fun initView() {
        deviceListBean =
            intent.getSerializableExtra(Keys.NODEDATA) as List<DeviceListBean.DevicesBean>
        subNodeBean = intent.getBundleExtra(Keys.DATA) ?: Bundle()
        scopeId = (subNodeBean.get("scopeId") ?: -1L) as Long
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
        deviceListBean?.let { adapter.addData(it) }

    }


    override fun initListener() {
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
                                empty_notice: TextView?) {
                                mPresenter.deviceRenameMethod(deviceListBean?.get(position)?.deviceId ,txt
                                )
                            }
                             override fun onCancel(dialog: DialogFragment?) {

                            }

                        }).setEditValue(devicesBean.deviceName).setTitle("填写名称")
                            .show(supportFragmentManager, "setting_name")
                    }

                    override fun onPositionDone(s: String) {
                        mPresenter.getAllDeviceLocation(scopeId)
                    }

                }).setTitle(deviceListBean?.get(position)?.deviceName).setPositionSite(deviceListBean?.get(position)?.pointName).show(supportFragmentManager, "setting_name_position")
            }

        })

        mdf_btn_next.setOnClickListener(View.OnClickListener {
            startActivity<MainActivity>()
        })

    }


    private fun getRoomData() {

    }

    override fun initPresenter(): MultiSignleRenamePresenter {
        return MultiSignleRenamePresenter()
    }

    override fun renameSuccess(nickName: String?) {
        CustomToast.makeText(this, "重命名成功", R.drawable.ic_success)
    }

    override fun renameFailed(throwable: Throwable?) {
        CustomToast.makeText(this, "重命名失败", R.drawable.ic_success)
    }

    override fun loadDeviceLocationSuccess(deviceListBean: MutableList<DeviceLocationBean>?) {//位置
        MultiDevicePisiteDialog.newInstance()
            .setTitle("控制设备")
            .setData(deviceListBean)
            .setDefaultIndex(1)
            .setCallback(object :
                MultiDevicePisiteDialog.Callback<PurposeCategoryBean> {
                override fun onCallback(s: PurposeCategoryBean) {

                }

            }).show(supportFragmentManager, "positionDialog")
    }
}