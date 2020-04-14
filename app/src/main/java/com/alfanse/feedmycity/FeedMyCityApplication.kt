package com.alfanse.feedmycity

import android.app.Application
import com.alfanse.feedmycity.di.AppComponent
import com.alfanse.feedmycity.di.AppModule
import com.alfanse.feedmycity.di.DaggerAppComponent

class FeedMyCityApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }

    private fun initDagger(app: FeedMyCityApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

}