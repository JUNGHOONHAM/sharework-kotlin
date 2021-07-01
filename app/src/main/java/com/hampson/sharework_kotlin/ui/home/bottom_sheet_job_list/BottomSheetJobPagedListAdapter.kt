package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job

class BottomSheetJobPagedListAdapter(public val context: Context) : PagedListAdapter<Job, RecyclerView.ViewHolder>(
    JobDiffCallback()
) {

    val JOB_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == JOB_VIEW_TYPE) {
            (holder as JobItemViewHolder).bind(getItem(position), context)
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
            JOB_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == JOB_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.item_job_list, parent, false)
            return JobItemViewHolder(
                view
            )
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(
                view
            )
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }

    }

    class JobItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val textViewTitle = itemView.findViewById<TextView>(R.id.textViewTitle)
        fun bind(job: Job?, context: Context) {
            Log.d("job TEST", job?.toString())
            Log.d("job TEST", job?.job_title.toString())
            textViewTitle.text = job?.job_title
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
                textViewErrorMessage.visibility = View.VISIBLE
                textViewErrorMessage.text = networkState.msg
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