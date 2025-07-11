package com.example.a1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Homefragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    /* ───── 데이터 배열 ───── */
    private val names  = arrayOf("A 타임캡슐", "B 타임캡슐", "C 타임캡슐")
    private val images = intArrayOf(
        R.drawable.hourglass,
        R.drawable.hourglass2,
        R.drawable.hourglass3
    )
    private val dDays  = arrayOf("D-Day 101", "D-Day 202", "D-Day 303")

    private var currIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName   = view.findViewById<TextView>(R.id.tvCapsuleName)
        val tvDDay   = view.findViewById<TextView>(R.id.tvDDay)            // ★ D-Day TextView 참조
        val ivPhoto  = view.findViewById<ImageView>(R.id.ivCapsulePhoto)
        val btnPrev  = view.findViewById<ImageButton>(R.id.btnPrev)
        val btnNext  = view.findViewById<ImageButton>(R.id.btnNext)

        fun updateUI() {
            tvName.text = names[currIndex]
            tvDDay.text = dDays[currIndex]                                 // ★ D-Day 갱신
            ivPhoto.setImageResource(images[currIndex])
        }
        updateUI()

        btnNext.setOnClickListener {
            currIndex = (currIndex + 1) % names.size
            updateUI()
        }

        btnPrev.setOnClickListener {
            currIndex = (currIndex - 1 + names.size) % names.size
            updateUI()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Homefragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
