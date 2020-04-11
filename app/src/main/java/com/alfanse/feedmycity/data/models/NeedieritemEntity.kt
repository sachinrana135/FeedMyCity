package com.alfanse.feedmycity.data.models

import com.google.gson.annotations.SerializedName

data class NeedieritemEntity (
    @SerializedName("needier_item_id")
    val needierItemId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("items_need")
    val needItems: String?,
    @SerializedName("status")
    val status: String?
)