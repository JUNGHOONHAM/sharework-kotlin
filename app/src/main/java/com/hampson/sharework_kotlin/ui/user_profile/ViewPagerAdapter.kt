package com.hampson.sharework_kotlin.ui.user_profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hampson.sharework_kotlin.ui.user_profile.introduce.IntroduceFragment
import com.hampson.sharework_kotlin.ui.user_profile.review.ReviewFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                       private val viewModel: ProfileViewModel): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                IntroduceFragment(viewModel)
            }
            1 -> {
                ReviewFragment(viewModel)
            }
            else -> {
                Fragment()
            }
        }
    }

}