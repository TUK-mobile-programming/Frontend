package com.example.a1.capsule

import java.time.LocalDate

data class Capsule(
    val title: String,
    val body: String,
    val tags: String,
    val mediaUri: String?,
    val ddayMillis: Long?,
    val condition: String?,
    val isJoint: Boolean,

    val latitude: Double? = null,
    val longitude: Double?=null
)