package com.alfanse.feedmycity.data.models

import com.google.gson.annotations.SerializedName

data class ApiResponseEntity<T>(
    @SerializedName("response")
    val response: T? = null,
    @SerializedName("error")
    val error: Error?
)