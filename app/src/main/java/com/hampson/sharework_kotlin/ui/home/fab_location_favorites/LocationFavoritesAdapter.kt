package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.databinding.ItemLocationFavoritesBinding

class LocationFavoritesAdapter(val context: Context?) : RecyclerView.Adapter<ViewHolder>() {

    private var data = mutableListOf<LocationFavorites>()

    interface OnItemClickListener {
        fun onDeleteClick(locationFavorites: LocationFavorites)
    }

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLocationFavoritesBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = data[position]

        holder.textViewLocationName.text = location.location_name

        holder.buttonDelete.setOnClickListener {
            onItemClickListener?.onDeleteClick(location)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnLocationFavoritesClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun replaceList(newList: MutableList<LocationFavorites>) {
        data = newList.toMutableList()
        notifyDataSetChanged()
    }
}

class ViewHolder (binding: ItemLocationFavoritesBinding) : RecyclerView.ViewHolder(binding.root) {
    val textViewLocationName: TextView = binding.textViewLocationName
    val buttonDelete: Button = binding.buttonDelete
}