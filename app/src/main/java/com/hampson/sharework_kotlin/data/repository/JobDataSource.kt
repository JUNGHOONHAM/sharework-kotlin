package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.hampson.sharework_kotlin.data.api.FIRST_PAGE
import com.hampson.sharework_kotlin.data.api.JobDBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class JobDataSource (private val apiService : JobDBInterface, private val compositeDisposable: CompositeDisposable) : PageKeyedDataSource<Int, Job>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Job>) {
        networkState.postValue(NetworkState.LOADING)
        Log.d("loadInitial in", "LOADING")
        val arr = arrayListOf<Int>(1775, 1776, 1777, 1778, 1779, 1800)
        compositeDisposable.add(
            apiService.getJob(arr.toString(), page, 10)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.d("loadInitial in", "start")
                        Log.d("loadInitial in", it.payload.jobList.toString())
                        callback.onResult(it.payload.jobList, null, page + 1)
                        networkState.postValue(NetworkState.LOADED)
                        Log.d("loadInitial in", "LOADED")
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        Log.d("loadInitial in err", it.message)
                        Log.d("loadInitial in", "ERROR")
                    }
                )
        )
    }

}