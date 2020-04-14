package com.alfanse.feedmycity.utils

import com.alfanse.feedmycity.BuildConfig

const val BASE_URL = BuildConfig.API_BASE_URL
const val WEB_BASE_URL = BuildConfig.WEB_BASE_URL
const val WEB_URL_UPGRADE_APP = WEB_BASE_URL + "upgradeApp"
const val FIREBASE_DYNAMIC_URL = "https://feedmycity.page.link"
const val FIREBASE_USER_ID_PREFS_KEY = "firebaseUserIdPrefsKey"
const val APP_USER_ID_PREFS_KEY = "appUserIdPrefsKey"
const val APP_GROUP_ID_PREFS_KEY = "appUserIdPrefsKey"
const val APP_UPGRADE_PREFS_KEY = "appUpgradeIdPrefsKey"
const val API_TOKEN_PREFS_KEY = "apiTokenPrefsKey"
const val BUNDLE_KEY_USER = "bundleKeyUser"
const val BUNDLE_KEY_NEEDIER_ITEM = "bundleKeyNeedierItem"
const val BUNDLE_KEY_GROUP_CODE = "bundleKeyGroupCode"
const val BUNDLE_KEY_FIRST_LAUNCH = "bundleKeyFirstLaunch"
const val BUNDLE_KEY_FORCE_UPGRADE = "bundleKeyForceUpgrade"
const val BUNDLE_KEY_SKIP_CLICKED = "bundleKeySkipClicked"

object UserType{
    const val MEMBER = "MBR"
    const val DONOR = "DNR"
    const val NEEDIER = "NDR"
    const val ALL = "ALL"
}
