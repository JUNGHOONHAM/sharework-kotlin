package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.LocationFavoritesNetworkDataSource
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable

class LocationFavoritesRepository (private val apiService : DBInterface) {
    lateinit var locationFavoritesDataSource: LocationFavoritesNetworkDataSource

    fun fetchSingleLocationFavorites (compositeDisposable: CompositeDisposable, userId: Int) : MutableLiveData<List<LocationFavorites>> {
        locationFavoritesDataSource = LocationFavoritesNetworkDataSource(apiService, compositeDisposable)
        locationFavoritesDataSource.fetchLocationFavorites(userId)

        return locationFavoritesDataSource.downlodedLocationFavoritesResponse
    }

    fun getLocationFavoritesNetworkState(): LiveData<NetworkState> {
        return locationFavoritesDataSource.networkState
    }
}