package com.example.a1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Glide import
import android.net.Uri // Uri import

class TimeCapsuleAdapter(private var timeCapsules: MutableList<TimeCapsule>) : // List를 MutableList로, val을 var로 변경
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
            .inflate(R.layout.list_item_capsule, parent, false) // list_item_capsule.xml 사용
        return TimeCapsuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeCapsuleViewHolder, position: Int) {
        val currentItem = timeCapsules[position]

        // Glide를 사용하여 이미지 로드 (imageResId 대신 mediaUri 사용)
        if (!currentItem.mediaUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse(currentItem.mediaUri))
                .placeholder(R.drawable.sampleimage) // 로딩 중 기본 이미지 (필요하면 추가)
                .error(R.drawable.sampleimage)       // 에러 시 기본 이미지 (필요하면 추가)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.sampleimage) // URI 없으면 기본 이미지
        }

        holder.titleTextView.text = currentItem.title
        holder.dateTextView.text = currentItem.openDate

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position, currentItem)
        }
    }

    override fun getItemCount(): Int {
        return timeCapsules.size
    }

    // 데이터 갱신 함수 (변수 이름 timeCapsules로 일치)
    fun updateData(newTimeCapsuleList: List<TimeCapsule>) {
        timeCapsules.clear() // 'timeCapsuleList' 대신 'timeCapsules' 사용
        timeCapsules.addAll(newTimeCapsuleList) // 'timeCapsuleList' 대신 'timeCapsules' 사용
        notifyDataSetChanged()
    }

    // ViewHolder 내부 클래스 (ID 확인 필수)
    class TimeCapsuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.listed_item_image)
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val dateTextView: TextView = itemView.findViewById(R.id.item_date)
    }
}