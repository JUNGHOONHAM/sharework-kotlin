package com.hampson.sharework_kotlin.ui.user_profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class ProfileViewModel (private val profileRepository: ProfileRepository, private val userId: Int,
                        private val subject: String, private val reviewCategory: String) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    // introduce fragment
    val tagLiveData : LiveData<Response> by lazy {
        profileRepository.getUserTagHistory(compositeDisposable, userId, subject)
    }

    // review fragment
    val rateReviewLiveData : LiveData<UserJobRateReview> by lazy {
        profileRepository.getRateReview(compositeDisposable, userId, reviewCategory)
    }

    val reviewPagedList : LiveData<PagedList<UserJobRateReview>> by lazy {
        profileRepository.getReviewPagedList(compositeDisposable, userId, "user")
    }

    val getPagedNetworkState : LiveData<NetworkState> by lazy {
        profileRepository.getPagedNetworkState()
    }

    // profile activity
    val userInfoLiveData : LiveData<User> by lazy {
        profileRepository.getUser(compositeDisposable, userId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        profileRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return reviewPagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}