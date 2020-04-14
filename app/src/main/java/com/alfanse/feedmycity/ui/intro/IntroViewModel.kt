package com.alfanse.feedmycity.ui.intro

import androidx.lifecycle.ViewModel
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.utils.BUNDLE_KEY_FIRST_LAUNCH
import javax.inject.Inject

class IntroViewModel @Inject constructor(
    private val storage: ApplicationStorage
) : ViewModel() {

    fun setFirstLaunch() = storage.putBoolean(BUNDLE_KEY_FIRST_LAUNCH, false)
}