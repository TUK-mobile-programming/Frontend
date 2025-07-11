package com.example.a1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeCapsuleAdapter(private val timeCapsules: List<TimeCapsule>) :
    RecyclerView.Adapter<TimeCapsuleAdapter.TimeCapsuleViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: TimeCapsule)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeCapsuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_capsule, parent, false) // <-- 여기서 list_item_capsule.xml을 사용합니다.
        return TimeCapsuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeCapsuleViewHolder, position: Int) {
        val currentItem = timeCapsules[position]
        holder.imageView.setImageResource(currentItem.imageResId)
        holder.titleTextView.text = currentItem.title
        holder.dateTextView.text = currentItem.openDate

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position, currentItem)
        }
    }

    override fun getItemCount(): Int {
        return timeCapsules.size
    }

    class TimeCapsuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.listed_item_image)
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val dateTextView: TextView = itemView.findViewById(R.id.item_date)
    }
}