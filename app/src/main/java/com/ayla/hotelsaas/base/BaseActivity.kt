package com.ayla.hotelsaas.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.application.MyApplication.getResource
import com.ayla.hotelsaas.vm.IView
import com.ayla.hotelsaas.widget.ProgressLoading
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), IView {
    val loadingDialog by lazy { ProgressLoading.create(this) }
    private var _binding: VB? = null
    abstract fun getViewBinding(): VB?
    val binding: VB get() = _binding!!
    open val isNeedPageStatusManager: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        initSpecialParams()
        initPage()
        initAppbar()
        initSpecialEvent()
        init(savedInstanceState)
        if (!ScreenUtils.isFullScreen(this)) {
            BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
            BarUtils.setStatusBarLightMode(this, true)
            window.navigationBarColor = getResource().getColor(R.color.white)
        }
    }


    private fun initPage() {
        setContentView(binding.root)
    }

    private fun createPageStateManager() {
    }

    /**
     * 用于设置一些特殊参数，需要在setContent之前的
     */
    open fun initSpecialParams() {

    }

    /**
     * 初始化一些特殊的事件，放在最底层初始化界面之前
     */
    open fun initSpecialEvent() {

    }

    /**
     * 仅初始化一些与界面相关的操作
     */
    abstract fun init(savedInstanceState: Bundle?)


    override fun showLoadings(isHideOther: Boolean) {
        loadingDialog.showLoading()
    }

    fun showLoading() {
        loadingDialog.showLoading()
    }

    override fun hideLoadings() {
        loadingDialog.hideLoading()
    }

    override fun showContent() {
    }

    override fun showEmpty() {
    }

    override fun showEmpty(imageResId: Int, tipText: String?, retryText: String?) {
    }

    override fun showError() {
    }

    override fun showError(imageResId: Int, tipText: String?, retryText: String?) {
    }

    override fun handleSpecialEvent(code: Int) {

    }


    private fun initAppbar() {
        val appbarRoot = findViewById<View>(R.id.appbar_root_rl_ff91090)
        if (appbarRoot != null) {
            val leftIV = appbarRoot.findViewById<View>(R.id.iv_left)
            if (leftIV != null && !leftIV.hasOnClickListeners()) {
                leftIV.setOnClickListener { appBarLeftIvClicked() }
            }
            val leftTV = appbarRoot.findViewById<View>(R.id.tv_left)
            if (leftTV != null && !leftTV.hasOnClickListeners()) {
                leftTV.setOnClickListener { appBarLeftTvClicked() }
            }
            val rightIV = appbarRoot.findViewById<View>(R.id.iv_right)
            if (rightIV != null && !rightIV.hasOnClickListeners()) {
                rightIV.setOnClickListener { appBarRightIvClicked() }
            }
            val rightTv = appbarRoot.findViewById<View>(R.id.tv_right)
            if (rightTv != null && !rightTv.hasOnClickListeners()) {
                rightTv.setOnClickListener { appBarRightTvClicked() }
            }
        }
    }

    /**
     * appbar左侧图标点击事件
     */
    open fun appBarLeftIvClicked() {
        onBackPressed()
    }

    open fun appBarLeftTvClicked() {
        onBackPressed()
    }

    open fun appBarRightIvClicked() {}

    open fun appBarRightTvClicked() {}

}
