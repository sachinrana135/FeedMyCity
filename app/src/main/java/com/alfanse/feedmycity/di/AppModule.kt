package com.alfanse.feedmycity.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.data.storage.MemoryApplicationStorage
import com.alfanse.feedmycity.data.storage.PreferencesApplicationStorage
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.utils.Utils
import dagger.Module
import dagger.Provides
import javax.inject.Named
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
    internal fun provideViewModelFactory(
        feedAppRepository: FeedAppRepository,
        sharedPreferences: ApplicationStorage,
        @Named("memory") memoryStorage: ApplicationStorage,
        utils: Utils
    ): ViewModelFactory {
        return ViewModelFactory(feedAppRepository, sharedPreferences,memoryStorage, utils  )
    }

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
        return PreferencesApplicationStorage(
            sharedPreferences
        )
    }

    @Provides
    @Singleton
    @Named("memory")
    fun provideMemoryApplicationStorage(): ApplicationStorage {
        return MemoryApplicationStorage()
    }

    @Provides
    fun provideUtils(app: Context, storage:ApplicationStorage): Utils {
        return Utils(app, storage )
    }
}