package com.hampson.sharework_kotlin.ui.user_profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.*
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable

class ProfileRepository (private val apiService : DBInterface) {

    lateinit var userInfoUpdateNetworkDataSource: UserInfoUpdateNetworkDataSource
    lateinit var tagNetworkDataSource: TagNetworkDataSource

    fun getUser (compositeDisposable: CompositeDisposable, userId: Int) : LiveData<User> {
        userInfoUpdateNetworkDataSource = UserInfoUpdateNetworkDataSource(apiService, compositeDisposable)
        userInfoUpdateNetworkDataSource.getUser(userId)

        return userInfoUpdateNetworkDataSource.downloadedUserResponse
    }

    fun getUserTagHistory (compositeDisposable: CompositeDisposable, userId: Int, subject: String) : LiveData<Response> {
        tagNetworkDataSource = TagNetworkDataSource(apiService, compositeDisposable)
        tagNetworkDataSource.getUserTagHistory(userId, subject)

        return tagNetworkDataSource.downloadedTagResponse
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return userInfoUpdateNetworkDataSource.networkState
    }
}