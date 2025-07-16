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
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import com.example.a1.capsule.CapsuleAdapter

class CapsuleFragment : Fragment() {

    private lateinit var mapView   : MapView
    private var       naverMap    : NaverMap? = null
    private lateinit var recycler : RecyclerView
    private lateinit var adapter  : CapsuleAdapter

    // we keep track of markers ourselves
    private val markers = mutableListOf<Marker>()

    /** Combine opened + closed capsules */
    private val allCapsules get() =
        CapsuleRepository.getOpeenedCapsules() + CapsuleRepository.getCosedCapsule()

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
        mapView.onCreate(savedInstanceState)
        setupRecyclerView()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
    }

    private fun setupMap() {
        mapView.getMapAsync { map ->
            naverMap = map
            map.moveCamera(
                CameraUpdate.toCameraPosition(
                    CameraPosition(LatLng(37.5665, 126.9780), 14.0)
                )
            )
            addCapsuleMarkers(map)
        }
    }

    private fun addCapsuleMarkers(map: NaverMap) {
        // clear old markers
        markers.forEach { it.map = null }
        markers.clear()

        // add new markers
        allCapsules.forEachIndexed { idx, cap ->
            val lat = cap.latitude  ?: 37.5665 + idx * 0.001
            val lng = cap.longitude ?: 126.9780 + idx * 0.001

            val marker = Marker().apply {
                position    = LatLng(lat, lng)
                icon        = if (cap.isOpened) MarkerIcons.RED else MarkerIcons.BLACK
                captionText = cap.title
                this.map    = map
            }
            markers += marker
        }
    }

    private fun setupRecyclerView() {
        adapter = CapsuleAdapter(allCapsules)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter       = adapter
    }

    private fun refreshUI() {
        adapter.submitList(allCapsules)
        naverMap?.let { addCapsuleMarkers(it) }
    }

    override fun onStart()       { super.onStart();  mapView.onStart() }
    override fun onResume()      { super.onResume(); mapView.onResume(); refreshUI() }
    override fun onPause()       { mapView.onPause(); super.onPause() }
    override fun onStop()        { mapView.onStop();  super.onStop() }
    override fun onLowMemory()   { mapView.onLowMemory(); super.onLowMemory() }
    override fun onDestroyView() { mapView.onDestroy(); super.onDestroyView() }
}