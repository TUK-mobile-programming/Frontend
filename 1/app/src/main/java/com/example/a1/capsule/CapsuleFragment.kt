package com.example.a1.capsule        // ← 패키지 맞춰 주세요

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.time.LocalDate

class CapsuleFragment : Fragment() {

    // ───────── 지도 ─────────
    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 실제 Client-ID 로 교체
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient("xfk2s2l1qt")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_capsule, container, false)

        // MapView
        mapView = v.findViewById(R.id.naverMapView)
        mapView.onCreate(savedInstanceState)

        // RecyclerView
        val recycler = v.findViewById<RecyclerView>(R.id.capsuleRecyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = CapsuleAdapter(dummyCapsules())

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.getMapAsync { map ->
            naverMap = map

            // 카메라 초기 위치
            val cam = CameraUpdate.toCameraPosition(
                CameraPosition(LatLng(37.5665, 126.9780), 14.0)
            )
            map.moveCamera(cam)

            addDummyMarkers(map)
        }
    }

    // ───────── 더미 데이터 ─────────
    private fun dummyCapsules() = listOf(
        Capsule("A 타임캡슐", "#가족",  LocalDate.of(2025, 12, 25)),
        Capsule("B 타임캡슐", "#친구",  LocalDate.of(2026,  5,  1)),
        Capsule("C 타임캡슐", "#혼자",  LocalDate.of(2030,  2, 18))
    )

    private fun addDummyMarkers(map: NaverMap) {
        val locs = listOf(
            LatLng(37.5665, 126.9780) to "A 타임캡슐",
            LatLng(37.5651, 126.9895) to "B 타임캡슐",
            LatLng(37.5700, 126.9768) to "C 타임캡슐"
        )
        for ((loc, title) in locs) {
            Marker().apply {
                position = loc
                icon = MarkerIcons.BLACK
                captionText = title
                this.map = map
            }
        }
    }

    // ───────── MapView 라이프사이클 ─────────
    override fun onStart()        { super.onStart();        mapView.onStart() }
    override fun onResume()       { super.onResume();       mapView.onResume() }
    override fun onPause()        { super.onPause();        mapView.onPause() }
    override fun onStop()         { super.onStop();         mapView.onStop() }
    override fun onLowMemory()    { super.onLowMemory();    mapView.onLowMemory() }
    override fun onDestroyView()  { mapView.onDestroy();    super.onDestroyView() }
}