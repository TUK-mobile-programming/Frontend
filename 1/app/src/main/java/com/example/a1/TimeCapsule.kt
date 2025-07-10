// com.example.a1/TimeCapsule.kt
package com.example.a1 // 이 부분을 본인의 프로젝트 패키지 이름으로 꼭 변경하세요!

data class TimeCapsule(
    val imageResId: Int, // 이미지 리소스 ID (예: R.mipmap.ic_launcher)
    val title: String,
    val openDate: String
)