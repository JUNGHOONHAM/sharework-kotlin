package com.hampson.sharework_kotlin.ui.home_giver

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hampson.sharework_kotlin.ui.home_giver.completed.CompletedFragment
import com.hampson.sharework_kotlin.ui.home_giver.proceeding.ProceddingFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                       private val viewModel: HomeGiverViewModel): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                ProceddingFragment(viewModel)
            }
            1 -> {
                CompletedFragment(viewModel)
            }
            else -> {
                Fragment()
            }
        }
    }

}