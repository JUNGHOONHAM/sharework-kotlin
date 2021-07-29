package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.*
import com.hampson.sharework_kotlin.databinding.ActivityJobInfoBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.toast

class JobInfoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mBinding: ActivityJobInfoBinding

    private lateinit var viewModel: JobInfoViewModel
    private lateinit var jobInfoRepository: JobInfoRepository
    private lateinit var apiService: DBInterface

    private var jobId = -1
    private var userId: Int = -1

    private lateinit var mMap: GoogleMap

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityJobInfoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.textViewToolbarTitle.text = "업무 상세보기"

        val sessionManagement = SessionManagement(this)
        userId = sessionManagement.getSessionID()

        jobId = intent.getIntExtra("jobId", -1)

        apiService = DBClient.getClient(this)
        jobInfoRepository = JobInfoRepository(apiService)
        viewModel = getViewModel()

        viewModel.getJobShow(jobId)

        viewModel.getJobInfo().observe(this, {
            bindUI(it)
            job = it.payload.job
        })

        viewModel.getJobApplied().observe(this, {
            if (it) {
                toast("지원이 완료되었습니다.")
            } else {
                toast("지원에 실패하였습니다.")
            }

            checkApplied(it)
        })

        viewModel.networkState.observe(this, {
            mBinding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            mBinding.textViewError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        mBinding.buttonCreateApplication.setOnClickListener {
            var dialog = DialogJobInfoChecklist(this, job.job_checklists as ArrayList<JobChecklists>)
            dialog.show(supportFragmentManager, "")

            dialog.setDialogResult(object : DialogJobInfoChecklist.OnDialogResult{
                override fun finish(result: ArrayList<JobChecklists>) {
                    createApplication(result)
                }
            })
        }

        var mapFragment: SupportMapFragment? = null
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun getViewModel(): JobInfoViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return JobInfoViewModel(jobInfoRepository) as T
            }
        }).get(JobInfoViewModel::class.java)
    }

    private fun bindUI(jobReponse: Response) {
        val job = jobReponse.payload.job
        val meta = jobReponse.payload.meta
        mBinding.textViewTitle.text = job.job_title
        mBinding.textViewJobDate.text = job.job_date
        mBinding.textViewJobTime.text = job.start_date + " ~ " + job.end_date
        mBinding.textViewPersonnel.text = job.personnel + "명"
        mBinding.textViewPayType.text = job.pay_type
        mBinding.textViewPay.text = job.pay
        mBinding.textViewContents.text = job.contents
        mBinding.textViewTotalPay.text = "예상알바비 " + job.total_pay + "원"

        Glide.with(this)
            .load(job.jobable.user.profile_img)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(mBinding.imageViewProfile)

        val tagList = job?.tags
        if (tagList != null) {
            for (tag in tagList) {
                bindTag(tag, this)
            }
        }

        val providedList = job.job_provideds
        if (providedList != null) {
            for (provided in providedList) {
                bindProvided(provided)
            }
        }

        bindMap(job.lat.toDouble(), job.lng.toDouble())

        checkApplied(meta.applied_check)
    }

    private fun bindTag(tag: Tag, context: Context) {
        val textView = TextView(context)
        textView.text = "#" + tag.tag_name
        textView.backgroundResource = R.drawable.background_fill_gray
        textView.setTextColor(Color.rgb(0, 0, 0))
        textView.textSize = 14.0F
        textView.setPadding(20, 10, 20, 10)

        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 8, 0)
        textView.layoutParams = layoutParams
        mBinding.layoutTag.addView(textView)
    }

    private fun bindProvided(provided: JobProvided) {
        when (provided.provided_name) {
            "식사제공" -> {
                mBinding.layoutProvidedFood.visibility = View.VISIBLE
            }
            "당일지급" -> {
                mBinding.textViewDayPayment.visibility = View.VISIBLE
            }
            "주휴수당" -> {
                mBinding.layoutProvidedExtrapay.visibility = View.VISIBLE
            }
            "교통비지원" -> {
                mBinding.layoutProvidedTransport.visibility = View.VISIBLE
            }
        }
    }

    private fun bindMap(lat: Double, lng: Double) {
        val location = LatLng(lat, lng)
        var markerOptions = MarkerOptions()
        markerOptions.position(location)

        //mMap.addMarker(markerOptions)
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17F))
    }

    private fun checkApplied(applied: Boolean) {
        if (applied) {
            mBinding.buttonCreateApplication.text = "지원완료"
            mBinding.buttonCreateApplication.setBackgroundResource(R.drawable.background_fill_gray)
            mBinding.buttonCreateApplication.isEnabled = false
        } else {
            mBinding.buttonCreateApplication.text = "지원하기"
            mBinding.buttonCreateApplication.setBackgroundResource(R.drawable.background_fill_mint)
            mBinding.buttonCreateApplication.isEnabled = true
        }
    }

    private fun createApplication(selectChecklist: ArrayList<JobChecklists>) {
        val userChecklistList = ArrayList<UserChecklist>()
        for (checklist in selectChecklist) {
            var userChecklist = UserChecklist(null, userId, job.id, checklist.id, true)
            userChecklistList.add(userChecklist)
        }

        val jobApplication = JobApplication(null, job.id, null, null, null, "open", userId, userChecklistList, null)
        viewModel.createApplication(jobApplication)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}