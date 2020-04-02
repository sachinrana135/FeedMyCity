package com.alfanse.feedindia.data

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiHeadersInterceptor(chain: Interceptor.Chain) {
    private var mChain: Interceptor.Chain = chain

    fun buildHeader(headerEntity: HeaderEntity): Response {
        val request = mChain.request().newBuilder().header(API_TOKEN, headerEntity.apiToken)
            .header(APP_VERSION_NAME, headerEntity.appVersionName)
            .header(APP_VERSION_CODE, headerEntity.appVersionCode)
            .addHeader(CORRELATION_ID, headerEntity.correlationId)
            .addHeader(DEVICE_ID, headerEntity.deviceId).build()
        return mChain.proceed(request)
    }

    companion object {
        private const val API_TOKEN = "apiToken"
        private const val APP_VERSION_NAME = "appVersionName"
        private const val APP_VERSION_CODE = "appVersionCode"
        private const val CORRELATION_ID = "correlationId"
        private const val DEVICE_ID = "deviceId"
    }
}