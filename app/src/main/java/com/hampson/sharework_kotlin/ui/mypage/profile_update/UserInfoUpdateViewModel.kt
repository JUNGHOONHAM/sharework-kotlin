package com.hampson.sharework_kotlin.ui.mypage.profile_update

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

class UserInfoUpdateViewModel (private val userInfoUpdateRepository: UserInfoUpdateRepository, private val userId: Int) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val updateLiveData: MediatorLiveData<Boolean> = MediatorLiveData()
    private val networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    val userInfoLiveData : LiveData<User> by lazy {
        userInfoUpdateRepository.getUser(compositeDisposable, userId)
    }

    fun getUpdateCheck() : LiveData<Boolean> {
        return updateLiveData
    }

    fun networkState() : LiveData<NetworkState> {
        return networkStateLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun updateUser(user: User) {
        val repositoryLiveData: LiveData<Boolean> =
            userInfoUpdateRepository.updateUser(compositeDisposable, userId, user)

        updateLiveData.addSource(repositoryLiveData) { value: Boolean ->
            updateLiveData.setValue(value)
        }
    }
}