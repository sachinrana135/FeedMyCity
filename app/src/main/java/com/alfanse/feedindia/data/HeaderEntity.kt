package com.alfanse.feedindia.data

data class HeaderEntity(val apiToken: String,
                        val appVersionName: String,
                        val appVersionCode: String,
                        val correlationId: String,
                        val deviceId: String)