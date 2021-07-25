package com.hampson.sharework_kotlin.ui.mypage.payment_history

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Meta
import com.hampson.sharework_kotlin.databinding.ActivityPaymentHistoryWorkerBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.JobPagedListRepository
import java.text.DecimalFormat
import java.util.*

class PaymentHistoryWorkerActivity : AppCompatActivity() {

    private lateinit var viewModel: PaymentHistoryWorkerViewModel
    private lateinit var applicationRepository: ApplicationPagedListRepository
    private lateinit var apiService: DBInterface

    private lateinit var mBinding: ActivityPaymentHistoryWorkerBinding

    private var userId: Int = -1
    private lateinit var sessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPaymentHistoryWorkerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.textViewToolbarTitle.text = "정산 내역"

        sessionManagement = SessionManagement(this)
        userId = sessionManagement.getSessionID()

        apiService = DBClient.getClient(this)
        applicationRepository = ApplicationPagedListRepository(apiService)

        viewModel = getViewModel(userId, getDate("week"), getDate("today"))

        val applicationAdapter = PaymentHistoryWorkerPagedListAdapter(this)

        val layout = LinearLayoutManager(this)
        mBinding.recyclerView.layoutManager = layout
        mBinding.recyclerView.adapter = applicationAdapter

        viewModel.getMeta().observe(this, {
            bindUI(it)
            Log.d("TEST", "TEST")
        })

        viewModel.networkState().observe(this, {
            mBinding.progressBar.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            mBinding.textViewError.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        viewModel.applicationPagedList.observe(this, {
            applicationAdapter.submitList(it)
        })

        viewModel.networkStatePagedList.observe(this, {
            mBinding.progressBarRecyclerView.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            mBinding.textViewErrorRecyclerView.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                applicationAdapter.setNetworkState(it)
            }
        })

        viewModel.getPage().observe(this, {
            Log.d("getPage", it[0].toString())
        })

        mBinding.textViewBtn1.setOnClickListener {
            //val startDateStr = getDate("week")
            //val endDateStr = getDate("today")

            //viewModel = getViewModel(userId, startDateStr, endDateStr)
            //viewModel.getPaymentHistory()
            val test = viewModel.test()
        }

        mBinding.textViewBtn2.setOnClickListener {
            val startDateStr = getDate("1month")
            val endDateStr = getDate("today")

            viewModel = getViewModel(userId, startDateStr, endDateStr)
            viewModel.getPaymentHistory()
        }

        mBinding.textViewBtn3.setOnClickListener {
            val startDateStr = getDate("3month")
            val endDateStr = getDate("today")

            viewModel = getViewModel(userId, startDateStr, endDateStr)
            viewModel.getPaymentHistory()
        }
    }

    private fun bindUI(meta: Meta) {
        mBinding.textViewTotalPay.text = meta.total_work_pay + "원"
        mBinding.textViewTotalWorkTime.text = meta.total_work_time + "분"
        mBinding.textViewStartDate.text = meta.start_date
        mBinding.textViewEndDate.text = meta.end_date
    }

    private fun getDate(date_type: String): String {
        val decimalFormat = DecimalFormat("00")
        val currentCalendar = Calendar.getInstance()

        when (date_type) {
            "today" -> {

            }
            "week" -> {
                currentCalendar.add(Calendar.DATE, -7)
            }
            "1month" -> {
                currentCalendar.add(Calendar.MONTH, -1)
            }
            "3month" -> {
                currentCalendar.add(Calendar.MONTH, -3)
            }
        }

        val yearStr = currentCalendar.get(Calendar.YEAR).toString()
        val monthStr = decimalFormat.format(currentCalendar.get(Calendar.MONTH) + 1)
        val dayStr = decimalFormat.format(currentCalendar.get(Calendar.DATE))

        return "$yearStr-$monthStr-$dayStr"
    }

    private fun getViewModel(userId: Int, startDateStr: String, endDateStr: String): PaymentHistoryWorkerViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return PaymentHistoryWorkerViewModel(applicationRepository, apiService, userId, startDateStr, endDateStr) as T
            }
        }).get(PaymentHistoryWorkerViewModel::class.java)
    }
}
