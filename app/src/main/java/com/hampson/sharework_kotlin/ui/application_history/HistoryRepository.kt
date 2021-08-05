package com.hampson.sharework_kotlin.ui.application_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.*
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable

class HistoryRepository (private val apiService : DBInterface) {

    lateinit var userInfoUpdateNetworkDataSource: UserInfoUpdateNetworkDataSource
    lateinit var tagNetworkDataSource: TagNetworkDataSource
    lateinit var userJobRateReviewNetworkDataSource: UserJobRateReviewNetworkDataSource
    lateinit var reviewDataSourceFactory: UserJobRateReviewDataSourceFactory

    lateinit var reviewPagedList: LiveData<PagedList<UserJobRateReview>>

    // introduce
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

    // review
    fun getRateReview (compositeDisposable: CompositeDisposable, userId: Int, reviewCategory: String) : LiveData<UserJobRateReview> {
        userJobRateReviewNetworkDataSource = UserJobRateReviewNetworkDataSource(apiService, compositeDisposable)
        userJobRateReviewNetworkDataSource.getRateReview(userId, reviewCategory)

        return userJobRateReviewNetworkDataSource.downloadedRateReviewResponse
    }

    fun getReviewPagedList (compositeDisposable: CompositeDisposable, userId: Int, sendTo: String) : LiveData<PagedList<UserJobRateReview>> {
        reviewDataSourceFactory = UserJobRateReviewDataSourceFactory(apiService, compositeDisposable, userId, sendTo)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        reviewPagedList = LivePagedListBuilder(reviewDataSourceFactory, config).build()

        return reviewPagedList
    }

    fun getPagedNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<UserJobRateReviewDataSource, NetworkState>(
            reviewDataSourceFactory.reviewLiveDataSource, UserJobRateReviewDataSource::networkState
        )
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return userInfoUpdateNetworkDataSource.networkState
    }
}