package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class LocationFavoritesViewModel (private val apiService : DBInterface,
                                  private val locationFavoritesRepository: LocationFavoritesRepository, userId: Int) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val locationFavoritesAddLiveData: MutableLiveData<LocationFavorites> = MutableLiveData()
    private val locationFavoritesDeleteLiveData: MutableLiveData<LocationFavorites> = MutableLiveData()
    private val mToast: MutableLiveData<String> = MutableLiveData()

    val locationFavoritesList : MutableLiveData<List<LocationFavorites>> by lazy {
        locationFavoritesRepository.fetchSingleLocationFavorites(compositeDisposable, userId)
    }

    fun getLocationFavorites(): LiveData<LocationFavorites> {
        return locationFavoritesAddLiveData
    }

    fun getDeleteLocationFavorites(): LiveData<LocationFavorites> {
        return locationFavoritesDeleteLiveData
    }

    fun getToast(): LiveData<String> {
        return mToast
    }

    val networkState : LiveData<NetworkState> by lazy {
        locationFavoritesRepository.getJobNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun createLocationFavorites(locationFavorites: LocationFavorites) {
        try {
            compositeDisposable.add(
                apiService.createLocationFavorites(locationFavorites)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            if (it.status == "success") {
                                this.locationFavoritesAddLiveData.postValue(it.payload.locationFavorites)
                            }
                        },
                        {
                            popupToToast("5개까지 가능합니다.")
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }

    fun deleteLocationFavorites(id: Int?) {
        try {
            compositeDisposable.add(
                apiService.deleteLocationFavorites(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            if (it.status == "success") {
                                this.locationFavoritesDeleteLiveData.postValue(it.payload.locationFavorites)
                            }
                        },
                        {

                        }
                    )
            )
        } catch (e: Exception) {

        }
    }

    private fun popupToToast(message: String) {
        mToast.postValue(message)
    }
}