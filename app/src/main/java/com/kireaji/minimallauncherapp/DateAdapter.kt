package com.kireaji.minimallauncherapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kireaji.minimallauncherapp.data.model.DateInfo

private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DateInfo>() {


    override fun areItemsTheSame(oldItem: DateInfo, newItem: DateInfo): Boolean {
        return oldItem.date == newItem.date
    }
    override fun areContentsTheSame(oldItem: DateInfo, newItem: DateInfo): Boolean {
        return oldItem == newItem
    }
}

class DateAdapter : ListAdapter<DateInfo, DateAdapter.DateViewHolder>(ITEM_CALLBACK) {
    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textDate = view.findViewById<TextView>(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val dateInfo: DateInfo = getItem(position)
        holder.textDate.text = dateInfo.date
        if (dateInfo.textColor != null) {
            holder.textDate.setTextColor(dateInfo.textColor!!)
        } else {
            val defaultColor = holder.textDate.textColors.defaultColor
            Log.d("saito", "default color $defaultColor")
            holder.textDate.setTextColor(defaultColor)
        }
    }
}