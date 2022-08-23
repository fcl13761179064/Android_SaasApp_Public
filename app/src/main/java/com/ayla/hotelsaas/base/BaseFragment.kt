package com.ayla.hotelsaas.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.ayla.hotelsaas.base.BaseMvpActivity


/*
    Fragment基类，业务无关
 */
abstract class BaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(getLayoutResId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view, savedInstanceState)
        fetchData()
    }


    open fun showProgress(msg: String?) {
        val activity = activity
        if (activity is BaseMvpActivity<*, *>) {
            activity.showProgress(msg)
        }
    }

    open fun showProgress() {
        val activity = activity
        if (activity is BaseMvpActivity<*, *>) {
            activity.showProgress()
        }
    }

    open fun hideProgress() {
        val activity = activity
        if (activity is BaseMvpActivity<*, *>) {
            activity.hideProgress()
        }
    }

    /**
     * 获取布局id
     */
    @LayoutRes
    abstract fun getLayoutResId(): Int

    /**
     * 初始化视图
     */
    abstract fun initViews(view: View, savedInstanceState: Bundle?)

    /**
     * 获取数据（可选）
     */
    open fun fetchData() {}

}
