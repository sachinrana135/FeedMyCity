package com.alfanse.feedmycity.data.models

data class SaveGroupRequest(
    val address: String,
    val admin_name: String,
    val firebaseId: String,
    val group_name: String,
    val lat: String,
    val lng: String,
    val location_address: String,
    val mobile: String,
    val reg_no: String
)