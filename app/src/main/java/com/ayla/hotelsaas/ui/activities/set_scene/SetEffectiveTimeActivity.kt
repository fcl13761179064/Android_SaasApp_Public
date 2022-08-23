package com.ayla.hotelsaas.ui.activities.set_scene

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean.EnableTime
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.widget.common_dialog.SelectTimeDialog
import kotlinx.android.synthetic.main.activity_set_effective_time.*
import java.util.*

class SetEffectiveTimeActivity : BasicActivity() {
    private var enableTime: EnableTime? = null
    private val selectStartTimeDialog by lazy {
        SelectTimeDialog(this, "开始时间", true, true, false)
    }
    private val selectEndTimeDialog by lazy {
        SelectTimeDialog(this, "结束时间", true, true, false)
    }

    override fun getLayoutId(): Int = R.layout.activity_set_effective_time

    override fun getLayoutView(): View? = null

    override fun initView() {
        enableTime = intent.getSerializableExtra("enableTime") as EnableTime?
        enableTime?.let {
            tv_repeat_day.text = formatRepeatDay(it.enableWeekDay)
            select_day.isChecked = it.isAllDay
            layout_set_time.visibility = if (it.isAllDay) View.GONE else View.VISIBLE
            if (it.isAllDay.not()) {
                val startTime =
                    String.format(Locale.CHINA, "%02d:%02d", it.startHour, it.startMinute)
                tv_start_time.text = startTime
                val endTime = String.format(Locale.CHINA, "%02d:%02d", it.endHour, it.endMinute)
                tv_end_time.text = endTime
            }
        }
    }

    override fun initListener() {
        selectStartTimeDialog.setOnConfirmSelectTimeListener { hour, min, _ ->
            val time = String.format(Locale.CHINA, "%02d:%02d", hour, min)
            tv_start_time.text = time
            enableTime?.startHour = hour
            enableTime?.startMinute = min
        }
        selectEndTimeDialog.setOnConfirmSelectTimeListener { hour, min, second ->
            val time = String.format(Locale.CHINA, "%02d:%02d", hour, min)
            enableTime?.let {
                if (hour < it.startHour)
                    CustomToast.makeText(this, "结束时间不能早于开始时间", R.drawable.ic_toast_warning)
                else if (hour == it.startHour && min <= it.startMinute)
                    CustomToast.makeText(this, "结束时间不能早于开始时间", R.drawable.ic_toast_warning)
                else {
                    tv_end_time.text = time
                    enableTime?.endHour = hour
                    enableTime?.endMinute = min
                }
            }

        }
        tv_repeat_day.singleClick {
            val intent = Intent(this, SceneSettingRepeatDataActivity::class.java)
            enableTime?.let {
                intent.putExtra("enable_position", it.enableWeekDay)
            }
            startActivityForResult(intent, 999)
        }

        select_day.setOnCheckedChangeListener { _, isChecked ->
            layout_set_time.visibility = if (isChecked) View.GONE else View.VISIBLE
            enableTime?.isAllDay = isChecked
            enableTime?.startHour = 0
            enableTime?.endMinute = 0
            if (isChecked.not()) {
                tv_start_time.text = "00:00"
                tv_end_time.text = "00:00"
            }
        }

        tv_start_time.singleClick {
            val timeStr = tv_start_time.text
            val timeArray = timeStr.split(":")
            selectStartTimeDialog.show()
            if (timeArray.size == 2)
                selectStartTimeDialog.setHourMinValue(timeArray[0].toInt(), timeArray[1].toInt())

        }
        tv_end_time.singleClick {
            val timeStr = tv_end_time.text
            val timeArray = timeStr.split(":")
            selectEndTimeDialog.show()
            if (timeArray.size == 2)
                selectEndTimeDialog.setHourMinValue(timeArray[0].toInt(), timeArray[1].toInt())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == RESULT_OK) {
            data?.let {
                val enableWeekDay = it.getIntArrayExtra("enable_position")
                enableTime?.enableWeekDay = enableWeekDay
                tv_repeat_day.text = enableWeekDay?.let { it1 -> formatRepeatDay(it1) }
            }

        }
    }

    private fun formatRepeatDay(days: IntArray): String {
        val sb = StringBuilder()
        if (days.size == 7) {
            sb.append("每天")
        } else {
            sb.append("周")
            for (i in days.indices) {
                when (days[i]) {
                    1 -> sb.append("一")
                    2 -> sb.append("二")
                    3 -> sb.append("三")
                    4 -> sb.append("四")
                    5 -> sb.append("五")
                    6 -> sb.append("六")
                    7 -> sb.append("日")
                }
                if (i < days.size - 1) {
                    sb.append(" ")
                }
            }
            if (TextUtils.equals("周一 二 三 四 五", sb.toString()))
                return "工作日"
        }
        return sb.toString()
    }

    override fun appBarRightTvClicked() {
        super.appBarRightTvClicked()
        //点击完成后
        val intent = Intent()
        intent.putExtra("enable_position", enableTime)
        enableTime?.let {
            if (it.isAllDay.not()) {
                if (it.startHour == 0 && it.endHour == 0 && it.endMinute == 0 && it.startMinute == 0) {
                    CustomToast.makeText(this, "结束时间不能早于开始时间", R.drawable.ic_toast_warning)
                    return@let
                }
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun appBarLeftIvClicked() {
        super.appBarLeftIvClicked()
        onBackPressed()
    }


}