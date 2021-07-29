package com.hampson.sharework_kotlin.ui.user_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.tabs.TabLayoutMediator
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.databinding.ActivityMainBinding
import com.hampson.sharework_kotlin.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var  mBinding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.textViewToolbarTitle.text = "프로필"

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        mBinding.viewPager.adapter = adapter

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = "소개 및 경력"
                }
                1 -> {
                    tab.text = "리뷰"
                }
            }
        }.attach()
    }
}