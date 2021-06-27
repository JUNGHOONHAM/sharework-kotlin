package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.databinding.DialogLocationFavoritesBinding
import com.hampson.sharework_kotlin.session.SessionManagement

class DialogLocationFavorites(context: FragmentActivity?, locationViewModel: LocationFavoritesViewModel, position: LatLng): Dialog(context!!),
    LocationFavoritesAdapter.OnItemClickListener {

    private lateinit var  mBinding : DialogLocationFavoritesBinding

    private lateinit var locationFavoritesAdapter: LocationFavoritesAdapter
    private lateinit var locationFavoritesList: ArrayList<LocationFavorites>

    private val locationViewModel = locationViewModel

    private val context = context
    private var userId: Int = -1
    private val position = position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DialogLocationFavoritesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val sessionManagement = SessionManagement(this.getContext())
        userId = sessionManagement.getSessionID()

        initDisplaySize()

        setRecyclerView()

        locationViewModel.locationFavoritesList.observe(context as FragmentActivity, Observer {
            locationFavoritesList = it as ArrayList<LocationFavorites>
            locationFavoritesAdapter.replaceList(it as MutableList<LocationFavorites>)
        })

        locationViewModel.getLocationFavorites().observe(context as FragmentActivity, Observer {
            locationFavoritesList.add(it)
            locationFavoritesAdapter.replaceList(locationFavoritesList)
        })

        mBinding.buttonAdd.setOnClickListener {
            val locaitionName = mBinding.editTextLocationName.text.toString()
            val location = LocationFavorites(null, userId, locaitionName, position.latitude, position.longitude)
            locationViewModel.createLocationFavorites(location)
        }

        mBinding.imageViewClose.setOnClickListener {
            dismiss()
        }
    }

    private fun initDisplaySize() {
        val param = this.window?.attributes
        param!!.width = WindowManager.LayoutParams.MATCH_PARENT
        param.height = WindowManager.LayoutParams.WRAP_CONTENT

        this.window?.attributes = param
    }

    private fun setRecyclerView() {
        locationFavoritesAdapter = LocationFavoritesAdapter(context)
        val layout = LinearLayoutManager(context)
        locationFavoritesAdapter.setOnLocationFavoritesClickListener(this)

        mBinding.recyclerView.layoutManager = layout
        mBinding.recyclerView.adapter = locationFavoritesAdapter
    }

    override fun onDeleteClick(locationFavorites: LocationFavorites) {

    }
}