package com.ayla.hotelsaas.widget

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.viewmodel.SceneSelectDeviceViewModel
import com.scwang.smart.drawable.ProgressDrawable
import org.jetbrains.anko.find

/*
    加载对话框封装
 */
class ProgressLoading private constructor(context: Context, theme: Int) : Dialog(context, theme) {

    private val handler = Handler(Looper.getMainLooper())

    companion object {

        private val animDrawable = ProgressDrawable()

        /*
            创建加载对话框
         */
        fun create(context: Context):ProgressLoading {
            //样式引入
            val mDialog = ProgressLoading(context, R.style.LightProgressDialog)
            //设置布局
            mDialog.setContentView(R.layout.progress_dialog)
            mDialog.setCancelable(true)
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.window?.attributes?.gravity = Gravity.CENTER

            val lp = mDialog.window?.attributes
            lp?.dimAmount = 0.2f
            //设置属性
            mDialog.window?.attributes = lp

            //获取动画视图
            val loadingView = mDialog.find<ImageView>(R.id.iv_loading)
            loadingView.setImageDrawable(animDrawable)

            return mDialog
        }
    }

    override fun show() {
        super.show()
        animDrawable.start()
    }

    override fun dismiss() {
        handler.post {
            if(animDrawable.isRunning){
                animDrawable.stop()
            }
            super.dismiss()
        }
    }

    /*
        显示加载对话框，动画开始
     */
    fun showLoading() {
        super.show()
        animDrawable.start()
    }

    /*
        隐藏加载对话框，动画停止
     */
    fun hideLoading(){
        super.dismiss()
        if(animDrawable.isRunning){
            animDrawable.stop()
        }
    }
}
