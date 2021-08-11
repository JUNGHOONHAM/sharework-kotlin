package com.hampson.sharework_kotlin.ui.management_user.authentication_phone_number

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.*
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.MainActivity
import com.hampson.sharework_kotlin.ui.management_user.sign_up.SignUpActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class AuthenticationPhoneNumberViewModel (private val authenticationPhoneNumberRepository : AuthenticationPhoneNumberRepository,
                                          application: Application) : AndroidViewModel(application) {

    private val compositeDisposable = CompositeDisposable()

    private val smsAuthLiveData: MediatorLiveData<SmsAuth> = MediatorLiveData()
    private val smsAuthMetaLiveData: MediatorLiveData<Response> = MediatorLiveData()

    private val mAction: MutableLiveData<Any> = MutableLiveData()
    private val mToast: MutableLiveData<String> = MutableLiveData()

    private val context = getApplication<Application>().applicationContext

    fun getSmsAuth(): LiveData<SmsAuth> {
        return smsAuthLiveData
    }

    fun getSmsAuthMeta(): LiveData<Response> {
        return smsAuthMetaLiveData
    }

    fun getAction(): LiveData<Any> {
        return mAction
    }

    fun getToast(): LiveData<String> {
        return mToast
    }

    val networkState : LiveData<NetworkState> by lazy {
        authenticationPhoneNumberRepository.getAuthenticationPhoneNumberNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun sendPhoneNumber(phoneNumber: String) {
        val repositoryLiveData: LiveData<SmsAuth> =
            authenticationPhoneNumberRepository.sendPhoneNumber(compositeDisposable, phoneNumber)

        smsAuthLiveData.addSource(repositoryLiveData) { value: SmsAuth ->
            smsAuthLiveData.setValue(value)
        }
    }

    fun sendVerifiedNumber(phoneNumber: String, token: String, verifiedNumber: String) {
        val repositoryLiveData: LiveData<Response?> =
            authenticationPhoneNumberRepository.sendVerifiedNumber(compositeDisposable, phoneNumber, token, verifiedNumber)

        smsAuthMetaLiveData.addSource(repositoryLiveData) { value: Response? ->
            smsAuthMetaLiveData.value = value
            if (value != null) {
                if (value.payload.meta.is_already_auth_phone_number) {
                    saveSession(value)
                    moveToActivity(MainActivity::class.java)
                } else {
                    isNotPhoneNumber()
                }
            } else {
                popupToToast("인증번호가 유효하지 않습니다.")
            }
        }
    }

    private fun saveSession(it: Response) {
        val id = it.payload.smsAuth.user.id
        val phoneNumber = it.payload.smsAuth.user.phone
        val email = it.payload.smsAuth.user.email
        var appType: String = context.getString(R.string.worker)

        val user = User(id, null, appType, null, null, null, null, null,
            null, null, null, null)

        val sessionManagement = SessionManagement(context)
        sessionManagement.saveSession(user)
    }

    private fun isNotPhoneNumber() {
        moveToActivity(SignUpActivity::class.java)
    }

    private fun moveToActivity(activity: Any) {
        mAction.postValue(activity)
    }

    private fun popupToToast(message: String) {
        mToast.postValue(message)
    }
}