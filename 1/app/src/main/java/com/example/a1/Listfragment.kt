package com.example.a1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a1.cpasule.CapsuleAdapter
import com.example.a1.databinding.FragmentListBinding
import com.example.a1.repository.CapsuleRepository
import java.util.Calendar

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
}