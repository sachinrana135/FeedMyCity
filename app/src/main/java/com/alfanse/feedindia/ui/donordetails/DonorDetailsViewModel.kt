package com.alfanse.feedindia.ui.donordetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedindia.utils.FIREBASE_USER_ID_PREFS_KEY
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class DonorDetailsViewModel
@Inject constructor(
    private val repository: FeedAppRepository,
    private val storage: ApplicationStorage,
    @Named("memory") private val memoryStorage: ApplicationStorage
) : ViewModel() {
    val saveDonorLiveData = MutableLiveData<Resource<String>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        val msg =
            if (throwable.message != null) throwable.message as String else "Something went wrong"
        saveDonorLiveData.value = Resource.error(msg, null)
    }

    fun saveDonorDetails(
        name: String,
        donateItems: String,
        status: String,
        lat: String,
        lng: String,
        mobile: String
    ) {
        saveDonorLiveData.value = Resource.loading(null)
        viewModelScope.launch(handler) {
            var firebaseId = memoryStorage.getString(FIREBASE_USER_ID_PREFS_KEY, "")!!
            val saveDonorRequest =
                SaveDonorRequest(donateItems, firebaseId, lat, lng, mobile, name, status)
            repository.saveDonor(saveDonorRequest).let { userId ->
                if (userId != null && userId != "") {
                    storage.putString(APP_USER_ID_PREFS_KEY, userId)
                    saveDonorLiveData.value = Resource.success(userId)
                }
            }
        }
    }
}