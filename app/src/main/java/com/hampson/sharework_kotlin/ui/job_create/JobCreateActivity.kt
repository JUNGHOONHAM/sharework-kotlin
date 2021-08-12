package com.hampson.sharework_kotlin.ui.job_create

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Meta
import com.hampson.sharework_kotlin.databinding.ActivityJobCreateBinding
import com.hampson.sharework_kotlin.databinding.ActivityPaymentHistoryWorkerBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import java.text.DecimalFormat
import java.util.*

class JobCreateActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityJobCreateBinding

    private var userId: Int = -1
    private lateinit var sessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityJobCreateBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.textViewToolbarTitle.text = "일감 등록"


    }
}
