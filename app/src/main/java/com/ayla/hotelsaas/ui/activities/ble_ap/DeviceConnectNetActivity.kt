package com.ayla.hotelsaas.ui.activities.ble_ap

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.RotateAnimation
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.base.BaseNewViewModelActivity
import com.ayla.hotelsaas.bean.DeviceCategoryBean
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.constant.ConstantValue
import com.ayla.hotelsaas.databinding.ActivityDeviceConnectNetBinding
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.utils.SharePreferenceUtils
import com.ayla.hotelsaas.viewmodel.DeviceConnectNetVM
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_device_connect_net.*
import kotlinx.android.synthetic.main.layout_ble_ap_connect_fail.*

class DeviceConnectNetActivity :
    BaseNewViewModelActivity<ActivityDeviceConnectNetBinding, DeviceConnectNetVM>() {
    override fun getViewBinding(): ActivityDeviceConnectNetBinding =
        ActivityDeviceConnectNetBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        device_connect_net_appbar.setLeftImageView(0)
        startRotate(device_connect_loading)
        fail_retry.singleClick {
//            device_connect_net_appbar.setCenterText("配网中")
//            layout_connect_fail.visibility = View.GONE
//            layout_connect_net.visibility = View.VISIBLE
//            startConnect()
            setResult(999)
            finish()
        }
        exit_connect_net.singleClick {
            val intent = Intent(this, BleApDeviceGuideActivity::class.java)
            startActivity(intent)
        }
        startConnect()
    }

    private fun startConnect() {
        val params = intent.getBundleExtra(KEYS.CONNECT_PARAM)
        params?.let { bundle ->
            val bleName = bundle.getString(KEYS.BLENAME) ?: ""
            val ssid = bundle.getString(KEYS.SSID) ?: ""
            val pw = bundle.getString(KEYS.PW) ?: ""
            val st = bundle.getString(KEYS.ST) ?: ""
            val beanS = bundle.getSerializable(KEYS.PRODUCTBEAN)
            var nodeBean: DeviceCategoryBean.SubBean.NodeBean? = null
            beanS?.let {
                if (beanS is DeviceCategoryBean.SubBean.NodeBean) {
                    nodeBean = beanS
                }
            }
            if (nodeBean == null)
                showWarnToast("参数错误")
            viewModel.configBleDevice(
                this,
                ssid,
                pw,
                bleName,
                st,
                nodeBean?.source?.toLong() ?: 0L
            )
                .observe(this, { configStatusWrapper ->
                    when (configStatusWrapper.configStatus) {
                        DeviceConnectNetVM.ConfigStatus.NET_ERROR -> {
                            device_connect_net_appbar.setCenterText("配网失败")
                            device_connect_net_appbar.setLeftImageView(R.drawable.icon_back_normal)

                            layout_connect_fail.visibility = View.VISIBLE
                            layout_connect_net.visibility = View.GONE
                            showWarnToast("网络连接失败，请检查网络")
                        }
                        DeviceConnectNetVM.ConfigStatus.CLOUD_CHECK_FAILED -> {
                            device_connect_net_appbar.setCenterText("配网失败")
                            device_connect_net_appbar.setLeftImageView(R.drawable.icon_back_normal)

                            layout_connect_fail.visibility = View.VISIBLE
                            layout_connect_net.visibility = View.GONE
                        }
                        DeviceConnectNetVM.ConfigStatus.CLOUD_CHECK_SUCCESS -> {
                            //配网成功  保存密码
                            saveWifiPwd(ssid, pw)
                            nodeBean?.let { bean ->
                                val addInfo = intent.getBundleExtra("addInfo")
                                val nickname = addInfo?.getString("nickname")
                                val waitBindDeviceId = addInfo?.getString("waitBindDeviceId")
                                val replaceDeviceId = addInfo?.getString("replaceDeviceId")
                                val scopeId =
                                    SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, 0)
                                val dc = if (bean.source == 0) {
                                    bean.oemModel["0"] ?: ""
                                } else {
                                    bean.oemModel["1"] ?: ""
                                }
                                viewModel.bindDevice(
                                    bean.source,
                                    scopeId,
                                    bean.pid,
                                    configStatusWrapper.deviceId ?: "",
                                    if (TextUtils.isEmpty(nickname)) bean.productName else nickname
                                        ?: "",
                                    dc,
                                    waitBindDeviceId,
                                    replaceDeviceId
                                ).observe(this, { result ->
                                    result.onSuccess {
                                        val intent = Intent(this, DeviceConfigActivity::class.java)
                                        if (it is DeviceListBean.DevicesBean) {
                                            intent.putExtra(KEYS.DEVICEID, it.deviceId)
                                            intent.putExtra(
                                                KEYS.PRODUCKTNAME,
                                                if (TextUtils.isEmpty(nickname)) bean.productName else nickname
                                                    ?: "",
                                            )
                                            startActivity(intent)
                                            finish()
                                        }

                                    }
                                    result.onFailure {
                                        device_connect_net_appbar.setCenterText("配网失败")
                                        layout_connect_fail.visibility = View.VISIBLE
                                        layout_connect_net.visibility = View.GONE
                                        showWarnToast(it.message ?: "")
                                    }
                                })
                            }
                        }

                        DeviceConnectNetVM.ConfigStatus.HARD_WARE_CONFIG_FAILED -> {
                            device_connect_net_appbar.setCenterText("配网失败")
                            device_connect_net_appbar.setLeftImageView(R.drawable.icon_back_normal)
                            layout_connect_fail.visibility = View.VISIBLE
                            layout_connect_net.visibility = View.GONE
                        }
                    }
                })
        }
    }

    private fun saveWifiPwd(ssid: String, pwd: String) {
        val instance = SPUtils.getInstance("wifi_info")
        instance.put(ssid, pwd, true) //保存wifi密码
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
}