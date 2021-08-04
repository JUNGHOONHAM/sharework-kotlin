package com.hampson.sharework_kotlin.ui.user_profile.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hampson.sharework_kotlin.data.vo.UserJobRateReview
import com.hampson.sharework_kotlin.databinding.ItemProfileRateReviewBinding

class ReviewBaseAdapter(val context: Context?, userJobRateReview: ArrayList<UserJobRateReview>) : RecyclerView.Adapter<ViewHolder>() {

    private var data = userJobRateReview

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProfileRateReviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rateReview = data[position]

        holder.textViewReviewCount.text = rateReview.review_base_count_list.values.toString()
        holder.textViewReviewName.text = rateReview.review_base_count_list.keys.toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class ViewHolder (binding: ItemProfileRateReviewBinding) : RecyclerView.ViewHolder(binding.root) {
    val textViewReviewCount: TextView = binding.textViewReviewCount
    val textViewReviewName: TextView = binding.textViewReviewName
}