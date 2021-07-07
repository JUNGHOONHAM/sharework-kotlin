package com.hampson.sharework_kotlin.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.FragmentMypageBinding
import com.hampson.sharework_kotlin.session.SessionManagement


class MyPageFragment : Fragment() {

    private var mBinding : FragmentMypageBinding? = null

    private lateinit var viewModel: MyPageViewModel
    private lateinit var apiService: DBInterface

    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMypageBinding.inflate(inflater, container, false)

        binding.toolbar.textViewToolbarTitle.text = "마이페이지"

        mBinding = binding

        val sessionManagement = SessionManagement(activity as FragmentActivity)
        userId = sessionManagement.getSessionID()

        apiService = DBClient.getClient(activity as FragmentActivity)
        viewModel = getViewModel()

        viewModel.getUser(userId)

        viewModel.getUserInfo().observe(activity as FragmentActivity, {
            bindUI(it)
        })

        viewModel.networkState().observe(activity as FragmentActivity, {
            binding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.textViewError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        binding.imageViewProfile.setOnClickListener {

        }

        binding.layoutMyProfile.setOnClickListener {

        }

        binding.textViewProfileUpdate.setOnClickListener {

        }

        binding.textViewAppTypeChange.setOnClickListener {

        }

        binding.textViewLogout.setOnClickListener {

        }

        binding.textViewPayment.setOnClickListener {

        }

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun getViewModel(): MyPageViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return MyPageViewModel(apiService) as T
            }
        }).get(MyPageViewModel::class.java)
    }

    private fun bindUI(user: User) {
        mBinding?.textViewName?.text = user.name

        Glide.with(this)
            .load(user.profile_img)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(mBinding?.imageViewProfile!!)

        if (user.app_type == "0") {
            mBinding?.textViewAppTypeChange?.text = "일 주는 사람으로 변경"
        } else {
            mBinding?.textViewAppTypeChange?.text = "일 하는 사람으로 변경"
        }
    }

}