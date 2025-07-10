package com.example.a1.capsule

import java.time.LocalDate

data class Capsule(
    val title: String,
    val tag:   String,
    val openDate: LocalDate          // 개봉(목표) 날짜
)