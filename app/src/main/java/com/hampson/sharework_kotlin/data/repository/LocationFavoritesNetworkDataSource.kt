package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class LocationFavoritesNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedLocationFavoritesResponse = MutableLiveData<List<LocationFavorites>>()
    val downlodedLocationFavoritesResponse: MutableLiveData<List<LocationFavorites>>
        get() = _downloadedLocationFavoritesResponse

    private val _downloadedLocationFavoriteResponse = MutableLiveData<LocationFavorites>()
    val downlodedLocationFavoriteResponse: MutableLiveData<LocationFavorites>
        get() = _downloadedLocationFavoriteResponse

    fun fetchLocationFavorites(userId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getLocationFavorites(userId)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedLocationFavoritesResponse.postValue(it.payload.locationFavoritesList)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }

    fun createLocationFavorites(locationFavorites: LocationFavorites) {
        try {
            compositeDisposable.add(
                apiService.createLocationFavorites(locationFavorites)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            if (it.status == "success") {
                                _downloadedLocationFavoriteResponse.postValue(it.payload.locationFavorites)
                            }
                        },
                        {

                        }
                    )
            )
        } catch (e: Exception) {

        }
    }

    fun deleteLocationFavorites(id: Int) {
        try {
            compositeDisposable.add(
                apiService.deleteLocationFavorites(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            if (it.status == "success") {
                                _downloadedLocationFavoriteResponse.postValue(it.payload.locationFavorites)
                            }
                        },
                        {

                        }
                    )
            )
        } catch (e: Exception) {

        }
    }
}