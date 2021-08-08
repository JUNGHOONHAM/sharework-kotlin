package com.hampson.sharework_kotlin.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.databinding.FragmentNotificationBinding
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.BottomSheetJobPagedListAdapter
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.BottomSheetJobViewModel
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.JobPagedListRepository

class NotificationFragment : Fragment() {

    private var mBinding : FragmentNotificationBinding? = null

    private lateinit var viewModel: NotificationViewModel
    lateinit var notificationRepository: NotificationRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNotificationBinding.inflate(inflater, container, false)

        mBinding = binding

        binding.toolbar.textViewToolbarTitle.text = "알림"

        val apiService : DBInterface = DBClient.getClient(activity as FragmentActivity)

        notificationRepository = NotificationRepository(apiService)

        viewModel = getViewModel()

        val adapter = NotificationPagedListAdapter(activity as FragmentActivity)

        val layout = LinearLayoutManager((activity as FragmentActivity))
        binding.recyclerView.layoutManager = layout
        binding.recyclerView.adapter = adapter

        viewModel.notificationPagedList.observe(activity as FragmentActivity, Observer {
            adapter.submitList(it)
        })

        viewModel.networkState.observe(activity as FragmentActivity, Observer {
            binding.progressBar.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.textViewError.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                adapter.setNetworkState(it)
            }
        })

        return mBinding?.root
    }

    private fun getViewModel(): NotificationViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return NotificationViewModel(notificationRepository) as T
            }
        }).get(NotificationViewModel::class.java)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}