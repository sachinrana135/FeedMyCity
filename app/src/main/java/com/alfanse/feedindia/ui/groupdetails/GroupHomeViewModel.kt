package com.alfanse.feedindia.ui.groupdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.NearByUsersEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.utils.User
import com.alfanse.feedindia.utils.UserType
import com.alfanse.feedindia.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupHomeViewModel
@Inject constructor(private val repository: FeedAppRepository,
                    private val utils: Utils) : ViewModel() {
    val nearByUsersLiveData = MutableLiveData<Resource<List<NearByUsersEntity>>>()
    val logOutUserLiveData = MutableLiveData<Boolean>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        nearByUsersLiveData.value = Resource.error(throwable.message, null)
    }

    fun getNearByUsers(distance: Int){
        nearByUsersLiveData.value = Resource.loading(null)
        viewModelScope.launch(handler){
            repository.getNearByUsers(User.lat!!.toDouble(), User.lng!!.toDouble(),
                distance, UserType.ALL).let { response ->
                if (response.isNotEmpty()){
                    nearByUsersLiveData.value = Resource.success(response)
                }
            }
        }
    }

    fun signOut(){
        utils.logoutUser().let {
            logOutUserLiveData.value = true
        }
    }

}