/*
 * create by cairurui on 8/1/19 2:42 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */
package com.ayla.hotelsaas.widget.common_dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.ayla.hotelsaas.R
import com.blankj.utilcode.util.SizeUtils

class NpCustomSheet : DialogFragment() {
    private var textColor = DEFAULT_TEXT_COLOR
    private var mDimEnabled = DEFAULT_DIMENABLED
    private var mTexts = arrayOf<String>()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textColor = arguments!!.getInt(EXTRA_TEXT_COLOR, DEFAULT_TEXT_COLOR)
        mDimEnabled = arguments!!.getBoolean(EXTRA_DIMENABLED, DEFAULT_DIMENABLED)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = activity?.let {
            Dialog(
                it,
                if (mDimEnabled) R.style.BottomDialogDim else R.style.BottomDialog
            )
        }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        initView(dialog!!)
        dialog?.setCanceledOnTouchOutside(true)
        val window = dialog.window
        if (window != null) {
            val params = window!!.attributes
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            params.windowAnimations = R.style.main_menu_animstyle
            params.width = resources.displayMetrics.widthPixels
            params.gravity = Gravity.BOTTOM
            dialog!!.window!!.attributes = params
        }
        return dialog
    }

    private fun initView(dialog: Dialog) {
        val contentView = LinearLayout(context)
        contentView.setBackgroundResource(R.drawable.actionsheet_single_selector)
        contentView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        contentView.orientation = LinearLayout.VERTICAL
        val padding = SizeUtils.dp2px(16f)
        contentView.setPadding(0, 0, 0, 0)
        dialog.setContentView(contentView)
        val onClickListener = View.OnClickListener { v ->
            dismissAllowingStateLoss()
            val tag = v.tag
            if (tag is Int && mCallBack != null) {
                mCallBack!!.callback(tag)
            }
        }
        for (i in mTexts.indices) {
            val text = mTexts[i]
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER
            textView.textSize = 16f
            textView.text = text
            textView.setTextColor(textColor)
            val layoutParams1 =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(46f))
            layoutParams1.gravity = Gravity.CENTER
            textView.layoutParams = layoutParams1
            if (mTexts.size == 1) {
                textView.setBackgroundResource(R.drawable.actionsheet_single_selector)
            } else if (i == 0) {
                textView.setBackgroundResource(R.drawable.actionsheet_top_selector)
            } else if (i == mTexts.size - 1) {
                textView.setBackgroundResource(R.drawable.actionsheet_center_selector)
            } else {
                textView.setBackgroundResource(R.drawable.actionsheet_center_selector)
            }
            if (i > 0) {
                val line = View(context)
                line.setBackgroundColor(resources.getColor(R.color.colorBackground))
                val layoutParams2 = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    SizeUtils.dp2px(1f)
                )
                layoutParams2.leftMargin = 100
                layoutParams2.rightMargin = 100
                contentView.addView(line, layoutParams2)
            }
            textView.tag = i
            textView.setOnClickListener(onClickListener)
            contentView.addView(textView)
        }

        val line = View(context)
        line.setBackgroundColor(resources.getColor(R.color.colorBackground))
        val layoutParams2 =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(10f))
        contentView.addView(line, layoutParams2)

        val cancelTextView = TextView(context)
        cancelTextView.gravity = Gravity.CENTER_HORIZONTAL
        cancelTextView.textSize = 16f
        cancelTextView.setPadding(0, 40, 0, 40)
        cancelTextView.setText(R.string.cancel)
        cancelTextView.setTextColor(textColor)
        cancelTextView.setBackgroundResource(R.drawable.actionsheet_center_selector)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutParams.gravity = Gravity.CENTER
        cancelTextView.layoutParams = layoutParams
        contentView.addView(cancelTextView)
        cancelTextView.setOnClickListener {
            dismissAllowingStateLoss()
            if (null != mCallBack) {
                mCallBack!!.onCancel()
            }
        }
    }

    private var mCallBack: CallBack? = null

    interface CallBack {
        fun callback(index: Int)
        fun onCancel()
    }

    class Builder(val mActivity: FragmentActivity) {
        var mCustomSheet: NpCustomSheet
        fun setTextColor(color: Int): Builder {
            mCustomSheet.textColor = color
            return this
        }

        fun setText(vararg texts: String?): Builder? {
            mCustomSheet.mTexts = texts as Array<String>
            return this
        }

        fun setChooseItems(items: List<String>): Builder {
            mCustomSheet.mTexts = items.toTypedArray()
            return this
        }

        fun dimEnabled(enable: Boolean): Builder {
            mCustomSheet.mDimEnabled = enable
            return this
        }

        fun show(callBack: CallBack?): NpCustomSheet {
            mCustomSheet.mCallBack = callBack
            val bundle = Bundle()
            bundle.putBoolean(EXTRA_DIMENABLED, mCustomSheet.mDimEnabled)
            bundle.putInt(EXTRA_TEXT_COLOR, mCustomSheet.textColor)
            mCustomSheet.arguments = bundle
            val fm = mActivity.supportFragmentManager
            val ft = fm.beginTransaction()
            val fragment = fm.findFragmentByTag("CustomSheet")
            if (fragment != null) {
                ft.remove(fragment)
            }
            ft.addToBackStack(null)
            mCustomSheet.show(ft, "CustomSheet")
            return mCustomSheet
        }


        init {
            mCustomSheet = NpCustomSheet()
        }
    }

    companion object {
        const val EXTRA_DIMENABLED = "dimEnabled"
        const val EXTRA_TEXT_COLOR = "text_color"
        const val DEFAULT_TEXT_COLOR = Color.BLACK
        const val DEFAULT_DIMENABLED = true
    }
}