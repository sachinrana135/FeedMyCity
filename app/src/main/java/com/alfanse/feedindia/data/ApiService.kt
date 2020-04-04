package com.alfanse.feedindia.data

import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.models.SaveDonorResponse
import com.alfanse.feedindia.data.models.UpdateDonorRequest
import com.alfanse.feedindia.data.models.UserEntity
import retrofit2.http.*

interface ApiService {
    @POST("saveDonor")
    suspend fun saveDonor(@Body saveDonorRequest: SaveDonorRequest): SaveDonorResponse

    @GET("getUserByMobile")
    suspend fun getUserByMobile(@Query("mobile")  mobile:String): UserEntity

    @GET("getUserById")
    suspend fun getUserById(@Query("userId")  userId:String): UserEntity

    @PUT("updateDonor")
    fun updateDonor(updateDonorRequest: UpdateDonorRequest): Any
}