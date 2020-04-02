package com.alfanse.feedindia

import android.app.Application
import com.alfanse.feedindia.di.AppComponent
import com.alfanse.feedindia.di.AppModule
import com.alfanse.feedindia.di.DaggerAppComponent

class FeedIndiaApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }

    private fun initDagger(app: FeedIndiaApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

}