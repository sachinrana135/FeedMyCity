package com.alfanse.feedindia.data.models

data class SaveDonorRequest(
    val donate_items: String,
    val firebaseId: String,
    val lat: String,
    val lng: String,
    val mobile: String,
    val name: String,
    val status: String
)