package com.hampson.sharework_kotlin.ui.user_profile.review

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
import com.hampson.sharework_kotlin.data.vo.Tag
import com.hampson.sharework_kotlin.data.vo.UserJobRateReview
import com.hampson.sharework_kotlin.databinding.ItemJobListBinding
import com.hampson.sharework_kotlin.databinding.ItemReviewListBinding
import com.hampson.sharework_kotlin.databinding.NetworkStateItemBinding
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoActivity
import org.jetbrains.anko.backgroundResource
import java.io.Serializable

class ReviewPagedListAdapter(public val context: Context) : PagedListAdapter<UserJobRateReview, RecyclerView.ViewHolder>(
    ReviewDiffCallback()
) {

    val REVIEW_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == REVIEW_VIEW_TYPE) {
            (holder as ReviewItemViewHolder).bind(getItem(position), context)
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
            REVIEW_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        if (viewType == REVIEW_VIEW_TYPE) {
            val binding = ItemReviewListBinding.inflate(layoutInflater)
            return ReviewItemViewHolder(
                binding
            )
        } else {
            val binding = NetworkStateItemBinding.inflate(layoutInflater)
            return NetworkStateItemViewHolder(
                binding
            )
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<UserJobRateReview>() {
        override fun areItemsTheSame(oldItem: UserJobRateReview, newItem: UserJobRateReview): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserJobRateReview, newItem: UserJobRateReview): Boolean {
            return oldItem == newItem
        }

    }

    class ReviewItemViewHolder (private val binding: ItemReviewListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: UserJobRateReview?, context: Context) {
            binding.textViewName.text = review?.sender?.name
            binding.textViewContents.text = review?.contents
            binding.ratingBar.rating = review!!.rating

            Glide.with(context)
                .load(review?.sender?.profile_img)
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(binding.imageViewProfile)
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