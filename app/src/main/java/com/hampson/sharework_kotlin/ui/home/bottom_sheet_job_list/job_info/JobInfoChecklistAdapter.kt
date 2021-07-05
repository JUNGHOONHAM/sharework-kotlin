package com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hampson.sharework_kotlin.data.vo.JobChecklists
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.UserChecklist
import com.hampson.sharework_kotlin.databinding.ItemJobInfoChecklistBinding
import com.hampson.sharework_kotlin.databinding.ItemLocationFavoritesBinding

class JobInfoChecklistAdapter(val context: Context?, jobChecklists: ArrayList<JobChecklists>) : RecyclerView.Adapter<ViewHolder>() {

    private var data = jobChecklists

    interface OnItemClickListener {
        fun onItemClick(jobChecklists: JobChecklists, textViewContents: TextView)
    }

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemJobInfoChecklistBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checklists = data[position]

        holder.textViewContents.text = checklists.contents

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(checklists, holder.textViewContents)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnJobInfoChecklistClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}

class ViewHolder (binding: ItemJobInfoChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
    val textViewContents: TextView = binding.textViewContents
}