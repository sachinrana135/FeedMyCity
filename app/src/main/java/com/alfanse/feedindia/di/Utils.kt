package com.alfanse.feedindia.di

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import com.alfanse.feedindia.BuildConfig
import java.util.*
import javax.inject.Inject


class Utils
@Inject constructor(context: Context) {
    private val mContext = context

    public fun getDeviceId(): String {
        var deviceId = ""
        deviceId = try {
            Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            ""
        }
        return deviceId
    }

    public fun getAppVersionName(): String {
        var versionName = ""
        try {
            versionName = BuildConfig.VERSION_NAME
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

    public fun getAppVersionCode(): Int {
        var versionCode = 0
        try {
            versionCode = BuildConfig.VERSION_CODE
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    fun getRandomString(): String {
        val SALTCHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < 10) { // length of the random string.
            val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }
        return salt.toString()
    }
}