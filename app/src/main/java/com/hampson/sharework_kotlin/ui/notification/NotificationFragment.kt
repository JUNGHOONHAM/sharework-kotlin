package com.hampson.sharework_kotlin.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hampson.sharework_kotlin.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var mBinding : FragmentNotificationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNotificationBinding.inflate(inflater, container, false)

        mBinding = binding

        binding.toolbar.textViewToolbarTitle.text = "알림"

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}