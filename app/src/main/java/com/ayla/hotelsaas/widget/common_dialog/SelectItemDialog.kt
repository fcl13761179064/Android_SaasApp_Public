package com.ayla.hotelsaas.widget.common_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.common_dialog.DialogSelectItem
import com.ayla.hotelsaas.adapter.common_dialog.DialogSelectItemAdapter
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.widget.NPItemDecoration
import com.blankj.utilcode.util.SizeUtils

class SelectItemDialog : DialogFragment() {
    private val selectItemAdapter = DialogSelectItemAdapter()
    private var onSelectItemListener: OnSelectItemListener? = null
    private val data = mutableListOf<DialogSelectItem>()

    fun setOnSelectItemListener(listener: OnSelectItemListener): SelectItemDialog {
        onSelectItemListener = listener
        return this
    }

    fun setData(newData: MutableList<DialogSelectItem>): SelectItemDialog {
        data.clear()
        data.addAll(newData)
        return this
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
            it.setWindowAnimations(R.style.FromBottomShow)
        }
        return inflater.inflate(R.layout.dialog_select_item, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.dialog_item_cancel).singleClick {
            dismissAllowingStateLoss()
        }
        val dialogItemRV = view.findViewById<RecyclerView>(R.id.dialog_item_rv)
        dialogItemRV.adapter = selectItemAdapter
        dialogItemRV.addItemDecoration(
            NPItemDecoration(
                SizeUtils.dp2px(20f).toFloat(),
                SizeUtils.dp2px(20f).toFloat()
            )
        )
        selectItemAdapter.setNewData(data)
        selectItemAdapter.setOnItemClickListener { adapter, view, position ->
            dismissAllowingStateLoss()
            onSelectItemListener?.onSelectItem(selectItemAdapter.getItem(position))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.let {
            it.attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    interface OnSelectItemListener {

        fun onSelectItem(item: DialogSelectItem?)

    }
}