package com.ayla.hotelsaas.ui.activities.ble_ap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import androidx.core.app.ActivityCompat
import com.ayla.hotelsaas.base.BaseNewViewModelActivity
import com.ayla.hotelsaas.databinding.ActivityNetConfigBinding
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.utils.WifiUtil
import com.ayla.hotelsaas.viewmodel.NetConfigVM
import com.ayla.hotelsaas.widget.common_dialog.FilterWifiDialog
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import com.ayla.hotelsaas.utils.WifiUtils
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog
import com.blankj.utilcode.util.*
import kotlinx.android.synthetic.main.activity_net_config.*

class NetConfigActivity : BaseNewViewModelActivity<ActivityNetConfigBinding, NetConfigVM>() {
    private val REQUEST_CODE_LOCATION_REQUIRE = 0X10
    private val REQUEST_CODE_CONNECT = 0X20

    //onResume 中请求权限 会造成循环，使用此标记进行判断
    private var isChainRequestPermission = true

    private var filterWifiDialog: FilterWifiDialog? = null

    private var changeWifi = false

    override fun getViewBinding(): ActivityNetConfigBinding =
        ActivityNetConfigBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        isChainRequestPermission = true
        net_config_next.isEnabled = false
        net_config_change_wifi.singleClick {
            //切换网络
            changeWifi = true
            checkWifiConnect()
        }
        net_config_pw_visible.singleClick {
            net_config_pw_visible.isSelected = !net_config_pw_visible.isSelected
            net_config_wifi_pw.transformationMethod =
                if (net_config_pw_visible.isSelected) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            val length = net_config_wifi_pw.text?.length
            net_config_wifi_pw.setSelection(length ?: 0)
        }
        net_config_next.singleClick {
            //连接蓝牙 进行配网
            val bleName = intent.getStringExtra(KEYS.BLENAME)
            val ssid = net_config_wifi_name.text
            val pw = net_config_wifi_pw.text
            val setupToken = CommonUtils.generateRandomToken(8)
            val bundle = Bundle()
            bundle.putString(KEYS.BLENAME, bleName)
            bundle.putString(KEYS.SSID, ssid.toString())
            bundle.putString(KEYS.PW, pw.toString())
            bundle.putString(KEYS.ST, setupToken)
            bundle.putSerializable(KEYS.PRODUCTBEAN, intent.getSerializableExtra(KEYS.PRODUCTBEAN))

            val newIntent = Intent(this, DeviceConnectNetActivity::class.java)
            newIntent.putExtra("addInfo", intent.getBundleExtra("addInfo"))
            newIntent.putExtra(KEYS.CONNECT_PARAM, bundle)
            startActivity(newIntent)
        }
        net_config_wifi_help.singleClick {
            TooltipBuilder().setTitle("找不到要连接的路由器").setContentGravity(Gravity.START).setContent(
                "1.请确保当前路由器已设置密码。部分设备不支持连接到到未加密的路由器。\n" +
                        "\n" +
                        "2.请确保当前路由器不需要二次身份与密码验证。\n" +
                        "\n" +
                        "3.部分设备暂不支持5GHz网络连接。请查看设备说明书，确定当前设备支持的网络连接类型。\n" +
                        "\n" +
                        "4.请确保路由器未设置隐藏SSID。已设置隐藏SSID的路由器无法在列表中显示，请修改路由器设置后重新尝试连接。"
            ).setShowLeftButton(false).setRightButtonName("确定").show(supportFragmentManager,"tips")
        }
        changeWifi = false
        checkWifiConnect()

    }


    private fun checkWifiConnect() {
        viewModel.getWifiEnable().observe(this) {
            if (!it) {
                TooltipBuilder().setTitle("WiFi 未开启").setContent("请到系统【设置】中开启 WiFi后，再进行添加设备")
                    .setLeftButtonName("退出").setRightButtonName("前往开启")
                    .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                        override fun onClickRight(dialog:AylaBaseDialog) {
                            isChainRequestPermission = true
                            startActivity(
                                Intent(Settings.ACTION_WIFI_SETTINGS)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }

                        override fun onClickLeft(dialog:AylaBaseDialog) {
                        }

                    }).show(supportFragmentManager, "open_wifi")
            } else {
                if (NetworkUtils.isWifiConnected().not()) {
                    TooltipBuilder().setTitle("未连接 WiFi").setContent("检查到当前手机未连接 Wi-Fi，请进行连接")
                        .setLeftButtonName("退出").setRightButtonName("去连接")
                        .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                            override fun onClickRight(dialog:AylaBaseDialog) {
                                isChainRequestPermission = true
                                startActivity(
                                    Intent(Settings.ACTION_WIFI_SETTINGS)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }

                            override fun onClickLeft(dialog:AylaBaseDialog) {}

                        }).show(supportFragmentManager,"connect_wifi")
                } else {
                    requestGps()
                }
            }
        }
    }

    private fun getConnectedWiFi() {
        if (changeWifi) {
            showChooseWifiDialog()
        } else {
            //获取连接的WiFi ssid
            val connectWifiSsid = WifiUtil.getConnectWifiSsid()
            net_config_wifi_name.text = connectWifiSsid
            val wifiPwd = getWifiPwd(connectWifiSsid)
            net_config_wifi_pw.setText(wifiPwd)
            net_config_next.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (isChainRequestPermission)
            checkWifiConnect()
    }

    private fun showChooseWifiDialog() {
        val scanResultList = WifiUtils.getInstance(this).wifiScanResult
        var selectIndex = 0
        if (TextUtils.isEmpty(net_config_wifi_name.text).not()) {
            for (x in scanResultList.indices) {
                if (TextUtils.equals(scanResultList.get(x).SSID, net_config_wifi_name.text)) {
                    selectIndex = x
                    break
                }
            }
        }
        if (scanResultList.size == 0) {
            TooltipBuilder().setTitle("提示").setContent("没有发现网络").setRightButtonName("确定")
                .setShowLeftButton(false).show(supportFragmentManager,"not_net")
        } else {
            filterWifiDialog?.dismissAllowingStateLoss()
            filterWifiDialog = FilterWifiDialog.newInstance()
                .setData(scanResultList)
                .setSubTitle("Wi-Fi")
                .setDefaultIndex(selectIndex)
                .setCallback(object : FilterWifiDialog.Callback<ScanResult> {
                    override fun onCallback(newLocationName: ScanResult) {
                        val ssid = newLocationName.SSID
                        if (TextUtils.isEmpty(ssid.trim())) {
                            showWarnToast("WiFi名称不能为空")
                            return
                        }

                        net_config_wifi_name.text = ssid
                        net_config_wifi_pw.setText(getWifiPwd(ssid))
                        net_config_next.isEnabled = true
                    }
                })
            filterWifiDialog?.show(supportFragmentManager, "dialog")
        }

    }

    private fun getWifiPwd(ssid: String): String {
        val instance = SPUtils.getInstance("wifi_info")
        var pwd = ""
        try {
            pwd = instance.getString(ssid)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        return pwd
    }

    private fun requestGps() {
        net_config_wifi_pw.setText("")
        if (!CommonUtils.gpsIsOpen(this)) {//9.0以上除了需要定位权限还需要开启GPS才能获取wifi名字
            //定位未开启
            TooltipBuilder().setTitle("定位未开启").setContent("请到系统中开启定位后，再进行添加设备")
                .setLeftButtonName("退出").setRightButtonName("前往开启")
                .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                    override fun onClickRight(dialog:AylaBaseDialog) {
                        isChainRequestPermission = true
                        val locationIntent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(locationIntent)
                    }

                    override fun onClickLeft(dialog:AylaBaseDialog) {
                    }

                }).show(supportFragmentManager,"open_location")
        } else
            requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isChainRequestPermission = false
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_REQUIRE
            )
        } else {
            getConnectedWiFi()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_REQUIRE) {
            if (grantResults.isNotEmpty()) {
                when (grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> getConnectedWiFi()
                    else ->
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {//用户拒绝了权限，并且不再询问
                            TooltipBuilder().setTitle("定位权限未开启")
                                .setContent("请到系统中开启定位权限后，再进行添加设备")
                                .setLeftButtonName("退出").setRightButtonName("前往开启")
                                .setOperateListener(object : AylaBaseDialog.OnOperateListener {
                                    override fun onClickRight(dialog:AylaBaseDialog) {
                                        isChainRequestPermission = true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CONNECT && resultCode == 999) {
            net_config_wifi_pw.setText("")
            changeWifi = false
        }
    }
}