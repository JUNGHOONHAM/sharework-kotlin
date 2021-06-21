package com.hampson.sharework_kotlin.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.hampson.sharework_kotlin.data.api.FIRST_PAGE
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class JobDataSource (private val apiService : DBInterface, private val compositeDisposable: CompositeDisposable,
                     private var jobIdList: ArrayList<Int>) : PageKeyedDataSource<Int, Job>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getJob(jobIdList.toString(), params.key, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.optional.total_page >= params.key) {
                            callback.onResult(it.payload.jobList, params.key + 1)
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

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Job>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getJob(jobIdList.toString(), page, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callback.onResult(it.payload.jobList, null, page + 1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

}