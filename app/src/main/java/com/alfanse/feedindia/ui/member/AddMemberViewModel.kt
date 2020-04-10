package com.alfanse.feedindia.ui.member

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.SaveMemberRequest
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedindia.utils.BUNDLE_KEY_GROUP_CODE
import com.alfanse.feedindia.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class AddMemberViewModel
@Inject constructor(
    private val repository: FeedAppRepository,
    private val storage: ApplicationStorage,
    @Named("memory") private val memoryStorage: ApplicationStorage,
    private val utils: Utils
) : ViewModel() {
    val addMemberLiveData = MutableLiveData<Resource<String>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        addMemberLiveData.value = Resource.error(throwable.message, null)
    }

    fun saveMember(
        request: SaveMemberRequest
    ) {
        addMemberLiveData.value = Resource.loading(null)

        viewModelScope.launch(handler) {
            repository.saveMember(request)?.let { response ->
                response.userId?.let { userId->
                    repository.getUserById(userId).let { user ->
                        storage.putString(APP_USER_ID_PREFS_KEY, user.userId)
                        utils.setLoggedUser(user)
                        addMemberLiveData.value = Resource.success(user.userId)
                    }
                }
            }
        }
    }

    fun getGroupId() = memoryStorage.getString(BUNDLE_KEY_GROUP_CODE, null)
}