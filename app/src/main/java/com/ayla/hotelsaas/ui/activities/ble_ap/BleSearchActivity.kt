package com.ayla.hotelsaas.ui.activities.ble_ap

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.ble_ap.SearchDeviceAdapter
import com.ayla.hotelsaas.base.BaseNewViewModelActivity
import com.ayla.hotelsaas.data.net.ExceptionCode
import com.ayla.hotelsaas.data.net.SpecialException
import com.ayla.hotelsaas.databinding.ActivityBleSearchBinding
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.utils.CommonUtils
import com.ayla.hotelsaas.viewmodel.BleSearchVM
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice
import com.blankj.utilcode.util.PermissionUtils
import kotlinx.android.synthetic.main.activity_ble_search.*
import kotlinx.android.synthetic.main.layout_ble_search_empty.*
import kotlinx.coroutines.Job

class BleSearchActivity : BaseNewViewModelActivity<ActivityBleSearchBinding, BleSearchVM>() {
    private val REQUEST_CODE_LOCATION_PERMISSION = 0X10
    private val REQUEST_CODE_LOCATION_ENABLE_REQUIRE = 0X12
    private val REQUEST_CODE_BLUETOOTH_REQUIRE = 0x11
    private val REQUEST_CODE_SET_PERMISSION = 0x13
    private val adapter = SearchDeviceAdapter()

    //搜索到了设备重新搜索 0   未搜索到设备重新搜索 1  权限允许一次后失效重新请求后搜索 2
    private var searchType = 0
    private var timerJob: Job? = null
    override fun getViewBinding(): ActivityBleSearchBinding =
        ActivityBleSearchBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        initRV()
        startSearchAni(ble_search_animation)
        viewModel.timeLD.observe(this, {
            val min = it / 60
            val timeTxt = String.format("搜索剩余 %02d:%02d", min, it % 60)
            search_ble_device_time.text = timeTxt
            if (it == 0) {
                if (adapter.data.size > 0) {
                    layout_searching.visibility = View.GONE
                    search_ble_device_count.visibility = View.VISIBLE
                    search_ble_device_count.text = String.format("搜索到%d个设备", adapter.data.size)
                    layout_re_search.visibility = View.VISIBLE
                } else {
                    layout_content.visibility = View.GONE
                    layout_ble_search_empty.visibility = View.VISIBLE
                }
            }
        })
        //开始计时
        timerJob = viewModel.startTimer()
        re_search.singleClick {
            searchType = 0
            checkAllStatus()
        }
        viewModel.bleScanResult.observe(this, { result ->
            result.onFailure {
                if (it is SpecialException) {
                    if (it.code == ExceptionCode.PERMISSIONNOTGRANT.code) {
                        searchType = 2
                        layout_content.visibility = View.GONE
                        layout_ble_search_empty.visibility = View.VISIBLE
                        requestLocation()
                    }
                } else {
                    timerJob?.cancel()
                    layout_content.visibility = View.GONE
                    layout_ble_search_empty.visibility = View.VISIBLE
                }
            }
            result.onSuccess {
                Log.d("BleSearch", "init: " + it.size)
                //倒计时结束，停止搜索
                if (layout_re_search.visibility == View.GONE) {
                    viewModel.searchForBleDevice(this)
                }
                if (it.isNotEmpty()) {
                    adapter.setNewData(it.toMutableList())
                }
            }
        })
        viewModel.searchForBleDevice(this)
        exit_connect_net.singleClick {
            finish()
        }
        empty_re_search.singleClick {
            searchType = 1
            checkAllStatus()
        }

    }

    private fun reSearch() {
        when (searchType) {
            0 -> {
                layout_searching.visibility = View.VISIBLE
                layout_re_search.visibility = View.GONE
                search_ble_device_count.visibility = View.GONE
            }
            1,2 -> {
                layout_ble_search_empty.visibility = View.GONE
                layout_content.visibility = View.VISIBLE
                adapter.setNewData(arrayListOf())
                timerJob?.cancel()
            }
        }
        viewModel.searchForBleDevice(this)
        timerJob = viewModel.startTimer()
    }

    private fun initRV() {
        ble_search_rv.layoutManager = LinearLayoutManager(this)
        ble_search_rv.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as AylaBLEWiFiSetupDevice
            val newIntent = Intent(this, NetConfigActivity::class.java)
            newIntent.putExtra(KEYS.PRODUCTBEAN, intent.getSerializableExtra(KEYS.PRODUCTBEAN))
            newIntent.putExtra(KEYS.BLENAME, item.deviceName)
            newIntent.putExtra("addInfo",intent.getBundleExtra("addInfo"))
            startActivity(newIntent)
            finish()
        }
    }

    private fun startSearchAni(imageView: ImageView) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_0_360)
        anim.fillAfter = true //设置旋转后停止
        imageView.startAnimation(anim)
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
            reSearch()
        }
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
                        reSearch()
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
        when (requestCode) {
            REQUEST_CODE_BLUETOOTH_REQUIRE -> {
                checkAllStatus()
            }
            REQUEST_CODE_LOCATION_ENABLE_REQUIRE -> {
                requestLocation()
            }
        }
    }
}