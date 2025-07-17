package com.example.a1

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.a1.capsule.Capsule
import com.example.a1.repository.CapsuleRepository
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class Homefragment : Fragment() {

    /* ---------- 로그 TAG ---------- */
    companion object { const val TAG = "HomeFragment" }

    /* ---------- 데이터 ---------- */
    private var capsules: List<Capsule> = emptyList()
    private var currIndex               = 0
    private var userId: String?         = null

    /* ---------- View refs ---------- */
    private lateinit var tvName      : TextView
    private lateinit var tvDDay      : TextView
    private lateinit var ivPhoto     : ImageView
    private lateinit var tvIndicator : TextView
    private lateinit var btnPrev     : ImageButton
    private lateinit var btnNext     : ImageButton

    /* ---------- 생명주기 ---------- */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* SharedPreferences 에서 userId 로드 */
        userId = requireContext()
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("userId", null)

        /* View 바인딩 */
        tvName       = view.findViewById(R.id.tvCapsuleName)
        tvDDay       = view.findViewById(R.id.tvDDay)
        ivPhoto      = view.findViewById(R.id.ivCapsulePhoto)
        tvIndicator  = view.findViewById(R.id.tvPageIndicator)
        btnPrev      = view.findViewById(R.id.btnPrev)
        btnNext      = view.findViewById(R.id.btnNext)

        btnNext.setOnClickListener { move(+1) }
        btnPrev.setOnClickListener { move(-1) }

        refreshCapsules()          // 첫 화면
    }

    override fun onResume() {
        super.onResume()
        refreshCapsules()          // 홈 탭으로 돌아올 때 새로고침
    }

    /* ---------- Repository 통해 목록 새로고침 ---------- */
    private fun refreshCapsules() {
        val uidInt = userId?.toIntOrNull()
        if (uidInt == null) {
            toast("로그인 정보를 찾을 수 없습니다.")
            capsules = emptyList()
            updateUI()
            return
        }

        CapsuleRepository.refreshCapsuleList(uidInt) { success, err ->
            requireActivity().runOnUiThread {
                if (success) {
                    capsules   = CapsuleRepository.getClosedCapsule()
                    currIndex  = if (capsules.isEmpty()) 0 else currIndex % capsules.size
                } else {
                    toast("캡슐 불러오기 실패: $err")
                    capsules = emptyList()
                }
                updateUI()
            }
        }
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
            ivPhoto.setImageResource(R.drawable.hourglass0)
            return
        }

        val cap = capsules[currIndex]
        tvName.text = cap.title

        /* D-Day 계산 */
        tvDDay.text = cap.ddayMillis?.let { ms ->
            val diff = ChronoUnit.DAYS.between(
                LocalDate.now(),
                Instant.ofEpochMilli(ms)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            ).toInt()
            "D${if (diff >= 0) "-" else "+"}${abs(diff)}"
        } ?: "—"

        /* 모래시계 단계별 아이콘 */
       ivPhoto.setImageResource(R.drawable.hourglass1)

        /* 페이지 표시 */
        tvIndicator.text = "${currIndex + 1} / ${capsules.size}"
    }



    /* ---------- 모래시계 단계 선택 ---------- */
/*
    private fun selectHourglass(cap: Capsule): Int {
        val open    = cap.ddayMillis     ?: return R.drawable.hourglass0
        val created = cap.createdMillis ?: return R.drawable.hourglass0

        /* ① open 이 created 이전(또는 동일)이면 데이터 오류 → 모두 내려간 상태로 처리 */
        if (open <= created) return R.drawable.hourglass4      // ★ 추가

        /* ② 정상 진행률 계산 */
        val span    = open - created                           // 항상 양수
        val elapsed = (System.currentTimeMillis() - created)
            .coerceIn(0L, span)
        val ratio   = elapsed.toDouble() / span                // 0.0 ~ 1.0

        return when {
            ratio < 0.25 -> R.drawable.hourglass1   // 상단 100 %
            ratio < 0.50 -> R.drawable.hourglass2   // 상단 ¾
            ratio < 0.75 -> R.drawable.hourglass3   // 상단 ½
            ratio < 1.00 -> R.drawable.hourglass4   // 상단 ¼
            else         -> R.drawable.hourglass4   // 모두 내려감
        }
    }
    */



    /* ---------- Toast 헬퍼 ---------- */
    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
