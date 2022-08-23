package com.ayla.hotelsaas.widget.scene_dialog

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.FragmentManager
import com.ayla.hotelsaas.R

/**
 * 滑动选择值 同时可以点击加减按钮设置值
 */

class SeekbarWithClickDialog : AylaBaseDialog() {
    override fun getLayoutView(inflater: LayoutInflater): View? =
        inflater.inflate(R.layout.dialog_seek_bar_with_click, null)

    override fun initView(view: View) {
        val seekbarValue = view.findViewById<TextView>(R.id.dialog_seekbar_value)
        if (params is SeekBarWithClickParams) {
            val sp = params as SeekBarWithClickParams
            val step = sp.step
            view.findViewById<TextView>(R.id.dialog_seekbar_tips).text =
                sp.tips
            if (sp.selectValue == 0)
                sp.selectValue = sp.min
            seekbarValue?.let {
                if (sp.selectValue >= sp.min && sp.selectValue <= sp.max)
                    seekbarValue.text = String.format("%s%s", sp.selectValue.toString(), sp.unit)
            }

            val seekBar = view.findViewById<AppCompatSeekBar>(R.id.dialog_seekbar)
            seekBar?.apply {
                max = sp.max - sp.min
                if (sp.selectValue >= sp.min && sp.selectValue <= sp.max)
                    progress = sp.selectValue - sp.min
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val value = sp.min + progress
                        sp.selectValue = value
                        seekbarValue?.text = String.format("%s%s", value.toString(), sp.unit)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }

            view.findViewById<View>(R.id.dialog_seekbar_add).setOnClickListener {
                val newValue = seekBar?.progress?.plus(step)?.plus(sp.min)
                if (newValue != null) {
                    if (newValue <= sp.max)
                        seekBar.progress = newValue - sp.min

                }

            }
            view.findViewById<View>(R.id.dialog_seekbar_reduce).setOnClickListener {
                val newValue = seekBar?.progress?.minus(step)?.plus(sp.min)
                if (newValue != null) {
                    if (newValue >= sp.min)
                        seekBar.progress = newValue - sp.min
                }

            }
        }


    }

    class SeekBarWithClickBuilder {
        var params = SeekBarWithClickParams()

        init {
            params.dialogGravity = Gravity.BOTTOM
            params.dialogWidth = -1
        }

        fun setTitle(title: String): SeekBarWithClickBuilder {
            params.title = title
            return this
        }

        fun setShowLeftButton(show: Boolean): SeekBarWithClickBuilder {
            params.showLeftButton = show
            return this
        }

        fun setLeftButtonColor(color: Int): SeekBarWithClickBuilder {
            params.leftButtonColor = color
            return this
        }

        fun setRightButtonColor(color: Int): SeekBarWithClickBuilder {
            params.rightButtonColor = color
            return this
        }

        fun setLeftButtonName(name: String): SeekBarWithClickBuilder {
            params.leftButtonName = name
            return this
        }

        fun setRightButtonName(name: String): SeekBarWithClickBuilder {
            params.rightButtonName = name
            return this
        }

        fun setOperateListener(listener: OnOperateListener): SeekBarWithClickBuilder {
            params.onOperateListener = listener
            return this
        }

        fun setDialogGravity(gravity: Int): SeekBarWithClickBuilder {
            params.dialogGravity = gravity
            if (gravity == Gravity.BOTTOM)
                params.dialogWidth = -1
            return this
        }

        fun setValue(value: Int): SeekBarWithClickBuilder {
            params.selectValue = value
            return this
        }

        fun setUnit(unit: String): SeekBarWithClickBuilder {
            params.unit = unit
            return this
        }

        fun setRange(min: Int, max: Int, step: Int = 0): SeekBarWithClickBuilder {
            params.max = max
            params.min = min
            params.step = step
            return this
        }

        fun setTips(tips: String): SeekBarWithClickBuilder {
            params.tips = tips
            return this
        }

        fun show(manager: FragmentManager, tag: String): SeekbarWithClickDialog {
            val dialog = SeekbarWithClickDialog()
            dialog.params = params
            dialog.show(manager, tag)
            return dialog
        }

    }

    class SeekBarWithClickParams : BaseDialogParams() {
        var max = 100
        var min = 0
        var step = 1
        var selectValue = 0
        var unit = ""
        var tips = ""
    }
}