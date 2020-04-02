package com.alfanse.feedindia.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.alfanse.feedindia.data.ApplicationStorage
import com.alfanse.feedindia.data.PreferencesApplicationStorage
import com.alfanse.feedindia.data.repository.FeedAppRepository
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
    internal fun provideViewModelFactory(feedAppRepository: FeedAppRepository,
                                         sharedPreferences: ApplicationStorage): ViewModelFactory {
        return ViewModelFactory(feedAppRepository, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideUtils(context: Context): Utils = Utils(context)

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return app.getSharedPreferences(
            "feed-app_prefs",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideApplicationStorage(sharedPreferences: SharedPreferences): ApplicationStorage {
        return PreferencesApplicationStorage(sharedPreferences)
    }
}