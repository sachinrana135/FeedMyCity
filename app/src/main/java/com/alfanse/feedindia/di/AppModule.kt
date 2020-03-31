package com.alfanse.feedindia.di

import android.app.Application
import android.content.Context
import com.alfanse.feedindia.data.repository.TodoRepository
import com.alfanse.feedindia.factory.ViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideApplication(): Application = app

    @Provides
    internal fun provideViewModelFactory(todoRepository: TodoRepository): ViewModelFactory {
        return ViewModelFactory(todoRepository)
    }
}