package com.alfanse.feedindia.data.repository

import com.alfanse.feedindia.data.ApiService
import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.models.SaveGroupRequest
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

    suspend fun getNeediers(groupId: String, status: String, page: Int, pageLoad:Int) =
        remote.getNeediers(groupId, status, page, pageLoad)


    suspend fun saveGroup(saveGroupRequest: SaveGroupRequest) =
        remote.saveGroup(saveGroupRequest)

    suspend fun getNearByUsers(lat: Double, lng: Double, distance: Int, userType: String) =
        remote.getNearByUsers(lat, lng, distance, userType)
}