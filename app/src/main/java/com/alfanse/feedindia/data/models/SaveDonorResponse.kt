package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class SaveDonorResponse(
    @SerializedName("userId")
    val userId: String?
)