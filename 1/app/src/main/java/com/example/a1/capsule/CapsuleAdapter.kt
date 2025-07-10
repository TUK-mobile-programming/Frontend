package com.example.a1.capsule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a1.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CapsuleAdapter(private val items: List<Capsule>)
    : RecyclerView.Adapter<CapsuleAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title = v.findViewById<TextView>(R.id.txtCapsuleTitle)
        val tag   = v.findViewById<TextView>(R.id.txtCapsuleTag)
        val dday  = v.findViewById<TextView>(R.id.txtDdayNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_capsule, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.tag.text   = item.tag

        // ── D-Day 계산 ──
        val today    = LocalDate.now()
        val diff     = ChronoUnit.DAYS.between(today, item.openDate).toInt()
        holder.dday.text = "D${if (diff >= 0) "-" else "+"}${kotlin.math.abs(diff)}"
    }

    override fun getItemCount() = items.size
}