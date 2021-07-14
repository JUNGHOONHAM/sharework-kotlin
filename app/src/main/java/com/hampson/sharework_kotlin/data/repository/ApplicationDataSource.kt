package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.hampson.sharework_kotlin.data.api.FIRST_PAGE
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ApplicationDataSource (private val apiService : DBInterface, private val compositeDisposable: CompositeDisposable) : PageKeyedDataSource<Int, JobApplication>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, JobApplication>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getApplications(66, "2020-01-01", "2021-09-09", params.key, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.payload.meta.total_page >= params.key) {
                            callback.onResult(it.payload.jobApplicationlist, params.key + 1)
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

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, JobApplication>) {

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, JobApplication>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getApplications(66, "2020-01-01", "2021-09-09", page, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callback.onResult(it.payload.jobApplicationlist, null, page + 1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

}