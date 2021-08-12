package com.hampson.sharework_kotlin.ui.application_history

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Tag
import com.hampson.sharework_kotlin.databinding.ItemJobListBinding
import com.hampson.sharework_kotlin.databinding.NetworkStateItemBinding
import com.hampson.sharework_kotlin.ui.home_worker.bottom_sheet_job_list.job_info.JobInfoActivity
import org.jetbrains.anko.backgroundResource

class HistoryPagedListAdapter(public val context: Context) : PagedListAdapter<JobApplication, RecyclerView.ViewHolder>(
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

        if (viewType == APPLICATION_VIEW_TYPE) {
            val binding = ItemJobListBinding.inflate(layoutInflater)
            return ApplicationItemViewHolder(
                binding
            )
        } else {
            val binding = NetworkStateItemBinding.inflate(layoutInflater)
            return NetworkStateItemViewHolder(
                binding
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

    class ApplicationItemViewHolder (private val binding: ItemJobListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(application: JobApplication?, context: Context) {
            binding.textViewTitle.text = application?.job?.job_title
            binding.textViewJobDate.text = application?.job?.job_date
            binding.textViewJobTime.text = application?.job?.start_date + " ~ " + application?.job?.end_date
            binding.textViewPay.text = application?.job?.pay
            binding.textViewJobType.text = application?.job?.job_type
            binding.textViewPayType.text = application?.job?.pay_type

            Glide.with(context)
                .load(application?.job?.jobable?.user?.profile_img)
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(binding.imageViewProfile)

            val tagList = application?.job?.tags
            if (tagList != null) {
                binding.layoutTag.removeAllViews()

                for (tag in tagList) {
                    bindTag(tag, context)
                }
            }

            itemView.setOnClickListener {
                Intent(context, JobInfoActivity::class.java).apply {
                    putExtra("jobId", application?.job?.id)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
            }
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
            binding.layoutTag.addView(textView)
        }
    }

    class NetworkStateItemViewHolder (private val binding: NetworkStateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(networkState: NetworkState?) {

            if (networkState != null && networkState == NetworkState.LOADING) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                binding.textViewErrorMessage.visibility = View.VISIBLE
                binding.textViewErrorMessage.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                binding.textViewErrorMessage.visibility = View.GONE
                // textViewErrorMessage.text = networkState.msg
            } else {
                binding.textViewErrorMessage.visibility = View.GONE
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