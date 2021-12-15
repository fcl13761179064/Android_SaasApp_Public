package com.ayla.hotelsaas.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.MultiDeviceFoundAdapter
import com.ayla.hotelsaas.api.CoroutineApiService
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.bean.DeviceCategoryBean
import com.ayla.hotelsaas.bean.DeviceCategoryBean.SubBean.NodeBean
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.common.ResultCode
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.utils.RecycleViewDivider
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonObject
import com.scwang.smart.drawable.ProgressDrawable
import io.reactivex.internal.operators.flowable.FlowableTimeInterval
import kotlinx.android.synthetic.main.activity_search_multi_device.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.RuntimeException

class SearchMultiDeviceActivity :BasicActivity(){

    private val api = RetrofitHelper.getRetrofit().create(CoroutineApiService::class.java)
    private val multiDeviceFoundAdapter = MultiDeviceFoundAdapter()
    private val addinfo by lazy {
       intent.getSerializableExtra("addInfo") as Bundle}

    private  val countDown by lazy {
        CountDown(COUNT_DOWN_MILLS,1000L)//这个是倒计3秒然后开始搜索
    }
    private val gatewayDeviceId by lazy {
        intent.getStringExtra("deviceId")?:""}

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
        mdf_rv_content.layoutManager = LinearLayoutManager(this)
        mdf_rv_content.addItemDecoration(
            RecycleViewDivider(
               this,
                LinearLayoutManager.VERTICAL,
                3,
                R.color.all_bg_color
            ))
        mdf_rv_content.adapter = multiDeviceFoundAdapter
        multiDeviceFoundAdapter.setEmptyView(R.layout.empty_scene_page)
        val progressDrawable = ProgressDrawable()
        mdf_iv_loading.setImageDrawable(progressDrawable)
        mdf_iv_loading.post { progressDrawable.start() }
        mdf_iv_retry.setOnClickListener() {
            ClickUtils.applySingleDebouncing(it, 500) {
                startFindDevice()
            }
        }
        startFindDevice()
    }

    override fun initListener() {
        TODO("Not yet implemented")
    }

    @FlowPreview
    private fun startFindDevice() {
        lifecycleScope.launch {
            flow {
                emit(api.updateProperty(gatewayDeviceId, createGatewayParam("100")))
            }.map {
                if(it?.code != ResultCode.SUCCESS){
                    throw RuntimeException("网关进入配网模式失败")
                }else{
                    System.currentTimeMillis()
                }
            }.flatMapConcat { startTime ->
                flow {
                    while (multiDeviceFoundAdapter.data.size <= MAX_DEVICE_COUNT_LIMIT
                        && System.currentTimeMillis() - startTime < POLL_REQUEST_TIME_OUT_MILLS) {
                        try {
                            val nodes = gatewayDeviceId?.let {
                                api.fetchCandidateNodes(
                                    it,
                                    addinfo.getString("deviceCategory") ?: "")
                            }
                            emit(nodes)
                        } catch (ignore: Exception) { }
                        delay(3000L)
                    }
                }
            }.onStart {
                doFindDeviceStart()
            }.catch {
                CustomToast.makeText(this@SearchMultiDeviceActivity,"服务异常，请稍后重试",Toast.LENGTH_LONG)
            }.onCompletion {
                doFindDeviceEnd()
            }.flowOn(Dispatchers.IO).collect {
                showFoundDevice(it.data)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showFoundDevice(data: List<DeviceListBean.DevicesBean>?) {
        val foundDevices = data?.toMutableList() ?: mutableListOf()
        var suffixTip = ""
        val result = if(foundDevices.size > MAX_DEVICE_COUNT_LIMIT){
            suffixTip = "（已达设备上限）"
            foundDevices.subList(0,MAX_DEVICE_COUNT_LIMIT)
        }else{
            suffixTip = ""
            foundDevices
        }
        multiDeviceFoundAdapter.setNewData(result)
        if(result.isNullOrEmpty()){
            mdf_tv_loading.text = "设备搜索中"
        }else{
            mdf_tv_loading.text = "已找到${result.size}个设备$suffixTip"
        }
        mdf_btn_next.isEnabled = result.isNotEmpty()
    }


    private fun doFindDeviceStart() {
        runOnUiThread {
            mdf_iv_retry.visibility==0
            mdf_iv_loading.visibility==1
            mdf_tv_countdown.visibility==0
            countDown.cancel()
            mdf_tv_loading.text = "设备搜索中"
            multiDeviceFoundAdapter.setNewData(null)
            mdf_btn_next.isEnabled = false
            (mdf_iv_loading.drawable as? ProgressDrawable)?.start()
        }
    }

    private fun doFindDeviceEnd() {
        runOnUiThread {
            (mdf_iv_loading.drawable as? ProgressDrawable)?.stop()
            mdf_iv_loading.visibility==1
            mdf_iv_retry.visibility==0
            if(multiDeviceFoundAdapter.data.isNullOrEmpty()){
                mdf_tv_loading.text = "未搜索到设备"
            }
        }
        if(!multiDeviceFoundAdapter.data.isNullOrEmpty()){
            countDown.start()
        }
        exitGatewayJoinMode()
    }

    private fun exitGatewayJoinMode() {
        lifecycleScope.launch {
            api.updateProperty(gatewayDeviceId,createGatewayParam("0"))
        }
    }

    private fun createGatewayParam(time:String): RequestBody {
        val jsonBody = JsonObject()
        jsonBody.addProperty("propertyCode", "zb_join_enable")
        jsonBody.addProperty("propertyValue", time)
        return jsonBody.toString().toReqBody()
    }


    /**
     * string转requestBody
     */
    fun String.toReqBody(): RequestBody {
        return toRequestBody("application/json; charset=UTF-8".toMediaType())
    }
}

class  CountDown( millisInFuture:Long ,  countDownInterval:Long) :CountDownTimer(millisInFuture,countDownInterval
)  {
    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    override fun onTick(millisUntilFinished: Long) {

    }

    /**
     * Callback fired when the time is up.
     */
    override fun onFinish() {

    }

}
