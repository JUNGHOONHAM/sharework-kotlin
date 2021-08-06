package com.hampson.sharework_kotlin.ui.application_history.applied

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.databinding.FragmentRecyclerViewBinding
import com.hampson.sharework_kotlin.ui.application_history.HistoryPagedListAdapter
import com.hampson.sharework_kotlin.ui.application_history.HistoryViewModel

class AppliedFragment(private val viewModel: HistoryViewModel) : Fragment() {

    private var mBinding : FragmentRecyclerViewBinding? = null

    private lateinit var adapter: HistoryPagedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecyclerViewBinding.inflate(inflater, container, false)

        mBinding = binding

        adapter = HistoryPagedListAdapter(activity as FragmentActivity)

        val layout = LinearLayoutManager(activity as FragmentActivity)
        binding.recyclerView.layoutManager = layout
        binding.recyclerView.adapter = adapter

        viewModel.appliedLiveData.observe(activity as FragmentActivity, {
            adapter.submitList(it)
        })

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}