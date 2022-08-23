package com.ayla.hotelsaas.ui.activities.set_scene

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseViewModelActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceLocationBean
import com.ayla.hotelsaas.databinding.ActivitySceneSelectDeviceBinding
import com.ayla.hotelsaas.ext.setVisible
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.ui.fragment.scene.SceneDeviceSelectFragment
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_scene_select_device.*

/**
 * 场景联动 选择设备页面 更加房间筛选设备
 */
class SceneSettingDeviceSelectActivity : BaseViewModelActivity<ActivitySceneSelectDeviceBinding>() {
    private val locationBeans: MutableList<DeviceLocationBean> = mutableListOf()
    private var devicesLocationBean: MutableList<DeviceLocationBean>? = null
    private var local = false
    private var remote = ""
    private var gatewayId = ""
    private var scenetype = 0

    private val selectDeviceAdapter by lazy {
        DeviceAdapter(supportFragmentManager)
    }

    inner class DeviceAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        /**
         * Return the number of views available.
         */
        override fun getCount(): Int {
            return locationBeans.size
        }

        /**
         * Return the Fragment associated with a specified position.
         */
        override fun getItem(position: Int): Fragment =
            when {
                local -> {
                    SceneDeviceSelectFragment.newInstance2(
                        scenetype,
                        local,
                        gatewayId,
                        locationBeans[position].regionId
                    )
                }
                "remote" == remote -> {//这个是云端联动
                    SceneDeviceSelectFragment.newRemoteInstance3(locationBeans[position].regionId,scenetype)
                }
                else -> {//这个是一键执行
                    SceneDeviceSelectFragment.newInstance(locationBeans[position].regionId)
                }
            }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? =
            locationBeans[position].regionName

    }


    /**
     * 仅初始化一些与界面相关的操作
     */
    override fun init(savedInstanceState: Bundle?) {
        devicesLocationBean =
            MyApplication.getInstance().devicesLocationBean as MutableList<DeviceLocationBean>
        val deviceBean = MyApplication.getInstance().devicesBean as DeviceListBean
        val devices = deviceBean.devices
        local = intent.getBooleanExtra(KEYS.LOCAL, false)
        remote = intent.getStringExtra(KEYS.REMOTE) ?: ""
        gatewayId = intent.getStringExtra(KEYS.GATEWAYID) ?: ""
        scenetype = intent.getIntExtra(KEYS.SCENETYPE, 0)
        val deviceLocationBean = DeviceLocationBean()
        deviceLocationBean.regionName = "全部"
        deviceLocationBean.regionId = -1L
        locationBeans.addAll(devicesLocationBean!!)
        locationBeans.add(0, deviceLocationBean)
        appBar.setCenterText("选择设备")
        devices?.let {
            val bean = it.filter { it.bindType == 0 } as MutableList<DeviceListBean.DevicesBean>
            bean.let {
                if (it.isNullOrEmpty()) {
                    rl_empty_show.setVisible(true)
                    return
                } else {
                    tabLayout.setupWithViewPager(viewpager)
                    viewpager.adapter = selectDeviceAdapter
                    viewpager.setCurrentItem(0, false)
                    rl_empty_show.setVisible(false)
                }
            }
        }
        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.text?.let {
                        tab.text = SpanUtils().append(it).setBold().create()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.text?.let {
                        tab.text = SpanUtils().append(it.toString()).create()
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            })
         tabLayout.getTabAt(tabLayout.selectedTabPosition)?.select()
    }


    override fun getViewBinding(): ActivitySceneSelectDeviceBinding? {
        return ActivitySceneSelectDeviceBinding.inflate(layoutInflater)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data)
            finish()
        }
    }

}