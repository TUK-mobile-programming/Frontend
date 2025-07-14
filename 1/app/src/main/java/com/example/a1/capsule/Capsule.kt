// com.example.a1.capsule.Capsule.kt
package com.example.a1.capsule

data class Capsule(
    val title: String,
    val body: String,             // <-- 이 필드가 있는지 확인 (String)
    val tags: String?,
    val condition: String?,
    val ddayMillis: Long?,
    val mediaUri: String?,       // <-- 이 필드가 있는지 확인 (String?)
    val latitude: Double?,
    val longitude: Double?,
    val isJoint: Boolean,
    val startDateMillis: Long?
) : java.io.Serializable