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
import com.google.android.material.tabs.TabLayoutMediator
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.databinding.FragmentHistoryBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.BottomSheetJobPagedListAdapter
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.BottomSheetJobViewModel
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.JobPagedListRepository
import com.hampson.sharework_kotlin.ui.user_profile.ProfileRepository
import com.hampson.sharework_kotlin.ui.user_profile.ProfileViewModel
import com.hampson.sharework_kotlin.ui.user_profile.ViewPagerAdapter

class HistoryFragment : Fragment() {

    private var mBinding : FragmentHistoryBinding? = null

    private lateinit var historyRepository: HistoryRepository
    private lateinit var viewModel: HistoryViewModel
    private lateinit var apiService: DBInterface

    private lateinit var sessionManagement: SessionManagement
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        mBinding = binding

        binding.toolbar.textViewToolbarTitle.text = "프로필"

        sessionManagement = SessionManagement(activity as FragmentActivity)
        userId = sessionManagement.getSessionID()

        val apiService : DBInterface = DBClient.getClient(activity as FragmentActivity)
        historyRepository = HistoryRepository(apiService)
        viewModel = getViewModel()

        val adapter = ViewPagerAdapter((activity as FragmentActivity).supportFragmentManager, lifecycle, viewModel)

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = "지원한 업무"
                }
                1 -> {
                    tab.text = "채택된 업무"
                }
                2 -> {
                    tab.text = "완료한 업무"
                }
            }
        }.attach()

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun getViewModel(): HistoryViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(historyRepository, userId) as T
            }
        }).get(HistoryViewModel::class.java)
    }
}