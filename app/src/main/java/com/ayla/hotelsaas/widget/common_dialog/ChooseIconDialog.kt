package com.ayla.hotelsaas.widget.common_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.common_dialog.DialogChooseIconAdapter
import com.ayla.hotelsaas.adapter.common_dialog.DialogChooseIconItem
import com.ayla.hotelsaas.ext.singleClick


class ChooseIconDialog : DialogFragment() {
    private var title = ""
    private val dialogChooseIconAdapter = DialogChooseIconAdapter()
    private var onDialogSelectIconListener: OnDialogSelectIconListener? = null
    private var data = mutableListOf<DialogChooseIconItem>()
    private var selectPos = 0

    fun setTitle(title: String): ChooseIconDialog {
        this.title = title
        return this
    }

    fun setOnDialogSelectIconListener(listener: OnDialogSelectIconListener): ChooseIconDialog {
        onDialogSelectIconListener = listener
        return this
    }

    fun setData(
        data: MutableList<DialogChooseIconItem>,
        selectPos: Int = 0
    ): ChooseIconDialog {
        this.data.clear()
        this.data.addAll(data)
        this.selectPos = selectPos
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
        return inflater.inflate(R.layout.dialog_choose_icon, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.dialog_title).text = title
        val rv = view.findViewById<RecyclerView>(R.id.dialog_choose_icon_rv)
        rv.adapter = dialogChooseIconAdapter
        rv.layoutManager = GridLayoutManager(context, 3)
        dialogChooseIconAdapter.setOnItemClickListener { adapter, view, position ->
            if (selectPos != position) {
                val item = dialogChooseIconAdapter.getItem(selectPos)
                item?.check = false
                dialogChooseIconAdapter.notifyItemChanged(selectPos)
                selectPos = position
                val selectItem = dialogChooseIconAdapter.getItem(selectPos)
                selectItem?.check = true
                dialogChooseIconAdapter.notifyItemChanged(selectPos)
            }
        }
        dialogChooseIconAdapter.setNewData(data)
        view.findViewById<View>(R.id.dialog_choose_icon_cancel).singleClick {
            dismissAllowingStateLoss()
            onDialogSelectIconListener?.onCancel()
        }
        view.findViewById<View>(R.id.dialog_choose_icon_confirm).singleClick {
            dismissAllowingStateLoss()
            dialogChooseIconAdapter.getItem(selectPos)
                ?.let { onDialogSelectIconListener?.onConfirm(selectPos, it) }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.let {
            it.attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    interface OnDialogSelectIconListener {
        fun onConfirm(pos: Int, item: DialogChooseIconItem)
        fun onCancel()
    }

}