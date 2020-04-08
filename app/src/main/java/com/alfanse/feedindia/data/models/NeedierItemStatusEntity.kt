package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class NeedierItemStatusEntity (
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)