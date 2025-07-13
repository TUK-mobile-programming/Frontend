package com.example.a1

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.a1.capsule.Capsule
import com.example.a1.databinding.FragmentAddBinding
import com.example.a1.repository.CapsuleRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    // ───────── 내부 상태 ─────────
    private var selectedMediaUri: Uri? = null
    private var selectedDateMillis: Long? = null
    private var currentLocation: Location? = null

    // ───────── Android Location ─────────
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPerm = Manifest.permission.ACCESS_FINE_LOCATION
    private val permRequestCode = 1001

    // ───────── 갤러리 선택 ─────────
    private val mediaPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedMediaUri = it
                Toast.makeText(requireContext(), "미디어 선택됨: $it", Toast.LENGTH_SHORT).show()
            }
        }

    // ──────────────────────────────────────────────────────────────────────
    // ▲ 변수 끝  ▼ 라이프사이클
    // ──────────────────────────────────────────────────────────────────────
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        initUi()
        requestLocation()       // ⬅ 최초 1회 시도
        return binding.root
    }

    // ──────────────────────────────────────────────────────────────────────
    // UI 초기화
    // ──────────────────────────────────────────────────────────────────────
    private fun initUi() = with(binding) {
        /* 조건 설정 on/off */
        switchCondition.setOnCheckedChangeListener { _, checked ->
            etCondition.isVisible = checked
        }

        /* 날짜 선택 */
        val dateClick = View.OnClickListener { showDatePicker() }
        tvDday.setOnClickListener(dateClick)
        btnPickDate.setOnClickListener(dateClick)

        /* 미디어 첨부 */
        btnAddMedia.setOnClickListener { mediaPicker.launch("image/* video/*") }

        /* 캡슐 생성 */
        btnCreateCapsule.setOnClickListener { createCapsule() }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 날짜 다이얼로그
    // ──────────────────────────────────────────────────────────────────────
    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply {
            selectedDateMillis?.let { timeInMillis = it }
        }
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                cal.set(y, m, d, 0, 0, 0)
                selectedDateMillis = cal.timeInMillis
                binding.tvDday.text = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(cal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // ──────────────────────────────────────────────────────────────────────
    // 위치 권한 + 값 요청
    // ──────────────────────────────────────────────────────────────────────
    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), locationPerm)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(locationPerm), permRequestCode)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            currentLocation = loc      // loc == null 일 수 있음
        }
    }

    /** 권한 요청 결과 */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == permRequestCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocation()   // 권한 허용 → 다시 시도
        } else {
            Toast.makeText(requireContext(), "위치 권한이 거부되어 위치가 저장되지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 캡슐 생성
    // ──────────────────────────────────────────────────────────────────────
    private fun createCapsule() = with(binding) {
        val title = etTitle.text.toString().trim()
        val body  = etBody.text.toString().trim()

        if (title.isEmpty()) { etTitle.error = "제목을 입력하세요"; return }
        if (body .isEmpty()) { etBody .error = "내용을 입력하세요"; return }

        val capsule = Capsule(
            title      = title,
            body       = body,
            tags       = etTag.text.toString().trim(),
            mediaUri   = selectedMediaUri?.toString(),
            ddayMillis = selectedDateMillis,
            condition  = if (switchCondition.isChecked) etCondition.text.toString().trim() else null,
            isJoint    = switchJoint.isChecked,
            latitude   = currentLocation?.latitude,
            longitude  = currentLocation?.longitude
        )

        CapsuleRepository.addCapsule(capsule)
        Toast.makeText(requireContext(), "캡슐이 생성되었습니다!", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    // ──────────────────────────────────────────────────────────────────────
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}