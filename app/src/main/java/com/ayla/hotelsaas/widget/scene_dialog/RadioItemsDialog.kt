package com.ayla.hotelsaas.widget.scene_dialog

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.scene_dialog.RadioItemAdapter
import com.ayla.hotelsaas.bean.dialog.BaseDialogItemData
import com.ayla.hotelsaas.interfaces.OnDialogItemClickListener

/**
 * 单选弹窗
 */
data class RadioItemData(override val id: String, val name: String, var check: Boolean = false) :
    BaseDialogItemData(id)

class RadioItemsDialog : AylaBaseDialog() {
    private var currentSelect = -1
    override fun getLayoutView(inflater: LayoutInflater): View? =
        inflater.inflate(R.layout.dialog_radio_items, null)

    override fun initView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.dialog_radio_item_rv)
        val radioItemAdapter = RadioItemAdapter()
        rv.adapter = radioItemAdapter
        val rp = params as RadioItemsParams
        for ((index, item) in rp.data.withIndex()) {
            if (item.check) {
                rp.selectData = item
                currentSelect = index
            }
        }
        radioItemAdapter.setNewData(rp.data)
        radioItemAdapter.setOnItemClickListener { adapter, _, position ->
            if (currentSelect == position) {
                if (rp.clickItemCancel) {
                    (adapter.getItem(position) as RadioItemData).check = false
                    rp.selectData = null
                    currentSelect = -1
                    adapter.notifyItemChanged(position)
                } else
                    return@setOnItemClickListener
            } else {
                if (currentSelect != -1 && adapter.getItem(currentSelect) is RadioItemData)
                    (adapter.getItem(currentSelect) as RadioItemData).check = false
                adapter.notifyItemChanged(currentSelect)
                currentSelect = position
                val item = adapter.getItem(position)
                if (item is RadioItemData) {
                    item.check = true
                    (params as RadioItemsParams).selectData = item
                }
                adapter.notifyItemChanged(currentSelect)
            }
        }
    }

    class RadioItemsBuilder {
        var params = RadioItemsParams()

        init {
            params.dialogGravity = Gravity.BOTTOM
            params.dialogWidth = -1
        }

        fun setTitle(title: String): RadioItemsBuilder {
            params.title = title
            return this
        }

        fun setShowLeftButton(show: Boolean): RadioItemsBuilder {
            params.showLeftButton = show
            return this
        }

        fun setLeftButtonColor(color: Int): RadioItemsBuilder {
            params.leftButtonColor = color
            return this
        }

        fun setRightButtonColor(color: Int): RadioItemsBuilder {
            params.rightButtonColor = color
            return this
        }

        fun setLeftButtonName(name: String): RadioItemsBuilder {
            params.leftButtonName = name
            return this
        }

        fun setRightButtonName(name: String): RadioItemsBuilder {
            params.rightButtonName = name
            return this
        }

        fun setData(data: MutableList<RadioItemData>): RadioItemsBuilder {
            params.data = data
            return this
        }

        fun setOperateListener(listener: OnOperateListener): RadioItemsBuilder {
            params.onOperateListener = listener
            return this
        }

        fun setClickItemCancel(clickItemCancel: Boolean): RadioItemsBuilder {
            params.clickItemCancel = clickItemCancel
            return this
        }


        fun setDialogGravity(gravity: Int): RadioItemsBuilder {
            params.dialogGravity = gravity
            if (gravity == Gravity.BOTTOM)
                params.dialogWidth = -1
            return this
        }

        fun show(manager: FragmentManager, tag: String): RadioItemsDialog {
            val dialog = RadioItemsDialog()
            dialog.params = params
            dialog.show(manager, tag)
            return dialog
        }

    }

    class RadioItemsParams : BaseDialogParams() {
        var data = mutableListOf<RadioItemData>()
        var selectData: RadioItemData? = null

        //是否可以点击选中的项进行取消
        var clickItemCancel = false
    }
}