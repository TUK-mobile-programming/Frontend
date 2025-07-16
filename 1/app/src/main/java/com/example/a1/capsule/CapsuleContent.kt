//  ⟫  app/src/main/java/com/example/a1/capsule/CapsuleContent.kt
package com.example.a1.capsule

import java.io.Serializable

/**
 * 캡슐 안에 들어가는 개별 컨텐트(텍스트‧이미지‧영상 등)를 표현하는 모델.
 *
 * @property type  서버에서 내려오는 컨텐트 유형
 *                 └ "text" | "image" | "video"
 * @property data  실제 데이터
 *                 └  text  → 문자열
 *                 └  image/video → S3(URL) 또는 로컬 URI
 */
data class CapsuleContent(
    val type: String,
    val data: String
) : Serializable