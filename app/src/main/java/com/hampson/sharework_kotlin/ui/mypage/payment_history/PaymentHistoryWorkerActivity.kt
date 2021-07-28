package com.hampson.sharework_kotlin.ui.mypage.payment_history

import android.graphics.Color
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

        viewModel = getViewModel()

        setDate(1)
        setDateUI(1)

        val applicationAdapter = PaymentHistoryWorkerPagedListAdapter(this)

        val layout = LinearLayoutManager(this)
        mBinding.recyclerView.layoutManager = layout
        mBinding.recyclerView.adapter = applicationAdapter

        viewModel.getMetaLiveData().observe(this, {
            bindUI(it)
        })

        viewModel.networkState().observe(this, {
            mBinding.progressBar.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            mBinding.textViewError.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        viewModel.getPageLiveData().observe(this, {
            applicationAdapter.submitList(it)
        })

        viewModel.networkStatePagedList.observe(this, {
            mBinding.progressBarRecyclerView.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            mBinding.textViewErrorRecyclerView.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                applicationAdapter.setNetworkState(it)
            }
        })

        mBinding.textViewBtn1.setOnClickListener {
            setDate(1)
            setDateUI(1)
        }

        mBinding.textViewBtn2.setOnClickListener {
            setDate(2)
            setDateUI(2)
        }

        mBinding.textViewBtn3.setOnClickListener {
            setDate(3)
            setDateUI(3)
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

    private fun setDate(type: Int) {
        var startDateStr = ""
        var endDateStr = getDate("today")

        when (type) {
            1 -> {
                startDateStr = getDate("week")
            }
            2 -> {
                startDateStr = getDate("1month")
            }
            3 -> {
                startDateStr = getDate("3month")
            }
        }

        viewModel.getPaymentHistory(userId, startDateStr, endDateStr)
        viewModel.getPaymentMeta(startDateStr, endDateStr)
    }

    private fun setDateUI(type: Int) {
        mBinding.textViewBtn1.setTextColor(Color.rgb(153, 153, 153))
        mBinding.textViewBtn2.setTextColor(Color.rgb(153, 153, 153))
        mBinding.textViewBtn3.setTextColor(Color.rgb(153, 153, 153))
        mBinding.textViewEndDate.text = getDate("today")

        when (type) {
            1 -> {
                mBinding.textViewStartDate.text = getDate("week")
                mBinding.textViewBtn1.setTextColor(Color.rgb(100, 216, 209))
            }
            2 -> {
                mBinding.textViewStartDate.text = getDate("1month")
                mBinding.textViewBtn2.setTextColor(Color.rgb(100, 216, 209))
            }
            3 -> {
                mBinding.textViewStartDate.text = getDate("3month")
                mBinding.textViewBtn3.setTextColor(Color.rgb(100, 216, 209))
            }
        }
    }

    private fun getViewModel(): PaymentHistoryWorkerViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return PaymentHistoryWorkerViewModel(applicationRepository) as T
            }
        }).get(PaymentHistoryWorkerViewModel::class.java)
    }
}
