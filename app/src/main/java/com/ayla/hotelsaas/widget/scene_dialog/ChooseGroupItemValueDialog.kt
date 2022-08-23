package com.ayla.hotelsaas.widget.scene_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.interfaces.ChoosePropertyValueListener
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet
import com.xw.repo.BubbleSeekBar

class ChooseGroupItemValueDialog : DialogFragment() {
    var setupCallBackBean: ISceneSettingFunctionDatumSet.SetupCallBackBean? = null
    var choosePropertyValueListener: ChoosePropertyValueListener? = null
    var currentPos = -1
    var groupActionChoosePropertyValueDialog: GroupActionChoosePropertyValueDialog? = null

    companion object {
        fun newInstance(
            pos: Int,
            setupCallBackBean: ISceneSettingFunctionDatumSet.SetupCallBackBean,
            choosePropertyValueListener: ChoosePropertyValueListener,
            groupActionChoosePropertyValueDialog: GroupActionChoosePropertyValueDialog
        ): ChooseGroupItemValueDialog {
            val chooseGroupItemValueDialog = ChooseGroupItemValueDialog()
            chooseGroupItemValueDialog.setupCallBackBean = setupCallBackBean
            chooseGroupItemValueDialog.choosePropertyValueListener = choosePropertyValueListener
            chooseGroupItemValueDialog.currentPos = pos
            chooseGroupItemValueDialog.groupActionChoosePropertyValueDialog =
                groupActionChoosePropertyValueDialog
            return chooseGroupItemValueDialog
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.let {
            it.attributes.gravity = Gravity.BOTTOM
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog?.setCanceledOnTouchOutside(false)
        return inflater.inflate(R.layout.dialog_choose_group_action_value, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initData()
    }

    private fun initView(view: View) {
        view.findViewById<View>(R.id.v_cancel).singleClick {
            dismissAllowingStateLoss()
        }
        view.findViewById<View>(R.id.v_done).singleClick {
            dismissAllowingStateLoss()
            groupActionChoosePropertyValueDialog?.dismissAllowingStateLoss()
            choosePropertyValueListener?.onUpdate(currentPos, getValue())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.let {
            it.attributes.width = WindowManager.LayoutParams.MATCH_PARENT

        }
    }

    private fun initData() {
        setupCallBackBean?.let { callBackBean ->
            if (callBackBean.setupBean != null) {
                view?.let {
                    val rlLayout = it.findViewById<View>(R.id.rl_k_sat_layout)
                    val seekBarK = it.findViewById<SeekBar>(R.id.seek_bar_k)
                    val lightBar = it.findViewById<BubbleSeekBar>(R.id.seek_bar_light)
                    val tvValue = it.findViewById<TextView>(R.id.tv_value)
                    val tvNotice = it.findViewById<TextView>(R.id.tv_notice)
                    it.findViewById<TextView>(R.id.tv_name)?.text = callBackBean.displayName
                    val min = callBackBean.setupBean.min
                    val max = callBackBean.setupBean.max
                    val step = callBackBean.setupBean.step
                    if (callBackBean.unit == "%" || TextUtils.isEmpty(callBackBean.unit)) {
                        //亮度
                        rlLayout.visibility = View.GONE

                        seekBarK.visibility = View.GONE
                        tvNotice.visibility = View.GONE
                        lightBar.visibility = View.VISIBLE
                        lightBar.onProgressChangedListener = object :
                            BubbleSeekBar.OnProgressChangedListenerAdapter() {
                            override fun onProgressChanged(
                                bubbleSeekBar: BubbleSeekBar,
                                progress: Int,
                                progressFloat: Float,
                                fromUser: Boolean
                            ) {
                                tvValue.text = progress.toString()
                            }
                        }
                        lightBar.configBuilder
                            .min(min.toFloat())
                            .max(max.toFloat())
                            .sectionCount(java.lang.Double.valueOf((max - min) / step).toInt())
                            .sectionTextColor(Color.parseColor("#648C1A"))
                            .trackColor(Color.parseColor("#DFE4EB"))
                            .hideBubble()
                            .build()
                        try {
                            if (TextUtils.isEmpty(callBackBean.targetValue)) {
                                lightBar.setProgress(min.toFloat())
                                tvValue.text = min.toInt().toString()
                            } else {
                                tvValue.text = callBackBean.targetValue.toFloat().toString()
                                lightBar.setProgress(callBackBean.targetValue.toFloat())
                            }
                        } catch (e: Exception) {

                        }

                    } else if ("K".equals(callBackBean.unit, true)) {
                        //色温
                        rlLayout.visibility = View.VISIBLE
                        seekBarK.visibility = View.VISIBLE
                        lightBar.visibility = View.GONE

                        seekBarK.max = max.toInt()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            seekBarK.min = min.toInt()
                        try {
                            if (TextUtils.isEmpty(callBackBean.targetValue)) {
                                seekBarK.progress = min.toInt()
                                tvValue.text = min.toInt().toString()
                            } else {
                                seekBarK.progress = callBackBean.targetValue.toInt()
                                tvValue.text = callBackBean.targetValue.toInt().toString()
                            }
                        } catch (e: Exception) {

                        }
                        seekBarK.setOnSeekBarChangeListener(object :
                            SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                tvValue.text = progress.toString()
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar) {}
                            override fun onStopTrackingTouch(seekBar: SeekBar) {}
                        })
                        tvNotice.visibility = View.VISIBLE
                        tvNotice.text = "色温调节仅在白光模式下有效"
                    }
                }
            }
        }
    }

    fun getValue(): ISceneSettingFunctionDatumSet.SetupCallBackBean? {
        val tvValue = view?.findViewById<TextView>(R.id.tv_value)
        if (tvValue != null) {
            setupCallBackBean?.unit = setupCallBackBean?.displayName + ":" +
                    tvValue.text.toString() + setupCallBackBean?.setupBean?.unit
            setupCallBackBean?.targetValue = tvValue.text.toString()
            return setupCallBackBean
        }
        return null
    }
}