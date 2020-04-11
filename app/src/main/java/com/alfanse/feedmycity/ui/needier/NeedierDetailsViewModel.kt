package com.alfanse.feedmycity.ui.needier

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.models.SaveNeedierRequest
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import com.alfanse.feedmycity.utils.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

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