package com.hampson.sharework_kotlin.ui.home_giver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.databinding.FragmentHomeGiverBinding
import com.hampson.sharework_kotlin.session.SessionManagement

class HomeGiverFragment : Fragment() {

    private var mBinding : FragmentHomeGiverBinding? = null

    private lateinit var homeGiverRepository: HomeGiverRepository
    private lateinit var viewModel: HomeGiverViewModel
    private lateinit var apiService: DBInterface

    private lateinit var sessionManagement: SessionManagement
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeGiverBinding.inflate(inflater, container, false)

        binding.toolbar.textViewToolbarTitle.text = "일감 리스트"

        mBinding = binding

        apiService = DBClient.getClient(requireContext())
        homeGiverRepository = HomeGiverRepository(apiService)
        viewModel = getViewModel()

        sessionManagement = SessionManagement(requireActivity())
        userId = sessionManagement.getSessionID()

        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle, viewModel)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = "진행중"
                }
                1 -> {
                    tab.text = "완료"
                }
            }
        }.attach()

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun getViewModel(): HomeGiverViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAS T")
                return HomeGiverViewModel(homeGiverRepository) as T
            }
        }).get(HomeGiverViewModel::class.java)
    }
}