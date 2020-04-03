package com.alfanse.feedindia.data.interceptor

import com.alfanse.feedindia.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class HeadersInterceptor @Inject constructor(
    val utils: Utils
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header(API_TOKEN, utils.getApiToken())
            .header(APP_VERSION_NAME, utils.getAppVersionName())
            .header(APP_VERSION_CODE, utils.getAppVersionCode().toString())
            .addHeader(CORRELATION_ID, utils.getRandomString())
            .addHeader(DEVICE_ID, utils.getDeviceId())
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        private const val API_TOKEN = "apiToken"
        private const val APP_VERSION_NAME = "appVersionName"
        private const val APP_VERSION_CODE = "appVersionCode"
        private const val CORRELATION_ID = "correlationId"
        private const val DEVICE_ID = "deviceId"
    }
}