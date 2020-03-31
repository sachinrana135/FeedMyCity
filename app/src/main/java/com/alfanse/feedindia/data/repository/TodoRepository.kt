package com.alfanse.feedindia.data.repository

import com.alfanse.feedindia.data.ApiService
import javax.inject.Inject

class TodoRepository
@Inject constructor(private val remote: ApiService) {

    suspend fun getTodoList() = remote.getTodoTaskList()

}