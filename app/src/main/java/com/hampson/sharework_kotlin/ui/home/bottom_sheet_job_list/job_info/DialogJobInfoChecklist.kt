package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.vo.JobChecklists
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.UserChecklist
import com.hampson.sharework_kotlin.databinding.DialogJobInfoChecklistBinding
import com.hampson.sharework_kotlin.session.SessionManagement

class DialogJobInfoChecklist(context: FragmentActivity?, jobChecklists: ArrayList<JobChecklists>): DialogFragment(),
    JobInfoChecklistAdapter.OnItemClickListener {

    private lateinit var  mBinding : DialogJobInfoChecklistBinding
    private val jobChecklists = jobChecklists
    private var selectChecklistList: MutableList<JobChecklists> = mutableListOf()

    private lateinit var mDialogResult: OnDialogResult

    private lateinit var adapter: JobInfoChecklistAdapter

    private val context = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = DialogJobInfoChecklistBinding.inflate(layoutInflater, container, false)

        setRecyclerView()

        mBinding.textViewCancel.setOnClickListener {
            dismiss()
        }

        mBinding.textViewOk.setOnClickListener {
            if (mDialogResult != null) {
                mDialogResult.finish(selectChecklistList as ArrayList<JobChecklists>)
                dismiss()
            }
        }

        return mBinding.root
    }

    private fun initDisplaySize() {
        var params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    private fun setRecyclerView() {
        adapter = JobInfoChecklistAdapter(context, jobChecklists)
        val layout = LinearLayoutManager(context)
        adapter.setOnJobInfoChecklistClickListener(this)

        mBinding.recyclerView.layoutManager = layout
        mBinding.recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        initDisplaySize()
    }

    fun setDialogResult(dialogResult: OnDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnDialogResult {
        fun finish(result: ArrayList<JobChecklists>)
    }

    override fun onItemClick(jobChecklists: JobChecklists, textViewContents: TextView) {

        if (selectChecklistList.contains(jobChecklists)) {
            selectChecklistList.remove(jobChecklists)
            textViewContents.setTextColor(Color.rgb(153, 153, 153))
            textViewContents.setBackgroundResource(R.drawable.background_border_gray)
            val checkImage = getContext()?.resources?.getDrawable(R.drawable.ic_baseline_check_circle_24_gray)
            textViewContents.setCompoundDrawablesWithIntrinsicBounds(checkImage, null, null, null)
        } else {
            selectChecklistList.add(jobChecklists)
            textViewContents.setTextColor(Color.rgb(100, 216, 209))
            textViewContents.setBackgroundResource(R.drawable.background_border_mint)
            val checkImage = getContext()?.resources?.getDrawable(R.drawable.ic_baseline_check_circle_24_mint)
            textViewContents.setCompoundDrawablesWithIntrinsicBounds(checkImage, null, null, null)
        }
    }
}