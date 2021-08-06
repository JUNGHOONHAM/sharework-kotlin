package com.hampson.sharework_kotlin.ui.application_history.completed

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.vo.Response
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.FragmentNotificationBinding
import com.hampson.sharework_kotlin.databinding.FragmentProfileIntroduceBinding
import com.hampson.sharework_kotlin.databinding.FragmentRecyclerViewBinding
import com.hampson.sharework_kotlin.ui.application_history.HistoryViewModel
import com.hampson.sharework_kotlin.ui.application_history.applied.AppliedPagedListAdapter
import com.hampson.sharework_kotlin.ui.user_profile.ProfileViewModel
import org.jetbrains.anko.custom.style

class CompletedFragment(private val viewModel: HistoryViewModel) : Fragment() {

    private var mBinding : FragmentRecyclerViewBinding? = null

    private lateinit var adapter: AppliedPagedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecyclerViewBinding.inflate(inflater, container, false)

        mBinding = binding

        val layout = LinearLayoutManager((activity as FragmentActivity))
        binding.recyclerView.layoutManager = layout
        binding.recyclerView.adapter = adapter

        viewModel.completedLiveData.observe(activity as FragmentActivity, {
            adapter.submitList(it)
        })

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}