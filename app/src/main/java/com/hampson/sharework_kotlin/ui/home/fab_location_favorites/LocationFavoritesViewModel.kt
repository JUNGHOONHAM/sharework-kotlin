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

    private val locationFavorites: MutableLiveData<LocationFavorites> = MutableLiveData()

    val locationFavoritesList : MutableLiveData<List<LocationFavorites>> by lazy {
        locationFavoritesRepository.fetchSingleLocationFavorites(compositeDisposable, userId)
    }

    fun getLocationFavorites(): LiveData<LocationFavorites> {
        return locationFavorites
    }

    val networkState : LiveData<NetworkState> by lazy {
        locationFavoritesRepository.getJobNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun createLocationFavorites(locationFavorites: LocationFavorites) {
        Log.d("skejfklse", locationFavorites.toString())
        try {
            Log.d("skejfklse", "1")
            Log.d("skejfklse", apiService.toString())
            compositeDisposable.add(
                apiService.createLocationFavorites(locationFavorites)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            if (it.status == "success") {
                                this.locationFavorites.postValue(it.payload.locationFavorites)
                            } else {

                            }
                        },
                        {
                            Log.d("sjeklfsef", it.message)
                        }
                    )
            )
        } catch (e: Exception) {
            Log.d("sjeklfsef", e.message)
        }
    }
}