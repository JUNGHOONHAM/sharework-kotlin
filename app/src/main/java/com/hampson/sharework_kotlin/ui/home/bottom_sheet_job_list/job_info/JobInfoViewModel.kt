package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class JobInfoViewModel (private val jobInfoRepository: JobInfoRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val jobLiveData: MediatorLiveData<Response> = MediatorLiveData()
    private val jobApplied: MediatorLiveData<Boolean> = MediatorLiveData()

    fun getJobInfo() : LiveData<Response> {
        return jobLiveData
    }

    fun getJobApplied() : LiveData<Boolean> {
        return jobApplied
    }

    val networkState : LiveData<NetworkState> by lazy {
        jobInfoRepository.getJobNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getJobShow(jobId: Int) {
        val repositoryLiveData: LiveData<Response> =
            jobInfoRepository.getJobShow(compositeDisposable, jobId)

        jobLiveData.addSource(repositoryLiveData) { value: Response ->
            jobLiveData.setValue(value)
        }
    }

    fun createApplication(jobApplication: JobApplication) {
        val repositoryLiveData: LiveData<Boolean> =
            jobInfoRepository.createApplication(compositeDisposable, jobApplication)

        jobApplied.addSource(repositoryLiveData) { value: Boolean ->
            jobApplied.setValue(value)
        }
    }
}