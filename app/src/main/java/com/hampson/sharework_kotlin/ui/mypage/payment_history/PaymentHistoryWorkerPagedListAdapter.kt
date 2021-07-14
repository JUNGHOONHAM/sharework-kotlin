package com.hampson.sharework_kotlin.ui.mypage.payment_history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Tag
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoActivity
import org.jetbrains.anko.backgroundResource
import java.io.Serializable

class PaymentHistoryWorkerPagedListAdapter(public val context: Context) : PagedListAdapter<JobApplication, RecyclerView.ViewHolder>(
    ApplicationDiffCallback()
) {

    val APPLICATION_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == APPLICATION_VIEW_TYPE) {
            (holder as ApplicationItemViewHolder).bind(getItem(position), context)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            APPLICATION_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == APPLICATION_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.item_payment_history_worker, parent, false)
            return ApplicationItemViewHolder(
                view
            )
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(
                view
            )
        }
    }

    class ApplicationDiffCallback : DiffUtil.ItemCallback<JobApplication>() {
        override fun areItemsTheSame(oldItem: JobApplication, newItem: JobApplication): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JobApplication, newItem: JobApplication): Boolean {
            return oldItem == newItem
        }

    }

    class ApplicationItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val textViewJobTitle = itemView.findViewById<TextView>(R.id.textViewJobTitle)
        private val textViewWorkDate = itemView.findViewById<TextView>(R.id.textViewWorkDate)
        private val textViewWorkTime = itemView.findViewById<TextView>(R.id.textViewWorkTime)
        private val textViewWorkPay = itemView.findViewById<TextView>(R.id.textViewWorkPay)

        fun bind(application: JobApplication?, context: Context) {
            textViewJobTitle.text = application?.job?.job_title

            itemView.setOnClickListener {
                //Intent(context, JobInfoActivity::class.java).apply {
                //    putExtra("jobId", application?.job)
                //    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //}.run { context.startActivity(this) }
            }
        }
    }

    class NetworkStateItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
        private val textViewErrorMessage = itemView.findViewById<TextView>(R.id.textViewErrorMessage)

        fun bind(networkState: NetworkState?) {

            if (networkState != null && networkState == NetworkState.LOADING) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                textViewErrorMessage.visibility = View.VISIBLE
                textViewErrorMessage.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                textViewErrorMessage.visibility = View.GONE
                // textViewErrorMessage.text = networkState.msg
            } else {
                textViewErrorMessage.visibility = View.GONE
            }

        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}