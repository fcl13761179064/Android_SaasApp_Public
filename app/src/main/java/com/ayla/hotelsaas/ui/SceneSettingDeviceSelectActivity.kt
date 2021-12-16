package com.ayla.hotelsaas.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.ayla.hotelsaas.application.Constance
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseViewModelActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceLocationBean
import com.ayla.hotelsaas.databinding.ActivitySceneSelectDeviceBinding
import com.ayla.hotelsaas.page.fragment.SceneDeviceSelectFrament
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_scene_select_device.*

/**
 * 场景创建，选择设备的页面
 * 进入时必须带入参数：
 * 1.long scopeId
 * 2.int type  ，0：condition  1：action
 * 可选参数：
 * 1.selectedDatum [<] 已经选择了的栏目。"dsn propertyName" 格式
 * 2.ruleSetMode 条件组合方式。 ALL(2,"多条条件全部命中")   ANY(3,"多条条件任一命中")
 * 3.String targetGateway 当设备是网关时，必须传
 *
 * action [com.ayla.hotelsaas.localBean.BaseSceneBean.DeviceAction] 正在编辑的action ，如果为null，就是新创建。
 */
class SceneSettingDeviceSelectActivity : BaseViewModelActivity<ActivitySceneSelectDeviceBinding>() {

    var devicesBean: List<DeviceListBean.DevicesBean>? = null
    var devicesLocationBean: List<DeviceLocationBean>? = null

    private val selectDeviceAdapter by lazy {
        deviceAdapter(supportFragmentManager)
    }

    inner class deviceAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        /**
         * Return the number of views available.
         */
        override fun getCount(): Int {
            return devicesLocationBean!!.size
        }

        /**
         * Return the Fragment associated with a specified position.
         */
        override fun getItem(position: Int): Fragment = SceneDeviceSelectFrament.newInstance(devicesLocationBean?.get(position)!!.regionId)

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? = devicesLocationBean?.get(position)?.regionName
    }


    /**
     * 仅初始化一些与界面相关的操作
     */
    override fun init(savedInstanceState: Bundle?) {
        devicesBean = MyApplication.getInstance().devicesBean.devices as List<DeviceListBean.DevicesBean>
        devicesLocationBean = MyApplication.getInstance().devicesLocationBean as List<DeviceLocationBean>
        appBar!!.setCenterText("选择设备")
        viewpager.adapter = selectDeviceAdapter
        selectDeviceAdapter.notifyDataSetChanged()
        tabLayout.setupWithViewPager(viewpager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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