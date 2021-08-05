package com.hampson.sharework_kotlin.ui.user_profile.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.JobChecklists
import com.hampson.sharework_kotlin.data.vo.UserJobRateReview
import com.hampson.sharework_kotlin.databinding.FragmentNotificationBinding
import com.hampson.sharework_kotlin.databinding.FragmentProfileReviewBinding
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.BottomSheetJobPagedListAdapter
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoChecklistAdapter
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoRepository
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoViewModel
import com.hampson.sharework_kotlin.ui.user_profile.ProfileViewModel

class ReviewFragment(private val viewModel: ProfileViewModel) : Fragment() {

    private var mBinding : FragmentProfileReviewBinding? = null

    private lateinit var adapter: ReviewBaseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileReviewBinding.inflate(inflater, container, false)

        mBinding = binding

        viewModel.rateReviewLiveData.observe(activity as FragmentActivity, {
            setRecyclerView(it.review_base_count_list)
            setRating(it.review_rate_sum)
        })

        val reviewAdapter = ReviewPagedListAdapter(activity as FragmentActivity)

        val layout = LinearLayoutManager((activity as FragmentActivity))
        binding.recyclerView.layoutManager = layout
        binding.recyclerView.adapter = reviewAdapter

        viewModel.reviewPagedList.observe(activity as FragmentActivity, {
            reviewAdapter.submitList(it)
        })

        viewModel.networkState.observe(activity as FragmentActivity, {
            binding.progressBar.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.textViewError.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                reviewAdapter.setNetworkState(it)
            }
        })

        return mBinding?.root
    }

    private fun setRecyclerView(reviewBaseCountList: HashMap<String, Int>) {
        adapter = ReviewBaseAdapter(context, reviewBaseCountList)
        val layout = LinearLayoutManager(context)

        mBinding?.recyclerViewReviewCount?.layoutManager = layout
        mBinding?.recyclerViewReviewCount?.adapter = adapter
    }

    private fun setRating(rate: Float) {
        mBinding?.ratingBar?.rating = rate
        mBinding?.textViewRatingCount?.text = rate.toString()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}