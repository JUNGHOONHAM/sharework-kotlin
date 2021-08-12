package com.hampson.sharework_kotlin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.databinding.ActivityGiverMainBinding
import com.hampson.sharework_kotlin.ui.chat.ChatFragment
import com.hampson.sharework_kotlin.ui.home_giver.HomeGiverFragment
import com.hampson.sharework_kotlin.ui.home_worker.bottom_sheet_job_list.job_info.JobInfoActivity
import com.hampson.sharework_kotlin.ui.job_create.JobCreateActivity
import com.hampson.sharework_kotlin.ui.mypage.MyPageFragment
import com.hampson.sharework_kotlin.ui.notification.NotificationFragment

class GiverMainActivity : AppCompatActivity() {

    private lateinit var  mBinding : ActivityGiverMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityGiverMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        // 네비게이션들을 담는 호스트
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바텀 네비게이션뷰 와 네비게이션을 묶어준다
        NavigationUI.setupWithNavController(mBinding.bottomNav, navController)

        mBinding.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeGiverFragment -> {
                    supportFragmentManager.beginTransaction().replace(mBinding.myNavHost.id, HomeGiverFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.chatFragment -> {
                    supportFragmentManager.beginTransaction().replace(mBinding.myNavHost.id, ChatFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.jobCreateActivity -> {
                    Intent(this, JobCreateActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.run { startActivity(this) }
                    return@setOnNavigationItemSelectedListener false
                }

                R.id.notificationFragment -> {
                    supportFragmentManager.beginTransaction().replace(mBinding.myNavHost.id, NotificationFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.myPageFragment -> {
                    supportFragmentManager.beginTransaction().replace(mBinding.myNavHost.id, MyPageFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                else -> false
            }

            false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(mBinding.myNavHost.id, fragment).commit()
    }
}