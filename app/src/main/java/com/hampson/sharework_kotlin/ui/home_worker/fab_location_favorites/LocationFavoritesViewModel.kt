package com.hampson.sharework_kotlin.ui.home_worker.fab_location_favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import io.reactivex.disposables.CompositeDisposable

class LocationFavoritesViewModel (private val locationFavoritesRepository: LocationFavoritesRepository, userId: Int) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val locationFavoritesAddLiveData: MediatorLiveData<LocationFavorites> = MediatorLiveData()
    private val locationFavoritesDeleteLiveData: MediatorLiveData<LocationFavorites> = MediatorLiveData()
    private val mToast: MutableLiveData<String> = MutableLiveData()

    val locationFavoritesList : MutableLiveData<List<LocationFavorites>> by lazy {
        locationFavoritesRepository.fetchSingleLocationFavorites(compositeDisposable, userId)
    }

    fun getAddLocationFavorites(): LiveData<LocationFavorites> {
        return locationFavoritesAddLiveData
    }

    fun getDeleteLocationFavorites(): LiveData<LocationFavorites> {
        return locationFavoritesDeleteLiveData
    }

    fun getToast(): LiveData<String> {
        return mToast
    }

    val networkState : LiveData<NetworkState> by lazy {
        locationFavoritesRepository.getLocationFavoritesNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun createLocationFavorites(locationFavorites: LocationFavorites) {
        val repositoryLiveData: LiveData<LocationFavorites> =
            locationFavoritesRepository.createLocationFavorites(compositeDisposable, locationFavorites)

        locationFavoritesAddLiveData.addSource(repositoryLiveData) { value: LocationFavorites ->
            locationFavoritesAddLiveData.setValue(value)
        }
    }

    fun deleteLocationFavorites(id: Int) {
        val repositoryLiveData: LiveData<LocationFavorites> =
            locationFavoritesRepository.deleteLocationFavorites(compositeDisposable, id)

        locationFavoritesDeleteLiveData.addSource(repositoryLiveData) { value: LocationFavorites ->
            locationFavoritesDeleteLiveData.setValue(value)
        }
    }

    private fun popupToToast(message: String) {
        mToast.postValue(message)
    }
}