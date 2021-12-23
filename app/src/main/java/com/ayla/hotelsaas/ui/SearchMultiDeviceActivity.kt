package com.ayla.hotelsaas.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.MultiDeviceFoundAdapter
import com.ayla.hotelsaas.api.CoroutineApiService
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.common.ResultCode
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.events.DeviceAddEvent
import com.ayla.hotelsaas.page.ext.setInvisible
import com.ayla.hotelsaas.page.ext.setVisible
import com.ayla.hotelsaas.page.ext.singleClick
import com.ayla.hotelsaas.utils.RecycleViewDivider
import com.ayla.hotelsaas.utils.WifiUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.JsonObject
import com.scwang.smart.drawable.ProgressDrawable
import kotlinx.android.synthetic.main.activity_search_multi_device.*
import kotlinx.android.synthetic.main.activity_search_multi_device.mdf_btn_next
import kotlinx.android.synthetic.main.new_empty_page_status_layout.*
import kotlinx.android.synthetic.main.new_empty_page_status_layout.view.*
import kotlinx.android.synthetic.main.test_wifi_fragment.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import java.lang.RuntimeException

class SearchMultiDeviceActivity : BasicActivity() {

    private val api = RetrofitHelper.getRetrofit().create(CoroutineApiService::class.java)
    private val multiDeviceFoundAdapter = MultiDeviceFoundAdapter()
    private lateinit var pollJob: Job
    private val addinfo by lazy {
        intent.getBundleExtra("addInfo")
    }
    private val countDown = CountDown(COUNT_DOWN_MILLS, 1000L)//这个是倒计3秒然后开始搜索
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

    private val cloudOemModel by lazy {
        addinfo?.getString("deviceCategory") ?: ""

    }

    companion object {
        private const val POLL_REQUEST_TIME_OUT_MILLS = 120000L
        private const val COUNT_DOWN_MILLS = 30000L
        private const val MAX_DEVICE_COUNT_LIMIT = 20
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_search_multi_device
    }

    override fun getLayoutView(): View? {
        return null
    }

    override fun initView() {
        if (!NetworkUtils.isConnected()) {
            CustomToast.makeText(this, "网络异常", R.drawable.ic_toast_warming)
            return
        }
        val intentRecevier = IntentRecevier() // intentRecevier定义为全局变量
        var filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(intentRecevier, filter)
        mdf_rv_content.layoutManager = LinearLayoutManager(this)
        multiDeviceFoundAdapter.bindToRecyclerView(mdf_rv_content)
        mdf_rv_content.adapter = multiDeviceFoundAdapter
        multiDeviceFoundAdapter.setEmptyView(R.layout.new_empty_page_status_layout)
        multiDeviceFoundAdapter.getEmptyView().bt_resert_search.singleClick {
            startFindDevice()
        }
        multiDeviceFoundAdapter.getEmptyView().log_out.singleClick {
            EventBus.getDefault().post(DeviceAddEvent())
            startActivity<MainActivity>()
        }
        //开始发现设备
        startFindDevice()
    }


    private fun toBindPage() {
        val deviceIdList = multiDeviceFoundAdapter.data.map { it.deviceId }
        startActivity<MultiDeviceDistributionNetActivity>(
            Keys.ID to gatewayDeviceId,
            Keys.DATA to addinfo,
            Keys.OEMMODEL to cloudOemModel,
            Keys.MULTI_DEVICE_IDS to deviceIdList
        )
    }

    override fun initListener() {
        mdf_btn_next.singleClick {
            if (!NetworkUtils.isConnected()) {
                CustomToast.makeText(this, "网络异常", R.drawable.ic_toast_warming)
                return@singleClick
            }
            if (mdf_btn_next.text.equals("重新搜索")) {
                startFindDevice()
                mdf_btn_next.setText("下一步")
            } else {
                toBindPage()
            }

        }
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
                                    cloudOemModel
                                )
                            }
                            emit(nodes)
                        } catch (ignore: Exception) {
                            ignore.printStackTrace()
                        }
                        delay(2000L)
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
     /*  val mutableListOf = mutableListOf<DeviceListBean.DevicesBean>()
          for (index in 0 until 100) {
              val devicesBean = DeviceListBean.DevicesBean()
              devicesBean.deviceId = "111111111"
              mutableListOf.add(devicesBean)
          }*/
        val foundDevices = data?.toMutableList() ?: mutableListOf()
        var suffixTip = ""
        val result = if (foundDevices.size > MAX_DEVICE_COUNT_LIMIT) {
            suffixTip = "设备搜索中...已达设备上限"
            foundDevices.subList(0, MAX_DEVICE_COUNT_LIMIT)
        } else if (foundDevices.size < MAX_DEVICE_COUNT_LIMIT && foundDevices.size > 0) {
            suffixTip = "搜索到${foundDevices.size}个设备"
            foundDevices
        } else {
            suffixTip = ""
            foundDevices
        }
        for (index in 0 until result.size) {
            result.get(index).iconUrl = NodeDeviceUrl
            result.get(index).deviceName = NodeDeviceName
        }
        multiDeviceFoundAdapter.setNewData(result)
        if (result.isNullOrEmpty()) {
            mdf_tv_loading.text = "设备搜索中"
        } else {
            mdf_tv_loading.text = "$suffixTip"
        }
        mdf_btn_next.isEnabled = result.isNotEmpty()
    }


    private fun doFindDeviceStart() {
        runOnUiThread {
            multiDeviceFoundAdapter.getEmptyView().tv_loading_search.setText("正在搜索设备中")
            tv_desc.setVisible(false)
            multiDeviceFoundAdapter.getEmptyView().cl_layout.setVisible(false)
            mdf_iv_loading.setVisible(true)
            mdf_iv_retry_or_remain_time.setVisible(true)
            mdf_tv_loading.setVisible(true)
            ll_next_layout.setVisible(true)
            remain120countDown.cancel()
            countDown.cancel()
            remain120countDown.start()
            mdf_tv_loading.text = "设备搜索中"
            multiDeviceFoundAdapter.setNewData(null)
            mdf_btn_next.isEnabled = false
            (mdf_iv_loading.drawable as? ProgressDrawable)?.start()
        }
    }

    private suspend fun doFindDeviceEnd() {
        runOnUiThread {
            (mdf_iv_loading.drawable as? ProgressDrawable)?.stop()
            mdf_iv_loading.setVisible(false)
            if (multiDeviceFoundAdapter.data.isNullOrEmpty()) {
                mdf_tv_loading.setVisible(false)
                mdf_iv_retry_or_remain_time.setInvisible(false)
                multiDeviceFoundAdapter.getEmptyView().tv_loading_search.setText("未搜索到设备，请排查以下问题")
                ll_next_layout.setVisible(false)
                multiDeviceFoundAdapter.getEmptyView().cl_layout.setVisible(true)
            }
        }
        if (!multiDeviceFoundAdapter.data.isNullOrEmpty()) {
            countDown.start()
        } else {
            remain120countDown.cancel()
        }
        exitGatewayJoinMode()
    }

    private suspend fun exitGatewayJoinMode() {
        try {
            api.updateProperty(gatewayDeviceId, createGatewayParam("0"))
        } catch (e: Exception) {
            e.printStackTrace()
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
            mdf_iv_retry_or_remain_time.setTextColor(resources.getColor(R.color.serch_retry))
            mdf_iv_retry_or_remain_time.text = "${millisUntilFinished / 1000}s 后重试"
            LogUtils.d("Multi", 1)
        }

        override fun onFinish() {
            mdf_iv_retry_or_remain_time.setVisible(false)
            mdf_btn_next.setText("重新搜索")
            tv_desc.setVisible(true)
            tv_desc.setText("搜索超时，请重新搜索")
            LogUtils.d("Multi", 11)
        }
    }

    inner class Count120Down(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(
        millisInFuture,
        countDownInterval
    ) {

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            LogUtils.d("Multi", 111)
            mdf_iv_retry_or_remain_time.setTextColor(resources.getColor(R.color.serch_multi_device_yellow))
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

    override fun onDestroy() {
        try {
            (mdf_iv_loading.drawable as? ProgressDrawable)?.stop()
            if (!pollJob.isCancelled) pollJob.cancel()
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
        super.onDestroy()
    }


    class IntentRecevier() : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.activeNetworkInfo

            // 判断网络情况
            if (networkInfo != null && networkInfo.isAvailable) {
                // 网络可用时的执行内容
            } else {
                // 网络不可用时的执行内容
                CustomToast.makeText(context, "网络异常", R.drawable.ic_toast_warming)
            }
        }

    }

}




