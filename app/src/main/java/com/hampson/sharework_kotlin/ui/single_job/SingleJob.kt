package com.hampson.sharework_kotlin.ui.single_job

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hampson.sharework_kotlin.data.api.JobDBClient
import com.hampson.sharework_kotlin.data.api.JobDBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.databinding.ActivitySingleJobBinding
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

class SingleJob : AppCompatActivity() {
    private lateinit var  mBinding : ActivitySingleJobBinding

    private lateinit var viewModel: SingleJobViewModel
    private lateinit var jobRepository: JobRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySingleJobBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        Log.d("onCreateStart", "@@@")
        val jobId: Int = intent.getIntExtra("id", 1)

        val apiService : JobDBInterface = JobDBClient.getClient()
        jobRepository = JobRepository(apiService)

        viewModel = getViewModel(jobId)
        Log.d("getViewModelEnd", "@@@")

        viewModel.job.observe(this, Observer {
            Log.d("bindUIStart", "@@@")
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            // progress_bar
            if (it == NetworkState.LOADING)
                Log.d("NETWORKSTATE1", "TEST")
            else if (it == NetworkState.ERROR)
                Log.d("NETWORKSTATE2", "TEST")
        })
    }

    fun bindUI(it: Job) {
        val jobs: Object = it.response
        Log.d("getDATAStart0", it.toString())
        Log.d("getDATAStart0", "@@@")
        Log.d("getDATAStart0", it.response
            .toString())
        val json = Gson().toJson(it)
        Log.d("getDATAStart1", "@@@")
        Log.d("getDATAStart1", json)

        try {
            val parser = JSONParser()
            val obj = parser.parse(json)
            val responseObj = obj as org.json.simple.JSONObject
            val res = responseObj["response"] as org.json.simple.JSONObject?
            val payload = res!!["payload"] as JSONObject?

            mBinding.textViewJobTitle.text = payload?.get("job_title") as String
        } catch (e: Exception) {

        }

    }

    private fun getViewModel(jobId : Int): SingleJobViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                Log.d("getViewModelStart", "@@@")
                @Suppress("UNCHECKED_CAST")
                return SingleJobViewModel(jobRepository, jobId) as T
            }
        }).get(SingleJobViewModel::class.java)
    }
}
