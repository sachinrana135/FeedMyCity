package com.alfanse.feedmycity.data.models

import com.google.gson.annotations.SerializedName

data class ConfigEntity(

    @SerializedName("apiStatus")
    val apiStatus: Boolean,

    @SerializedName("isUpdateAvailable")
    val isUpdateAvailable: Boolean,

    @SerializedName("isForceUpdate")
    val isForceUpdate: Boolean,

    @SerializedName("notifyUpdateFrequency")
    val notifyUpdateFrequency: String

)