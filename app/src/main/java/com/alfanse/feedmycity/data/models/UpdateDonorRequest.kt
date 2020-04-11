package com.alfanse.feedmycity.data.models

data class UpdateDonorRequest(
    val userId: String,
    val donate_items: String,
    val status: String
)