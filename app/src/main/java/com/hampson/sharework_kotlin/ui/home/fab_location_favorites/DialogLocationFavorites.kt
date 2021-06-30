package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.databinding.DialogLocationFavoritesBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.home.JobInMapRepository

class DialogLocationFavorites(context: FragmentActivity?, position: LatLng): DialogFragment(),
    LocationFavoritesAdapter.OnItemClickListener {

    private lateinit var  mBinding : DialogLocationFavoritesBinding

    private lateinit var locationViewModel: LocationFavoritesViewModel
    private lateinit var locationFavoritesRepository: LocationFavoritesRepository
    private lateinit var apiService: DBInterface

    private lateinit var locationFavoritesAdapter: LocationFavoritesAdapter
    private lateinit var locationFavoritesList: ArrayList<LocationFavorites>

    private  lateinit var mDialogResult: OnDialogResult

    private val context = context
    private var userId: Int = -1
    private val position = position

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = DialogLocationFavoritesBinding.inflate(layoutInflater, container, false)

        val sessionManagement = SessionManagement(activity as FragmentActivity)
        userId = sessionManagement.getSessionID()

        setRecyclerView()

        apiService = DBClient.getClient()
        locationFavoritesRepository = LocationFavoritesRepository(apiService)
        locationViewModel = getViewModelLocation(userId)

        locationViewModel.locationFavoritesList.observe(this, Observer {
            locationFavoritesList = it as ArrayList<LocationFavorites>
            locationFavoritesAdapter.replaceList(it as MutableList<LocationFavorites>)

            checkLocationFavoritesNone()
        })

        locationViewModel.getAddLocationFavorites().observe(this, Observer {
            locationFavoritesList.add(it)
            locationFavoritesAdapter.replaceList(locationFavoritesList)

            checkLocationFavoritesNone()

            if (mDialogResult != null) {
                mDialogResult.finish(locationFavoritesList)
            }
        })

        locationViewModel.getDeleteLocationFavorites().observe(this, Observer {
            locationFavoritesList.remove(it)
            locationFavoritesAdapter.replaceList(locationFavoritesList)

            checkLocationFavoritesNone()

            if (mDialogResult != null) {
                mDialogResult.finish(locationFavoritesList)
            }
        })

        locationViewModel.networkState.observe(this, Observer {
            // progress_bar
            if (it == NetworkState.ERROR) {
                mBinding.textViewNetworkError.visibility = View.VISIBLE
            } else {
                mBinding.textViewNetworkError.visibility = View.GONE
            }
        })

        locationViewModel.getToast().observe(activity as FragmentActivity, {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        mBinding.buttonAdd.setOnClickListener {
            mBinding.buttonAdd.isEnabled = false

            val locaitionName = mBinding.editTextLocationName.text.toString()
            mBinding.editTextLocationName.setText("")

            if (locaitionName.trim() != "") {
                val location = LocationFavorites(null, userId, locaitionName, position.latitude, position.longitude)
                locationViewModel.createLocationFavorites(location)
            } else {
                Toast.makeText(context, "위치 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }

            mBinding.buttonAdd.isEnabled = true
        }

        mBinding.imageViewClose.setOnClickListener {
            dismiss()
        }

        return mBinding.root
    }

    private fun initDisplaySize() {
        var params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    private fun setRecyclerView() {
        locationFavoritesAdapter = LocationFavoritesAdapter(context)
        val layout = LinearLayoutManager(context)
        locationFavoritesAdapter.setOnLocationFavoritesClickListener(this)

        mBinding.recyclerView.layoutManager = layout
        mBinding.recyclerView.adapter = locationFavoritesAdapter
    }

    private fun checkLocationFavoritesNone() {
        if (locationFavoritesList.isEmpty()) {
            mBinding.textViewNoneFavorite.visibility = View.VISIBLE
        } else {
            mBinding.textViewNoneFavorite.visibility = View.GONE
        }
    }

    private fun getViewModelLocation(userId: Int): LocationFavoritesViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return LocationFavoritesViewModel(apiService, locationFavoritesRepository, userId) as T
            }
        }).get(LocationFavoritesViewModel::class.java)
    }

    override fun onDeleteClick(locationFavorites: LocationFavorites) {
        locationViewModel.deleteLocationFavorites(locationFavorites.id)
    }

    override fun onResume() {
        super.onResume()

        initDisplaySize()
    }

    fun setDialogResult(dialogResult: OnDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnDialogResult {
        fun finish(result: ArrayList<LocationFavorites>)
    }

}