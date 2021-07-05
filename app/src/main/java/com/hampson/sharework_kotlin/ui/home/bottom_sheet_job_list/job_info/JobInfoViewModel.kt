package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class JobInfoViewModel (private val apiService : DBInterface) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val jobLiveData: MutableLiveData<Response> = MutableLiveData()
    private val jobApplied: MutableLiveData<Boolean> = MutableLiveData()
    private val networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    fun getJobInfo() : LiveData<Response> {
        return jobLiveData
    }

    fun getJobApplied() : LiveData<Boolean> {
        return jobApplied
    }

    fun networkState() : LiveData<NetworkState> {
        return networkStateLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getJobShow(jobId: Int) {
        networkStateLiveData.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getJobShow(jobId, 135)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            jobLiveData.postValue(it)
                            networkStateLiveData.postValue(NetworkState.LOADED)
                        },
                        {
                            networkStateLiveData.postValue(NetworkState.ERROR)
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }

    fun createApplication(jobApplication: JobApplication) {
        try {
            compositeDisposable.add(
                apiService.createApplication(jobApplication)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            jobApplied.postValue(true)
                        },
                        {
                            jobApplied.postValue(true)
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }
}