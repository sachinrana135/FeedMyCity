package com.alfanse.feedmycity.ui.needier

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.models.NeedierItemStatusEntity
import com.alfanse.feedmycity.data.models.NeedieritemEntity
import com.alfanse.feedmycity.data.models.UpdateNeedierItemStatusRequest
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class NeedierDetailViewModel
@Inject constructor(private val repository: FeedAppRepository) : ViewModel() {
    val needierLiveData = MutableLiveData<Resource<NeedieritemEntity>>()
    val needierStatusTypesLiveData = MutableLiveData<Resource<List<NeedierItemStatusEntity>>>()
    val updateneedierStatusLiveData = MutableLiveData<Resource<Any>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        needierLiveData.value = Resource.error(throwable.message, null)
    }

    fun getNeedier(needierItemId: String) {
        needierLiveData.value = Resource.loading(null)
        viewModelScope.launch(handler) {
            repository.getNeedier(needierItemId).let { response ->
                response?.let {
                    needierLiveData.value = Resource.success(response)
                }
            }
        }
    }

    fun getNeedierItemStatus() {
        needierStatusTypesLiveData.value = Resource.loading(null)
        viewModelScope.launch(handler) {
            repository.getNeedierItemStatusTypes().let { response ->
                response?.let {
                    needierStatusTypesLiveData.value = Resource.success(response)
                }
            }
        }
    }

    fun updateNeedierItemStatus(request: UpdateNeedierItemStatusRequest) {
        updateneedierStatusLiveData.value = Resource.loading(null)
        viewModelScope.launch(handler) {
            repository.updateNeedierItemStatus(request).let { response ->
                response?.let {
                    updateneedierStatusLiveData.value = Resource.success(response)
                }
            }
        }
    }
}