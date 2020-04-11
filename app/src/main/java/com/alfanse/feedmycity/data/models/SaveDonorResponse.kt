package com.alfanse.feedmycity.data.models

import com.google.gson.annotations.SerializedName

data class SaveDonorResponse(
    @SerializedName("userId")
    val userId: String?
)