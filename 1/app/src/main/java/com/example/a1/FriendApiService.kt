package com.example.a1

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FriendApiService {
    @POST("api/v1/friend/add")
    fun addFriend(
        @Body request: AddFriendRequest
    ): Call<FriendAddResponse>
}