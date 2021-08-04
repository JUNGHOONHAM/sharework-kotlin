package com.hampson.sharework_kotlin.ui.user_profile.review

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hampson.sharework_kotlin.data.vo.UserJobRateReview
import com.hampson.sharework_kotlin.databinding.ItemProfileRateReviewBinding

class ReviewBaseAdapter(val context: Context?, userJobRateReview: HashMap<String, Int>) : RecyclerView.Adapter<ViewHolder>() {

    private val valueList = ArrayList(userJobRateReview.values)
    private val keyList = ArrayList(userJobRateReview.keys)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProfileRateReviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val value = valueList[position]
        val key = keyList[position]

        holder.textViewReviewCount.text = value.toString() + "회"
        holder.textViewReviewName.text = key
    }

    override fun getItemCount(): Int {
        return keyList.size
    }

}

class ViewHolder (binding: ItemProfileRateReviewBinding) : RecyclerView.ViewHolder(binding.root) {
    val textViewReviewCount: TextView = binding.textViewReviewCount
    val textViewReviewName: TextView = binding.textViewReviewName
}