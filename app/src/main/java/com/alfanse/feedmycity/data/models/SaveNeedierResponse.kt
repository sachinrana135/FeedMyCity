package com.alfanse.feedmycity.data.models

import com.google.gson.annotations.SerializedName

data class SaveNeedierResponse(
    @SerializedName("userId")
    val userId: String?
)