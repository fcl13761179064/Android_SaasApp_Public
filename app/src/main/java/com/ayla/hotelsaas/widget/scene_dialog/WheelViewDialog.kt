package com.ayla.hotelsaas.widget.scene_dialog

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.bean.dialog.BaseDialogItemData
import com.blankj.utilcode.util.SizeUtils
import com.zyyoona7.wheel.IWheelEntity
import com.zyyoona7.wheel.WheelView

/**
 * 用于一些提示的弹窗
 */
class WheelViewItemData(override val id: String, val name: String) : BaseDialogItemData(id),
    IWheelEntity {
    override fun getWheelText(): String = name

}

class WheelViewDialog : AylaBaseDialog() {

    override fun getLayoutView(inflater: LayoutInflater): View? =
        inflater.inflate(R.layout.dialog_wheel_view, null)


    override fun initView(view: View) {
        val wheelView = view.findViewById<WheelView<WheelViewItemData>>(R.id.dialog_wheel_view)
        if (params is WheelViewDialogParams) {
            val sp = params as WheelViewDialogParams
            wheelView.data = sp.data
            wheelView.isCyclic = true
            wheelView.isCurved = false
            wheelView.visibleItems = 3
            wheelView.normalItemTextColor = Color.parseColor("#A9A9A9")
            wheelView.selectedItemTextColor = Color.parseColor("#3C3C3E")
            sp.selectData?.let {
                var selectIndex = 0
                for ((index, item) in sp.data.withIndex()) {
                    if (item.name == it.name)
                        selectIndex = index
                }
                wheelView.setSelectedItemPosition(selectIndex)
            }
            wheelView.textSize = SizeUtils.sp2px(20f).toFloat()
            wheelView.lineSpacing = SizeUtils.dp2px(16.0f).toFloat()
            wheelView.onItemSelectedListener =
                WheelView.OnItemSelectedListener { _, data, _ ->
                    sp.selectData = data
                }
        }
    }

    class WheelViewDialogBuilder {
        var params = WheelViewDialogParams()

        fun setTitle(title: String): WheelViewDialogBuilder {
            params.title = title
            return this
        }

        fun setShowLeftButton(show: Boolean): WheelViewDialogBuilder {
            params.showLeftButton = show
            return this
        }

        fun setLeftButtonColor(color: Int): WheelViewDialogBuilder {
            params.leftButtonColor = color
            return this
        }

        fun setRightButtonColor(color: Int): WheelViewDialogBuilder {
            params.rightButtonColor = color
            return this
        }

        fun setLeftButtonName(name: String): WheelViewDialogBuilder {
            params.leftButtonName = name
            return this
        }

        fun setRightButtonName(name: String): WheelViewDialogBuilder {
            params.rightButtonName = name
            return this
        }


        fun setOperateListener(listener: AylaBaseDialog.OnOperateListener): WheelViewDialogBuilder {
            params.onOperateListener = listener
            return this
        }

        fun setData(data: MutableList<WheelViewItemData>): WheelViewDialogBuilder {
            params.data = data
            return this
        }

        fun setValue(value: WheelViewItemData): WheelViewDialogBuilder {
            params.selectData = value
            return this
        }

        fun setDialogGravity(gravity: Int): WheelViewDialogBuilder {
            params.dialogGravity = gravity
            if (gravity == Gravity.BOTTOM)
                params.dialogWidth = -1
            return this
        }

        fun show(manager: FragmentManager, tag: String): WheelViewDialog {
            val dialog = WheelViewDialog()
            dialog.params = params
            dialog.show(manager, tag)
            return dialog
        }

    }

    class WheelViewDialogParams : BaseDialogParams() {
        var data = mutableListOf<WheelViewItemData>()
        var selectData: WheelViewItemData? = null
    }
}
