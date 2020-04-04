package com.alfanse.feedindia.data.repository

import com.alfanse.feedindia.data.ApiService
import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.models.UpdateDonorRequest
import javax.inject.Inject

class FeedAppRepository
@Inject constructor(private val remote: ApiService) {

    suspend fun saveDonor(saveDonorRequest: SaveDonorRequest) =
        remote.saveDonor(saveDonorRequest)

    suspend fun getUserByMobile(mobile: String) =
        remote.getUserByMobile(mobile)

    suspend fun getUserById(userId: String) =
        remote.getUserById(userId)

    suspend fun updateDonor(updateDonorRequest: UpdateDonorRequest) =
        remote.updateDonor(updateDonorRequest)

}