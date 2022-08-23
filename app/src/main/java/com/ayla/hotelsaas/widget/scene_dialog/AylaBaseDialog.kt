package com.ayla.hotelsaas.widget.scene_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ayla.hotelsaas.R
import com.blankj.utilcode.util.ScreenUtils

/**
 * 常用弹窗
 */

abstract class AylaBaseDialog : DialogFragment() {
    private var onOperateListener: OnOperateListener? = null
    var params = BaseDialogParams()

    private fun setOnOperateListener(listener: OnOperateListener) {
        onOperateListener = listener
    }

    abstract fun getLayoutView(inflater: LayoutInflater): View?

    abstract fun initView(view: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = getLayoutView(inflater)
        view?.apply {
            findViewById<View>(R.id.dialog_cancel).setOnClickListener {
                dismissAllowingStateLoss()
                onOperateListener?.onClickLeft(this@AylaBaseDialog)
            }
            findViewById<View>(R.id.dialog_confirm).setOnClickListener {
                dismissAllowingStateLoss()
                onOperateListener?.onClickRight(this@AylaBaseDialog)
            }
            findViewById<TextView>(R.id.dialog_title)?.text = params.title
            findViewById<View>(R.id.layout_cancel)?.visibility =
                if (params.showLeftButton) View.VISIBLE else View.GONE
            params.leftButtonColor?.let {
                findViewById<TextView>(R.id.dialog_cancel)?.setTextColor(it)
            }
            params.rightButtonColor?.let {
                findViewById<TextView>(R.id.dialog_confirm)?.setTextColor(it)
            }
            params.leftButtonName?.let {
                findViewById<TextView>(R.id.dialog_cancel)?.text = it
            }
            params.rightButtonName?.let {
                findViewById<TextView>(R.id.dialog_confirm)?.text = it
            }
        }
        params.onOperateListener?.let {
            setOnOperateListener(it)
        }
        dialog?.window?.let {
            it.attributes.gravity = params.dialogGravity
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.let {
            if (params.dialogWidth == -1)
                it.attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            else
                it.attributes.width = (ScreenUtils.getScreenWidth() * 0.85).toInt()
        }
    }

    interface OnOperateListener {
        fun onClickRight(dialog:AylaBaseDialog)
        fun onClickLeft(dialog:AylaBaseDialog)
    }

}

open class BaseDialogParams {
    var dialogGravity = Gravity.CENTER
    var title = ""
    var leftButtonName: String? = null
    var rightButtonName: String? = null
    var rightButtonColor: Int? = null
    var leftButtonColor: Int? = null
    var showLeftButton = true
    var onOperateListener: AylaBaseDialog.OnOperateListener? = null
    var dialogWidth = 0
}