package com.alfanse.feedindia.ui.volunteer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.NearByGroupsEntity
import com.alfanse.feedindia.data.models.NearByUsersEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class VolunteerViewModel
@Inject constructor(private val repository: FeedAppRepository
) : ViewModel()  {
    val nearByGroupsLiveData = MutableLiveData<Resource<List<NearByGroupsEntity>>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        nearByGroupsLiveData.value = Resource.error(throwable.message, null)
    }

    fun getNearByUsers(lat: Double,
                       lng: Double,
                       distance: Int){
        nearByGroupsLiveData.value = Resource.loading(null)
        viewModelScope.launch(handler){
            repository.getNearByGroups(
                lat, lng, distance).let { response ->
                if (response.isNotEmpty()){
                    nearByGroupsLiveData.value = Resource.success(response)
                }
            }
        }
    }
}