package com.ayla.hotelsaas.ui.activities.firmware

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.base.BaseNewViewModelActivity
import com.ayla.hotelsaas.bean.BaseResult
import com.ayla.hotelsaas.bean.FirmwareUpdateData
import com.ayla.hotelsaas.bean.FirmwareUpdateStatus
import com.ayla.hotelsaas.data.net.ServerBadException
import com.ayla.hotelsaas.databinding.ActivityFirmwareUpateBinding
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.utils.ImageLoader
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.viewmodel.FirmwareUpdateVM
import kotlinx.android.synthetic.main.activity_firmware_upate.*
import kotlinx.coroutines.Job

class FirmwareUpdateActivity :
    BaseNewViewModelActivity<ActivityFirmwareUpateBinding, FirmwareUpdateVM>() {
    private var repeatUpdateJob: Job? = null
    override fun getViewBinding(): ActivityFirmwareUpateBinding =
        ActivityFirmwareUpateBinding.inflate(layoutInflater)


    override fun init(savedInstanceState: Bundle?) {
        val firmwareUpdateData =
            intent.getParcelableExtra<FirmwareUpdateData>(KEYS.FIRMWAREVESIONDATA)
        firmwareUpdateData?.let { data ->
            firmware_device_name.text = firmwareUpdateData.deviceName
            ImageLoader.loadImg(
                firmware_icon,
                data.iconUrl,
                R.drawable.ic_empty_device,
                R.drawable.ic_empty_device
            )
            firmware_current_version.text = firmwareUpdateData.currentVersion
            firmware_new_version.text = firmwareUpdateData.version
            showProgress("加载中...")
            viewModel.getStatus(data.jobId, data.dsn).observe(this, { result ->
                hideProgress()
                result.onSuccess {
                    if (it is BaseResult<*>) {
                        if (it.code == "0") {
                            if (it.data != null && it.data is String) {
                                when (it.data as String) {
                                    FirmwareUpdateStatus.SUCCEED.name -> {
                                        setResult(RESULT_OK)
                                        finish()
                                    }
                                    FirmwareUpdateStatus.FAILED.name, FirmwareUpdateStatus.PARTIAL_FAILED.name,
                                    FirmwareUpdateStatus.CANCELLED.name, FirmwareUpdateStatus.RETRY.name -> {
                                        retry()
                                    }
                                    else -> {
                                        update()
                                        //轮询结果
                                        repeatUpdateJob =
                                            viewModel.repeatGetUpdateResult(
                                                data.jobId,
                                                data.dsn
                                            )
                                    }

                                }
                            }
                        } else {
                            showWarnToast(TempUtils.getLocalErrorMsg(ServerBadException(it)))
                        }
                    }
                }
                result.onFailure {
                    showWarnToast(TempUtils.getLocalErrorMsg(it))
                }
            })


        }
        layout_firmware_update.singleClick {
            if (TextUtils.equals(firmware_update_txt.text, "重试")) {
                firmwareUpdateData?.let { updateData ->
                    viewModel.retry(updateData.jobId.toString(), updateData.dsn).observe(this, {
                        it.onSuccess {
                            //轮询结果
                            update()
                            repeatUpdateJob =
                                viewModel.repeatGetUpdateResult(updateData.jobId, updateData.dsn)
                        }
                        it.onFailure {
                            showWarnToast(TempUtils.getLocalErrorMsg(it))
                        }
                    })
                }
            } else {
                firmwareUpdateData?.let { updateData ->
                    viewModel.update(updateData.deviceJobId, updateData.dsn).observe(this, {
                        it.onSuccess {
                            //轮询结果
                            update()
                            repeatUpdateJob =
                                viewModel.repeatGetUpdateResult(updateData.jobId, updateData.dsn)
                        }
                        it.onFailure {
                            showWarnToast(TempUtils.getLocalErrorMsg(it))
                        }
                    })
                }
            }

        }
        viewModel.repeatUpdateResult.observe(this, {
            when (it) {
                FirmwareUpdateStatus.SUCCEED.name -> {
                    showSuccessToast("更新完成")
                    setResult(RESULT_OK)
                    finish()
                }
                FirmwareUpdateStatus.FAILED.name -> {
                    retry()
                    showWarnToast("更新失败")
                    repeatUpdateJob?.cancel()
                }
                "error" -> {
                    repeatUpdateJob?.cancel()
                    Log.e("FirmwareUpdateActivity", "错误")
                }
            }
        })
//        viewModel.test()
//        viewModel.testLD.observe(this,{
//            Log.d("gyz", "init: "+it.getOrNull())
//        })
    }

    private fun update() {
        update_anim.visibility = View.VISIBLE
        startUpdateAni(update_anim)
        firmware_update_txt.text = "正在更新"
        layout_firmware_update.isEnabled = false
    }

    private fun retry() {
        if (update_anim.animation != null) {
            update_anim.animation.cancel()
        }
        update_anim.clearAnimation()
        update_anim.visibility = View.GONE
        firmware_update_txt.text = "重试"
        layout_firmware_update.isEnabled = true
    }

    private fun startUpdateAni(imageView: ImageView) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_0_360)
        anim.fillAfter = true
        imageView.startAnimation(anim)
    }
}