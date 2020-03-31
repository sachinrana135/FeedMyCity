package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class TodoEntity(
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("completed")
    val completed: Boolean,

    var userName: String?

)