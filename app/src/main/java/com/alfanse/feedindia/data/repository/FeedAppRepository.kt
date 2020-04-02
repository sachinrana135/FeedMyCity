package com.alfanse.feedindia.data.repository

import com.alfanse.feedindia.data.ApiService
import com.alfanse.feedindia.data.models.SaveDonorRequest
import javax.inject.Inject

class FeedAppRepository
@Inject constructor(private val remote: ApiService) {

    suspend fun saveDonor(saveDonorRequest: SaveDonorRequest) =
        remote.saveDonor(saveDonorRequest)
}