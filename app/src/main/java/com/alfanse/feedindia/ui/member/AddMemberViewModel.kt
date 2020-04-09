package com.alfanse.feedindia.ui.member

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.SaveNeedierRequest
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.utils.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddMemberViewModel
@Inject constructor(private val repository: FeedAppRepository) : ViewModel() {
    val addMemberLiveData = MutableLiveData<Resource<String>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        addMemberLiveData.value = Resource.error(throwable.message, null)
    }

    fun saveMember(
        name: String,
        mobile: String,
        neededItems: String,
        lat: String,
        lng: String,
        locationAddress: String,
        groupId: String
    ) {
        addMemberLiveData.value = Resource.loading(null)

        viewModelScope.launch(handler) {
            val saveNeedierRequest = SaveNeedierRequest(groupId, lat, lng,
                locationAddress, mobile, name, neededItems)
            repository.saveMember(saveNeedierRequest)?.let { response ->
                addMemberLiveData.value = Resource.success(response.userId)
            }
        }
    }
}