package com.example.ftpplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileListAdapter(
    private val items: MutableList<FtpFileItem> = mutableListOf(),
    private val onClick: (FtpFileItem) -> Unit
) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    fun submitList(newItems: List<FtpFileItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val name: TextView = view.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        val iconRes = when {
            item.isDirectory -> android.R.drawable.ic_menu_agenda
            item.isVideoFile() -> android.R.drawable.ic_media_play
            else -> android.R.drawable.ic_menu_help
        }
        holder.icon.setImageResource(iconRes)
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
