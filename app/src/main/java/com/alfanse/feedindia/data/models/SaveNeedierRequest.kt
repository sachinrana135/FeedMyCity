package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class SaveNeedierRequest(
    @SerializedName("group_id") val groupId: String,
    val lat: String,
    val lng: String,
    @SerializedName("location_address") val locationAddress: String,
    val mobile: String,
    val name: String,
    @SerializedName("need_items") val needItems: String
)