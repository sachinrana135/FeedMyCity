package com.alfanse.feedindia.ui.needier

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.alfanse.feedindia.data.NeedierDataSource
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.NeedieritemEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.User
import javax.inject.Inject


class NeedierListViewModel
@Inject constructor(
    private val repository: FeedAppRepository,
    private val storage: ApplicationStorage
) : ViewModel() {
    val groupId: String = User.groupId!!
    private val config: PagedList.Config =
        PagedList.Config.Builder()
            .setPageSize(LOAD_SIZE)
            .setEnablePlaceholders(true)
            .build()

    lateinit var needierLiveData: LiveData<PagedList<NeedieritemEntity>>
    lateinit var needierResourceLiveData: LiveData<Resource<List<NeedieritemEntity>>>

    fun getNeediers(status: String) {
        dataSourceFactory.status = status
        needierLiveData =
            LivePagedListBuilder<Int, NeedieritemEntity>(dataSourceFactory, config).build()
        needierResourceLiveData = Transformations.switchMap(
            dataSourceFactory.needierDataSourceLiveData,
            NeedierDataSource::responseLiveData
        )
    }

    private val dataSourceFactory = object : DataSource.Factory<Int, NeedieritemEntity>() {

        open var status: String = ""
        val needierDataSourceLiveData = MutableLiveData<NeedierDataSource>()

        override fun create(): DataSource<Int, NeedieritemEntity> {
            val needierDataSource = NeedierDataSource(viewModelScope, repository, groupId, status)
            needierDataSourceLiveData.postValue(needierDataSource)
            return needierDataSource
        }
    }

    fun refresh() {
        dataSourceFactory.needierDataSourceLiveData.value?.invalidate()
    }

    companion object {
        const val LOAD_SIZE = 10
    }
}
