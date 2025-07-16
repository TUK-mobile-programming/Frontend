package com.example.a1.cpasule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a1.R
import com.example.a1.capsule.Capsule
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * CapsuleAdapter
 *
 * @param items     캡슐 목록
 * @param onClick   아이템 클릭 이벤트 (선택)
 */
class CapsuleAdapter(
    private var items: List<Capsule>,
    private val onClick: (Capsule) -> Unit = {}
) : RecyclerView.Adapter<CapsuleAdapter.VH>() {

    /** ViewHolder */
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle       : TextView = view.findViewById(R.id.txtCapsuleTitle)
        val txtTags        : TextView = view.findViewById(R.id.txtCapsuleTag)
        val txtDdayNumber  : TextView = view.findViewById(R.id.txtDdayNumber)
        val imgCapsuleIcon : ImageView = view.findViewById(R.id.imgCapsuleIcon)

        init {
            view.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_capsule, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        /* ───── 텍스트 바인딩 ───── */
        holder.txtTitle.text = item.title
        holder.txtTags.text  = item.tags

        /* ───── D-Day 계산 ───── */
        holder.txtDdayNumber.text = item.ddayMillis?.let { millis ->
            val openDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val diff = ChronoUnit.DAYS.between(LocalDate.now(), openDate).toInt()
            "D${if (diff >= 0) "-" else "+"}${abs(diff)}"
        } ?: "—"

        /* ───── 알약 색상 결정 ─────
           · 공동 캡슐(on)          → purple_500
           · 조건 설정 값 존재       → green_500
           · 그 외                 → gray_400
         */
        val pillColorRes = when {
            item.isJoint           -> R.color.purple_500
            item.condition != null -> R.color.green_500
            else                   -> R.color.gray_400
        }
        holder.imgCapsuleIcon.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, pillColorRes),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    override fun getItemCount(): Int = items.size

    /** 외부에서 리스트 갱신 */
    fun submitList(newItems: List<Capsule>) {
        items = newItems
        notifyDataSetChanged()
    }
}