package com.alfanse.feedindia.data.repository

import com.alfanse.feedindia.data.ApiService
import com.alfanse.feedindia.data.models.*
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

    suspend fun getMembers(groupId: String, page: Int, pageLoad:Int) =
        remote.getMembers(groupId, page, pageLoad)


    suspend fun saveGroup(saveGroupRequest: SaveGroupRequest) =
        remote.saveGroup(saveGroupRequest)

    suspend fun getNearByUsers(lat: Double, lng: Double, distance: Int, userType: String) =
        remote.getNearByUsers(lat, lng, distance, userType)

    suspend fun getComments(needierItemId: String, page: Int, pageLoad:Int) =
        remote.getComments(needierItemId, page, pageLoad)

    suspend fun getNeedier(needierItemId: String) =
        remote.getNeedier(needierItemId)

    suspend fun getNeedierItemStatusTypes() =
        remote.getNeedierItemStatusTypes()

    suspend fun updateNeedierItemStatus(request: UpdateNeedierItemStatusRequest) =
        remote.updateNeedierItemStatus(request)


    suspend fun saveComment(saveCommentRequest: SaveCommentRequest): Any =
        remote.saveComment(saveCommentRequest)
}