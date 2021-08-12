package com.hampson.sharework_kotlin.ui.home_worker

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception

class HomeWorkerViewModel (private val homeWorkerRepository: HomeWorkerRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val jobListLiveData: MediatorLiveData<List<Job>> = MediatorLiveData()
    private val jobListNetworkState: MutableLiveData<NetworkState> = MutableLiveData()
    private val searchPositionLiveData: MutableLiveData<LatLng> = MutableLiveData()

    fun jobInMapList() : LiveData<List<Job>> {
        return jobListLiveData
    }

    fun networkState() : LiveData<NetworkState> {
        return jobListNetworkState
    }

    fun getSearchPosition(): LiveData<LatLng> {
        return searchPositionLiveData
    }

    val networkState : LiveData<NetworkState> by lazy {
        homeWorkerRepository.getJobNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return jobListLiveData.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getJobList(northeast_lat: Double, northeast_lng: Double, southwest_lat: Double, southwest_lng: Double) {
        val repositoryLiveData: LiveData<List<Job>> =
            homeWorkerRepository.getjobList(compositeDisposable, northeast_lat, northeast_lng, southwest_lat, southwest_lng)

        jobListLiveData.addSource(repositoryLiveData) { value: List<Job> ->
            jobListLiveData.setValue(value)
        }
    }

    fun searchMap(searchName: String, geocoder: Geocoder) {
        var addresses: List<Address>? = null

        try {
            addresses = geocoder.getFromLocationName(searchName, 3)
            if (addresses != null && !addresses.equals(" ")) {
                getAddressInfo(addresses)
            }
        } catch (e: Exception) {

        }
    }

    private fun getAddressInfo(addresses: List<Address>) {
        val address = addresses[0]
        val latLng = LatLng(address.latitude, address.longitude)

        searchPositionLiveData.postValue(latLng)
    }
}