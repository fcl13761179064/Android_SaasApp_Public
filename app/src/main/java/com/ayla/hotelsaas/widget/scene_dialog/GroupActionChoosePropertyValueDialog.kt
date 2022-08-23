package com.ayla.hotelsaas.widget.scene_dialog

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.action.GroupActionAdapter
import com.ayla.hotelsaas.bean.DeviceTemplateBean
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.events.GroupActionDoneEvent
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.interfaces.HandleItemGroupPropertyListener
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListenerAdapter
import org.greenrobot.eventbus.EventBus

class GroupActionChoosePropertyValueDialog : DialogFragment() {
    private var attributesBean: DeviceTemplateBean.AttributesBean? = null
    private var handleItemGroupPropertyListener: HandleItemGroupPropertyListener? = null

    fun setHandleItemGroupPropertyListener(listener: HandleItemGroupPropertyListener) {
        handleItemGroupPropertyListener = listener
    }


    private val mAdapter: GroupActionAdapter by lazy {
        var mAdapter = GroupActionAdapter(R.layout.item_group_action)
        attributesBean?.let {
            if (it.value != null && it.value.size > 0) {
                if (it.value[0].setupBean != null) {
                    mAdapter = GroupActionAdapter(R.layout.item_group_next_action)
                }
            }
        }
        mAdapter
    }

    private var currentPos = -1

    companion object {
        fun newInstance(
            bean: DeviceTemplateBean.AttributesBean,
            action: BaseSceneBean.GroupAction?,
            currentPos: Int,
            edit: Boolean
        ): GroupActionChoosePropertyValueDialog {
            val args = Bundle()
            args.putSerializable(KEYS.GROUPBEAN, bean)
            args.putSerializable(KEYS.GROUPACTION, action)
            args.putInt(KEYS.POS, currentPos)
            args.putBoolean(KEYS.EDIT, edit)
            val fragment = GroupActionChoosePropertyValueDialog()
            fragment.arguments = args
            return fragment
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
        return inflater.inflate(R.layout.dialog_choose_group_property, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let {
            it.attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        }
        attributesBean =
            arguments?.getSerializable(KEYS.GROUPBEAN) as DeviceTemplateBean.AttributesBean
        val action = arguments?.getSerializable(KEYS.GROUPACTION) as BaseSceneBean.GroupAction?
        val edit = arguments?.getBoolean(KEYS.EDIT, false) ?: false
        currentPos = arguments?.getInt(KEYS.POS) ?: 0
        view.findViewById<View>(R.id.dialog_cancel).setOnClickListener {
            dismissAllowingStateLoss()

        }
        view.findViewById<View>(R.id.dialog_confirm).setOnClickListener {
            dismissAllowingStateLoss()
            //单选属性
            getSelectData()?.let { data ->
                handleItemGroupPropertyListener?.onUpdateValue(
                    currentPos,
                    data
                )
            }

        }
        view.findViewById<TextView>(R.id.dialog_title)?.text = attributesBean?.displayName
        val rvView = view.findViewById<RecyclerView>(R.id.rv)
        rvView.layoutManager = LinearLayoutManager(context)
        rvView.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val size = SizeUtils.dp2px(10f)
                val position = parent.getChildAdapterPosition(view)
                outRect[0, if (position == 0) size else 0, 0] = size
            }
        })
        rvView.adapter = mAdapter

        if (action != null) {
            val split = action.rightValue.split(";")
            if (split.size == 2 && split[1].length > 1 && split[1].contains(":")) {
                val valueStr = split[1].substring(1, split[1].length - 1)
                val valueArray = valueStr.split(":")
                if (valueArray.size == 2 && valueArray[0].length > 2) {
                    action.abilitySubCode = valueArray[0].substring(1, valueArray[0].length - 1)
                }
            }
        }

        attributesBean?.let {
            if (it.callBackBean != null && it.callBackBean is ISceneSettingFunctionDatumSet.ValueCallBackBean) {
                for (item in it.value) {
                    if (TextUtils.equals(
                            item.value,
                            (it.callBackBean as ISceneSettingFunctionDatumSet.ValueCallBackBean).valueBean.value
                        )
                    ) {
                        item.isCheck = true
                    }
                }
            } else if (action != null) {
                if (action.abilitySubCode == null) {
                    for (item in it.value) {
                        val split = action.rightValue.split(";")
                        if (split.size == 2) {
                            if (TextUtils.equals(split[1], item.value)) {
                                item.isCheck = true
                                item.version = split[1]
                            }
                        }
                    }
                } else if (action.abilitySubCode == "l" || action.abilitySubCode == "t"
                    || action.abilitySubCode == "h" || action.abilitySubCode == "s"
                    || action.abilitySubCode == "b"
                ) {
                    var valueBean: DeviceTemplateBean.AttributesBean.ValueBean? = null
                    for (item in it.value) {
                        if (item.abilitySubCode == action.abilitySubCode) {
                            valueBean = item
                            break
                        }
                    }
                    val split = action.rightValue.split(";")
                    if (split.size == 2 && split[1].length > 1 && split[1].contains(":")) {
                        val valueStr = split[1].substring(1, split[1].length - 1)
                        val valueArray = valueStr.split(":")
                        var targetValue = ""
                        if (valueArray.size == 2) {
                            targetValue = valueArray[1]
                        }
                        val setupCallBackBean = ISceneSettingFunctionDatumSet.SetupCallBackBean(
                            "==",
                            targetValue,
                            valueBean?.setupBean,
                            valueBean?.setupBean?.unit
                        )
                        setupCallBackBean.version = split[0]
                        setupCallBackBean.displayName = valueBean?.displayName
                        setupCallBackBean.abilitySubCode = action.abilitySubCode
                        handleItemGroupPropertyListener?.onShowChooseGroupItemValueDialog(
                            setupCallBackBean
                        )
                        if (edit) dismissAllowingStateLoss()
//                        showSetValueDialog.setupCallBackBean = setupCallBackBean
//                        showSetValueDialog.show(fm, "")
                    } else {
                        Log.e("GROUPACTION", "onViewCreated: Value值错误")
                    }

                } else if (action.abilitySubCode == "duration") {
                    var valueBean: DeviceTemplateBean.AttributesBean.ValueBean? = null
                    for (item in it.value) {
                        if (item.abilitySubCode == action.abilitySubCode) {
                            valueBean = item
                            break
                        }
                    }
                    val split = action.rightValue.split(";")
                    if (split.size == 2 && split[1].length > 1 && split[1].contains(":")) {
                        val valueStr = split[1].substring(1, split[1].length - 1)
                        val valueArray = valueStr.split(":")
                        var targetValue = ""
                        if (valueArray.size == 2) {
                            targetValue = valueArray[1]
                        }
                        if (valueBean != null) {
                            handleItemGroupPropertyListener?.onShowInputValueDialog(
                                valueBean,
                                targetValue
                            )
                            if (edit) dismissAllowingStateLoss()
//                            showSetNumberValueDialog(
//                                action.abilitySubCode,
//                                targetValue,
//                                valueBean,
//                                true
//                            )
                        }
                    }
                }
            } else if (it.value.size > 0) {
                it.value[0].isCheck = true
            }

            mAdapter.setNewData(it.value)
        }
        mAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                val item = adapter.getItem(position) as DeviceTemplateBean.AttributesBean.ValueBean
                if (item.setupBean == null) {
                    for (i in adapter.data.indices) {
                        val bean = adapter.getItem(i) as DeviceTemplateBean.AttributesBean.ValueBean
                        bean.isCheck = i == position
                    }
                    mAdapter.notifyDataSetChanged()
                } else {
                    var targetValue = ""
                    if (attributesBean?.callBackBean != null && attributesBean?.callBackBean is ISceneSettingFunctionDatumSet.SetupCallBackBean) {
                        val setupCallBackBean =
                            attributesBean?.callBackBean as ISceneSettingFunctionDatumSet.SetupCallBackBean
                        if (setupCallBackBean.displayName == item.displayName)
                            targetValue =
                                setupCallBackBean.targetValue
                    }
                    val setupCallBackBean = ISceneSettingFunctionDatumSet.SetupCallBackBean(
                        "==",
                        targetValue,
                        item.setupBean,
                        item.setupBean.unit
                    )
                    setupCallBackBean.displayName = item.displayName
                    if (item.setupBean.unit == "ms") {
                        item.value = targetValue
                        handleItemGroupPropertyListener?.onShowInputValueDialog(item, targetValue)
                        if (edit) dismissAllowingStateLoss()
//                        showSetNumberValueDialog(
//                            item.abilitySubCode,
//                            targetValue,
//                            item,
//                            false
//                        )
                    } else {
                        setupCallBackBean.abilitySubCode = item.abilitySubCode
                        setupCallBackBean.version = item.version
                        handleItemGroupPropertyListener?.onShowChooseGroupItemValueDialog(
                            setupCallBackBean
                        )
                        if (edit) dismissAllowingStateLoss()
//                        showSetValueDialog.setupCallBackBean =
//                            setupCallBackBean
//                        showSetValueDialog.show(fm, "")
                    }

                }

            }

    }

    fun getSelectData(): ISceneSettingFunctionDatumSet.ValueCallBackBean? {
        for (i in mAdapter.data.indices) {
            val bean = mAdapter.getItem(i) as DeviceTemplateBean.AttributesBean.ValueBean
            if (bean.isCheck) {
                return ISceneSettingFunctionDatumSet.ValueCallBackBean(bean);
            }
        }
        return null
    }

}