package com.alfanse.feedindia.ui.mobileauth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alfanse.feedindia.data.ApplicationStorage
import javax.inject.Inject
import com.alfanse.feedindia.utils.FIREBASE_USER_ID_PREFS_KEY

class CodeVerificationViewModel
@Inject constructor(private val sharedPrefs: ApplicationStorage): ViewModel() {
    var firebaseUserIdLiveData = MutableLiveData<Boolean>()

    fun saveFirebaseUserId(id: String){
        sharedPrefs.putString(FIREBASE_USER_ID_PREFS_KEY, id)
        firebaseUserIdLiveData.value = true
    }
}