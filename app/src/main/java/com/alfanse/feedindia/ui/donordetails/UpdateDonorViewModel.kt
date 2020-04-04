package com.alfanse.feedindia.ui.donordetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.UpdateDonorRequest
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.utils.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateDonorViewModel
@Inject constructor(
    private val repository: FeedAppRepository
) : ViewModel() {
    val updateDonorLiveData = MutableLiveData<Resource<Any>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        updateDonorLiveData.value = Resource.error(throwable.message, null)
    }

    fun updateDonorDetails(
        userId: String,
        donateItems: String,
        status: String
    ) {
        updateDonorLiveData.value = Resource.loading(null)

        viewModelScope.launch(handler) {
            val updateDonorRequest = UpdateDonorRequest(userId, donateItems, status)

            repository.updateDonor(updateDonorRequest).let {
                User.donateItems = donateItems
                User.donorVisibility = status == "1"
                updateDonorLiveData.value = Resource.success(it)
            }
        }
    }
}