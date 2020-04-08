package com.alfanse.feedindia.data.models

data class UpdateNeedierItemStatusRequest(
    val needier_item_id: String,
    val status_id: String,
    val member_id: String
)