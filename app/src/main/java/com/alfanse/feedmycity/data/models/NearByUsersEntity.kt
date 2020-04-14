package com.alfanse.feedmycity.data.models

data class NearByUsersEntity(
    val distance: String,
    val lat: String,
    val lng: String,
    val mobile: String,
    val name: String,
    val user_type: String,
    val items: String
)