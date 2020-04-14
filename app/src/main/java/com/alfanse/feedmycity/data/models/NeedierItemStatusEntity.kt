package com.alfanse.feedmycity.data.models

import com.google.gson.annotations.SerializedName

data class NeedierItemStatusEntity (
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)