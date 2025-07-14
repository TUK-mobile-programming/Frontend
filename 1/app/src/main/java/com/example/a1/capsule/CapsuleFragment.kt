package com.example.a1.capsule

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.activityViewModels
import com.example.a1.R
import com.example.a1.viewmodel.CapsuleViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons

//2025-07-14 14:14

class CapsuleFragment : Fragment() {

    /* ---------- UI ---------- */
    private lateinit var mapView : MapView
    private lateinit var adapter : CapsuleAdapter
    private lateinit var recycler: RecyclerView
    private val currentMarkers = mutableListOf<Marker>() // **[수정]** 마커 리스트 추가

    /* ---------- 위치 ---------- */
    private lateinit var fused  : FusedLocationProviderClient
    private var currentLocation : Location? = null
    private val OPEN_RADIUS_M   = 50      // 반경 50 m 이내에서만 열림

    /* ---------- ViewModel ---------- */
    private val capsuleViewModel: CapsuleViewModel by activityViewModels()

    /* ---------- 라이프사이클 ---------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient("xfk2s2l1qt")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_capsule, container, false)

        mapView  = v.findViewById(R.id.naverMapView)
        recycler = v.findViewById(R.id.capsuleRecyclerView)

        fused   = LocationServices.getFusedLocationProviderClient(requireContext())

        setupRecycler()
        mapView.onCreate(savedInstanceState)
        // mapView.getMapAsync { addMarkers(it) } // 이 호출은 ViewModel 관찰 블록으로 이동했습니다.

        return v
    }

    /* ───────── 권한 & 위치 ───────── */
    private fun ensureLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocation() {
        if (!ensureLocationPermission()) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }
        fused.lastLocation.addOnSuccessListener { currentLocation = it }
    }

    override fun onRequestPermissionsResult(
        reqCode: Int, perms: Array<out String>, grant: IntArray
    ) {
        super.onRequestPermissionsResult(reqCode, perms, grant)
        if (reqCode == 1001 && grant.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        }
    }

    /* ───────── RecyclerView ───────── */
    private fun setupRecycler() {
        adapter = CapsuleAdapter(mutableListOf()) { tryOpenCapsule(it) }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter       = adapter

        // ViewModel의 allCapsules LiveData를 관찰하여 데이터가 변경될 때마다 RecyclerView 업데이트
        capsuleViewModel.allCapsules.observe(viewLifecycleOwner) { capsules ->
            adapter.updateData(capsules) // CapsuleAdapter에 updateData(List<Capsule>) 함수가 필요
            mapView.getMapAsync { naverMap -> addMarkers(naverMap) } // 캡슐 목록이 변경되면 지도 마커도 업데이트
        }
    }

    /* ───────── 지도 & 마커 ───────── */
    private fun addMarkers(map: NaverMap) {
        // **[수정]** 기존 마커 모두 제거: 각 마커의 map 속성을 null로 설정
        currentMarkers.forEach { it.map = null } // 모든 마커를 지도에서 제거
        currentMarkers.clear() // 리스트도 비웁니다.

        // ViewModel의 allCapsules에서 데이터를 가져와 마커 추가
        capsuleViewModel.allCapsules.value
            ?.filter { it.latitude != null && it.longitude != null }
            ?.forEach { cap ->
                Marker().apply {
                    position    = LatLng(cap.latitude!!, cap.longitude!!)
                    icon        = MarkerIcons.BLACK
                    captionText = cap.title
                    this.map    = map // 지도에 마커를 추가
                    currentMarkers.add(this) // **[추가]** 리스트에 마커 저장
                }
            }

        // 카메라 초기 위치 설정도 ViewModel 데이터 기반
        val first = capsuleViewModel.allCapsules.value
            ?.firstOrNull { it.latitude != null && it.longitude != null }

        val target = first?.let { LatLng(it.latitude!!, it.longitude!!) }
            ?: LatLng(37.5665, 126.9780)         // 기본 좌표 (서울시청)

        map.moveCamera(CameraUpdate.scrollTo(target))
    }

    /* ───────── 캡슐 열기 시도 (거리 + D-Day) ───────── */
    private fun tryOpenCapsule(capsule: Capsule) {
        // ① 최신 위치 요청
        requestLocation()

        val loc = currentLocation
        if (loc == null) {          // 위치 미확정
            toast("현재 위치를 가져오는 중입니다.")
            return
        }

        /* ---------- 거리 조건 ---------- */
        val lat = capsule.latitude
        val lng = capsule.longitude
        if (lat == null || lng == null) {
            toast("이 캡슐은 위치 정보가 없습니다.")
            return
        }
        val results = FloatArray(1)
        Location.distanceBetween(loc.latitude, loc.longitude, lat, lng, results)
        val distanceM = results[0]

        /* ---------- D-Day 조건 ---------- */
        val today     = System.currentTimeMillis()
        val ddayMs    = capsule.ddayMillis          // null 이면 ‘시간 제한 없음’
        val isTimeOk  = ddayMs == null || today >= ddayMs   // D-Day 가 지났는가?

        /* ---------- 최종 판정 ---------- */
        if (distanceM <= OPEN_RADIUS_M && isTimeOk) {
            toast("🎉 캡슐이 열렸습니다!")
            // TODO: 상세 화면·다이얼로그 등으로 이동
        } else {
            // 거리 또는 날짜가 충족되지 않음 → 남은 정보 안내
            val sb = StringBuilder()

            if (distanceM > OPEN_RADIUS_M) {
                sb.append("남은 거리: ${distanceM.toInt()} m  ")
            }
            if (!isTimeOk) {
                val daysLeft = ((ddayMs!! - today) / (1000*60*60*24)) + 1
                sb.append("D-${daysLeft} 이후 열 수 있습니다.")
            }
            toast(sb.toString())
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    /* ───────── MapView 생명주기 ───────── */
    override fun onStart()        { super.onStart(); mapView.onStart() }
    override fun onResume()       { super.onResume(); mapView.onResume(); requestLocation() }
    override fun onPause()        { super.onPause(); mapView.onPause() }
    override fun onStop()         { super.onStop();  mapView.onStop() }
    override fun onLowMemory()    { super.onLowMemory(); mapView.onLowMemory() }
    override fun onDestroyView()  { mapView.onDestroy(); super.onDestroyView() }
}