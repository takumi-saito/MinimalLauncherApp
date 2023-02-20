package com.kireaji.minimallauncherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

internal class AppAdapter(
    val onClickAppInfo: (view: View, appInfo: AppInfo) -> Unit
) : ListAdapter<AppInfo, AppInfoViewHolder>(ITEM_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppInfoViewHolder(inflater.inflate(R.layout.item_app, parent, false))
    }
    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
        val appInfo = getItem(position)
        with(holder) {
            textAppName.text = appInfo.label
            textPackageName.text = appInfo.componentName.packageName
            imageIcon.setImageDrawable(appInfo.icon)
            holder.itemView.setOnClickListener {
                onClickAppInfo(it, appInfo)
            }
        }
    }

    fun updateList(newList: List<AppInfo>) {
       submitList(newList)
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<AppInfo>() {
            override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
                return oldItem.componentName == newItem.componentName
            }
            override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
                return oldItem.icon.equals(newItem.icon) && oldItem.label == newItem.label
            }
        }
    }
}
internal class AppInfoViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val textAppName: TextView = root.findViewById(R.id.text_app_name)
    val imageIcon: ImageView = root.findViewById(R.id.image_icon)
    val textPackageName: TextView = root.findViewById(R.id.package_name)
}