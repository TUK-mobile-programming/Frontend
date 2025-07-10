// CapsuleFragment.kt
package com.example.a1   // ← 실제 패키지 경로로 맞춰 주세요

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a1.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons

/** 캡슐(리스트) 데이터 */
data class Capsule(val title: String, val tag: String, val dday: String)

class CapsuleFragment : Fragment() {

    // ───────────────────── 지도 변수 ─────────────────────
    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null

    // ───────────────────── 프래그먼트 라이프사이클 ─────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ① 네이버 지도 SDK Client ID (실제 ID로 교체)
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient("xfk2s2l1qt")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_capsule, container, false)

        // ② MapView 초기화
        mapView = v.findViewById(R.id.naverMapView)
        mapView.onCreate(savedInstanceState)

        // ③ RecyclerView 설정
        val recycler = v.findViewById<RecyclerView>(R.id.capsuleRecyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = CapsuleAdapter(
            listOf( // 더미 데이터
                Capsule("A 타임캡슐", "#가족",  "D-101"),
                Capsule("B 타임캡슐", "#친구",  "D-203"),
                Capsule("C 타임캡슐", "#혼자",  "D-301")
            )
        )
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ④ 지도 준비
        mapView.getMapAsync { map ->
            naverMap = map

            // 카메라 위치(서울 시청 근처)
            val cam = CameraUpdate.toCameraPosition(
                CameraPosition(LatLng(37.5665, 126.9780), 14.0)
            )
            map.moveCamera(cam)

            // 더미 마커 추가
            addDummyCapsules(map)
        }
    }

    // ───────────────────── 더미 마커 ─────────────────────
    private fun addDummyCapsules(map: NaverMap) {
        val dummy = listOf(
            LatLng(37.5665, 126.9780) to "A 타임캡슐",
            LatLng(37.5651, 126.9895) to "B 타임캡슐",
            LatLng(37.5700, 126.9768) to "C 타임캡슐"
        )
        for ((loc, title) in dummy) {
            Marker().apply {
                position = loc
                icon     = MarkerIcons.BLACK
                captionText = title
                this.map = map          // ← 반드시 this.map!
            }
        }
    }

    // ───────────────────── 지도 라이프사이클 위임 ─────────────────────
    override fun onStart()        { super.onStart();        mapView.onStart() }
    override fun onResume()       { super.onResume();       mapView.onResume() }
    override fun onPause()        { super.onPause();        mapView.onPause() }
    override fun onStop()         { super.onStop();         mapView.onStop() }
    override fun onLowMemory()    { super.onLowMemory();    mapView.onLowMemory() }
    override fun onDestroyView()  { mapView.onDestroy();    super.onDestroyView() }

    // ───────────────────── RecyclerView Adapter ─────────────────────
    class CapsuleAdapter(private val items: List<Capsule>) :
        RecyclerView.Adapter<CapsuleAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val title = v.findViewById<TextView>(R.id.txtCapsuleTitle)
            val tag   = v.findViewById<TextView>(R.id.txtCapsuleTag)
            val dday  = v.findViewById<TextView>(R.id.labelDday)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_capsule, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.tag.text   = item.tag
            holder.dday.text  = item.dday
        }

        override fun getItemCount() = items.size
    }
}