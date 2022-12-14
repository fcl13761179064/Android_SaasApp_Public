package com.ayla.hotelsaas.ui.activities

import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView
import com.ayla.hotelsaas.mvp.present.DeviceAddGuidePresenter
import com.ayla.hotelsaas.R
import android.os.Bundle
import com.ayla.hotelsaas.application.MyApplication
import android.content.Intent
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean
import com.ayla.hotelsaas.utils.TempUtils
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.animation.CycleInterpolator
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.utils.ImageLoader
import com.ayla.hotelsaas.utils.FastClickUtils
import kotlinx.android.synthetic.main.activity_device_add_guide.*

/**
 * wifi设备、节点设备 配网引导页面
 * 进入必须带上[addInfo][Bundle]
 * @作者 chunlei.fan
 */
class DeviceAddGuideActivity : BaseMvpActivity<DeviceAddGuideView?, DeviceAddGuidePresenter?>(),
    DeviceAddGuideView {
    private val REQUEST_CODE_GET_WIFI_SSID_PWD = 0X10
    private var addInfo: Bundle? = null
    private var deviceId:String? = null
    private var replaceDeviceId:String? = null
    private var waitBindDeviceId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addInfo = intent.getBundleExtra("addInfo")
        deviceId = (addInfo?.get("deviceId") ?:"") as String
        val pid = (addInfo?.get("pid") ?:"") as String
        replaceDeviceId = (addInfo?.get("replaceDeviceId") ?:"") as String
        waitBindDeviceId = (addInfo?.get("waitBindDeviceId") ?:"") as String
        mPresenter!!.getNetworkConfigGuide(pid)
    }

    override fun initPresenter(): DeviceAddGuidePresenter {
        return DeviceAddGuidePresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_device_add_guide
    }

    override fun initView() {}
    override fun initListener() {
      bt.setOnClickListener(View.OnClickListener {
          if (cb!!.isChecked) {
              handleJump()
          } else {
              shakeButton()
          }
      })
    }
    private fun handleJump() {

        val networkType = addInfo!!.getInt("networkType")
        val devicesBean = MyApplication.getInstance().devicesBean?.devices
        if (networkType == 5) { //艾拉WiFi设备配网
            if (!FastClickUtils.isDoubleClick()){
                val mainActivity = Intent(this, AylaWiFiAddInputActivity::class.java)
                startActivityForResult(mainActivity, REQUEST_CODE_GET_WIFI_SSID_PWD)
            }
        }  else if (networkType == 3 && devicesBean?.find {  it.deviceId == deviceId }?.isAylaSmartGateway == true &&TextUtils.isEmpty(waitBindDeviceId) && TextUtils.isEmpty(replaceDeviceId) ) {//这是yala智能网关，支持批量配网
            // networkType == 3 艾拉节点设备配网  networkType == 4 鸿雁节点设备配网
                if (!FastClickUtils.isDoubleClick()){
                    val searchMultiDeviceActivity = Intent(this, SearchMultiDeviceActivity::class.java)
                    searchMultiDeviceActivity.putExtras(intent)
                    startActivity(searchMultiDeviceActivity)
                }
        } else {//这里是鸿雁网关节点设备配网，不支持批量配网
            if (!FastClickUtils.isDoubleClick()){
                val mainActivity = Intent(this, DeviceAddActivity::class.java)
                mainActivity.putExtra("addInfo", addInfo)
                startActivity(mainActivity)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GET_WIFI_SSID_PWD && resultCode == RESULT_OK) { //获取到了输入的wifi ssid 、pwd
            val mainActivity = Intent(this, DeviceAddActivity::class.java)
            addInfo!!.putString("wifiName", data!!.getStringExtra("wifiName"))
            addInfo!!.putString("wifiPassword", data.getStringExtra("wifiPassword"))
            mainActivity.putExtra("addInfo", addInfo)
            startActivity(mainActivity)
        }
    }

    override fun getGuideInfoSuccess(o: NetworkConfigGuideBean) {
        if (o != null) {
            val guidePic = o.networkGuidePic
            val guideDesc = o.networkGuideDesc
            ImageLoader.loadImg(iv, guidePic, 0, 0)
            tv_content.text = guideDesc
        }
    }

    override fun getGuideInfoFailed(throwable: Throwable) {
        CustomToast.makeText(
            this,
            TempUtils.getLocalErrorMsg(throwable),
            R.drawable.ic_toast_warning
        )
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun shakeButton() {
        val animSet = AnimatorSet()
        val animX = ObjectAnimator.ofFloat(bt, "translationX", 0f, 10f, 0f, -10f)
        animX.interpolator = CycleInterpolator(5F)
        animSet.duration = 200
        animSet.play(animX)
        animSet.start()
    }
}