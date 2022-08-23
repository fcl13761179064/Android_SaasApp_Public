package com.ayla.hotelsaas.ui.activities

import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.CheckMarshallAdapter
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.GroupDeviceItem
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.mvp.present.CheckMarshallSubsetPresenter
import com.ayla.hotelsaas.mvp.view.CheckMarshallSubsetView
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import kotlinx.android.synthetic.main.activity_marshall_check_subset.*
import org.jetbrains.anko.startActivity

class CheckMarshallSubsetActivity :
    BaseMvpActivity<CheckMarshallSubsetView, CheckMarshallSubsetPresenter>(),
    CheckMarshallSubsetView {

    private lateinit var groupItem: MutableList<GroupDeviceItem>
    private var roomId: Long = 0L
    override fun getLayoutId(): Int {
        return R.layout.activity_marshall_check_subset
    }

    private val CheckSubsetAdapter by lazy { CheckMarshallAdapter(R.layout.item_common_delect_device) }
    private val groupId by lazy { intent.getStringExtra(KEYS.GROUP) }

    override fun initView() {
        roomId = SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, 0)
        recyclerview.layoutManager = LinearLayoutManager(this)
        CheckSubsetAdapter.bindToRecyclerView(recyclerview)
        recyclerview.adapter = CheckSubsetAdapter
        CheckSubsetAdapter.setEmptyView(R.layout.empty_marshall_device)
        mPresenter.getMarShallSubSet(groupId)

    }

    override fun initListener() {
        CheckSubsetAdapter.setOnItemClickListener { adapter, view, position ->
            //判断是否是h5页面
            val data = groupItem.get(position)
            startActivity<DeviceDetailH5Activity>(
                KEYS.PRODUCTLABEL to intent.getStringExtra(KEYS.PRODUCTLABEL),
                "domainUrl" to data.domain, "h5url" to data.h5Url, "DevicePid" to data.pid,
                "deviceId" to data.deviceId, "scopeId" to roomId, "subsetData" to data
            )

        }
    }

    override fun initPresenter(): CheckMarshallSubsetPresenter = CheckMarshallSubsetPresenter()
    override fun getSubsetSucess(groupItem: MutableList<GroupDeviceItem>) {
        var iterator = groupItem.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            for (item in ConstantValue.FOUR_SWITCH_PID) {
                if (next.pid == item) {
                    iterator.remove()
                    break
                }
            }
        }
        this.groupItem = groupItem
        CheckSubsetAdapter.setNewData(
            groupItem
        )
        tv_subset_num.setText("已添加设备${groupItem.size}")
    }


    override fun getSubsetFail(o: String?) {
    }

}

