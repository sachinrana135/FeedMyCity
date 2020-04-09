package com.alfanse.feedindia.data.models

data class SaveCommentRequest(
    val needier_item_id: String?,
    val member_id: String,
    val comment: String
)