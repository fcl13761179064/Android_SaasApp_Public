package com.ayla.hotelsaas.ui.activities.ble_ap

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.app.ActivityCompat
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.base.BaseNewViewModelActivity
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean
import com.ayla.hotelsaas.databinding.ActivityBleApDeviceGuideBinding
import com.ayla.hotelsaas.databinding.ConnecNetWaysBinding
import com.ayla.hotelsaas.ext.showAsDropDownRight
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.ui.activities.ApWifiDistributeActivity
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.utils.ImageLoader
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.viewmodel.BleApDeviceGuideVM
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SizeUtils
import kotlinx.android.synthetic.main.activity_ble_ap_device_guide.*

class BleApDeviceGuideActivity :
    BaseNewViewModelActivity<ActivityBleApDeviceGuideBinding, BleApDeviceGuideVM>() {
    private val REQUEST_CODE_LOCATION_PERMISSION = 0X10
    private val REQUEST_CODE_BLUETOOTH_REQUIRE = 0x11
    private val REQUEST_CODE_LOCATION_ENABLE_REQUIRE = 0X12

    //配网方式 默认是Ap配网 0  1：蓝牙配网
    private var connectNetWay = ConnectNetWay.BLE

    enum class ConnectNetWay {
        AP, BLE
    }

    private val chooseConnectWay by lazy {
        val connecNetWaysBinding = ConnecNetWaysBinding.inflate(layoutInflater)
        val view = connecNetWaysBinding.root
        val chooseConnectWay = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        connecNetWaysBinding.layoutAp.singleClick {
            connectNetWay = ConnectNetWay.AP
            ble_ap_guide_appBar.setRightText("AP配网")
            chooseConnectWay.dismiss()
        }
        connecNetWaysBinding.layoutBle.singleClick {
            connectNetWay = ConnectNetWay.BLE
            ble_ap_guide_appBar.setRightText("蓝牙配网")
            chooseConnectWay.dismiss()
        }
        chooseConnectWay.isOutsideTouchable = true
        chooseConnectWay
    }

    override fun getViewBinding(): ActivityBleApDeviceGuideBinding =
        ActivityBleApDeviceGuideBinding.inflate(layoutInflater)


    override fun init(savedInstanceState: Bundle?) {
        ble_ap_guide_appBar.setRightText("蓝牙配网")
        ble_ap_guide_appBar.rightTextView.singleClick {
            chooseConnectWay.showAsDropDownRight(ble_ap_guide_appBar.rightImageView)
            setWaySelect()
        }
        ble_ap_guide_appBar.rightImageView.singleClick {
            chooseConnectWay.showAsDropDownRight(ble_ap_guide_appBar.rightImageView)
            setWaySelect()
        }
        val pid = intent.getStringExtra(KEYS.PID)
        pid?.let { id ->
            showProgress()
            viewModel.getNetworkConfigGuide(id).observe(this) { result ->
                hideProgress()
                result.onSuccess {
                    if (it is NetworkConfigGuideBean) {
                        val guidePic: String = it.networkGuidePic
                        val guideDesc: String = it.networkGuideDesc
                        ImageLoader.loadImg(ble_ap_iv_des, guidePic, 0, 0)
                        ble_ap_tv_content.text = guideDesc
                    }
                }
                result.onFailure {
                    showWarnToast(TempUtils.getLocalErrorMsg(it))
                }
            }
        }
        ble_ap_next.singleClick {
            if (check_confirm.isChecked) {
                if (connectNetWay == ConnectNetWay.AP) {
                    val intent = Intent(this, ApWifiDistributeActivity::class.java)
                    intent.putExtras(getIntent())
                    intent.putExtra("is_coat_hanger", true)
                    startActivity(intent)
                } else {
                    checkAllStatus()
                }
            } else
                shakeCheckBoxView()
        }

    }

    private fun checkAllStatus() {
        //检查蓝牙是否开启
        if (!getBleStatus()) {
            TooltipBuilder().setTitle("蓝牙未开启").setContent("请到系统【设置】中开启蓝牙后，再进行添加设备")
                .setLeftButtonName("退出")
                .setRightButtonName("前往开启")
                .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                    override fun onClickRight(dialog:AylaBaseDialog) {
                        startActivityForResult(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            REQUEST_CODE_BLUETOOTH_REQUIRE
                        )
                    }

                    override fun onClickLeft(dialog:AylaBaseDialog) {}

                }).show(supportFragmentManager,"ble_open")

        } else {
            requestLocation()
        }
    }

    private fun requestLocation() {
        //检查GPS是否开启
        if (CommonUtils.gpsIsOpen(this)) {
            //检查定位权限
            checkPermission()
        } else {
            //定位未开启
            TooltipBuilder().setTitle("定位未开启").setContent("请到系统中开启定位后，再进行添加设备")
                .setLeftButtonName("退出").setRightButtonName("前往开启")
                .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                    override fun onClickRight(dialog:AylaBaseDialog) {
                        val locationIntent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivityForResult(
                            locationIntent,
                            REQUEST_CODE_LOCATION_ENABLE_REQUIRE
                        )
                    }

                    override fun onClickLeft(dialog:AylaBaseDialog) {
                    }

                }).show(supportFragmentManager,"open_location")
        }
    }

    private fun getBleStatus(): Boolean {
        val bm = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bm.adapter.isEnabled
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        } else {
            //进入蓝牙搜索页面
            jumpToBleSearch()
        }
    }

    private fun jumpToBleSearch() {
        val newIntent = Intent(this, BleSearchActivity::class.java)
        newIntent.putExtra(KEYS.PRODUCTBEAN, intent.getSerializableExtra(KEYS.PRODUCTBEAN))
        newIntent.putExtra("addInfo", intent.getBundleExtra("addInfo"))
        startActivity(newIntent)
    }

    private fun shakeCheckBoxView() {
        val traAnim = TranslateAnimation(-SizeUtils.dp2px(40f).toFloat(), 0F, 0F, 0F)
        traAnim.interpolator = BounceInterpolator()
        traAnim.duration = 800
        check_confirm.startAnimation(traAnim)
    }

    private fun setWaySelect() {
        val apWay = chooseConnectWay.contentView.findViewById<View>(R.id.ic_ap)
        val bleWay = chooseConnectWay.contentView.findViewById<View>(R.id.ic_ble)
        apWay.visibility =
            if (connectNetWay == ConnectNetWay.AP) View.VISIBLE else View.INVISIBLE
        bleWay.visibility =
            if (connectNetWay == ConnectNetWay.BLE) View.VISIBLE else View.INVISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty()) {
                when (grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> {
                        jumpToBleSearch()
                    }
                    else -> {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            //拒绝权限 并且不再询问
                            TooltipBuilder().setTitle("定位权限未开启")
                                .setContent("请到系统中开启定位权限后，再进行添加设备")
                                .setLeftButtonName("退出").setRightButtonName("前往开启")
                                .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                                    override fun onClickRight(dialog:AylaBaseDialog) {
                                        PermissionUtils.launchAppDetailsSettings()
                                    }

                                    override fun onClickLeft(dialog:AylaBaseDialog) {
                                    }

                                }).show(supportFragmentManager,"open_permission")
                        }

                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                REQUEST_CODE_BLUETOOTH_REQUIRE -> {
//                    checkAllStatus()
//                }
//                REQUEST_CODE_LOCATION_ENABLE_REQUIRE -> {
//                    requestLocation()
//                }
//                REQUEST_CODE_LOCATION_PERMISSION -> {
//                    checkPermission()
//                }
//            }
//
//        }
    }
}