package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class SaveMemberResponse(
    @SerializedName("userId")
    val userId: String?
)