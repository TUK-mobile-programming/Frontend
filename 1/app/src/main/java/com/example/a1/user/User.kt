package com.example.a1.model

data class User(
    val userId: Int,
    val email: String,
    val accessToken: String? = null   // 토큰을 쓰고 있다면
)