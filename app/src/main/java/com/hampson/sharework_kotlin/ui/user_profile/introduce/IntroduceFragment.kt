package com.hampson.sharework_kotlin.ui.user_profile.introduce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hampson.sharework_kotlin.data.vo.Response
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.FragmentNotificationBinding
import com.hampson.sharework_kotlin.databinding.FragmentProfileIntroduceBinding
import com.hampson.sharework_kotlin.ui.user_profile.ProfileViewModel

class IntroduceFragment(private val viewModel: ProfileViewModel) : Fragment() {

    private var mBinding : FragmentProfileIntroduceBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileIntroduceBinding.inflate(inflater, container, false)

        mBinding = binding

        viewModel.userInfoLiveData.observe(activity as FragmentActivity, {
            bindUI(it)
        })

        viewModel.tagLiveData.observe(activity as FragmentActivity, {
            bindTagUI(it)
        })

        return mBinding?.root
    }

    private fun bindUI(user: User) {
        if (user.comment != "" || user.comment != null) {
            mBinding?.textViewComment!!.text = user.comment
        } else {
            mBinding?.textViewComment!!.text = "등록되지 않았습니다."
        }
    }

    private fun bindTagUI(response: Response) {
        val meta = response.payload.meta

        if (meta.subject == "worker") {
            mBinding?.textViewTitle?.text = "경력"
            mBinding?.textViewMessage?.text = "쉐어워크를 통해 근무한 경력입니다."
        } else if (meta.subject == "giver") {
            mBinding?.textViewTitle?.text = "지난 등록 공고"
            mBinding?.textViewMessage?.text = "쉐어워크를 통해 올린 공고 수 입니다."
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}