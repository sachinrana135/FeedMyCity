package com.alfanse.feedindia.data

import com.alfanse.feedindia.data.models.SaveDonorRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("saveDonor")
    suspend fun saveDonor(@Body saveDonorRequest: SaveDonorRequest): String
}