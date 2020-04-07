package com.alfanse.feedindia.ui.groupdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.NearByUsersEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.utils.UserType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupHomeViewModel
@Inject constructor(private val repository: FeedAppRepository) : ViewModel() {
    val nearByUsersLiveData = MutableLiveData<Resource<List<NearByUsersEntity>>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        nearByUsersLiveData.value = Resource.error(throwable.message, null)
    }

    fun getNearByUsers(lat: Double,
                               lng: Double,
                               distance: Int){
        nearByUsersLiveData.value = Resource.loading(null)
        viewModelScope.launch(handler){
            repository.getNearByUsers(lat, lng, distance, UserType.ALL).let { response ->
                if (response.isNotEmpty()){
                    nearByUsersLiveData.value = Resource.success(response)
                }
            }
        }
    }
}