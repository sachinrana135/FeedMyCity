package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class SaveDonorResponse(
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("groupId")
    val groupId: String?
)