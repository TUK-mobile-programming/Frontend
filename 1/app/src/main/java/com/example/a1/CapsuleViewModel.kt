package com.example.a1.viewmodel // 이 패키지 이름이 정확해야 합니다.

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.a1.capsule.Capsule // Capsule 데이터 클래스 import
import com.example.a1.TimeCapsule // TimeCapsule 데이터 클래스 import (ListFragment에서 사용)

class CapsuleViewModel : ViewModel() {

    // 모든 캡슐 데이터를 저장하는 LiveData (원본 Capsule 객체)
    private val _allCapsules = MutableLiveData<List<Capsule>>(emptyList())
    val allCapsules: LiveData<List<Capsule>> get() = _allCapsules

    // ListFragment의 RecyclerView에 표시될 TimeCapsule 목록 (간략화된 버전)
    // allCapsules가 변경될 때마다 이 목록을 업데이트합니다.
    private val _capsules = MutableLiveData<List<TimeCapsule>>(emptyList())
    val capsules: LiveData<List<TimeCapsule>> get() = _capsules

    init {
        // ViewModel이 처음 생성될 때 초기 데이터를 로드하거나 설정할 수 있습니다.
        // 여기서는 시작할 때 빈 목록으로 설정합니다.
        // 실제 앱에서는 데이터베이스 등에서 데이터를 로드할 수 있습니다.
        updateTimeCapsules(emptyList()) // 초기화 시 빈 TimeCapsule 목록으로 설정
    }

    fun addCapsule(capsule: Capsule) {
        val currentList = _allCapsules.value.orEmpty().toMutableList()
        currentList.add(capsule)
        _allCapsules.value = currentList // 원본 캡슐 목록 업데이트

        // 원본 캡슐 목록이 업데이트될 때 TimeCapsule 목록도 함께 업데이트합니다.
        updateTimeCapsules(currentList)
    }

    // 원본 Capsule 목록을 TimeCapsule 목록으로 변환하여 업데이트하는 함수
    private fun updateTimeCapsules(capsuleList: List<Capsule>) {
        val newTimeCapsuleList = capsuleList.map { capsule ->
            // Capsule 객체를 TimeCapsule 객체로 변환합니다.
            // TimeCapsule에 어떤 필드가 필요한지 확인하여 매핑합니다.
            TimeCapsule(
                title = capsule.title,
                openDate = capsule.ddayMillis?.let {
                    // ddayMillis가 있다면 날짜 형식으로 변환하여 표시 (예: "2024-07-20")
                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(it))
                } ?: "날짜 미정", // ddayMillis가 null이면 "날짜 미정"
                mediaUri = capsule.mediaUri // mediaUri 필드를 TimeCapsule에도 포함
                // TimeCapsule에 다른 필드가 있다면 여기에 추가 매핑
            )
        }
        _capsules.value = newTimeCapsuleList
    }

    // TODO: 필요하다면 캡슐 삭제, 수정 등의 메서드 추가
}