package com.ayla.hotelsaas.ui.fragment.scene

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.SceneDeviceGroupSelectAdapter
import com.ayla.hotelsaas.bean.DeviceItem
import com.ayla.hotelsaas.bean.GroupItem
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.base.BaseFragment
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.ui.activities.set_scene.SceneSettingFunctionSelectActivity
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.ayla.hotelsaas.viewmodel.SceneSelectDeviceViewModel
import com.ayla.hotelsaas.utils.FastClickUtils
import com.blankj.utilcode.util.SizeUtils
import kotlinx.android.synthetic.main.fragment_scene_select_device.*
import java.util.ArrayList

/**
 * 场景联动 选择设备 设备列表页面
 */

class SceneDeviceSelectFragment : BaseFragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(SceneSelectDeviceViewModel::class.java) }
    var mAdapter: SceneDeviceGroupSelectAdapter? = null
    var roomId: Long = -1L

    companion object {
        @JvmStatic
        fun newInstance(regionId: Long): SceneDeviceSelectFragment {
            val sceneDeviceSelectFragment = SceneDeviceSelectFragment()
            sceneDeviceSelectFragment.arguments = Bundle().apply {
                putLong(KEYS.REGION_ID, regionId)
                //一键执行的动作
                putInt(KEYS.SCENETYPE, 1)
            }
            return sceneDeviceSelectFragment
        }

        /**
         * 本地场景新建
         */
        fun newInstance2(
            sceneType: Int,
            local: Boolean,
            gatewayId: String,
            regionId: Long
        ): SceneDeviceSelectFragment {
            val sceneDeviceSelectFragment = SceneDeviceSelectFragment()
            sceneDeviceSelectFragment.arguments = Bundle().apply {
                putLong(KEYS.REGION_ID, regionId)
                putBoolean(KEYS.LOCAL, local)
                putString(KEYS.GATEWAYID, gatewayId)
                putInt(KEYS.SCENETYPE, sceneType)
            }
            return sceneDeviceSelectFragment
        }

        fun newRemoteInstance3(regionId: Long, sceneType: Int): SceneDeviceSelectFragment {
            val sceneDeviceSelectFragment = SceneDeviceSelectFragment()
            sceneDeviceSelectFragment.arguments = Bundle().apply {
                putLong(KEYS.REGION_ID, regionId)
                //一键执行的动作
                putInt(KEYS.SCENETYPE, sceneType)
            }
            return sceneDeviceSelectFragment
        }
    }

    /**
     * 获取布局id
     */
    override fun getLayoutResId(): Int = R.layout.fragment_scene_select_device

    override fun onResume() {
        super.onResume()
        roomId = SharePreferenceUtils.getLong(activity, ConstantValue.SP_ROOM_ID, -1) ?: -1L
        val locationId = arguments?.getLong(KEYS.REGION_ID, -1L) ?: -1L
        arguments?.let {
            when {
                it.getBoolean(KEYS.LOCAL) -> {
                    val gatewayId = it.getString(KEYS.GATEWAYID)
                    val sceneType = it.getInt(KEYS.SCENETYPE)
                    viewModel.getLocalConditionOrActionDevices(
                        roomId,
                        locationId,
                        gatewayId ?: "",
                        sceneType
                    )
                }
                it.getInt(KEYS.SCENETYPE) == 0 -> {
                    viewModel.getConditionOrActionDevices(roomId, locationId, true)//这里是条件
                }
                else -> {
                    viewModel.getConditionOrActionDevices(roomId, locationId, false)//这里是动作
                }
            }
        }

    }


    /**
     * 初始化视图
     */
    override fun initViews(view: View, savedInstanceState: Bundle?) {
        recyclerview.layoutManager = LinearLayoutManager(activity)
        mAdapter =
            SceneDeviceGroupSelectAdapter(R.layout.scene_item_select_device_list)
        mAdapter?.bindToRecyclerView(recyclerview)
        recyclerview.adapter = mAdapter
        mAdapter?.setEmptyView(R.layout.scene_data_empty_page)
        recyclerview.addItemDecoration(object : ItemDecoration() {
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
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            if (!FastClickUtils.isDoubleClick()) {
                val item = adapter.getItem(position)
                if (item is DeviceItem) {
                    jumpNext(item)
                }
                if (item is GroupItem) {
                    jumpToGroup(item);
                }
            }

        }

        viewModel.conditionActionDeviceGroup.observe(this) {
            mAdapter?.setNewData(it)
        }
    }

    private fun jumpNext(deviceBean: DeviceItem) {
        val mainActivity = Intent(activity, SceneSettingFunctionSelectActivity::class.java)
        mainActivity.putExtra("deviceId", deviceBean.deviceId)
//        mainActivity.putExtra("type", 1)//这里标记为动作
        mainActivity.putExtra("deviceName", deviceBean.nickname)

        arguments?.let {
            val sceneType = it.getInt(KEYS.SCENETYPE)
            mainActivity.putExtra(KEYS.SCENETYPE, sceneType)
        }
        startActivityForResult(mainActivity, 0)
    }

    private fun jumpToGroup(groupItem: GroupItem) {
        val mainActivity = Intent(activity, SceneSettingFunctionSelectActivity::class.java)
        mainActivity.putExtra(KEYS.GROUPID, groupItem.groupId)
        mainActivity.putExtra("deviceName", groupItem.groupName)//这里标记为动作
        mainActivity.putStringArrayListExtra(
            KEYS.GROUPACTION,
            groupItem.actionAbilities as ArrayList<String>
        )

        arguments?.let {
            val sceneType = it.getInt(KEYS.SCENETYPE)
            mainActivity.putExtra(KEYS.SCENETYPE, sceneType)
        }
        startActivityForResult(mainActivity, 0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            activity?.setResult(Activity.RESULT_OK, data)
            activity?.finish()
        }
    }
}