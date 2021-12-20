package com.ayla.hotelsaas.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.RotateAnimation
import carlwu.top.lib_device_add.NodeHelper
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.common.Constance
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.mvp.present.MultiDeviceAddPresenter
import com.ayla.hotelsaas.mvp.view.MultiDeviceAddView
import com.ayla.hotelsaas.page.ext.setVisible
import com.ayla.hotelsaas.protocol.MultiBindResp
import com.ayla.hotelsaas.protocol.MultiBindResultBean
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_multi_device_bind.*
import kotlinx.android.synthetic.main.new_empty_page_status_layout.view.*
import org.jetbrains.anko.startActivity


/**
 * 批量设备配网及绑定页
 */
class MultiDeviceDistributionNetActivity : BaseMvpActivity<MultiDeviceAddView, MultiDeviceAddPresenter>(), MultiDeviceAddView {

    private var gatewayDeviceId = ""
    private var cloudModel = ""
    private lateinit var subNodeBean: Bundle

    override fun getLayoutId(): Int {
        return R.layout.activity_multi_device_bind
    }

    override fun initView() {
        gatewayDeviceId = intent.getStringExtra(Keys.ID) ?: ""
        cloudModel = intent.getStringExtra(Keys.OEMMODEL) ?: ""
        subNodeBean = intent.getBundleExtra(Keys.DATA) ?: Bundle()
        Keys.RoomId=subNodeBean.getString("scopeId")?:""
        val multiDeviceIds = intent.getStringArrayListExtra(Keys.MULTI_DEVICE_IDS)
        val gatewayCuId = MyApplication.getInstance().devicesBean?.find { it.deviceId == gatewayDeviceId }?.cuId ?: 0
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
        Log.d("2323232",data.toString())
        startActivity<MultiDeviceSettingNameSiteActivity>(Keys.NODEDATA to data,Keys.DATA to subNodeBean)
    }

    /**
     *  5.节点绑定流程失败
     *
     */
    override fun multiBindFailure(errorMsg: String?) {
      toBindFailPage()
    }
    private fun toBindFailPage() {
        startActivity<MultiDeviceDistributionFailActivity>()
    }
}