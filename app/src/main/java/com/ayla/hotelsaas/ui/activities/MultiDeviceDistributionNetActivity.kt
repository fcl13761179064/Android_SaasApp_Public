package com.ayla.hotelsaas.ui.activities

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.RotateAnimation
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.MultiBindResp
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.mvp.present.MultiDeviceAddPresenter
import com.ayla.hotelsaas.mvp.view.MultiDeviceAddView
import kotlinx.android.synthetic.main.activity_multi_device_bind.*
import org.jetbrains.anko.startActivity


/**
 * 批量设备配网及绑定页
 */
class MultiDeviceDistributionNetActivity : BaseMvpActivity<MultiDeviceAddView, MultiDeviceAddPresenter>(), MultiDeviceAddView {

    private var gatewayDeviceId = ""
    private var cloudModel = ""
    private var ScopeId = ""
    private lateinit var subNodeBean: Bundle

    override fun getLayoutId(): Int {
        return R.layout.activity_multi_device_bind
    }

    override fun initView() {
        gatewayDeviceId = intent.getStringExtra(KEYS.ID) ?: ""
        cloudModel = intent.getStringExtra(KEYS.OEMMODEL) ?: ""
        subNodeBean = intent.getBundleExtra(KEYS.DATA) ?: Bundle()
        ScopeId = subNodeBean.getString("scopeId") ?: ""
        val multiDeviceIds = intent.getStringArrayListExtra(KEYS.MULTI_DEVICE_IDS)
       val gatewayCuId = MyApplication.getInstance().devicesBean.devices.find { it.deviceId == gatewayDeviceId }?.cuId ?: 0
        multiDeviceIds?.let {
            mPresenter.multiBindNodeDevice(gatewayCuId,cloudModel,subNodeBean,
                it
            )
        }
        startRotate(iv_01)
    }

    /**
     * 参数1: 开始角
     * 参数2: 旋转角
     * 参数3: **/
    private fun startRotate(target: View) {
        //以自身中心旋转
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F
        )
        rotateAnimation.duration = 2000
        rotateAnimation.repeatMode = ValueAnimator.RESTART
        rotateAnimation.repeatCount = 1000000000
        target.startAnimation(rotateAnimation)
    }

    override fun initListener() {

    }

    override fun initPresenter(): MultiDeviceAddPresenter {
      return  MultiDeviceAddPresenter()
    }


    /**
     * 4.绑定节点成功
     *
     */
    override fun multiBindSuccess(data: MultiBindResp) {
        val deviceList = ArrayList<DeviceListBean.DevicesBean>()
            for ((index,e) in data.success.withIndex()){
                val devicesBean = DeviceListBean.DevicesBean()
                devicesBean.deviceId=e.deviceId
                devicesBean.regionName=e.regionName
                devicesBean.regionId=e.regionId
                devicesBean.pointName=e.pointName
                devicesBean.iconUrl=(subNodeBean.get("deviceUrl") ?: "") as String
                devicesBean.deviceName= (subNodeBean.get("productName") ?: "") as String
                deviceList.add(devicesBean)
        }
        startActivity<MultiDeviceSettingNameSiteActivity>(KEYS.NODEDATA to deviceList,KEYS.NODEFailDATA to data.failed,KEYS.DATA to subNodeBean)
    }

    /**
     *  5.节点绑定流程失败
     *
     */
    override fun multiBindFailure(errorMsg: String?) {
      toBindFailPage()
    }
    private fun toBindFailPage() {
        startActivity<MultiDeviceDistributionFailActivity>(KEYS.MULTIDISTRIBUREFAIL to "配网失败")
    }
}