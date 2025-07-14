// CapsuleAdapter.kt
package com.example.a1.capsule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a1.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * 캡슐 목록을 표시하는 RecyclerView Adapter
 *
 * @param items   처음에 표시할 캡슐 리스트
 * @param onClick 아이템 클릭 시 호출되는 람다
 */
class CapsuleAdapter(
    // **[수정]**: 'List'를 'MutableList'로, 'val'을 'var'로 변경
    private var items: MutableList<Capsule>,
    private val onClick: (Capsule) -> Unit
) : RecyclerView.Adapter<CapsuleAdapter.VH>() {

    /** 뷰홀더 */
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle      : TextView  = view.findViewById(R.id.txtCapsuleTitle)
        private val txtTags       : TextView  = view.findViewById(R.id.txtCapsuleTag)
        private val txtDdayNumber : TextView  = view.findViewById(R.id.txtDdayNumber)
        private val imgCapsuleIcon: ImageView = view.findViewById(R.id.imgCapsuleIcon)

        init {
            view.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(items[pos])
            }
        }

        /** 데이터 바인딩 */
        fun bind(item: Capsule) {
            // 제목 & 태그
            txtTitle.text = item.title
            txtTags.text  = item.tags

            // D-Day 계산
            txtDdayNumber.text = item.ddayMillis?.let { millis ->
                val openDate = Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val diff = ChronoUnit.DAYS.between(LocalDate.now(), openDate).toInt()
                "D${if (diff >= 0) "-" else "+"}${abs(diff)}"
            } ?: "—"

            // 아이콘 색상 결정
            val colorRes = when {
                item.isJoint           -> R.color.purple_500   // 공동 캡슐
                item.condition != null -> R.color.green_500    // 조건 존재
                else                   -> R.color.gray_400     // 기본
            }
            imgCapsuleIcon.setColorFilter(
                ContextCompat.getColor(itemView.context, colorRes),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            // TODO: 이미지 로드 로직 (mediaUri가 Capsule에 있다면 Glide 등으로 추가)
            // if (!item.mediaUri.isNullOrEmpty()) {
            //     Glide.with(itemView.context)
            //         .load(Uri.parse(item.mediaUri))
            //         .into(imgCapsuleIcon)
            // } else {
            //     imgCapsuleIcon.setImageResource(R.drawable.default_capsule_image)
            // }
        }
    }

    /* ---------------- RecyclerView  콜백 ---------------- */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_capsule, parent, false) // <-- 레이아웃 ID 확인
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /* ------------- 외부에서 리스트 갱신 ------------- */
    // **[수정]**: 'submitList'를 'updateData'로 변경하고, 로직을 clear/addAll로 변경
    fun updateData(newItems: List<Capsule>) {
        items.clear()          // 기존 목록을 지웁니다.
        items.addAll(newItems) // 새 목록을 추가합니다.
        notifyDataSetChanged() // RecyclerView에 데이터가 변경되었음을 알립니다.
    }
}