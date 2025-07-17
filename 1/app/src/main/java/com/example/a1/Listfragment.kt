package com.example.a1

import android.content.Intent // 이 줄을 추가합니다.
import android.content.pm.PackageManager
import java.io.Serializable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a1.capsule.Capsule
import com.example.a1.cpasule.CapsuleAdapter
import com.example.a1.databinding.FragmentListBinding // fragment_list.xml에 대한 뷰 바인딩
import com.example.a1.repository.CapsuleRepository
import com.example.a1.repository.UserRepository
import com.google.android.gms.location.LocationServices
import java.util.Calendar
import android.Manifest
import android.location.Location


/**
 * 수정 전 코드
 */

/*
class Listfragment : Fragment() {

    // 뷰 바인딩 선언
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var capsuleAdapter: CapsuleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩 초기화
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root // 뷰 바인딩의 루트 뷰 반환

        initRecyclerView() // RecyclerView 설정 함수 호출

        // ListActivity로 이동하는 기존 로직 (필요에 따라 유지하거나 제거)
        // binding.firstList (fragment_list.xml에 first_list가 있다면 사용)
        // 만약 기존 LinearLayout이 제거되었다면 이 부분을 삭제하세요.
        // val firstListLayout: LinearLayout = view.findViewById(R.id.first_list) // ID가 없다면 오류 발생
        // firstListLayout.setOnClickListener {
        //     val intent = Intent(activity, Listactivity::class.java)
        //     startActivity(intent)
        // }

        return view
    }

    override fun onResume() {
        super.onResume()
        // 프래그먼트가 다시 활성화될 때마다 캡슐 목록을 새로고침
        displayExpiredCapsules()
    }

    private fun initRecyclerView() {
        // 어댑터 초기화. 캡슐 아이템 클릭 시 CapsuleDetailActivity로 이동
        capsuleAdapter = CapsuleAdapter(emptyList()) { capsule ->
            // --- 이 아래 부분이 새로 추가되거나 수정되는 부분입니다. ---
            // 클릭된 캡슐 객체를 CapsuleDetailActivity로 전달하는 Intent 생성
            val intent = Intent(requireContext(), CapsuleDetailActivity::class.java).apply {
                putExtra("selected_capsule", capsule) // Capsule 객체를 Intent에 추가 (Serializable 객체)
            }
            startActivity(intent) // CapsuleDetailActivity 시작
            // --- 여기까지입니다. ---
        }

        binding.capsuleRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = capsuleAdapter
        }
    }

    // 만료된 캡슐을 필터링하고 RecyclerView에 표시하는 함수
    private fun displayExpiredCapsules() {
        val allCapsules = CapsuleRepository.getOpenedCapsules() // 저장소에서 모든 캡슐 가져오기
        val currentTimeMillis = Calendar.getInstance().timeInMillis // 현재 시간 (밀리초)

        val expiredCapsules = allCapsules.filter { capsule ->
            // ddayMillis가 null이 아니면서, 현재 시간보다 작은 경우 (즉, 이미 지난 경우)
            capsule.ddayMillis != null && capsule.ddayMillis!! < currentTimeMillis
        }

        // 필터링된 목록을 어댑터에 전달하여 RecyclerView 업데이트
        capsuleAdapter.submitList(expiredCapsules)

        // 만료된 캡슐이 없을 때 메시지 표시 (필요하다면)
        if (expiredCapsules.isEmpty()) {
            binding.emptyListMessage.visibility = View.VISIBLE // "만료된 캡슐이 없습니다." 메시지 표시
            binding.capsuleRecyclerView.visibility = View.GONE // RecyclerView 숨김
        } else {
            binding.emptyListMessage.visibility = View.GONE // 메시지 숨김
            binding.capsuleRecyclerView.visibility = View.VISIBLE // RecyclerView 표시
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 뷰 바인딩 참조 해제하여 메모리 누수 방지
    }
}*/
/**
 * 만료된 캡슐만 보여주는 중
 */
class Listfragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var capsuleAdapter: CapsuleAdapter
    private val TAG = "ListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val userId = UserRepository.getCurrentUser()?.userId
        Log.d(TAG, "onResume 호출됨 - userId: $userId")

        if (userId != null) {
            Log.d(TAG, "캡슐 목록 서버 요청 시작")
            CapsuleRepository.refreshCapsuleList(userId) { ok, err ->
                activity?.runOnUiThread {
                    if (ok) {
                        Log.d(TAG, "✅ 서버에서 캡슐 목록 새로고침 성공")
                        CapsuleRepository.getOpenedCapsules().forEachIndexed { index, capsule ->
                            Log.d(
                                TAG,
                                "서버에서 받은 [$index] capsuleId=${capsule.capsuleId}, title=${capsule.title}, ddayMillis=${capsule.ddayMillis}, isOpened=${capsule.isOpened}"
                            )
                        }
                        displayExpiredCapsules()
                    } else {
                        Log.e(TAG, "❌ 목록 로딩 실패: $err")
                        Toast.makeText(requireContext(), "목록 로딩 실패: $err", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Log.e(TAG, "❗ 로그인 정보가 없음")
            Toast.makeText(requireContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView() {
        capsuleAdapter = CapsuleAdapter(emptyList()) { capsule ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                return@CapsuleAdapter
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    Log.d(TAG, "📍 위치 정보 획득: ${loc.latitude}, ${loc.longitude}")
                    val intent = Intent(requireContext(), CapsuleDetailActivity::class.java).apply {
                        putExtra("selected_capsule", capsule)
                        putExtra("location", "${loc.latitude},${loc.longitude}")
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Log.e(TAG, "❌ 위치 가져오기 실패: ${it.message}")
                Toast.makeText(requireContext(), "위치 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.capsuleRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = capsuleAdapter
        }
    }

    private fun displayExpiredCapsules() {
        Log.d(TAG, "displayExpiredCapsules 호출됨")

        val allCapsules = CapsuleRepository.getOpenedCapsules().distinctBy { it.capsuleId }

        Log.d(TAG, "전체 캡슐 개수: ${allCapsules.size}")
        allCapsules.forEachIndexed { index, capsule ->
            Log.d(
                TAG,
                "[$index] capsuleId=${capsule.capsuleId}, ddayMillis=${capsule.ddayMillis}, title=${capsule.title}, isOpened=${capsule.isOpened}"
            )
        }

        val expiredCapsules = allCapsules.filter { it.isOpened == true }

        Log.d(TAG, "📌 isOpened == true 인 만료된 캡슐 개수: ${expiredCapsules.size}")

        capsuleAdapter.submitList(expiredCapsules)

        if (expiredCapsules.isEmpty()) {
            Log.d(TAG, "⚠️ 만료된 캡슐 없음 → emptyListMessage 표시")
            binding.emptyListMessage.visibility = View.VISIBLE
            binding.capsuleRecyclerView.visibility = View.GONE
        } else {
            Log.d(TAG, "✅ 만료된 캡슐 있음 → RecyclerView 표시")
            binding.emptyListMessage.visibility = View.GONE
            binding.capsuleRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}