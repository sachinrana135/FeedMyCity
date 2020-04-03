package com.alfanse.feedindia.data

import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.models.SaveDonorResponse
import com.alfanse.feedindia.data.models.UserEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("saveDonor")
    suspend fun saveDonor(@Body saveDonorRequest: SaveDonorRequest): SaveDonorResponse

    @GET("getUserByMobile")
    suspend fun getUserByMobile(@Query("mobile")  mobile:String): UserEntity

    @GET("getUserById")
    suspend fun getUserById(@Query("userId")  userId:String): UserEntity
}