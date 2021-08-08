package com.hampson.sharework_kotlin.ui.application_history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.*
import com.hampson.sharework_kotlin.ui.user_profile.ProfileRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class HistoryViewModel (private val historyRepository: HistoryRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    // applied
    val appliedLiveData : LiveData<PagedList<JobApplication>> by lazy {
        historyRepository.getReviewPagedList(compositeDisposable, "applied")
    }

    // hired
    val hiredLiveData : LiveData<PagedList<JobApplication>> by lazy {
        historyRepository.getReviewPagedList(compositeDisposable, "hired")
    }

    // completed
    val completedLiveData : LiveData<PagedList<JobApplication>> by lazy {
        historyRepository.getReviewPagedList(compositeDisposable, "completed")
    }

    fun appliedIsEmpty(): Boolean {
        return appliedLiveData.value?.isEmpty() ?: true
    }

    fun hiredIsEmpty(): Boolean {
        return hiredLiveData.value?.isEmpty() ?: true
    }

    fun completedIsEmpty(): Boolean {
        return completedLiveData.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}