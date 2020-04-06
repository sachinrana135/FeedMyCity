package com.alfanse.feedindia.data

import com.alfanse.feedindia.data.models.*
import retrofit2.http.*

interface ApiService {
    @POST("saveDonor")
    suspend fun saveDonor(@Body saveDonorRequest: SaveDonorRequest): SaveDonorResponse

    @GET("getUserByMobile")
    suspend fun getUserByMobile(@Query("mobile")  mobile:String): UserEntity

    @GET("getUserById")
    suspend fun getUserById(@Query("userId")  userId:String): UserEntity

    @PUT("updateDonor")
    suspend fun updateDonor(@Body updateDonorRequest: UpdateDonorRequest): Any

    @GET("getGroupNeedierItems")
    suspend fun getNeediers(@Query("group_id") groupId: String,@Query("status") status: String,@Query("page") page: Int, @Query("page_load") pageLoad: Int): List<NeedieritemEntity>
}