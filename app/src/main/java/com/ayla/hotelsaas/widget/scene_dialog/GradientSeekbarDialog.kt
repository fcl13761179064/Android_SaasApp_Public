package com.ayla.hotelsaas.widget.scene_dialog

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.FragmentManager
import com.ayla.hotelsaas.R

/**
 * 渐变色滑动选择值
 */

class GradientSeekbarDialog : AylaBaseDialog() {
    override fun getLayoutView(inflater: LayoutInflater): View? =
        inflater.inflate(R.layout.dialog_gradient_seek_bar, null)

    override fun initView(view: View) {
        val seekbarValue = view.findViewById<TextView>(R.id.dialog_seekbar_value)
        if (params is GradientSeekBarParams) {
            val sp = params as GradientSeekBarParams
            view.findViewById<TextView>(R.id.dialog_gradient_seekbar_left_tv)?.text = sp.leftTxt
            view.findViewById<TextView>(R.id.dialog_gradient_seekbar_right_tv)?.text = sp.rightTxt
            view.findViewById<TextView>(R.id.dialog_seekbar_tips).text =
                sp.tips
            if (sp.selectValue == 0)
                sp.selectValue = sp.min
            seekbarValue?.let {
                if (sp.selectValue >= sp.min && sp.selectValue <= sp.max)
                    seekbarValue.text = sp.selectValue.toString()
            }
            val rightImageRes = sp.rightImageRes
            if (rightImageRes != 0)
                view.findViewById<ImageView>(R.id.dialog_seekbar_right_iv)
                    .setImageResource(rightImageRes)
            val seekBar = view.findViewById<AppCompatSeekBar>(R.id.dialog_seekbar)
            seekBar?.max = sp.max - sp.min
            if (sp.selectValue >= sp.min && sp.selectValue <= sp.max)
                seekBar?.progress = sp.selectValue - sp.min
            seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val value = sp.min + progress
                    sp.selectValue = value
                    seekbarValue?.text = value.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }


    }

    class GradientSeekbarBuilder {
        var params = GradientSeekBarParams()

        init {
            params.dialogGravity = Gravity.BOTTOM
            params.dialogWidth = -1
        }

        fun setTitle(title: String): GradientSeekbarBuilder {
            params.title = title
            return this
        }

        fun setShowLeftButton(show: Boolean): GradientSeekbarBuilder {
            params.showLeftButton = show
            return this
        }

        fun setLeftButtonColor(color: Int): GradientSeekbarBuilder {
            params.leftButtonColor = color
            return this
        }

        fun setRightButtonColor(color: Int): GradientSeekbarBuilder {
            params.rightButtonColor = color
            return this
        }

        fun setLeftButtonName(name: String): GradientSeekbarBuilder {
            params.leftButtonName = name
            return this
        }

        fun setRightButtonName(name: String): GradientSeekbarBuilder {
            params.rightButtonName = name
            return this
        }

        fun setOperateListener(listener: OnOperateListener): GradientSeekbarBuilder {
            params.onOperateListener = listener
            return this
        }

        fun setDialogGravity(gravity: Int): GradientSeekbarBuilder {
            params.dialogGravity = gravity
            if (gravity == Gravity.BOTTOM)
                params.dialogWidth = -1
            return this
        }

        fun setValue(value: Int): GradientSeekbarBuilder {
            params.selectValue = value
            return this
        }

        fun setRightImageRes(resId: Int): GradientSeekbarBuilder {
            params.rightImageRes = resId
            return this
        }

        fun setTips(tips: String): GradientSeekbarBuilder {
            params.tips = tips
            return this
        }

        fun setRange(min: Int, max: Int): GradientSeekbarBuilder {
            params.max = max
            params.min = min
            return this
        }

        fun setRLTxt(left: String, right: String): GradientSeekbarBuilder {
            params.leftTxt = left
            params.rightTxt = right
            return this
        }

        fun show(manager: FragmentManager, tag: String): GradientSeekbarDialog {
            val dialog = GradientSeekbarDialog()
            dialog.params = params
            dialog.show(manager, tag)
            return dialog
        }

    }

    class GradientSeekBarParams : BaseDialogParams() {
        var max = 100
        var min = 0
        var selectValue = 0
        var rightImageRes = 0
        var tips = ""
        var leftTxt = ""
        var rightTxt = ""
    }
}