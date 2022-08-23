/*
 * create by cairurui on 18-11-1 下午3:29.
 * Copyright (c) 2018 SunseaIoT. All rights reserved.
 */
package com.ayla.hotelsaas.ui.activities

import android.Manifest
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.mvp.view.MoveWallView
import com.ayla.hotelsaas.mvp.present.MoveWallPresenter
import com.ayla.hotelsaas.R
import com.tbruyelle.rxpermissions2.RxPermissions
import com.ayla.hotelsaas.widget.common_dialog.CustomAlarmDialog
import android.content.Intent
import com.ayla.hotelsaas.bean.ZxingMoveWallBean
import android.text.TextUtils
import android.annotation.SuppressLint
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.util.Base64
import android.widget.FrameLayout
import com.huawei.hms.hmsscankit.RemoteView
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.bean.MoveWallBean
import com.ayla.hotelsaas.data.net.ServerBadException
import com.ayla.hotelsaas.ext.dp
import com.ayla.hotelsaas.ext.showToast
import com.blankj.utilcode.util.*
import com.google.gson.reflect.TypeToken
import com.huawei.hms.ml.scan.HmsScan
import kotlinx.android.synthetic.main.activity_move_wallo_scan_share.*
import java.lang.Exception

/**
 *
 */
class MoveExhibitionWallScanActivity : BaseMvpActivity<MoveWallView?, MoveWallPresenter?>(),
    MoveWallView {

    private var firstLoad = true
    private val scanFrameSize = 250.dp()
    private var remoteView: RemoteView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startScan(savedInstanceState)
        scan_cb_light.setOnClickListener {
            remoteView?.switchLight()
        }
    }

    protected fun startScan(savedInstanceState: Bundle?) {
        RxPermissions(this)
            .requestEachCombined(Manifest.permission.CAMERA)
            .subscribe { permission ->
                if (permission.granted) {
                    initScanRemoteView(savedInstanceState)
                } else if (permission.shouldShowRequestPermissionRationale) {
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
                                finish()
                            }
                        })
                        .setTitle("获取相机权限")
                        .setEnsureText("前往开启")
                        .setContent("需要使用相机权限，用以扫描二维码点击“前往开启”打开相机权限")
                        .show(supportFragmentManager, "")
                } else if (firstLoad) {
                    firstLoad = false
                    CustomAlarmDialog
                        .newInstance(object : CustomAlarmDialog.Callback {
                            override fun onDone(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                                finish()
                                PermissionUtils.launchAppDetailsSettings()
                            }

                            override fun onCancel(dialog: CustomAlarmDialog) {
                                dialog.dismissAllowingStateLoss()
                            }
                        })
                        .setContent("请允许使用相机权限").show(supportFragmentManager, null)
                }
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
        scan_fl_frame.addView(
            remoteView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        remoteView?.onResume()
    }

    override fun initPresenter(): MoveWallPresenter {
        return MoveWallPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_move_wallo_scan_share
    }

    override fun initView() {
    }

    override fun initListener() {

    }

    private fun onScanQRCodeSuccess(result: String?) {
        remoteView?.pauseContinuouslyScan()
        VibrateUtils.vibrate(200L)
        if (TextUtils.isEmpty(result)) {
            showToast("识别失败，请重试！")
        } else {
            try {
                val zxingMoveWallBean = result?.let { setDecrypt(it) }
                val type = object : TypeToken<ZxingMoveWallBean?>() {}.type
                val obj = GsonUtils.fromJson<ZxingMoveWallBean>(zxingMoveWallBean, type)
                if (!TextUtils.isEmpty(obj.id) && obj.roomId != 0L && !TextUtils.isEmpty(obj.name)) {
                    mPresenter?.getNetworkConfigGuide(obj)
                } else {
                    showDsnErrorDialog()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showDsnErrorDialog()
            }
        }
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

    /**
     * 解密
     * encodeWord：加密后的文字/比如密码
     */
    fun setDecrypt(encodeWord: String): String? {
        try {
            val decodeWord = String(Base64.decode(encodeWord.toByteArray(), Base64.DEFAULT))
            val decodeWordtwo = Uri.decode(decodeWord)
            val type = object : TypeToken<ZxingMoveWallBean?>() {}.type
            val obj = GsonUtils.fromJson<ZxingMoveWallBean>(decodeWordtwo, type)
            val bytes = EncryptUtils.decryptHexStringAES(
                obj.param,
                "SHOWWALLSHOWWALL".toByteArray(),
                "AES/ECB/PKCS5Padding",
                null
            )
            return String(bytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }


    override fun getMoveWallDataSuccess(
        moveWallBean: MoveWallBean,
        move_wall_data: ZxingMoveWallBean
    ) {
        if (moveWallBean != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("roomId", move_wall_data.roomId)
            intent.putExtra("roomName", move_wall_data.name)
            intent.putExtra("businessId", moveWallBean.businessId)
            intent.putExtra("billId", moveWallBean.id)
            intent.putExtra("move_wall_type", moveWallBean.type)
            startActivityForResult(intent, MainActivity.RESULT_CODE_RENAMED)
            finish()
        } else {
            showDsnErrorDialog()
        }
    }

    override fun getMoveWallDataFail(message: String, throwable: ServerBadException) {
        if ("215000" == throwable.code || "216000" == throwable.code || "217000" == throwable.code) {
            CustomToast.makeText(baseContext, throwable.msg, R.drawable.ic_toast_warning)
            remoteView?.postDelayed({
                remoteView?.resumeContinuouslyScan()
            }, 2000)
        } else {
            if (message == "timeout") {
                CustomAlarmDialog.newInstance().setTitle("时间超时")
                    .setContent(String.format("二维码时间超时"))
                    .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                    .setEnsureText("重试")
                    .setDoneCallback(object : CustomAlarmDialog.Callback {
                        override fun onDone(dialog: CustomAlarmDialog) {
                            dialog.dismissAllowingStateLoss()
                            remoteView?.postDelayed({
                                remoteView?.resumeContinuouslyScan()
                            }, 1000)
                        }

                        override fun onCancel(dialog: CustomAlarmDialog) {}
                    })
                    .show(supportFragmentManager, "dialog")
            } else {
                CustomAlarmDialog.newInstance().setTitle("无施工权限")
                    .setContent(String.format("您没有当前房间的施工权限，请联系艾拉客服部进行开通"))
                    .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                    .setEnsureText("重试")
                    .setDoneCallback(object : CustomAlarmDialog.Callback {
                        override fun onDone(dialog: CustomAlarmDialog) {
                            dialog.dismissAllowingStateLoss()
                            remoteView?.postDelayed({
                                remoteView?.resumeContinuouslyScan()
                            }, 1000)
                        }

                        override fun onCancel(dialog: CustomAlarmDialog) {}
                    })
                    .show(supportFragmentManager, "dialog")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        remoteView?.onStart()
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

}