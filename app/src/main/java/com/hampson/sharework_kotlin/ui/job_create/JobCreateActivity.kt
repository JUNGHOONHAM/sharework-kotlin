package com.hampson.sharework_kotlin.ui.job_create

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.databinding.ActivityJobCreateBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import java.util.*
import androidx.annotation.IntRange as AndroidIntRange
class JobCreateActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityJobCreateBinding

    private var userId: Int = -1
    private lateinit var sessionManagement: SessionManagement

    private val DEFAULT_INTERVAL = 10
    private val MINUTES_MIN = 0
    private val MINUTES_MAX = 60

    private var jobDate = ""
    private var startDate = ""
    private var endDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityJobCreateBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.textViewToolbarTitle.text = "일감 등록"

        sessionManagement = SessionManagement(this)
        userId = sessionManagement.getSessionID()

        // 주소 검색
        mBinding.buttonAddress.setOnClickListener {

        }

        // 날짜 선택
        mBinding.textViewDate.setOnClickListener {
            showDatePickerDialog()
        }

        // 시작 시간 선택
        mBinding.textViewStartTime.setOnClickListener {
            showTimePickerDialog(mBinding.textViewStartTime)
        }

        // 마감 시간 선택
        mBinding.textViewEndTime.setOnClickListener {
            showTimePickerDialog(mBinding.textViewEndTime)
        }

        // 인원 선택
        mBinding.textViewPersonnel.setOnClickListener {

        }

        // 태그 선택
        mBinding.buttonTag.setOnClickListener {

        }

        // 임금 선택
        //mBinding.spinnerPayType.setOnClickListener {

        //}

        // 체크리스트 생성
        mBinding.buttonChecklist.setOnClickListener {

        }

        // 일감 등록
        mBinding.buttonCreateJob.setOnClickListener {

        }
    }

    private fun showDatePickerDialog() {

        val calendar: Calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // 선택된 날짜가 필요하면 이 currentDate 변수를 적절하게 사용하면 된다.
                //val currentDate =
                //    Calendar.getInstance().apply { set(year, monthOfYear, dayOfMonth) }

                val selectYear = year
                val selectMonth = monthOfYear + 1
                val selectDay = dayOfMonth

                jobDate = "$selectYear-$selectMonth-$selectDay"
                mBinding.textViewDate.text = jobDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            val calendar: Calendar = Calendar.getInstance()
            val currentTime = calendar.timeInMillis // 현재 시간

            calendar.add(Calendar.DATE, 7)
            val maxDay = calendar.timeInMillis // 일주일

            datePicker.minDate = currentTime
            datePicker.maxDate = maxDay
        }.show()
    }

    private fun showTimePickerDialog(textView: TextView) {
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            if (textView == mBinding.textViewStartTime) {
                startDate = "$hour:$minute"
                textView.text = startDate
            } else if (textView == mBinding.textViewEndTime) {
                endDate = "$hour:$minute"
                textView.text = endDate
            }
        }

        val test = TimePickerDialog(
            this,
            android.R.style.Theme_Holo_Dialog,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }



    @SuppressLint("PrivateApi")
    fun TimePicker.setTimeInterval(
        @AndroidIntRange(from = 0, to = 30)
        timeInterval: Int = DEFAULT_INTERVAL
    ) {
        try {
            val classForId = Class.forName("com.android.internal.R\$id")
            val fieldId = classForId.getField("minute").getInt(null)

            (this.findViewById(fieldId) as NumberPicker).apply {
                minValue = MINUTES_MIN
                maxValue = MINUTES_MAX / timeInterval - 1
                displayedValues = getDisplayedValue(timeInterval)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDisplayedValue(
        @AndroidIntRange(from = 0, to = 30)
        timeInterval: Int = DEFAULT_INTERVAL
    ): Array<String> {
        val minutesArray = ArrayList<String>()
        for (i in 0 until MINUTES_MAX step timeInterval) {
            minutesArray.add(i.toString())
        }

        return minutesArray.toArray(arrayOf(""))
    }
}
