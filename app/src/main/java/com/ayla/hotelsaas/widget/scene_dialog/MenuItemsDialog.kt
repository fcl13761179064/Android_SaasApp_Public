package com.ayla.hotelsaas.widget.scene_dialog

import android.media.MediaParser
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.scene_dialog.MenuItemAdapter
import com.ayla.hotelsaas.bean.dialog.BaseDialogItemData
import com.ayla.hotelsaas.interfaces.OnDialogItemClickListener
import com.ayla.hotelsaas.widget.NPItemDecoration
import com.blankj.utilcode.util.SizeUtils

/**
 * 菜单选项弹窗，可以点击进入到下一步
 */
data class MenuItemsData(override val id: String, var name: String, var content: String) :
    BaseDialogItemData(id)

class MenuItemsDialog : AylaBaseDialog() {
    override fun getLayoutView(inflater: LayoutInflater): View? =
        inflater.inflate(R.layout.dialog_menu_items, null)

    override fun initView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.dialog_menu_item_rv)
        rv.addItemDecoration(
            NPItemDecoration(
                SizeUtils.dp2px(12.0f).toFloat(),
                SizeUtils.dp2px(12.0f).toFloat()
            )
        )
        val menuItemAdapter = MenuItemAdapter()
        rv.adapter = menuItemAdapter
        if (params is MenuItemsParams)
            menuItemAdapter.setNewData((params as MenuItemsParams).data)
        menuItemAdapter.setOnItemClickListener { adapter, _, position ->
            if (params is MenuItemsParams) {
                val item = menuItemAdapter.getItem(position)
                if (item is MenuItemsData)
                    (params as MenuItemsParams).onDialogItemsClickListener?.onItemClick(item)

            }
        }
        val tipsView = view.findViewById<TextView>(R.id.dialog_menu_item_tips)
        tipsView.text = (params as MenuItemsParams).tips
    }

    fun updateItem(id: String, value: String) {
        val rv = view?.findViewById<RecyclerView>(R.id.dialog_menu_item_rv)
        rv?.let {
            val menuItemAdapter = rv.adapter as MenuItemAdapter
            for ((index, item) in menuItemAdapter.data.withIndex()) {
                if (item.id == id) {
                    item.content = value
                    menuItemAdapter.notifyItemChanged(index)
                    break
                }
            }
        }
    }

    fun getItemData(): List<MenuItemsData> {
        val rv = view?.findViewById<RecyclerView>(R.id.dialog_menu_item_rv)
        if (rv != null) {
            val menuItemAdapter = rv.adapter as MenuItemAdapter
            return menuItemAdapter.data
        }
        return mutableListOf()
    }


    class MenuItemsBuilder {
        var params = MenuItemsParams()

        init {
            params.dialogGravity = Gravity.BOTTOM
            params.dialogWidth = -1
        }

        fun setTitle(title: String): MenuItemsBuilder {
            params.title = title
            return this
        }

        fun setShowLeftButton(show: Boolean): MenuItemsBuilder {
            params.showLeftButton = show
            return this
        }

        fun setLeftButtonColor(color: Int): MenuItemsBuilder {
            params.leftButtonColor = color
            return this
        }

        fun setRightButtonColor(color: Int): MenuItemsBuilder {
            params.rightButtonColor = color
            return this
        }

        fun setLeftButtonName(name: String): MenuItemsBuilder {
            params.leftButtonName = name
            return this
        }

        fun setRightButtonName(name: String): MenuItemsBuilder {
            params.rightButtonName = name
            return this
        }

        fun setData(data: MutableList<MenuItemsData>): MenuItemsBuilder {
            params.data = data
            return this
        }

        fun setOperateListener(listener: AylaBaseDialog.OnOperateListener): MenuItemsBuilder {
            params.onOperateListener = listener
            return this
        }

        fun setOnDialogItemClickListener(listener: OnDialogItemClickListener<MenuItemsData>): MenuItemsBuilder {
            params.onDialogItemsClickListener = listener
            return this
        }

        fun setTips(tips: String): MenuItemsBuilder {
            params.tips = tips
            return this
        }

        fun setDialogGravity(gravity: Int): MenuItemsBuilder {
            params.dialogGravity = gravity
            if (gravity == Gravity.BOTTOM)
                params.dialogWidth = -1
            return this
        }

        fun show(manager: FragmentManager, tag: String): MenuItemsDialog {
            val dialog = MenuItemsDialog()
            dialog.params = params
            dialog.show(manager, tag)
            return dialog
        }

    }

    class MenuItemsParams : BaseDialogParams() {
        var data = mutableListOf<MenuItemsData>()
        var onDialogItemsClickListener: OnDialogItemClickListener<MenuItemsData>? = null
        var tips = ""
    }
}
