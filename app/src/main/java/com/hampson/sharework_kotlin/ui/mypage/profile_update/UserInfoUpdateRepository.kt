package com.hampson.sharework_kotlin.ui.mypage.profile_update

import androidx.lifecycle.LiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.*
import com.hampson.sharework_kotlin.data.repository.user.UserInfoUpdateNetworkDataSource
import com.hampson.sharework_kotlin.data.vo.User
import io.reactivex.disposables.CompositeDisposable

class UserInfoUpdateRepository (private val apiService : DBInterface) {

    lateinit var userInfoUpdateNetworkDataSource: UserInfoUpdateNetworkDataSource

    fun getUser (compositeDisposable: CompositeDisposable, userId: Int) : LiveData<User> {
        userInfoUpdateNetworkDataSource = UserInfoUpdateNetworkDataSource(apiService, compositeDisposable)
        userInfoUpdateNetworkDataSource.getUser(userId)

        return userInfoUpdateNetworkDataSource.downloadedUserResponse
    }

    fun updateUser (compositeDisposable: CompositeDisposable, userId: Int, user: User) : LiveData<Boolean> {
        userInfoUpdateNetworkDataSource = UserInfoUpdateNetworkDataSource(apiService, compositeDisposable)
        userInfoUpdateNetworkDataSource.updateUser(userId, user)

        return userInfoUpdateNetworkDataSource.updateResult
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return userInfoUpdateNetworkDataSource.networkState
    }
}