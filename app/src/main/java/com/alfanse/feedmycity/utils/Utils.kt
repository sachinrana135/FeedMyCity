package com.alfanse.feedmycity.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.provider.Settings
import com.alfanse.feedmycity.BuildConfig
import com.alfanse.feedmycity.data.models.UserEntity
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import java.util.*
import javax.inject.Inject


class Utils
@Inject constructor(context: Context, val storage: ApplicationStorage) {
    private val mContext = context

     fun getDeviceId(): String {
        var deviceId = ""
        deviceId = try {
            Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            ""
        }
        return deviceId
    }

     fun getAppVersionName(): String {
        var versionName = ""
        try {
            versionName = BuildConfig.VERSION_NAME
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

     fun getApiToken(): String {
        var versionName = ""
        try {
            versionName = BuildConfig.API_TOKEN
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

     fun getAppVersionCode(): Int {
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

    fun setLoggedUser(user: UserEntity) {
        // setting logged user in singleton to access anywhere in app
        User.apply {
            userId = user.userId
            firebaseId = user.firebaseId
            name = user.name
            mobile = user.mobile
            userType = user.userType
            isAdmin = user.isAdmin
            donateItems = user.donateItems
            needItems = user.needItems
            donorVisibility = user.donorVisibility
            groupId = user.groupId
            lat = user.lat
            lng = user.lng
            groupName = user.groupName
            address = user.address
            groupCode = user.groupCode
        }
    }

    fun logoutUser() {
        storage.clearValue(APP_USER_ID_PREFS_KEY)
        User.apply {
            userId = null
            firebaseId = null
            name = null
            mobile = null
            userType = null
            isAdmin = null
            donateItems = null
            needItems = null
            donorVisibility = null
        }
    }

    /**
     * isNetworkConnected  function is used ot check whether the internet service is connected in the
     * device and return true/false
     *
     * @return true/false on the basis of the state of the network connectivity.
     */
    fun isNetworkConnected(): Boolean? {
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (cm != null) {
            val activeNetwork = cm.activeNetworkInfo
            activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } else {
            false
        }
    }

}