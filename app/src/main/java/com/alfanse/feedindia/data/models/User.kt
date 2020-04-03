package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class UserEntity(
    @SerializedName("id")
    val userId: String?,
    @SerializedName("firebaseId")
    val firebaseId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("userType")
    val userType: String?,
    @SerializedName("isAdmin")
    val isAdmin: Boolean

)