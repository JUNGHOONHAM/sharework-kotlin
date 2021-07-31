package com.hampson.sharework_kotlin.ui.user_profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Response
import com.hampson.sharework_kotlin.data.vo.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class ProfileViewModel (private val profileRepository: ProfileRepository, private val userId: Int, private val subject: String) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    val userInfoLiveData : LiveData<User> by lazy {
        profileRepository.getUser(compositeDisposable, userId)
    }

    val tagLiveData : LiveData<Response> by lazy {
        profileRepository.getUserTagHistory(compositeDisposable, userId, subject)
    }

    fun networkState() : LiveData<NetworkState> {
        return networkStateLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}