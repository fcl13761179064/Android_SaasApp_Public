package com.ayla.hotelsaas.ui

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.RotateAnimation
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.mvp.present.MultiDeviceAddPresenter
import com.ayla.hotelsaas.mvp.view.MultiDeviceAddView


/**
 * 批量设备配网及绑定页
 */
class MultiDeviceDistributionNetActivity : BaseMvpActivity<MultiDeviceAddView, MultiDeviceAddPresenter>(), MultiDeviceAddView {

    override fun getLayoutId(): Int {
        return R.layout.activity_multi_device
    }

    override fun initView() {


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
     * 节点绑定流程结束
     */
    override fun bindSuccess(devicesBean: DeviceListBean.DevicesBean?) {

    }

    /**
     * 节点绑定流程失败
     *
     * @param throwable
     */
    override fun bindFailed(throwable: Throwable?) {

    }

    /**
     * 5.绑定节点成功
     */
    override fun step3Finish() {

    }

    /**
     * 5.开始绑定节点
     */
    override fun step3Start() {

    }

    /**
     * 4.候选节点查找成功
     */
    override fun step2Finish() {
        TODO("Not yet implemented")
    }

    /**
     * 3.开始查找候选节点
     */
    override fun step2Start() {

    }


}