package com.hampson.sharework_kotlin.ui.application_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.databinding.FragmentHistoryBinding
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.BottomSheetJobPagedListAdapter
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.BottomSheetJobViewModel
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.JobPagedListRepository

class HistoryFragment : Fragment() {

    private var mBinding : FragmentHistoryBinding? = null

    private lateinit var viewModel: BottomSheetJobViewModel
    lateinit var jobRepository: JobPagedListRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        mBinding = binding

        val apiService : DBInterface = DBClient.getClient(activity as FragmentActivity)

        jobRepository =
            JobPagedListRepository(
                apiService
            )

        viewModel = getViewModel()

        val jobAdapter =
            BottomSheetJobPagedListAdapter(
                (activity as FragmentActivity)
            )

        val gridLayoutManager = GridLayoutManager((activity as FragmentActivity), 3)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = jobAdapter.getItemViewType(position)
                if (viewType == jobAdapter.JOB_VIEW_TYPE) return 1
                else return 3
            }
        }

        val layout = LinearLayoutManager((activity as FragmentActivity))
        binding.recyclerView.layoutManager = layout
        //binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = jobAdapter


        viewModel.networkState.observe((activity as FragmentActivity), Observer {
            binding.progressBar.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.textViewError.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                jobAdapter.setNetworkState(it)
            }
        })

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun getViewModel(): BottomSheetJobViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                val jobIdList = ArrayList<Int>()
                @Suppress("UNCHECKED_CAST")
                return BottomSheetJobViewModel(
                    jobRepository, jobIdList
                ) as T
            }
        }).get(BottomSheetJobViewModel::class.java)
    }
}