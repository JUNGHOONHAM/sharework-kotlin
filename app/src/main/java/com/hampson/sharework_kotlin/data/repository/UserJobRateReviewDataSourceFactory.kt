package com.hampson.sharework_kotlin.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.UserJobRateReview
import io.reactivex.disposables.CompositeDisposable

class UserJobRateReviewDataSourceFactory (private val apiService: DBInterface, private val compositeDisposable: CompositeDisposable,
                                          private var userId: Int, private var sendTo: String) : DataSource.Factory<Int, UserJobRateReview>() {

    val reviewLiveDataSource = MutableLiveData<UserJobRateReviewDataSource>()

    override fun create(): DataSource<Int, UserJobRateReview> {
        val userJobRateReviewDataSource = UserJobRateReviewDataSource(apiService, compositeDisposable, userId, sendTo)

        reviewLiveDataSource.postValue(userJobRateReviewDataSource)
        return userJobRateReviewDataSource
    }

}