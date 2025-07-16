package com.example.a1.capsule

import java.io.Serializable

data class Capsule(
    val capsuleId : Int = -1,
    val title: String,
    val body: String,
    val tags: String,
    val mediaUri: String?,
    val ddayMillis: Long?,
    val condition: String?,
    val isJoint: Boolean,

    val latitude: Double? = null,
    val longitude: Double?=null,

    val isOpened : Boolean = false,
    val contents : List<CapsuleContent> = emptyList()
) : Serializable