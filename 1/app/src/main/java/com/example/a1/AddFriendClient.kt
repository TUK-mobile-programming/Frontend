package com.example.a1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AddFriendClient {
    private const val BASE_URL = "https://bold-seal-only.ngrok-free.app/"

    val instance: FriendApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(FriendApiService::class.java)
    }
}