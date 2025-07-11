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

class Listfragment : Fragment() {

    // RecyclerView와 Adapter 변수 선언
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimeCapsuleAdapter // TimeCapsuleAdapter 사용 선언
    private lateinit var timeCapsuleList: MutableList<TimeCapsule> // 데이터 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fragment 생성 시 필요한 초기화 작업 (여기서는 arguments 처리)
        arguments?.let {
            // 이 부분은 현재 예시에서는 사용되지 않지만, 필요에 따라 활용 가능
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // fragment_list.xml 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // ----------------------------------------------------
        // 1. 첫 번째 더미 리스트 (first_list)에 대한 클릭 리스너 설정
        // ----------------------------------------------------
        val firstListLayout: LinearLayout = view.findViewById(R.id.first_list)
        firstListLayout.setOnClickListener {
            // 클릭 시 ListActivity로 이동하는 예시 Intent
            val intent = Intent(activity, Listactivity::class.java).apply {
                putExtra("title", "여름 휴가")
                putExtra("openDate", "Opened on 2024-07-20 (Dummy)")
            }
            startActivity(intent)
        }

        // ----------------------------------------------------
        // 2. RecyclerView 초기화 및 설정
        // ----------------------------------------------------
        // RecyclerView를 XML 레이아웃에서 찾습니다.
        // ID는 fragment_list.xml에 정의된 time_capsule_recycler_view 여야 합니다.
        recyclerView = view.findViewById(R.id.time_capsule_recycler_view)

        // LinearLayoutManager를 설정하여 아이템들이 수직으로 나열되도록 합니다.
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ----------------------------------------------------
        // 3. 데이터 리스트 초기화 및 샘플 데이터 추가
        // ----------------------------------------------------
        timeCapsuleList = mutableListOf()
        // 여기에 RecyclerView에 표시하고 싶은 실제 TimeCapsule 데이터를 추가합니다.
        // 첫 번째 더미 항목 "여름 휴가"는 포함하지 않습니다.
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "첫 번째 여행", "Opened on 2024-07-15"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "졸업식 날", "Opened on 2024-07-10"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "새로운 추억", "Opened on 2025-01-01"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 1", "Opened on 2024-06-01"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 2", "Opened on 2024-05-20"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 3", "Opened on 2024-04-10"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "호날두와 함께한 여행", "Opened on 2025-07-10"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 4", "Opened on 2024-06-01"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 5", "Opened on 2024-05-20"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 6", "Opened on 2024-04-10"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "메시와 함께한 여행", "Opened on 2025-07-10"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 7", "Opened on 2024-06-01"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 8", "Opened on 2024-05-20"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "추가된 아이템 9", "Opened on 2024-04-10"))
        timeCapsuleList.add(TimeCapsule(R.mipmap.ic_launcher, "음바페와 함께한 여행", "Opened on 2025-07-10"))


        // ----------------------------------------------------
        // 4. TimeCapsuleAdapter 초기화 및 RecyclerView에 설정
        // ----------------------------------------------------
        adapter = TimeCapsuleAdapter(timeCapsuleList) // TimeCapsuleAdapter 인스턴스 생성
        recyclerView.adapter = adapter // RecyclerView에 어댑터 설정

        // ----------------------------------------------------
        // 5. RecyclerView 아이템 클릭 리스너 설정 (선택 사항)
        //    각 list_item_capsule 항목 클릭 시 동작을 정의합니다.
        // ----------------------------------------------------
        adapter.setOnItemClickListener(object : TimeCapsuleAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, item: TimeCapsule) {
                // 클릭된 아이템의 데이터를 가지고 ListActivity로 이동
                val intent = Intent(activity, Listactivity::class.java).apply {
                    putExtra("title", item.title)
                    putExtra("openDate", item.openDate)
                    // item.imageResId 등 다른 데이터도 필요하다면 추가할 수 있습니다.
                }
                startActivity(intent)
            }
        })

        // 인플레이트된 뷰를 반환합니다.
        return view
    }

    // Fragment 인스턴스 생성을 위한 companion object (필요한 경우)
    companion object {
        // TODO: 이 newInstance 메서드는 현재 사용되지 않으므로 필요하지 않으면 제거하거나,
        // Fragment에 인자를 전달할 때 사용됩니다.
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Listfragment().apply {
                arguments = Bundle().apply {
                    // putString(ARG_PARAM1, param1)
                    // putString(ARG_PARAM2, param2)
                }
            }
    }
}