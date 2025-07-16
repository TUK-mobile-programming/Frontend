package com.example.a1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.a1.capsule.Capsule
import com.example.a1.repository.CapsuleRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class Homefragment : Fragment() {

    /* ---------- 데이터 ---------- */
    private var capsules: List<Capsule> = emptyList()
    private var currIndex               = 0

    /* ---------- View refs ---------- */
    private lateinit var tvName        : TextView
    private lateinit var tvDDay        : TextView
    private lateinit var ivPhoto       : ImageView
    private lateinit var tvIndicator   : TextView
    private lateinit var btnPrev       : ImageButton
    private lateinit var btnNext       : ImageButton

    /* ---------- 생명주기 ---------- */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* View 바인딩 */
        tvName      = view.findViewById(R.id.tvCapsuleName)
        tvDDay      = view.findViewById(R.id.tvDDay)
        ivPhoto     = view.findViewById(R.id.ivCapsulePhoto)
        tvIndicator = view.findViewById(R.id.tvPageIndicator)
        btnPrev     = view.findViewById(R.id.btnPrev)
        btnNext     = view.findViewById(R.id.btnNext)

        btnNext.setOnClickListener { move(+1) }
        btnPrev.setOnClickListener { move(-1) }

        refreshData()          // 첫 화면
    }

    override fun onResume() {
        super.onResume()
        refreshData()          // 홈 탭으로 돌아올 때 새로고침
    }

    /* ---------- 데이터 로드 ---------- */
    private fun refreshData() {
        capsules = CapsuleRepository.getClosedCapsule()
        if (capsules.isEmpty()) currIndex = 0 else currIndex %= capsules.size
        updateUI()
    }

    /* ---------- 좌·우 이동 ---------- */
    private fun move(step: Int) {
        if (capsules.isNotEmpty()) {
            currIndex = (currIndex + step + capsules.size) % capsules.size
            updateUI()
        }
    }

    /* ---------- UI 표기 ---------- */
    private fun updateUI() {
        if (capsules.isEmpty()) {
            tvName.text      = "캡슐이 없습니다"
            tvDDay.text      = "—"
            tvIndicator.text = "0 / 0"
            ivPhoto.setImageResource(R.drawable.hourglass1)
            return
        }
        val cap = capsules[currIndex]

        tvName.text = cap.title

        // D-Day
        tvDDay.text = cap.ddayMillis?.let { ms ->
            val diff = ChronoUnit.DAYS.between(
                LocalDate.now(),
                Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()
            ).toInt()
            "D${if (diff >= 0) "-" else "+"}${abs(diff)}"
        } ?: "—"

        // 이미지
        if (!cap.mediaUri.isNullOrBlank()) {
            Glide.with(this).load(cap.mediaUri)
                .placeholder(R.drawable.hourglass1)
                .into(ivPhoto)
        } else ivPhoto.setImageResource(R.drawable.hourglass1)

        // ★ 페이지 인디케이터
        tvIndicator.text = "${currIndex + 1} / ${capsules.size}"
    }
}
