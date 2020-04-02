package com.alfanse.feedindia.data.models

data class SaveDonorResponse(
    val response: Response
){
    data class Response(
        val userId: String
    )
}