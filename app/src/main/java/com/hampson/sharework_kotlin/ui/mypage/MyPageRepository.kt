package com.hampson.sharework_kotlin.ui.mypage

import androidx.lifecycle.LiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.*
import com.hampson.sharework_kotlin.data.repository.user.UserInfoUpdateNetworkDataSource
import com.hampson.sharework_kotlin.data.vo.User
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MyPageRepository (private val apiService : DBInterface) {

    lateinit var userInfoUpdateNetworkDataSource: UserInfoUpdateNetworkDataSource

    fun getUser (compositeDisposable: CompositeDisposable, userId: Int) : LiveData<User> {
        userInfoUpdateNetworkDataSource = UserInfoUpdateNetworkDataSource(apiService, compositeDisposable)
        userInfoUpdateNetworkDataSource.getUser(userId)

        return userInfoUpdateNetworkDataSource.downloadedUserResponse
    }

    fun updateProfileImage (compositeDisposable: CompositeDisposable, profileImage: MultipartBody.Part, user_id: RequestBody) : LiveData<Boolean> {
        userInfoUpdateNetworkDataSource = UserInfoUpdateNetworkDataSource(apiService, compositeDisposable)
        userInfoUpdateNetworkDataSource.updateProfileImage(profileImage, user_id)

        return userInfoUpdateNetworkDataSource.updateProfileImageResult
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return userInfoUpdateNetworkDataSource.networkState
    }
}