package com.alfanse.feedindia.ui.groupdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.models.SaveGroupRequest
import com.alfanse.feedindia.data.models.UserEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedindia.utils.FIREBASE_USER_ID_PREFS_KEY
import com.alfanse.feedindia.utils.UserType
import com.alfanse.feedindia.utils.Utils
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
                if (response.userId != null){
                    storage.putString(APP_USER_ID_PREFS_KEY, response.userId)
                    var user = UserEntity(
                        response.userId,
                        firebaseId,
                        name,
                        mobile,
                        UserType.MEMBER,
                        true,
                        "",
                        "",
                        false,
                        lat,
                        lng,
                        groupName
                    )
                    utils.setLoggedUser(user)
                    saveGroupLiveData.value = Resource.success(response.userId)
                }
            }
        }
    }
}