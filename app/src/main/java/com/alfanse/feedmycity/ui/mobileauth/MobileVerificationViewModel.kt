package com.alfanse.feedmycity.ui.mobileauth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.utils.FIREBASE_USER_ID_PREFS_KEY
import javax.inject.Inject
import javax.inject.Named

class MobileVerificationViewModel
@Inject constructor(
    @Named("memory") private val memoryStorage: ApplicationStorage
) : ViewModel() {
    var firebaseUserIdLiveData = MutableLiveData<Boolean>()

    fun saveFirebaseUserId(id: String) {
        memoryStorage.putString(FIREBASE_USER_ID_PREFS_KEY, id)
        firebaseUserIdLiveData.value = true
    }
}