package com.alfanse.feedmycity.ui.mobileauth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.models.UserEntity
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedmycity.utils.BUNDLE_KEY_GROUP_CODE
import com.alfanse.feedmycity.utils.FIREBASE_USER_ID_PREFS_KEY
import com.alfanse.feedmycity.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class MobileVerificationViewModel
@Inject constructor(
    @Named("memory") private val memoryStorage: ApplicationStorage,
    private val repository: FeedAppRepository,
    private val utils: Utils,
    private val storage: ApplicationStorage
) : ViewModel() {

    var firebaseUserIdLiveData = MutableLiveData<Boolean>()
    val userLiveData = MutableLiveData<Resource<UserEntity>>()

    private val userLiveDataHandler = CoroutineExceptionHandler { _, throwable ->
        userLiveData.value = Resource.error(throwable.message, null)
    }

    fun saveFirebaseUserId(id: String) {
        memoryStorage.putString(FIREBASE_USER_ID_PREFS_KEY, id)
        firebaseUserIdLiveData.value = true
    }

    fun getUserByMobile(mobile: String) {
        userLiveData.value = Resource.loading(null)

        viewModelScope.launch(userLiveDataHandler) {

            repository.getUserByMobile(mobile).let { user ->
                memoryStorage.clearValue(BUNDLE_KEY_GROUP_CODE)
                storage.putString(APP_USER_ID_PREFS_KEY, user.userId)
                utils.setLoggedUser(user)
                userLiveData.value = Resource.success(user)
            }
        }
    }
}