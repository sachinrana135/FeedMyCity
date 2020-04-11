package com.alfanse.feedmycity.data.models

data class ApiResponseEntity<T>(
    val response: T? = null,
    val error: Error?
)