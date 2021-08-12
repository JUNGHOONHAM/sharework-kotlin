package com.hampson.sharework_kotlin.ui.user_profile.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.databinding.FragmentProfileReviewBinding
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

        viewModel.rateReviewLiveData.observe(requireActivity(), {
            setRecyclerView(it.review_base_count_list)
            setRating(it.review_rate_sum)
        })

        val reviewAdapter = ReviewPagedListAdapter(requireActivity())

        val layout = LinearLayoutManager(requireActivity())
        binding.recyclerView.layoutManager = layout
        binding.recyclerView.adapter = reviewAdapter

        viewModel.reviewPagedList.observe(requireActivity(), {
            reviewAdapter.submitList(it)
        })

        viewModel.getPagedNetworkState.observe(requireActivity(), {
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