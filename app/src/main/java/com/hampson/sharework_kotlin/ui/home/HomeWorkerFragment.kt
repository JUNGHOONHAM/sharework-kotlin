package com.hampson.sharework_kotlin.ui.home

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.databinding.FragmentHomeworkerBinding
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.GoogleTaskExampleDialog
import com.hampson.sharework_kotlin.ui.home.fab_location_favorites.LocationFavoritesRepository
import com.hampson.sharework_kotlin.ui.home.fab_location_favorites.LocationFavoritesViewModel

class HomeWorkerFragment : Fragment(), OnMapReadyCallback, ClusterManager.OnClusterClickListener<HomeWorkerFragment.MyClusterItem> {

    private var mBinding : FragmentHomeworkerBinding? = null
    private lateinit var mView : MapView

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var map: GoogleMap

    // Declare a variable for the cluster manager.
    private lateinit var clusterManager: ClusterManager<MyClusterItem>
    private var clusterRenderer: ClusterRenderer? = null

    private lateinit var viewModel: ClusterJobInMapViewModel
    private lateinit var locationViewModel: LocationFavoritesViewModel
    private lateinit var jobInMapRepository: JobInMapRepository
    private lateinit var locationFavoritesRepository: LocationFavoritesRepository

    private var myLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeworkerBinding.inflate(inflater, container, false)

        mBinding = binding

        mView = binding.map
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)
        //mView.getMapAsync {
        //    clusterRenderer = ClusterRenderer(activity as FragmentActivity, it, clusterManager)
        //}

        val apiService : DBInterface = DBClient.getClient()

        jobInMapRepository = JobInMapRepository(apiService)
        locationFavoritesRepository = LocationFavoritesRepository(apiService)

        //viewModel = getViewModel(jobId)
        viewModel = getViewModel(0.0, 0.0, 0.0, 0.0)

        viewModel.jobInMapList.observe(activity as FragmentActivity, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(activity as FragmentActivity, Observer {
            // progress_bar
            Log.d("network11 TEST", it.status.toString())
            binding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.textViewError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        locationViewModel = getViewModelLocation()
        locationViewModel.locationFavorites.observe(activity as FragmentActivity, Observer {
            Log.d("locationView result", it.toString())
        })

        binding.speedDial.inflate(R.menu.favorite_floating_menu)


        binding.floatingMyLocation.setOnClickListener {
            map.animateCamera(CameraUpdateFactory.newLatLng(myLocation))
        }

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap ?: return

        val defaultLocation = LatLng(37.715133, 126.734086)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17f))

        locationInit()
        addLocationListener()
        setUpClusterer()
    }

    private fun addItems(jobList: List<Job>) {
        clusterManager.clearItems()

        var offsetItem: MyClusterItem
        for (i in 0..(jobList.size - 1)) {
            offsetItem = MyClusterItem(jobList.get(i).lat.toDouble(), jobList.get(i).lng.toDouble(), jobList.get(i).job_id)
            clusterManager.addItem(offsetItem)
        }

        clusterManager.renderer =
            ClusterRenderer(
                activity as FragmentActivity,
                map,
                clusterManager
            )
    }

    class ClusterRenderer(
            context: Context?,
            map: GoogleMap?,
            clusterManager: ClusterManager<MyClusterItem>?
    ): DefaultClusterRenderer<MyClusterItem>(context, map, clusterManager) {

        init {
            clusterManager?.renderer = this
            minClusterSize = 1
        }
    }

    private fun setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(context, map)
        clusterManager.setOnClusterClickListener(this)
        clusterManager.renderer =
            ClusterRenderer(
                activity as FragmentActivity,
                map,
                clusterManager
            )

        // Point the map's listeners at the listeners implemented by the cluster
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        map.setOnCameraIdleListener {
            var northeast = map.projection.visibleRegion.latLngBounds.northeast
            var southwest = map.projection.visibleRegion.latLngBounds.southwest

             viewModel.mapUpdate(northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude).observe(activity as FragmentActivity, Observer {
                 addItems(it)
             })
        }
    }

    inner class MyClusterItem(
            lat: Double,
            lng: Double,
            job_id: Int
    ) : ClusterItem {

        private val position: LatLng
        private val job_id: Int

        fun getJob_id(): Int {
            return job_id
        }

        override fun getSnippet(): String? {
            TODO("Not yet implemented")
        }

        override fun getTitle(): String? {
            TODO("Not yet implemented")
        }

        override fun getPosition(): LatLng {
            return position
        }

        init {
            position = LatLng(lat, lng)
            this.job_id = job_id
        }
    }

    private fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(activity as FragmentActivity)
        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest()   // LocationRequest객체로 위치 정보 요청 세부 설정을 함
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY   // GPS 우선
        //locationRequest.interval = 10000   // 10초. 상황에 따라 다른 앱에서 더 빨리 위치 정보를 요청하면
        // 자동으로 더 짧아질 수도 있음
        //locationRequest.fastestInterval = 5000  // 이보다 더 빈번히 업데이트 하지 않음 (고정된 최소 인터벌)
    }

    private fun addLocationListener() {
        if (ActivityCompat.checkSelfPermission(activity as FragmentActivity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity as FragmentActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }

        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null)  // 혹시 안드로이드 스튜디오에서 비정상적으로 권한 요청 오류를 표시할 경우, 'Alt+Enter'로
        // 표시되는 제안 중, Suppress: Add @SuppressLint("MissingPermission") annotation
        // 을 클릭할 것
        // (에디터가 원래 권한 요청이 필요한 코드 주변에서만 권한 요청 코딩을 허용했었기 때문임.
        //  현재 우리 코딩처럼 이렇게 별도의 메소드에 권한 요청 코드를 작성하지 못하게 했었음)
    }

    inner class MyLocationCallBack: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation   // GPS가 꺼져 있을 경우 Location 객체가
            // null이 될 수도 있음

            myLocation = location?.latitude?.let { LatLng(it, location.longitude) }!!   // 위도, 경도
            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation))  // 카메라 이동

            location?.run {
                myLocation = LatLng(latitude, longitude)   // 위도, 경도
            }
        }
    }

    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) {   // 전달인자도, 리턴값도 없는
        // 두 개의 함수를 받음

        if (ContextCompat.checkSelfPermission(activity as FragmentActivity,                  // 권한이 없는 경우
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity as FragmentActivity,
                            ACCESS_FINE_LOCATION)) {       // 권한 거부 이력이 있는 경우

                cancel()

            } else {
                ActivityCompat.requestPermissions(activity as FragmentActivity,
                        arrayOf(ACCESS_FINE_LOCATION),
                        REQUEST_ACCESS_FINE_LOCATION)
            }
        } else {                                                    // 권한이 있는 경우
            ok()
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addLocationListener()
                } else {
                    // toast("권한이 거부 됨")
                }
                return
            }
        }
    }

    private fun removeLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    override fun onStart() {
        super.onStart()
        mView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mView.onResume()

        // 권한 요청
        //permissionCheck(
        //        cancel = { },   // 권한 필요 안내창
        //        ok = { addLocationListener()}      // ③   주기적으로 현재 위치를 요청
        //)
    }

    override fun onPause() {
        super.onPause()
        mView.onPause()
        removeLocationListener()    // 앱이 동작하지 않을 때에는 위치 정보 요청 제거
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }

    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }

    override fun onClusterClick(cluster: Cluster<MyClusterItem>?): Boolean {
        val item = cluster?.items?.toTypedArray()
        var jobIdList = ArrayList<Int>()

        if (item != null) {
            for (i in 0..(item.size - 1)) {
                jobIdList.add(item.get(i).getJob_id())
            }
        }

        val fragment =
            GoogleTaskExampleDialog()
        val bundle = Bundle()
        bundle.putIntegerArrayList("jobIdList", jobIdList)
        fragment.arguments = bundle

        (activity as FragmentActivity).supportFragmentManager?.beginTransaction()?.add(
            fragment, "test")
            ?.commit()

        return true
    }

    private fun getViewModel(norteast_lat: Double, northeast_lng: Double, southweast_lat: Double, southweast_lng: Double): ClusterJobInMapViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return ClusterJobInMapViewModel(
                    jobInMapRepository,
                    norteast_lat,
                    northeast_lng,
                    southweast_lat,
                    southweast_lng
                ) as T
            }
        }).get(ClusterJobInMapViewModel::class.java)
    }

    private fun getViewModelLocation(): LocationFavoritesViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return LocationFavoritesViewModel(locationFavoritesRepository, 66) as T
            }
        }).get(LocationFavoritesViewModel::class.java)
    }

    fun bindUI(it: List<Job>) {

    }
}