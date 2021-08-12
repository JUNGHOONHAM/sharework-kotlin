package com.hampson.sharework_kotlin.ui.home_worker

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.databinding.FragmentHomeWorkerBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.home_worker.bottom_sheet_job_list.BottomSheetJobList
import com.hampson.sharework_kotlin.ui.home_worker.fab_location_favorites.DialogLocationFavorites
import com.hampson.sharework_kotlin.ui.home_worker.fab_location_favorites.LocationFavoritesRepository
import com.hampson.sharework_kotlin.ui.home_worker.fab_location_favorites.LocationFavoritesViewModel
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class HomeWorkerFragment : Fragment(), OnMapReadyCallback, ClusterManager.OnClusterClickListener<HomeWorkerFragment.MyClusterItem> {

    private var mBinding : FragmentHomeWorkerBinding? = null
    private lateinit var mView : MapView

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var map: GoogleMap

    private lateinit var clusterManager: ClusterManager<MyClusterItem>
    private var clusterRenderer: ClusterRenderer? = null

    private lateinit var viewModel: HomeWorkerViewModel
    private lateinit var locationViewModel: LocationFavoritesViewModel
    private lateinit var homeWorkerRepository: HomeWorkerRepository
    private lateinit var locationFavoritesRepository: LocationFavoritesRepository
    private lateinit var apiService: DBInterface

    private var myLocation: LatLng? = null

    private lateinit var speedDialView: SpeedDialView

    private var userId: Int = -1

    private lateinit var imm: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeWorkerBinding.inflate(inflater, container, false)

        mBinding = binding
        speedDialView = binding.speedDial

        mView = binding.map
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)
        //mView.getMapAsync {
        //    clusterRenderer = ClusterRenderer(requireActivity(), it, clusterManager)
        //}

        imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val sessionManagement = SessionManagement(requireActivity())
        userId = sessionManagement.getSessionID()

        apiService = DBClient.getClient(requireActivity())

        homeWorkerRepository = HomeWorkerRepository(apiService)
        locationFavoritesRepository = LocationFavoritesRepository(apiService)

        viewModel = getViewModel()

        viewModel.jobInMapList().observe(requireActivity(), {
            addItems(it)
        })

        viewModel.getSearchPosition().observe(requireActivity(), {
            map.moveCamera(CameraUpdateFactory.newLatLng(it))
        })

        viewModel.networkState().observe(requireActivity(), {
            binding.textViewError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        locationViewModel = getViewModelLocation(userId)
        locationViewModel.locationFavoritesList.observe(requireActivity(), Observer {
            initSpeedDial(it)
        })

        binding.floatingMyLocation.setOnClickListener {
            if (myLocation != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(myLocation))
            }
        }

        binding.editTextSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchName = binding.editTextSearch.text.toString()

                    binding.editTextSearch.setText("")
                    binding.editTextSearch.clearFocus()
                    imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)

                    val geocoder = Geocoder(context)
                    viewModel.searchMap(searchName, geocoder)
                }

                return false
            }

        })

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val defaultLocation = LatLng(37.715133, 126.734086)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17f))

        locationInit()
        addLocationListener()
        setUpClusterer()
    }

    private fun addItems(jobList: List<Job>) {
        clusterManager.clearItems()

        var offsetItem: MyClusterItem
        for (i in jobList.indices) {
            offsetItem = MyClusterItem(jobList[i].lat.toDouble(), jobList[i].lng.toDouble(), jobList[i].job_id)
            clusterManager.addItem(offsetItem)
        }

        clusterManager.renderer =
            ClusterRenderer(
                requireActivity(),
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
                requireActivity(),
                map,
                clusterManager
            )

        // Point the map's listeners at the listeners implemented by the cluster
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        map.setOnCameraIdleListener {
            var northeast = map.projection.visibleRegion.latLngBounds.northeast
            var southwest = map.projection.visibleRegion.latLngBounds.southwest

             viewModel.getJobList(northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude)
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
        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest()   // LocationRequest객체로 위치 정보 요청 세부 설정을 함
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY   // GPS 우선
        //locationRequest.interval = 10000   // 10초. 상황에 따라 다른 앱에서 더 빨리 위치 정보를 요청하면
        // 자동으로 더 짧아질 수도 있음
        //locationRequest.fastestInterval = 5000  // 이보다 더 빈번히 업데이트 하지 않음 (고정된 최소 인터벌)
    }

    private fun addLocationListener() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

            location.run {
                myLocation = LatLng(latitude, longitude)   // 위도, 경도
            }
        }
    }

    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) { // 전달인자도, 리턴값도 없는
        // 두 개의 함수를 받음
        if (ContextCompat.checkSelfPermission(requireActivity(), // 권한이 없는 경우
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), ACCESS_FINE_LOCATION)) { // 권한 거부 이력이 있는 경우
                cancel()
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(ACCESS_FINE_LOCATION),
                        REQUEST_ACCESS_FINE_LOCATION)
            }
        } else { // 권한이 있는 경우
            ok()
        }
    }

    private fun showPermissionInfoDialog() {
        alert("위치 권한을 허용하셔야 서비스 이용이 가능합니다.", "권한이 필요한 이유"){
            yesButton {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION)
            }
            noButton {  }
        }.show()
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
                    Toast.makeText(context, "위치 권한을 허용하셔야 서비스 이용이 가능합니다.", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun removeLocationListener() {
        if (this::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
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

        permissionCheck(cancel = {
            showPermissionInfoDialog()
        }, ok = {
            if (this::map.isInitialized) {
                locationInit()
                addLocationListener()
            }

        })
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
            for (i in item.indices) {
                jobIdList.add(item[i].getJob_id())
            }
        }

        val fragment =
            BottomSheetJobList()
        val bundle = Bundle()
        bundle.putIntegerArrayList("jobIdList", jobIdList)
        fragment.arguments = bundle

        (requireActivity()).supportFragmentManager.beginTransaction()?.add(
            fragment, "test")
            .commit()

        return true
    }

    private fun getViewModel(): HomeWorkerViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return HomeWorkerViewModel(homeWorkerRepository) as T
            }
        }).get(HomeWorkerViewModel::class.java)
    }

    private fun getViewModelLocation(userId: Int): LocationFavoritesViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return LocationFavoritesViewModel(locationFavoritesRepository, userId) as T
            }
        }).get(LocationFavoritesViewModel::class.java)
    }

    fun bindUI(it: List<Job>) {

    }

    private fun initSpeedDial(locationList: List<LocationFavorites>) {
        speedDialView.clearActionItems()

        for (i in locationList.indices) {
            var id = 0
            if (i == 0) {
                id = R.id.location_favorites_1
            } else if (i == 1) {
                id = R.id.location_favorites_2
            } else if (i == 2) {
                id = R.id.location_favorites_3
            } else if (i == 3) {
                id = R.id.location_favorites_4
            } else if (i == 4) {
                id = R.id.location_favorites_5
            }

            val location = locationList[i]

            speedDialView.addActionItem(SpeedDialActionItem.Builder(id, R.drawable.ic_baseline_location_on_24)
                .setLabel(location.location_name)
                .create())
        }

        speedDialView.addActionItem(SpeedDialActionItem.Builder(R.id.location_favorites_add, R.drawable.ic_baseline_add_circle_24)
            .create())

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.location_favorites_1 -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(locationList[0].lat, locationList[0].lng)))
                }

                R.id.location_favorites_2 -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(locationList[1].lat, locationList[1].lng)))
                }

                R.id.location_favorites_3 -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(locationList[2].lat, locationList[2].lng)))
                }

                R.id.location_favorites_4 -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(locationList[3].lat, locationList[3].lng)))
                }

                R.id.location_favorites_5 -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(locationList[4].lat, locationList[4].lng)))
                }

                R.id.location_favorites_add -> {
                    speedDialView.close()

                    val position = map.projection.visibleRegion.latLngBounds.center

                    var dialog = DialogLocationFavorites(requireActivity(), position)
                    dialog.show(requireActivity().supportFragmentManager, "")

                    dialog.setDialogResult(object : DialogLocationFavorites.OnDialogResult{
                        override fun finish(result: ArrayList<LocationFavorites>) {
                            initSpeedDial(result)
                        }
                    })
                }
            }
            true // To keep the Speed Dial open
        })
    }
}