// CapsuleFragment.kt
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
import com.example.a1.R
import com.example.a1.repository.CapsuleRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons

class CapsuleFragment : Fragment() {

    /* ---------- UI ---------- */
    private lateinit var mapView : MapView
    private lateinit var adapter : CapsuleAdapter
    private lateinit var recycler: RecyclerView

    /* ---------- 위치 ---------- */
    private lateinit var fused  : FusedLocationProviderClient
    private var currentLocation : Location? = null
    private val OPEN_RADIUS_M   = 50      // 반경 50 m 이내에서만 열림

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
        mapView.getMapAsync { addMarkers(it) }

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
        adapter = CapsuleAdapter(CapsuleRepository.getAllCapsules()) { tryOpenCapsule(it) }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter       = adapter
    }

    /* ───────── 지도 & 마커 ───────── */
    private fun addMarkers(map: NaverMap) {
        // 마커 표시
        CapsuleRepository.getAllCapsules()
            .filter { it.latitude != null && it.longitude != null }
            .forEach { cap ->
                Marker().apply {
                    position    = LatLng(cap.latitude!!, cap.longitude!!)
                    icon        = MarkerIcons.BLACK
                    captionText = cap.title
                    this.map    = map
                }
            }

        // 카메라 초기 위치 = 좌표가 있는 첫 캡슐 → 없으면 서울시청
        val first = CapsuleRepository.getAllCapsules()
            .firstOrNull { it.latitude != null && it.longitude != null }

        val target = first?.let { LatLng(it.latitude!!, it.longitude!!) }
            ?: LatLng(37.5665, 126.9780)         // 기본 좌표

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