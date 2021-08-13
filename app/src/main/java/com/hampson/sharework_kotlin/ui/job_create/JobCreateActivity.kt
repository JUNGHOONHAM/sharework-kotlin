package com.hampson.sharework_kotlin.ui.job_create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Meta
import com.hampson.sharework_kotlin.databinding.ActivityJobCreateBinding
import com.hampson.sharework_kotlin.databinding.ActivityPaymentHistoryWorkerBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import java.text.DecimalFormat
import java.util.*

class JobCreateActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var mBinding: ActivityJobCreateBinding

    private var userId: Int = -1
    private lateinit var sessionManagement: SessionManagement

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

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
            getDateCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }

        // 시작 시간 선택
        mBinding.textViewStartTime.setOnClickListener {

        }

        // 마감 시간 선택
        mBinding.textViewEndTime.setOnClickListener {

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

    private fun pickDate() {

    }

    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateCalendar()
    }
}
