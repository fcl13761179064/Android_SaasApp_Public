package com.ayla.hotelsaas.page.weidge

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.ayla.hotelsaas.R

open class SceneStoreSiteDialog : DialogFragment() {
    private var editValue: String? = null
    fun setEditValue(editValue: String?): SceneStoreSiteDialog {
        this.editValue = editValue
        return this
    }

    private var inputType = InputType.text
    fun setInputType(inputType: InputType): SceneStoreSiteDialog {
        this.inputType = inputType
        return this
    }

    private var title: String? = null
    fun setTitle(title: String?): SceneStoreSiteDialog {
        this.title = title
        return this
    }

    private var maxLength = 20
    fun setMaxLength(maxLength: Int): SceneStoreSiteDialog {
        this.maxLength = maxLength
        return this
    }

    private var editHint: String? = null
    fun setEditHint(editHint: String?): SceneStoreSiteDialog {
        this.editHint = editHint
        return this
    }

    override fun onStart() {
        super.onStart()
        val params = dialog!!.window!!.attributes
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        params.windowAnimations = R.style.main_menu_animstyle
        params.width = resources.displayMetrics.widthPixels
        params.gravity = Gravity.BOTTOM
        dialog!!.window!!.attributes = params
    }

    private var doneCallback: DoneCallback? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.scene_storesite_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ConstraintLayout>(R.id.constraintLayout).setOnClickListener {
            if (doneCallback != null) {
                dialog?.dismiss()
                doneCallback!!.localStoreSite()
            }
        }
        view.findViewById<ConstraintLayout>(R.id.constraintLayout_two).setOnClickListener {
            if (doneCallback != null) {
                dialog?.dismiss()
                doneCallback!!.cloudSerivedialog()
            }
        }
        if (!TextUtils.isEmpty(title)) {
            view.findViewById<TextView>(R.id.title).setText(title)
        }
        view.findViewById<View>(R.id.cancel).setOnClickListener { dismissAllowingStateLoss() }
    }

    enum class InputType(val value: Int) {
        numberSigned(0x00001002), text(0x00000001), numberSignedDecimal(0x00003002);
    }

    interface DoneCallback {
        fun localStoreSite()
        fun cloudSerivedialog()
    }

    companion object {
        fun newInstance(doneCallback: DoneCallback?): SceneStoreSiteDialog {
            val args = Bundle()
            val fragment = SceneStoreSiteDialog()
            fragment.arguments = args
            fragment.doneCallback = doneCallback
            return fragment
        }
    }
}