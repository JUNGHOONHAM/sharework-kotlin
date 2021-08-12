package com.hampson.sharework_kotlin.ui.home_giver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable

class HomeGiverViewModel (private val homeRepository: HomeGiverRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    // review fragment
    //val reviewPagedList : LiveData<PagedList<UserJobRateReview>> by lazy {
    //    profileRepository.getReviewPagedList(compositeDisposable, userId, "user")
    //}

    //val getPagedNetworkState : LiveData<NetworkState> by lazy {
    //    profileRepository.getPagedNetworkState()
    //}

    //fun listIsEmpty(): Boolean {
    //    return reviewPagedList.value?.isEmpty() ?: true
    //}

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}