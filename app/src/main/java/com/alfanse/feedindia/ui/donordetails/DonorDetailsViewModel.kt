package com.alfanse.feedindia.ui.donordetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.ApplicationStorage
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.SaveDonorRequest
import com.alfanse.feedindia.data.models.SaveDonorResponse
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.utils.FIREBASE_USER_ID_PREFS_KEY
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DonorDetailsViewModel
@Inject constructor(private val repository: FeedAppRepository,
                    private val sharedPrefs: ApplicationStorage) : ViewModel() {
    val saveDonorLiveData = MutableLiveData<Resource<SaveDonorResponse>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        val msg = if (throwable.message != null) throwable.message as String else "Error"
        saveDonorLiveData.value = Resource.error(msg, null)
    }

    fun saveDonorDetails(name: String,
                         donateItems: String,
                         status: String,
                         lat: String,
                         lng: String,
                         mobile: String){
        saveDonorLiveData.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO){
            val saveDonorRequest = getDonorDetailsRequest(name, donateItems, status, lat, lng, mobile)
            repository.saveDonor(saveDonorRequest).let {
                if (it.isCallSuccess()){
                    saveDonorLiveData.value = Resource.success(it.response)
                } else {
                    val exception = Throwable("Something went wrong")
                    throw Exception(exception)
                }
            }
        }
    }

    private fun getDonorDetailsRequest(name: String,
                                       donateItems: String,
                                       status: String,
                                       lat: String,
                                       lng: String,
                                       mobile: String): SaveDonorRequest{
        var saveFirebaseUserId = sharedPrefs.getString(FIREBASE_USER_ID_PREFS_KEY, "")
        if (saveFirebaseUserId == null){
            saveFirebaseUserId = ""
        }
        return SaveDonorRequest(donateItems, saveFirebaseUserId, lat,
            lng, mobile, name, status)
    }
}