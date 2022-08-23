/*
 * crate by cairurui on 18-11-1 下午3:29.
 * Copyright (c) 2018 SunseaIoT. All rights reserved.
 */
package com.ayla.hotelsaas.ui.activities

import android.Manifest
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.mvp.view.A6ScanRelativeView
import com.ayla.hotelsaas.mvp.present.A6ScanRelativePresenter
import com.ayla.hotelsaas.R
import android.os.Bundle
import com.tbruyelle.rxpermissions2.RxPermissions
import com.ayla.hotelsaas.widget.common_dialog.CustomAlarmDialog
import android.content.Intent
import android.text.TextUtils
import android.graphics.Rect
import android.net.Uri
import com.ayla.hotelsaas.utils.CustomToast
import android.os.Vibrator
import android.util.Base64
import android.widget.FrameLayout
import com.ayla.hotelsaas.ext.dp
import com.ayla.hotelsaas.ext.showToast
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.VibrateUtils
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import kotlinx.android.synthetic.main.activity_a6_scan_share.*
import kotlinx.android.synthetic.main.activity_move_wallo_scan_share.*
import kotlinx.android.synthetic.main.activity_move_wallo_scan_share.scan_cb_light
import java.lang.Exception
import java.util.ArrayList

/**
 *
 */
class ScanA6Activity : BaseMvpActivity<A6ScanRelativeView?, A6ScanRelativePresenter?>() {
    private var mDeviceId: String? = null
    private var scanRelative = false

    private val scanFrameSize = 250.dp()
    private var remoteView: RemoteView? = null


    override fun onStart() {
        super.onStart()
        mDeviceId = intent.getStringExtra("deviceId")
        scanRelative = intent.getBooleanExtra("scanRelative", false)
        remoteView?.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startScan(savedInstanceState)
        scan_A6_light.setOnClickListener {
            remoteView?.switchLight()
        }
    }

    private fun initScanRemoteView(savedInstanceState: Bundle?) {
        val mScreenWidth = ScreenUtils.getAppScreenWidth()
        val mScreenHeight = ScreenUtils.getAppScreenHeight()
        val scanRect = Rect().apply {
            left = mScreenWidth / 2 - scanFrameSize / 2
            right = mScreenWidth / 2 + scanFrameSize / 2
            top = mScreenHeight / 2 - scanFrameSize / 2
            bottom = mScreenHeight / 2 + scanFrameSize / 2
        }
        remoteView = RemoteView.Builder()
            .setContext(this)
            .setBoundingBox(scanRect)
            .setFormat(HmsScan.ALL_SCAN_TYPE)
            .build()
        remoteView?.setOnResultCallback {
            remoteView?.pauseContinuouslyScan()
            val scanResult = it.firstOrNull()?.originalValue
            if (scanResult.isNullOrEmpty() || scanResult.isBlank()) {
                remoteView?.resumeContinuouslyScan()
                return@setOnResultCallback
            }
            onScanQRCodeSuccess(scanResult)
        }
        remoteView?.onCreate(savedInstanceState)
        scan_A6_frame.addView(
            remoteView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        remoteView?.onResume()
    }


    protected fun startScan(savedInstanceState: Bundle?) {
        RxPermissions(this)
            .requestEachCombined(Manifest.permission.CAMERA)
            .subscribe { permission ->
                if (permission.granted) {
                    initScanRemoteView(savedInstanceState)
                } else if (!permission.shouldShowRequestPermissionRationale) {
                    CustomAlarmDialog
                        .newInstance(object : CustomAlarmDialog.Callback {
                            override fun onDone(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                                val localIntent = Intent()
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                                localIntent.data = Uri.fromParts("package", packageName, null)
                                startActivity(localIntent)
                            }

                            override fun onCancel(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                            }
                        })
                        .setTitle("获取相机权限")
                        .setEnsureText("前往开启")
                        .setContent("需要使用相机权限，用以扫描二维码点击“前往开启”打开相机权限")
                        .show(supportFragmentManager, "")
                }
            }
    }


    override fun initPresenter(): A6ScanRelativePresenter {
        return A6ScanRelativePresenter()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_a6_scan_share
    }

    override fun initView() {

    }

    override fun initListener() {}

    fun onScanQRCodeSuccess(result: String) {
        remoteView?.pauseContinuouslyScan()
        VibrateUtils.vibrate(200L)
        if (TextUtils.isEmpty(result)) {
            showToast("识别失败，请重试！")
        } else {
            try {
                val deviceId = result.trim { it <= ' ' }
                if (!TextUtils.isEmpty(deviceId)) {
                    val arrayList: ArrayList<String> = ArrayList<String>()
                    arrayList.add("deviceId")
                    arrayList.add("regToken")
                    arrayList.add("tempToken")
                    if (deviceId.startsWith("http") && Uri.parse(deviceId).queryParameterNames.containsAll(
                            arrayList
                        )
                    ) {
                        val bundle = Bundle()
                        val uri = Uri.parse(deviceId)
                        val paramNames = uri.queryParameterNames
                        for (paramName in paramNames) {
                            bundle.putString(
                                paramName,
                                String(
                                    Base64.decode(
                                        uri.getQueryParameter(paramName),
                                        Base64.NO_WRAP
                                    )
                                )
                            )
                        }
                        val device_Id = bundle["deviceId"] as String?
                        if (!scanRelative) { //说明是绑定A6
                            setResult(RESULT_OK, Intent().putExtra("result", result))
                            finish()
                        } else {
                            if (mDeviceId == device_Id) {
                                setResult(RESULT_OK, Intent().putExtra("result", result))
                                finish()
                            } else {

                                CustomToast.makeText(
                                    this,
                                    "不可扫描其他A6网关进行关联操作",
                                    R.drawable.ic_toast_warning
                                )
                            }
                        }
                    } else {
                        showDsnErrorDialog()
                    }
                } else {
                    showDsnErrorDialog()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showDsnErrorDialog()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        remoteView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        remoteView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView?.onDestroy()
    }


    fun showDsnErrorDialog() {
        CustomAlarmDialog.newInstance().setTitle("信息错误")
            .setContent(String.format("二维码信息错误，请检查信息正确后再扫描二维码"))
            .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
            .setEnsureText("重试")
            .setDoneCallback(object : CustomAlarmDialog.Callback {
                override fun onDone(dialog: CustomAlarmDialog) {
                    dialog.dismissAllowingStateLoss()
                    remoteView?.postDelayed({
                        remoteView?.resumeContinuouslyScan()
                    }, 1000)
                }

                override fun onCancel(dialog: CustomAlarmDialog) {
                    remoteView?.resumeContinuouslyScan()
                }
            })
            .show(supportFragmentManager, "dialog")
    }

}