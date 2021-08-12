package com.hampson.sharework_kotlin.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

class MyPageViewModel (private val myPageRepository: MyPageRepository, private val userId: Int) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()
    private val updateProfileLiveData: MediatorLiveData<Boolean> = MediatorLiveData()

    val userInfoLiveData : LiveData<User> by lazy {
        myPageRepository.getUser(compositeDisposable, userId)
    }

    fun updateProfileLiveData() : LiveData<Boolean> {
        return updateProfileLiveData
    }

    fun networkState() : LiveData<NetworkState> {
        return networkStateLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun updateProfileImage(profileImage: MultipartBody.Part, user_id: RequestBody) {
        val repositoryLiveData: LiveData<Boolean> =
            myPageRepository.updateProfileImage(compositeDisposable, profileImage, user_id)

        updateProfileLiveData.addSource(repositoryLiveData) { value: Boolean ->
            updateProfileLiveData.setValue(value)
        }
    }
}