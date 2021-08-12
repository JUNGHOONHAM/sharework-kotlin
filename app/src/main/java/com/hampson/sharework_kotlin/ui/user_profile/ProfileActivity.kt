package com.hampson.sharework_kotlin.ui.user_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.ActivityProfileBinding
import com.hampson.sharework_kotlin.session.SessionManagement

class ProfileActivity : AppCompatActivity() {

    private lateinit var  mBinding : ActivityProfileBinding

    private lateinit var profileRepository: ProfileRepository
    private lateinit var viewModel: ProfileViewModel
    private lateinit var apiService: DBInterface

    private lateinit var sessionManagement: SessionManagement
    private var myUserId: Int = -1
    private lateinit var appType: String

    private var userId: Int = -1

    private lateinit var subject: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.textViewToolbarTitle.text = "프로필"

        sessionManagement = SessionManagement(this)
        myUserId = sessionManagement.getSessionID()
        appType = sessionManagement.getAppType()!!

        userId = intent.getIntExtra("userId", -1)

        // 프로필 type check
        checkProfileType()

        apiService = DBClient.getClient(this)
        profileRepository = ProfileRepository(apiService)
        viewModel = getViewModel()

        viewModel.userInfoLiveData.observe(this, {
            bindUI(it)
        })

        viewModel.networkState.observe(this, {
            mBinding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            mBinding.textViewError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, viewModel)

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

    private fun bindUI(user: User) {
        mBinding.textViewName.text = user.name

        Glide.with(this)
            .load(user.profile_img)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(mBinding.imageViewProfile)
    }

    private fun checkProfileType() {
        if (userId == myUserId) {
            subject = appType
        } else {
            if (appType == "giver") {
                subject = "worker"
            } else {
                subject = "giver"
            }
        }
    }

    private fun getViewModel(): ProfileViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAS T")
                return ProfileViewModel(profileRepository, userId, subject, "user_job_owner") as T
            }
        }).get(ProfileViewModel::class.java)
    }
}