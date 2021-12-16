package com.ayla.hotelsaas.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.MultiDeviceFoundAdapter
import com.ayla.hotelsaas.api.CoroutineApiService
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.common.ResultCode
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.page.ext.setInvisible
import com.ayla.hotelsaas.page.ext.setVisible
import com.ayla.hotelsaas.page.ext.singleClick
import com.ayla.hotelsaas.utils.RecycleViewDivider
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.JsonObject
import com.scwang.smart.drawable.ProgressDrawable
import kotlinx.android.synthetic.main.activity_search_multi_device.*
import kotlinx.android.synthetic.main.activity_search_multi_device.mdf_btn_next
import kotlinx.android.synthetic.main.new_empty_page_status_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.RuntimeException

class SearchMultiDeviceActivity : BasicActivity() {

    private val api = RetrofitHelper.getRetrofit().create(CoroutineApiService::class.java)
    private val multiDeviceFoundAdapter = MultiDeviceFoundAdapter()
    private lateinit var pollJob: Job
    private val addinfo by lazy {
        intent.getBundleExtra("addInfo")
    }
    private val countDown = CountDown(COUNT_DOWN_MILLS, 1500L)//这个是倒计3秒然后开始搜索
    private val remain120countDown =
        Count120Down(POLL_REQUEST_TIME_OUT_MILLS, 1000L)//这个是倒计120秒,120秒就停止搜索
    private val gatewayDeviceId by lazy {
        addinfo?.getString("deviceId") ?: ""

    }
    private val NodeDeviceName by lazy {
        addinfo?.getString("productName") ?: ""

    }
    private val NodeDeviceUrl by lazy {
        addinfo?.getString("deviceUrl") ?: ""

    }
    companion object {
        private const val POLL_REQUEST_TIME_OUT_MILLS = 10000L
        private const val COUNT_DOWN_MILLS = 10000L
        private const val MAX_DEVICE_COUNT_LIMIT = 20
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_search_multi_device
    }

    override fun getLayoutView(): View? {
        return null
    }

    override fun initView() {
        mdf_rv_content.layoutManager = LinearLayoutManager(this)
        mdf_rv_content.addItemDecoration(
            RecycleViewDivider(
                this,
                LinearLayoutManager.VERTICAL,
                3,
                R.color.all_bg_color
            )
        )
        multiDeviceFoundAdapter.bindToRecyclerView(mdf_rv_content)
        mdf_rv_content.adapter = multiDeviceFoundAdapter
        multiDeviceFoundAdapter.setEmptyView(R.layout.new_empty_page_status_layout)
        mdf_btn_next.singleClick {
            if (mdf_btn_next.text.equals("重新搜索")) {
                ClickUtils.applySingleDebouncing(mdf_btn_next, 500) {
                    startFindDevice()
                    mdf_btn_next.setText("下一步")
                }
            } else {
                toBindPage()
            }

        }
        //开始发现设备
        startFindDevice()
    }


    private fun toBindPage() {
        if (!pollJob.isCancelled) pollJob.cancel()
        val deviceIdList = multiDeviceFoundAdapter.data.map { it.deviceId } as Array<String>
        val activity = Intent(this, DeviceAddActivity::class.java)
        addinfo?.putSerializable("multi_device_list", deviceIdList)
        activity.putExtra("addInfo", addinfo)
        startActivity(activity)
    }

    override fun initListener() {
    }


    @FlowPreview
    private fun startFindDevice() {
        pollJob = lifecycleScope.launch {
            flow {
                emit(api.updateProperty(gatewayDeviceId, createGatewayParam("120")))
            }.map {
                if (it?.code != ResultCode.SUCCESS) {
                    throw RuntimeException("网关进入配网模式失败")
                } else {
                    System.currentTimeMillis()
                }
            }.flatMapConcat { startTime ->
                flow {
                    while (multiDeviceFoundAdapter.data.size <= MAX_DEVICE_COUNT_LIMIT
                        && System.currentTimeMillis() - startTime < POLL_REQUEST_TIME_OUT_MILLS
                    ) {
                        try {
                            val nodes = gatewayDeviceId?.let {
                                api.fetchCandidateNodes(
                                    it,
                                    addinfo?.getString("deviceCategory") ?: ""
                                )
                            }
                            emit(nodes)
                        } catch (ignore: Exception) {
                        }
                        delay(3000L)

                    }
                }
            }.onStart {
                doFindDeviceStart()
            }.catch {
                CustomToast.makeText(
                    this@SearchMultiDeviceActivity,
                    "服务异常，请稍后重试",
                    Toast.LENGTH_LONG
                )
            }.onCompletion {
                doFindDeviceEnd()
            }.flowOn(Dispatchers.IO).collect {
                showFoundDevice(it?.data)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showFoundDevice(data: List<DeviceListBean.DevicesBean>?) {
        val foundDevices = data?.toMutableList() ?: mutableListOf()
        var suffixTip = ""
        val result = if (foundDevices.size > MAX_DEVICE_COUNT_LIMIT) {
            suffixTip = "（设备搜索中...已达设备上限）"
            foundDevices.subList(0, MAX_DEVICE_COUNT_LIMIT)
        } else {
            suffixTip = ""
            foundDevices
        }
        for (index in  0 until  result.size){
            result.get(index).iconUrl=NodeDeviceUrl
            result.get(index).deviceName=NodeDeviceName
        }
        multiDeviceFoundAdapter.setNewData(result)
        if (result.isNullOrEmpty()) {
            mdf_tv_loading.text = "设备搜索中"
        } else {
            mdf_tv_loading.text = "搜索到${result.size}个设备$suffixTip"
        }
        mdf_btn_next.isEnabled = result.isNotEmpty()
    }


    private fun doFindDeviceStart() {
        runOnUiThread {
            mdf_iv_loading.setVisible(true)
            remain120countDown.cancel()
            remain120countDown.start()
            countDown.cancel()
            mdf_tv_loading.text = "设备搜索中"
            multiDeviceFoundAdapter.setNewData(null)
            mdf_btn_next.isEnabled = false
            (mdf_iv_loading.drawable as? ProgressDrawable)?.start()
        }
    }

    private fun doFindDeviceEnd() {
        runOnUiThread {
            remain120countDown.cancel()
            (mdf_iv_loading.drawable as? ProgressDrawable)?.stop()
            mdf_iv_loading.setVisible(false)
            if (multiDeviceFoundAdapter.data.isNullOrEmpty()) {
                mdf_tv_loading.setVisible(false)
                cl_layout.setVisible(true)
                mdf_iv_retry_or_remain_time.setInvisible(false)
            }
        }
        if (!multiDeviceFoundAdapter.data.isNullOrEmpty()) {
            mdf_iv_retry_or_remain_time.setVisible(true)
            countDown.start()
        }
        exitGatewayJoinMode()
    }

    private fun exitGatewayJoinMode() {
        lifecycleScope.launch {
            api.updateProperty(gatewayDeviceId, createGatewayParam("0"))
        }
    }

    private fun createGatewayParam(time: String): RequestBody {
        val jsonBody = JsonObject()
        jsonBody.addProperty("propertyCode", "zb_join_enable")
        jsonBody.addProperty("propertyValue", time)
        return jsonBody.toString().toReqBody()
    }


    /**
     * string转requestBody
     */
    fun String.toReqBody(): RequestBody {
        return this.toRequestBody("application/json; charset=UTF-8".toMediaType())
    }

    inner class CountDown(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(
        millisInFuture,
        countDownInterval
    ) {

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            mdf_iv_retry_or_remain_time.text = "${millisUntilFinished / 1000}s 后重试"
        }

        override fun onFinish() {
            mdf_iv_retry_or_remain_time.setVisible(false)
            mdf_btn_next.setText("重新搜索")
        }
    }

    inner class Count120Down(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(
        millisInFuture,
        countDownInterval
    ) {

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            mdf_iv_retry_or_remain_time.setText(
                "搜索剩余 ${
                    (TimeUtils.millis2String(
                        millisUntilFinished,
                        "m:ss"
                    ))
                }s"
            )
        }

        override fun onFinish() {
        }
    }

    @FlowPreview
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startFindDevice()
    }

    override fun onDestroy() {
        try {
            (mdf_iv_loading.drawable as? ProgressDrawable)?.stop()
            if (!pollJob.isCancelled) pollJob.cancel()
        } catch (ignore: Exception) {
        }
        super.onDestroy()
    }
}




