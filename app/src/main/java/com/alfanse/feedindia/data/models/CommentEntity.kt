package com.alfanse.feedindia.data.models

import com.google.gson.annotations.SerializedName

data class CommentEntity(
    @SerializedName("member_name")
    val userName: String?,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("dateAdded")
    val date: String?
)