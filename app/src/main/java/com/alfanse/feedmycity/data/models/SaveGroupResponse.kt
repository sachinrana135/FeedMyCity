package com.alfanse.feedmycity.data.models

import com.google.gson.annotations.SerializedName

data class SaveGroupResponse(
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("groupId")
    val groupId: String?
)