package com.alfanse.feedindia.ui.needier

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.alfanse.feedindia.data.NeedierDataSource
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.NeedieritemEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.APP_GROUP_ID_PREFS_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NeedierListViewModel
@Inject constructor(
    private val repository: FeedAppRepository,
    private val storage: ApplicationStorage
) : ViewModel() {
    val groupId: String = storage.getString(APP_GROUP_ID_PREFS_KEY, "")!!
    private val config: PagedList.Config =
        PagedList.Config.Builder()
            .setPageSize(LOAD_SIZE)
            .setEnablePlaceholders(true)
            .build()

    val needierLiveData = MutableLiveData<Resource<PagedList<NeedieritemEntity>>>()

    fun getNeediers(status: String) {
        needierLiveData.value = Resource.loading(null)
        initializedPagedListBuilder(config, groupId, status).build().observeForever {
            needierLiveData.value = Resource.success(it)
        }
    }

    private fun initializedPagedListBuilder(
        config: PagedList.Config,
        groupId: String,
        status: String
    ): LivePagedListBuilder<Int, NeedieritemEntity> {

        val dataSourceFactory = object : DataSource.Factory<Int, NeedieritemEntity>() {
            override fun create(): DataSource<Int, NeedieritemEntity> {
                val source = NeedierDataSource(viewModelScope, repository, groupId, status)
                viewModelScope.launch(Dispatchers.Main) {
                    source.errorLiveData.observeForever {
                        needierLiveData.value = Resource.error(it, null)
                    }
                }
                return source
            }
        }
        return LivePagedListBuilder<Int, NeedieritemEntity>(dataSourceFactory, config)
    }

    companion object {
        const val LOAD_SIZE = 10
    }
}
