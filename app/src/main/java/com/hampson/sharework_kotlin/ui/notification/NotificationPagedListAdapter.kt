package com.hampson.sharework_kotlin.ui.notification

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
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
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.Notification
import com.hampson.sharework_kotlin.data.vo.Tag
import com.hampson.sharework_kotlin.databinding.ItemJobListBinding
import com.hampson.sharework_kotlin.databinding.ItemNotificationBinding
import com.hampson.sharework_kotlin.databinding.NetworkStateItemBinding
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoActivity
import org.jetbrains.anko.backgroundResource
import java.io.Serializable

class NotificationPagedListAdapter(public val context: Context) : PagedListAdapter<Notification, RecyclerView.ViewHolder>(
    NotificationDiffCallback()
) {

    val Notification_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == Notification_VIEW_TYPE) {
            (holder as NotificationItemViewHolder).bind(getItem(position), context)
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
            Notification_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        if (viewType == Notification_VIEW_TYPE) {
            val binding = ItemNotificationBinding.inflate(layoutInflater)
            return NotificationItemViewHolder(
                binding
            )
        } else {
            val binding = NetworkStateItemBinding.inflate(layoutInflater)
            return NetworkStateItemViewHolder(
                binding
            )
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

    }

    class NotificationItemViewHolder (private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification?, context: Context) {
            val notType = notification?.not_type
            val userType = notification?.user_type
            val job = notification?.job

            if (notType == context.getString(R.string.hired)) {
                binding.imageView.setImageResource(R.drawable.ic_alram1)
                binding.textViewMessage.text = job?.job_title + " 에 채택되었습니다. 늦지 않게 도착해주세요."
            } else if (notType == context.getString(R.string.approved)) {
                binding.imageView.setImageResource(R.drawable.ic_alram2)
                binding.textViewMessage.text = job?.job_title + " 사장님이 알바를 승인 하셨어요. 힘차게 일해 볼까요!"
            } else if (notType == context.getString(R.string.completed)) {
                binding.imageView.setImageResource(R.drawable.ic_alram3)
                binding.textViewMessage.text = job?.job_title + " 일이 끝났어요. 고생 많으셨습니다!"
            } else if (notType == context.getString(R.string.rejected)) {
                binding.imageView.setImageResource(R.drawable.ic_alram4)
                binding.textViewMessage.text = job?.job_title + " 에서 아쉽게도 일을 취소하셨어요. 다른 곳에 지원해주세요."
            } else if (notType == context.getString(R.string.extended)) {
                binding.imageView.setImageResource(R.drawable.ic_alram5)
                binding.textViewMessage.text = job?.job_title + " 사장님이 일을 연장하셨습니다."
            } else if (notType == context.getString(R.string.review)) {
                binding.imageView.setImageResource(R.drawable.ic_alram3)
                binding.textViewMessage.text = job?.job_title + " 님이 소중한 리뷰를 남겨주셨습니다."
            } else if (notType == context.getString(R.string.applied)) {
                binding.imageView.setImageResource(R.drawable.ic_alram3)
                binding.textViewMessage.text = job?.job_title + " 에 " + notification.sender.name + "님이 지원하였습니다."
            } else if (notType == context.getString(R.string.failed)) {
                binding.imageView.setImageResource(R.drawable.ic_alram3)
                binding.textViewMessage.text = job?.job_title + " 에 아무도 채택되지 않거나 출근하지 않아 구인이 자동으로 마감되었습니다."
            }


            itemView.setOnClickListener {
                Intent(context, JobInfoActivity::class.java).apply {
                    putExtra("jobId", job?.id)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
            }
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