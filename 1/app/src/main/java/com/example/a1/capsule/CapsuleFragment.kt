package com.example.a1.capsule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a1.R
import com.example.a1.repository.CapsuleRepository
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons

class CapsuleFragment : Fragment() {

    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CapsuleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient("xfk2s2l1qt")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_capsule, container, false)

        mapView = view.findViewById(R.id.naverMapView)
        recyclerView = view.findViewById(R.id.capsuleRecyclerView)

        mapView.onCreate(savedInstanceState)
        setupRecyclerView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
    }

    // ───────── 지도 설정 ─────────
    private fun setupMap() {
        mapView.getMapAsync { map ->
            naverMap = map

            // 초기 카메라 위치
            val initialCamera = CameraUpdate.toCameraPosition(
                CameraPosition(LatLng(37.5665, 126.9780), 14.0)
            )
            map.moveCamera(initialCamera)

            // 저장된 캡슐 기반 마커 추가
            addCapsuleMarkers(map)
        }
    }

    private fun addCapsuleMarkers(map: NaverMap) {
        val capsules = CapsuleRepository.getAllCapsules()

        capsules.forEachIndexed { index, capsule ->
            // 현재는 위치 정보 없으므로 더미 위치로 분산해서 배치
            val lat = 37.5665 + (index * 0.001)
            val lng = 126.9780 + (index * 0.001)

            Marker().apply {
                position = LatLng(lat, lng)
                icon = MarkerIcons.BLACK
                captionText = capsule.title
                this.map = map
            }
        }
    }

    // ───────── 리스트 설정 ─────────
    private fun setupRecyclerView() {
        val capsules = CapsuleRepository.getAllCapsules()
        adapter = CapsuleAdapter(capsules)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    // ───────── MapView 라이프사이클 ─────────
    override fun onStart()        { super.onStart();        mapView.onStart() }
    override fun onResume()       { super.onResume();       mapView.onResume() }
    override fun onPause()        { super.onPause();        mapView.onPause() }
    override fun onStop()         { super.onStop();         mapView.onStop() }
    override fun onLowMemory()    { super.onLowMemory();    mapView.onLowMemory() }
    override fun onDestroyView()  { mapView.onDestroy();    super.onDestroyView() }
}