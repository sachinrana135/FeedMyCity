package com.alfanse.feedindia.ui.needier

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.models.SaveNeedierRequest
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedindia.utils.FIREBASE_USER_ID_PREFS_KEY
import com.alfanse.feedindia.utils.User
import com.alfanse.feedindia.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class NeedierDetailsViewModel
@Inject constructor(private val repository: FeedAppRepository) : ViewModel() {
    val saveNeedierLiveData = MutableLiveData<Resource<String>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        saveNeedierLiveData.value = Resource.error(throwable.message, null)
    }

    fun saveNeedierDetails(
        name: String,
        mobile: String,
        neededItems: String,
        lat: String,
        lng: String,
        locationAddress: String
    ) {
        saveNeedierLiveData.value = Resource.loading(null)

        viewModelScope.launch(handler) {
            val groupId = User.groupId
            val saveNeedierRequest = SaveNeedierRequest(groupId!!, lat, lng,
                locationAddress, mobile, name, neededItems)
            repository.saveNeedierDetails(saveNeedierRequest)?.let { response ->
                saveNeedierLiveData.value = Resource.success(response.userId)
            }
        }
    }
}