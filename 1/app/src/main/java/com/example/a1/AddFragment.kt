package com.example.a1

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.a1.databinding.FragmentAddBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    /** 선택된 D-Day 날짜(밀리초) */
    private var selectedDateMillis: Long? = null

    /** 갤러리 결과 콜백 */
    private val mediaPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // 미디어가 선택되었을 때 처리 (예: 미리보기 추가 등)
                Toast.makeText(requireContext(), "Media selected: $uri", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    /**
     * UI 리스너 및 기본 상태 설정
     */
    private fun initUi() = with(binding) {

        /* ───── 조건 설정 스위치 ───── */
        switchCondition.setOnCheckedChangeListener { _, isChecked ->
            etCondition.isVisible = isChecked
        }

        /* ───── D-Day 선택 ───── */
        val dateClickListener = View.OnClickListener { showDatePicker() }
        tvDday.setOnClickListener(dateClickListener)
        btnPickDate.setOnClickListener(dateClickListener)

        /* ───── 미디어 첨부 ───── */
        btnAddMedia.setOnClickListener {
            // 이미지/비디오 둘 다 선택 가능하도록 MIME type "*/*" 사용
            mediaPicker.launch("image/* video/*")
        }

        /* ───── 타임 캡슐 생성 ───── */
        btnCreateCapsule.setOnClickListener { createCapsule() }
    }

    /**
     * DatePickerDialog 표시
     */
    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply {
            selectedDateMillis?.let { timeInMillis = it }
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth, 0, 0, 0)
                selectedDateMillis = cal.timeInMillis
                val fmt = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                binding.tvDday.text = fmt.format(cal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * 데이터 검증 & 저장(예시)
     */
    private fun createCapsule() = with(binding) {
        val title = etTitle.text.toString().trim()
        val body = etBody.text.toString().trim()
        val tags = etTag.text.toString().trim()
        val conditionText = etCondition.text.toString().trim()
        val isJoint = switchJoint.isChecked

        if (title.isEmpty()) {
            etTitle.error = "제목을 입력하세요"
            return
        }
        if (body.isEmpty()) {
            etBody.error = "내용을 입력하세요"
            return
        }

        // TODO: 저장 로직(뷰모델 또는 DB)으로 전달
        Toast.makeText(requireContext(), "캡슐이 생성되었습니다!", Toast.LENGTH_SHORT).show()

        // 예시로 화면 종료
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}