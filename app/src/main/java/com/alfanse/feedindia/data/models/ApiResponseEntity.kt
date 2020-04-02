package com.alfanse.feedindia.data.models

data class ApiResponseEntity<T>(val response: T? = null,
                                val errorEntity: ApiErrorEntity?) {
    fun isCallSuccess(): Boolean {
        if (errorEntity != null) {
            return errorEntity.error.message.isNotEmpty()
        }
        return false
    }
}