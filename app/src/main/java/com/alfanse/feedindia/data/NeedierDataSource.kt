package com.alfanse.feedindia.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.alfanse.feedindia.data.models.NeedieritemEntity
import com.alfanse.feedindia.data.repository.FeedAppRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NeedierDataSource(
    private val scope: CoroutineScope,
    private val repository: FeedAppRepository,
    private val groupId: String,
    private val status: String
) : PageKeyedDataSource<Int, NeedieritemEntity>() {

    var errorLiveData = MutableLiveData<String>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        errorLiveData.value = throwable.message
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, NeedieritemEntity>
    ) {
        scope.launch (handler){
            val response = repository.getNeediers(
                groupId = groupId,
                status = status,
                page = 1,
                pageLoad = params.requestedLoadSize
            )
            callback.onResult(response, null, 2)
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NeedieritemEntity>
    ) {
        scope.launch(handler) {

            val response = repository.getNeediers(
                groupId = groupId,
                status = status,
                page = params.key,
                pageLoad = params.requestedLoadSize
            )

            callback.onResult(response, params.key + 1)
        }

    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NeedieritemEntity>
    ) {
    }
}