package com.alfanse.feedindia.data

import com.alfanse.feedindia.data.models.TodoEntity
import retrofit2.http.GET

interface ApiService {
    @GET("todos")
    suspend fun getTodoTaskList(): List<TodoEntity>
}