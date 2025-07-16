package com.example.a1

import android.util.Log
import android.view.*
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.a1.capsule.Capsule
import com.example.a1.network.ApiClient
import org.json.JSONObject
import java.time.*
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class Homefragment : Fragment() {

    /* ---------- 상수 ---------- */
    companion object {
        const val USER_ID = "1"                 // ← 임시 테스트 계정
        const val TAG     = "HomeFragment"
    }

    /* ---------- 데이터 ---------- */
    private var capsules: List<Capsule> = emptyList()
    private var currIndex               = 0

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

        /* View 바인딩 */
        tvName       = view.findViewById(R.id.tvCapsuleName)
        tvDDay       = view.findViewById(R.id.tvDDay)
        ivPhoto      = view.findViewById(R.id.ivCapsulePhoto)
        tvIndicator  = view.findViewById(R.id.tvPageIndicator)
        btnPrev      = view.findViewById(R.id.btnPrev)
        btnNext      = view.findViewById(R.id.btnNext)

        btnNext.setOnClickListener { move(+1) }
        btnPrev.setOnClickListener { move(-1) }

        fetchCapsules()          // 첫 화면
    }

    override fun onResume() {
        super.onResume()
        fetchCapsules()          // 홈 탭으로 돌아올 때 새로고침
    }

    /* ---------- 서버에서 캡슐 목록 가져오기 ---------- */
    private fun fetchCapsules() {
        ApiClient.getJson("capsule/closed/$USER_ID") { ok, res ->
            requireActivity().runOnUiThread {
                if (ok) {
                    capsules = parseCapsules(res)
                    if (capsules.isEmpty()) currIndex = 0
                    else currIndex %= capsules.size
                    updateUI()
                } else {
                    toast("캡슐 불러오기 실패: $res")
                    capsules = emptyList()
                    updateUI()
                }
            }
        }
    }

    /* ---------- JSON → Capsule 리스트 ---------- */
    private fun parseCapsules(json: String): List<Capsule> {
        val list = mutableListOf<Capsule>()
        try {
            val root = JSONObject(json)
            val arr  = root.optJSONArray("capsules") ?: return emptyList()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)

                val title  = obj.optString("capsule_name")
                val openAt = obj.optString("open_at", null)
                val openMs = openAt?.let { isoToMillis(it) }

                list += Capsule(
                    title       = title,
                    body        = "",
                    tags        = "",
                    mediaUri    = null,
                    ddayMillis  = openMs,
                    condition   = null,
                    isJoint     = obj.optInt("type", 0) == 1
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "parseCapsules error", e)
        }
        return list
    }

    private fun isoToMillis(iso: String): Long? = try {
        OffsetDateTime.parse(iso).toInstant().toEpochMilli()
    } catch (_: DateTimeParseException) { null }

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

        // D-Day
        tvDDay.text = cap.ddayMillis?.let { ms ->
            val diff = ChronoUnit.DAYS.between(
                LocalDate.now(),
                Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()
            ).toInt()
            "D${if (diff >= 0) "-" else "+"}${abs(diff)}"
        } ?: "—"

        // (현재는 사진 대신 모래시계 아이콘만)
        //ivPhoto.setImageResource(selectHourglass(cap))
        ivPhoto.setImageResource(R.drawable.hourglass1)
        // 페이지 인디케이터
        tvIndicator.text = "${currIndex + 1} / ${capsules.size}"
    }

    /* ---------- 모래시계 단계 선택 ---------- */
    /*
    private fun selectHourglass(cap: Capsule): Int {

        val openRaw    = cap.ddayMillis
        val createdRaw = cap.createdMillis
        if (openRaw == null || createdRaw == null)
            return R.drawable.hourglass0

        val open    : Long = openRaw
        val created : Long = createdRaw

        val span    = (open - created).coerceAtLeast(1L)
        val elapsed = (System.currentTimeMillis() - created).coerceIn(0L, span)
        val ratio   = elapsed.toDouble() / span

        return when {
            ratio < 0.25 -> R.drawable.hourglass1
            ratio < 0.50 -> R.drawable.hourglass2
            ratio < 0.75 -> R.drawable.hourglass3
            ratio < 1.00 -> R.drawable.hourglass4
            else         -> R.drawable.hourglass4
        }
    }*/

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
