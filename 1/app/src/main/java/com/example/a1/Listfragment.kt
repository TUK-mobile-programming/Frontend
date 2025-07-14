// Listfragment.kt
package com.example.a1

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.activityViewModels
import com.example.a1.viewmodel.CapsuleViewModel
import com.example.a1.capsule.Capsule // Capsule 데이터 클래스 import 확인!
import android.widget.Toast // 오류 메시지 테스트용으로 추가

class Listfragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimeCapsuleAdapter

    private val capsuleViewModel: CapsuleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // 첫 번째 더미 리스트 (first_list) 클릭 리스너 (이 부분은 상세 데이터 전달과 무관)
        val firstListLayout: LinearLayout = view.findViewById(R.id.first_list)
        firstListLayout.setOnClickListener {
            val intent = Intent(activity, Listactivity::class.java).apply {
                putExtra("CAPSULE_TITLE", "여름 휴가")
                putExtra("CAPSULE_BODY", "이것은 더미 캡슐의 본문입니다.") // 더미 본문 추가
                putExtra("CAPSULE_IMAGE_URI", "") // 더미 이미지가 없다면 비워두세요
                putExtra("CAPSULE_START_DATE_MILLIS", -1L)
                putExtra("CAPSULE_DDAY_MILLIS", -1L)
            }
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.time_capsule_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = TimeCapsuleAdapter(mutableListOf())

        adapter.setOnItemClickListener(object : TimeCapsuleAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, item: TimeCapsule) {
                // TimeCapsule에서 원본 Capsule 객체를 찾습니다.
                // 이전에 'startDateMillis' 오류가 났던 부분입니다.
                // TimeCapsule과 Capsule 간의 매핑을 위해 고유 ID를 사용하는 것이 가장 좋습니다.
                // 현재는 title과 mediaUri를 기준으로 찾습니다.
                val clickedOriginalCapsule = capsuleViewModel.allCapsules.value?.find { capsule ->
                    capsule.title == item.title && capsule.mediaUri == item.mediaUri
                    // 만약 TimeCapsule에 id 필드가 있다면: capsule.id == item.id 로 변경 (더 정확함)
                }

                if (clickedOriginalCapsule != null) {
                    val intent = Intent(activity, Listactivity::class.java).apply {
                        // **여기서 원본 clickedOriginalCapsule 객체의 필드를 Intent에 담습니다.**
                        // `activity_listactivity.xml`의 ID와 매칭될 키 이름을 사용합니다.
                        putExtra("CAPSULE_TITLE", clickedOriginalCapsule.title) // listed_title
                        putExtra("CAPSULE_BODY", clickedOriginalCapsule.body)   // listed_text
                        putExtra("CAPSULE_IMAGE_URI", clickedOriginalCapsule.mediaUri) // list_image

                        // 날짜는 Long 타입 밀리초로 전달하고 Listactivity에서 포맷팅합니다.
                        putExtra("CAPSULE_START_DATE_MILLIS", clickedOriginalCapsule.startDateMillis ?: -1L) // start_date
                        putExtra("CAPSULE_DDAY_MILLIS", clickedOriginalCapsule.ddayMillis ?: -1L)     // end_date

                        // TODO: 필요하다면 다른 Capsule 필드(tags, condition 등)도 추가 전달
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "오류: 캡슐 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        recyclerView.adapter = adapter

        capsuleViewModel.capsules.observe(viewLifecycleOwner) { capsules ->
            adapter.updateData(capsules)
        }

        return view
    }

    // ... (companion object 등 나머지 코드는 동일)
}