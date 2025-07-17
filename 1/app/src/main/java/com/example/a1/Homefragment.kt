package com.example.a1

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.a1.capsule.Capsule
import com.example.a1.repository.CapsuleRepository
import com.example.a1.repository.UserRepository
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class Homefragment : Fragment() {

    companion object { const val TAG = "HomeFragment" }

    private var capsules: List<Capsule> = emptyList()
    private var currIndex = 0

    private lateinit var tvName      : TextView
    private lateinit var tvDDay      : TextView
    private lateinit var ivPhoto     : ImageView
    private lateinit var tvIndicator : TextView
    private lateinit var btnPrev     : ImageButton
    private lateinit var btnNext     : ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UserRepository.init(requireContext())   // 혹시 모를 미초기화 대비

        tvName       = view.findViewById(R.id.tvCapsuleName)
        tvDDay       = view.findViewById(R.id.tvDDay)
        ivPhoto      = view.findViewById(R.id.ivCapsulePhoto)
        tvIndicator  = view.findViewById(R.id.tvPageIndicator)
        btnPrev      = view.findViewById(R.id.btnPrev)
        btnNext      = view.findViewById(R.id.btnNext)

        btnNext.setOnClickListener { move(+1) }
        btnPrev.setOnClickListener { move(-1) }

    }

    override fun onResume() { super.onResume(); refreshCapsules() }

    /* ───────── 목록 새로고침 ───────── */
    private fun refreshCapsules() {
        val uid = UserRepository.getCurrentUser()?.userId
        if (uid == null) { toast("로그인 정보를 찾을 수 없습니다."); return }

        CapsuleRepository.refreshCapsuleList(uid) { ok, err ->
            requireActivity().runOnUiThread {
                if (ok) {
                    capsules  = CapsuleRepository.getClosedCapsule()
                    currIndex = currIndex.coerceAtMost((capsules.size - 1).coerceAtLeast(0))
                } else toast("캡슐 불러오기 실패: $err")
                updateUI()
            }
        }
    }

    /* ───────── UI 업데이트 ───────── */
    private fun updateUI() {
        if (capsules.isEmpty()) {
            tvName.text = "캡슐이 없습니다"
            tvDDay.text = "—"
            tvIndicator.text = "0 / 0"
            ivPhoto.setImageResource(R.drawable.hourglass0)
            return
        }

        val cap = capsules[currIndex]
        tvName.text = cap.title

        /* D-Day */
        tvDDay.text = cap.ddayMillis?.let { ms ->
            val diff = ChronoUnit.DAYS.between(
                LocalDate.now(),
                Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()
            ).toInt()
            "D${if (diff >= 0) "-" else "+"}${abs(diff)}"
        } ?: "—"

        /* 진행도별 모래시계 */
        ivPhoto.setImageResource(selectHourglass(cap))

        tvIndicator.text = "${currIndex + 1} / ${capsules.size}"
    }

    /** 진행도 → hourglass1~4 매핑 */
    private fun selectHourglass(cap: Capsule): Int {
        val open    = cap.ddayMillis    ?: return R.drawable.hourglass0
        val created = cap.createdMillis ?: return R.drawable.hourglass0
        if (open <= created)             return R.drawable.hourglass4  // 데이터 이상

        val span    = open - created
        val elapsed = (System.currentTimeMillis() - created).coerceIn(0L, span)
        val ratio   = elapsed.toDouble() / span        // 0.0 ~ 1.0

        return when {
            ratio < 0.25 -> R.drawable.hourglass1
            ratio < 0.50 -> R.drawable.hourglass2
            ratio < 0.75 -> R.drawable.hourglass3
            else         -> R.drawable.hourglass4
        }
    }

    /* ───────── 기타 헬퍼 ───────── */
    private fun move(step: Int) {
        if (capsules.isNotEmpty()) {
            currIndex = (currIndex + step + capsules.size) % capsules.size
            updateUI()
        }
    }
    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
