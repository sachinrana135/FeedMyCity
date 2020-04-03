package com.alfanse.feedindia.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.UserEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedindia.utils.UserType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class UserViewModel @Inject constructor(
    private val repository: FeedAppRepository,
    private val storage: ApplicationStorage,
    @Named("memory") private val memoryStorage: ApplicationStorage
) : ViewModel() {

    val userLiveData = MutableLiveData<Resource<UserEntity>>()

    private val userLiveDataHandler = CoroutineExceptionHandler { _, throwable ->
        userLiveData.value = Resource.error(throwable.message, null)
    }

    fun getUserByMobile(mobile: String) {
        userLiveData.value = Resource.loading(null)

        viewModelScope.launch(userLiveDataHandler) {

            repository.getUserByMobile(mobile).let { response ->
                userLiveData.value = Resource.success(response)
            }
        }
    }

    fun getUserById(userId: String) {
        userLiveData.value = Resource.loading(null)

        viewModelScope.launch(userLiveDataHandler) {

            repository.getUserById(userId).let { user ->
                userLiveData.value = Resource.success(user)

                if(user.userType == UserType.DONOR) {
                    //navigate to donor screen
                }else if(user.userType == UserType.MEMBER) {
                    //navigate to member screen
                }
            }
        }
    }

    fun getLoggedUser() = storage.getString(APP_USER_ID_PREFS_KEY, null)
}