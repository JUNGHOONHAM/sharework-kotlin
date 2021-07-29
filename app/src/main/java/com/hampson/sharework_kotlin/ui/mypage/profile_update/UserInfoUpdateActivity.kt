package com.hampson.sharework_kotlin.ui.mypage.profile_update

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.ActivityUserInfoUpdateBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import org.jetbrains.anko.toast

class UserInfoUpdateActivity : AppCompatActivity() {
    private lateinit var  mBinding : ActivityUserInfoUpdateBinding

    private  lateinit var apiService: DBInterface
    private lateinit var viewModel: UserInfoUpdateViewModel
    private lateinit var userInfoUpdateRepository: UserInfoUpdateRepository

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityUserInfoUpdateBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.textViewToolbarTitle.text = "내 프로필 수정"

        val sessionManagement = SessionManagement(this)
        userId = sessionManagement.getSessionID()

        apiService = DBClient.getClient(this)
        userInfoUpdateRepository = UserInfoUpdateRepository(apiService)

        viewModel = getViewModel()

        viewModel.userInfoLiveData.observe(this, {
            bindUI(it)
        })

        viewModel.getUpdateCheck().observe(this, {
            if (it) {
                toast("저장되었습니다.")
                finish()
            } else {
                toast("저장 실패")
            }
        })

        viewModel.networkState().observe(this, {
            mBinding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            mBinding.textViewError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        mBinding.buttonUpdateProfile.setOnClickListener {
            val user = User(null, null, null, null, null, null, mBinding.editTextContents.text.toString(), null,
                null, null, null, null)
            viewModel.updateUser(user)
        }
    }

    private fun bindUI(user: User) {
        mBinding.textViewName.text = user.name
        mBinding.textViewPhone.text = user.phone
        mBinding.textViewBirth.text = user.resident_number

        if (user.gender == "male") {
            mBinding.textViewGender.text = "남"
        } else {
            mBinding.textViewGender.text = "여"
        }

        if (user.comment != null) {
            mBinding.editTextContents.setText(user.comment)
        }
    }

    private fun getViewModel(): UserInfoUpdateViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return UserInfoUpdateViewModel(userInfoUpdateRepository, userId) as T
            }
        }).get(UserInfoUpdateViewModel::class.java)
    }
}
