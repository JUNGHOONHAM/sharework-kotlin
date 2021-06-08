package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.JobDataSource
import com.hampson.sharework_kotlin.data.repository.JobDataSourceFactory
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class JobPagedListRepository (private val apiService : DBInterface) {

    lateinit var jobPagedList: LiveData<PagedList<Job>>
    lateinit var jobDataSourceFactory: JobDataSourceFactory

    fun fetchLiveJobPagedList (compositeDisposable: CompositeDisposable, jobIdList: ArrayList<Int>) : LiveData<PagedList<Job>> {
        jobDataSourceFactory = JobDataSourceFactory(apiService, compositeDisposable, jobIdList)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        jobPagedList = LivePagedListBuilder(jobDataSourceFactory, config).build()

        return jobPagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<JobDataSource, NetworkState>(
            jobDataSourceFactory.jobLiveDataSource, JobDataSource::networkState
        )
    }
}