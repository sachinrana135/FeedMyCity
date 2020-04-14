package com.alfanse.feedmycity.ui.groupdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.models.SaveGroupRequest
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedmycity.utils.FIREBASE_USER_ID_PREFS_KEY
import com.alfanse.feedmycity.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class GroupDetailsViewModel
@Inject constructor(
    private val repository: FeedAppRepository,
    private val storage: ApplicationStorage,
    @Named("memory") private val memoryStorage: ApplicationStorage,
    private val utils: Utils
) : ViewModel() {
    val saveGroupLiveData = MutableLiveData<Resource<String>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        saveGroupLiveData.value = Resource.error(throwable.message, null)
    }

    fun saveGroupDetails(
        name: String,
        address: String,
        groupName: String,
        lat: String,
        lng: String,
        locationAddress: String,
        mobile: String,
        regNo: String
    ) {
        saveGroupLiveData.value = Resource.loading(null)

        viewModelScope.launch(handler) {
            var firebaseId = memoryStorage.getString(FIREBASE_USER_ID_PREFS_KEY, "")!!
            val saveGroupRequest = SaveGroupRequest(address, name,
                firebaseId, groupName, lat, lng, locationAddress, mobile, regNo)

            repository.saveGroup(saveGroupRequest).let { response ->
                response.userId?.let {
                    memoryStorage.clearValue(FIREBASE_USER_ID_PREFS_KEY)
                    storage.putString(APP_USER_ID_PREFS_KEY, it)
                    repository.getUserById(it).let { userEntity ->
                        utils.setLoggedUser(userEntity)
                        saveGroupLiveData.value = Resource.success(it)
                    }
                }
            }
        }
    }
}