package com.hampson.sharework_kotlin.ui.application_history.hired

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.databinding.FragmentRecyclerViewBinding
import com.hampson.sharework_kotlin.ui.application_history.HistoryViewModel
import com.hampson.sharework_kotlin.ui.application_history.HistoryPagedListAdapter

class HiredFragment(private val viewModel: HistoryViewModel) : Fragment() {

    private var mBinding : FragmentRecyclerViewBinding? = null

    private lateinit var adapter: HistoryPagedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecyclerViewBinding.inflate(inflater, container, false)

        mBinding = binding

        adapter = HistoryPagedListAdapter(requireActivity())

        val layout = LinearLayoutManager(requireActivity())
        binding.recyclerView.layoutManager = layout
        binding.recyclerView.adapter = adapter

        viewModel.hiredLiveData.observe(requireActivity(), {
            adapter.submitList(it)
        })

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}