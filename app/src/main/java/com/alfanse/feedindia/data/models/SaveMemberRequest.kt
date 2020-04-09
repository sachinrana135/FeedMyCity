package com.alfanse.feedindia.data.models

data class SaveMemberRequest(
    val lat: String,
    val lng: String,
    val mobile: String,
    val name: String,
    val groupCode: String,
    val location_address: String
)