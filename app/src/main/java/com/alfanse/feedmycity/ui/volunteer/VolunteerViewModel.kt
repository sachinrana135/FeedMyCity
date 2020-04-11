package com.alfanse.feedmycity.ui.volunteer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.models.NearByGroupsEntity
import com.alfanse.feedmycity.data.repository.FeedAppRepository
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