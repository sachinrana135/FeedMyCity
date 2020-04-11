package com.alfanse.feedmycity.data.models

data class SaveMemberRequest(
    val lat: String,
    val lng: String,
    val mobile: String,
    var firebaseId:String,
    val name: String,
    val groupCode: String,
    val location_address: String
)