// AddFragment.kt (수정)
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
import androidx.fragment.app.activityViewModels // <-- 이 줄을 추가합니다. (필수)
import com.example.a1.capsule.Capsule
import com.example.a1.databinding.FragmentAddBinding
// import com.example.a1.repository.CapsuleRepository // <-- 이 줄은 더 이상 필요 없으니 제거 또는 주석 처리
import com.example.a1.viewmodel.CapsuleViewModel // <-- 이 줄을 추가합니다. (필수)
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
    private var selectedStartDateMillis: Long? = null
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

    // ───────── ViewModel 인스턴스 ─────────
    // 액티비티 범위의 ViewModel을 가져와 ListFragment와 공유합니다.
    private val capsuleViewModel: CapsuleViewModel by activityViewModels() // <-- 이 부분을 추가합니다. (필수)

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

        /* 날짜 선택 (ddayMillis) */
        // 시작 날짜도 선택하게 하려면 여기에 추가 UI 및 로직 필요
        val ddayClick = View.OnClickListener { showDatePicker(isStartDate = false) } // isStartDate 인자 추가
        tvDday.setOnClickListener(ddayClick)
        btnPickDate.setOnClickListener(ddayClick)

        /* 미디어 첨부 */
        btnAddMedia.setOnClickListener { mediaPicker.launch("image/* video/*") }

        /* 캡슐 생성 */
        btnCreateCapsule.setOnClickListener { createCapsule() }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 날짜 다이얼로그 (isStartDate 매개변수 추가)
    // ──────────────────────────────────────────────────────────────────────
    // AddFragment에 시작 날짜 선택 UI가 추가되었다면 이 함수를 사용
    private fun showDatePicker(isStartDate: Boolean) {
        val currentMillis = if (isStartDate) selectedStartDateMillis else selectedDateMillis
        val targetTextView = if (isStartDate) binding.tvDday else binding.tvDday // <-- tvDday는 ddayMillis용. 시작 날짜용 TextView를 추가해야 함.

        val cal = Calendar.getInstance().apply {
            currentMillis?.let { timeInMillis = it }
        }
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                cal.set(y, m, d, 0, 0, 0)
                val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(cal.time)

                if (isStartDate) {
                    selectedStartDateMillis = cal.timeInMillis
                    // binding.tvStartDate.text = formattedDate // <-- 시작 날짜용 TextView가 있다면 사용
                    // 현재 코드에는 시작 날짜 선택 UI가 없으므로 이 부분은 주석 처리 또는 제거 필요
                } else {
                    selectedDateMillis = cal.timeInMillis
                    binding.tvDday.text = formattedDate
                }
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
            longitude  = currentLocation?.longitude,
            startDateMillis = selectedStartDateMillis
        )

        // 더 이상 CapsuleRepository에 직접 추가하지 않고 ViewModel을 통해 추가합니다.
        capsuleViewModel.addCapsule(capsule) // <-- 이 줄이 핵심입니다!

        Toast.makeText(requireContext(), "캡슐이 생성되었습니다!", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.popBackStack() // 이전 화면으로 돌아갑니다.
    }

    // ──────────────────────────────────────────────────────────────────────
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}