package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable

class LocationFavoritesViewModel (private val locationFavoritesRepository: LocationFavoritesRepository, userId: Int) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val locationFavorites : MutableLiveData<List<LocationFavorites>> by lazy {
        Log.d("locationviewmodel", userId.toString())
        locationFavoritesRepository.fetchSingleLocationFavorites(compositeDisposable, userId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        locationFavoritesRepository.getJobNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}