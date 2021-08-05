package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.hampson.sharework_kotlin.data.api.FIRST_PAGE
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.UserJobRateReview
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserJobRateReviewDataSource (private val apiService : DBInterface, private val compositeDisposable: CompositeDisposable,
                                   private var userId: Int, private var sendTo: String) : PageKeyedDataSource<Int, UserJobRateReview>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserJobRateReview>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getReviewPagedList(userId, sendTo, params.key, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.payload.meta.total_page >= params.key) {
                            callback.onResult(it.payload.userJobRateReviewList, params.key + 1)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            networkState.postValue(NetworkState.ENDOFLIST)
                        }
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserJobRateReview>) {

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserJobRateReview>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getReviewPagedList(userId, sendTo, page, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callback.onResult(it.payload.userJobRateReviewList, null, page + 1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

}