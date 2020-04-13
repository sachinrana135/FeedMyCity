package com.alfanse.feedmycity.ui.app_maintenance

import androidx.lifecycle.ViewModel
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.utils.APP_UPGRADE_PREFS_KEY
import com.alfanse.feedmycity.utils.Utils
import javax.inject.Inject

class AppUpgradeViewModel @Inject constructor(
    private val storage: ApplicationStorage,
    private val utils: Utils
) : ViewModel() {

    fun updateVersionUpgradeState(state: Boolean) {
        val versionCode = utils.getAppVersionCode()
        storage.putBoolean(APP_UPGRADE_PREFS_KEY + versionCode, state)
    }
}