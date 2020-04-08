package com.alfanse.feedindia.ui.comment

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.alfanse.feedindia.data.CommentDataSource
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.models.CommentEntity
import com.alfanse.feedindia.data.models.SaveCommentRequest
import com.alfanse.feedindia.data.repository.FeedAppRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommentViewModel @Inject constructor(
    private val repository: FeedAppRepository)
    : ViewModel() {


    private val config: PagedList.Config =
        PagedList.Config.Builder()
            .setPageSize(LOAD_SIZE)
            .setEnablePlaceholders(true)
            .build()

    lateinit var commentLiveData: LiveData<PagedList<CommentEntity>>
    lateinit var commentResourceLiveData: LiveData<Resource<List<CommentEntity>>>
     val saveCommentLiveData = MutableLiveData<Resource<Any>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        saveCommentLiveData.value = Resource.error(throwable.message, null)
    }

    fun getComments(needierItemId: String) {
        dataSourceFactory.needierItemId = needierItemId
        commentLiveData =
            LivePagedListBuilder<Int, CommentEntity>(dataSourceFactory, config).build()
        commentResourceLiveData = Transformations.switchMap(
            dataSourceFactory.commentDataSourceLiveData,
            CommentDataSource::responseLiveData
        )
    }

    private val dataSourceFactory = object : DataSource.Factory<Int, CommentEntity>() {

        open var needierItemId: String = ""
        val commentDataSourceLiveData = MutableLiveData<CommentDataSource>()

        override fun create(): DataSource<Int, CommentEntity> {
            val commentDataSource = CommentDataSource(viewModelScope, repository, needierItemId)
            commentDataSourceLiveData.postValue(commentDataSource)
            return commentDataSource
        }
    }

    fun refresh() {
        dataSourceFactory.commentDataSourceLiveData.value?.invalidate()
    }

    fun saveComment(saveCommentRequest: SaveCommentRequest) {
        saveCommentLiveData.value = Resource.loading(null)

        viewModelScope.launch(handler) {

            repository.saveComment(saveCommentRequest).let { response ->
                saveCommentLiveData.value = Resource.success(response)
            }
        }
    }

    companion object {
        const val LOAD_SIZE = 10
    }

}
