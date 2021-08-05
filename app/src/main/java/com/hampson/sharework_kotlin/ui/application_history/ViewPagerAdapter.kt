package com.hampson.sharework_kotlin.ui.application_history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hampson.sharework_kotlin.ui.application_history.applied.AppliedFragment
import com.hampson.sharework_kotlin.ui.application_history.completed.CompletedFragment
import com.hampson.sharework_kotlin.ui.application_history.hired.HiredFragment
import com.hampson.sharework_kotlin.ui.user_profile.introduce.IntroduceFragment
import com.hampson.sharework_kotlin.ui.user_profile.review.ReviewFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                       private val viewModel: HistoryViewModel): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                AppliedFragment(viewModel)
            }
            1 -> {
                HiredFragment(viewModel)
            }
            2 -> {
                CompletedFragment(viewModel)
            }
            else -> {
                Fragment()
            }
        }
    }

}