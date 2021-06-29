package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.ui.MainActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class LocationFavoritesViewModel (private val apiService : DBInterface,
                                  private val locationFavoritesRepository: LocationFavoritesRepository, userId: Int) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val locationFavoritesLiveData: MutableLiveData<LocationFavorites> = MutableLiveData()
    private val mToast: MutableLiveData<String> = MutableLiveData()

    val locationFavoritesList : MutableLiveData<List<LocationFavorites>> by lazy {
        locationFavoritesRepository.fetchSingleLocationFavorites(compositeDisposable, userId)
    }

    fun getLocationFavorites(): LiveData<LocationFavorites> {
        return locationFavoritesLiveData
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
                                this.locationFavoritesLiveData.postValue(it.payload.locationFavorites)
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

    private fun popupToToast(message: String) {
        mToast.postValue(message)
    }
}