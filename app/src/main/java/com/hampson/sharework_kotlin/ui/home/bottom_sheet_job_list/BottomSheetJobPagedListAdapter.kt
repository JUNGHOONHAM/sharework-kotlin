package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list

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
import com.hampson.sharework_kotlin.data.vo.Tag
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoActivity
import org.jetbrains.anko.backgroundResource
import java.io.Serializable

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
        private val textViewJobDate = itemView.findViewById<TextView>(R.id.textViewJobDate)
        private val textViewJobTime = itemView.findViewById<TextView>(R.id.textViewJobTime)
        private val textViewPay = itemView.findViewById<TextView>(R.id.textViewPay)
        private val textViewJobType = itemView.findViewById<TextView>(R.id.textViewJobType)
        private val textViewPayType = itemView.findViewById<TextView>(R.id.textViewPayType)
        private val imageViewProfile = itemView.findViewById<ImageView>(R.id.imageViewProfile)
        private val layoutTag = itemView.findViewById<LinearLayout>(R.id.layoutTag)

        fun bind(job: Job?, context: Context) {
            textViewTitle.text = job?.job_title
            textViewJobDate.text = job?.job_date
            textViewJobTime.text = job?.start_date + " ~ " + job?.end_date
            textViewPay.text = job?.pay
            textViewJobType.text = job?.job_type
            textViewPayType.text = job?.pay_type

            Glide.with(context)
                .load(job?.jobable?.user?.profile_img)
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(imageViewProfile)

            val tagList = job?.tags
            if (tagList != null) {
                for (tag in tagList) {
                    bindTag(tag, context)
                }
            }

            itemView.setOnClickListener {
                Intent(context, JobInfoActivity::class.java).apply {
                    putExtra("jobId", job?.id)
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
            layoutTag.addView(textView)
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