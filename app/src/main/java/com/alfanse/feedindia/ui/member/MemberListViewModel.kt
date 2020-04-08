package com.alfanse.feedindia.ui.member

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.alfanse.feedindia.data.MemberDataSource
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.UserEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.utils.APP_GROUP_ID_PREFS_KEY
import javax.inject.Inject


class MemberListViewModel
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

    lateinit var memberLiveData: LiveData<PagedList<UserEntity>>
    lateinit var memberResourceLiveData: LiveData<Resource<List<UserEntity>>>

    fun getMembers() {
        memberLiveData =
            LivePagedListBuilder<Int, UserEntity>(dataSourceFactory, config).build()
        memberResourceLiveData = Transformations.switchMap(
            dataSourceFactory.memberDataSourceLiveData,
            MemberDataSource::responseLiveData
        )
    }

    private val dataSourceFactory = object : DataSource.Factory<Int, UserEntity>() {
        val memberDataSourceLiveData = MutableLiveData<MemberDataSource>()

        override fun create(): DataSource<Int, UserEntity> {
            val memberDataSource = MemberDataSource(viewModelScope, repository, groupId)
            memberDataSourceLiveData.postValue(memberDataSource)
            return memberDataSource
        }
    }

    fun refresh() {
        dataSourceFactory.memberDataSourceLiveData.value?.invalidate()
    }

    companion object {
        const val LOAD_SIZE = 10
    }
}
