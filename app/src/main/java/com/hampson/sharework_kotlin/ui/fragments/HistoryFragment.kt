package com.hampson.sharework_kotlin.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.hampson.sharework_kotlin.data.api.JobDBClient
import com.hampson.sharework_kotlin.data.api.JobDBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.databinding.FragmentHistoryBinding
import com.hampson.sharework_kotlin.ui.home.bottom_sheet.ClusterJobPagedListAdapter
import com.hampson.sharework_kotlin.ui.home.bottom_sheet.ClusterJobViewModel
import com.hampson.sharework_kotlin.ui.home.bottom_sheet.JobPagedListRepository

class HistoryFragment : Fragment() {

    private var mBinding : FragmentHistoryBinding? = null

    private lateinit var viewModel: ClusterJobViewModel
    lateinit var jobRepository: JobPagedListRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        mBinding = binding

        val apiService : JobDBInterface = JobDBClient.getClient()

        jobRepository =
            JobPagedListRepository(
                apiService
            )

        viewModel = getViewModel()

        val jobAdapter =
            ClusterJobPagedListAdapter(
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

        viewModel.jobPagedList.observe((activity as FragmentActivity), Observer {
            Log.d("TESTviewModel", "START")
            jobAdapter.submitList(it)
            Log.d("TESTFviewModel", "END")
        })

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

    private fun getViewModel(): ClusterJobViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                val jobIdList = ArrayList<Int>()
                @Suppress("UNCHECKED_CAST")
                return ClusterJobViewModel(
                    jobRepository, jobIdList
                ) as T
            }
        }).get(ClusterJobViewModel::class.java)
    }
}