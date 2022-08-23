package com.ayla.hotelsaas.widget.scene_dialog

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.ayla.hotelsaas.R

/**
 * 用于一些提示的弹窗
 */

class TooltipDialog : AylaBaseDialog() {

    override fun getLayoutView(inflater: LayoutInflater): View? =
        inflater.inflate(R.layout.dialog_tooltip, null)


    override fun initView(view: View) {
        val contentView = view.findViewById<TextView>(R.id.content)
        if (params is TooltipParams) {
            dialog?.setCanceledOnTouchOutside((params as TooltipParams).clickOutCancel)
            contentView.text = (params as TooltipParams).content
            contentView.gravity = (params as TooltipParams).contentGravity
        }
    }
}

class TooltipBuilder {
    var params = TooltipParams()

    fun setTitle(title: String): TooltipBuilder {
        params.title = title
        return this
    }

    fun setShowLeftButton(show: Boolean): TooltipBuilder {
        params.showLeftButton = show
        return this
    }

    fun setLeftButtonColor(color: Int): TooltipBuilder {
        params.leftButtonColor = color
        return this
    }

    fun setRightButtonColor(color: Int): TooltipBuilder {
        params.rightButtonColor = color
        return this
    }

    fun setLeftButtonName(name: String): TooltipBuilder {
        params.leftButtonName = name
        return this
    }

    fun setRightButtonName(name: String): TooltipBuilder {
        params.rightButtonName = name
        return this
    }


    fun setOperateListener(listener: AylaBaseDialog.OnOperateListener): TooltipBuilder {
        params.onOperateListener = listener
        return this
    }

    fun setContent(content: String): TooltipBuilder {
        params.content = content
        return this
    }

    fun setContentGravity(gravity: Int): TooltipBuilder {
        params.contentGravity = gravity
        return this
    }

    fun setDialogGravity(gravity: Int): TooltipBuilder {
        params.dialogGravity = gravity
        return this
    }

    fun setClickOutCancel(cancel: Boolean): TooltipBuilder {
        params.clickOutCancel = cancel
        return this
    }

    fun show(manager: FragmentManager, tag: String): TooltipDialog {
        val dialog = TooltipDialog()
        dialog.params = params
        dialog.show(manager, tag)
        return dialog
    }

}

class TooltipParams : BaseDialogParams() {
    var content = ""
    var contentGravity = Gravity.CENTER
    var clickOutCancel = true
}