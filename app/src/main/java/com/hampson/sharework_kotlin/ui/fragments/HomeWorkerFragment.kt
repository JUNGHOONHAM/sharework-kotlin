package com.hampson.sharework_kotlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hampson.sharework_kotlin.ui.cluster_job.GoogleTaskExampleDialog
import com.hampson.sharework_kotlin.databinding.FragmentHomeworkerBinding

class HomeWorkerFragment : Fragment(), OnMapReadyCallback {

    private var mBinding : FragmentHomeworkerBinding? = null
    private lateinit var mView : MapView

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

        binding.button.setOnClickListener {
            //val intent = Intent(context, SingleJob::class.java)
            //intent.putExtra("id", 1775)
            //this.startActivity(intent)

            (activity as FragmentActivity).supportFragmentManager?.beginTransaction()?.add(
                GoogleTaskExampleDialog(), "test")
                ?.commit()
        }


        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val myLocation = LatLng(37.654601, 127.060530)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))

        //마커 출력
        val marker = MarkerOptions()
            .position(myLocation)
            .title("Nowon")
            .snippet("노원역입니다.")
        googleMap?.addMarker(marker)
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
    }

    override fun onPause() {
        super.onPause()
        mView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }

    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }

}