package com.alfanse.feedindia.di

import com.alfanse.feedindia.ui.main.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        ApiModule::class
    ]
)
interface AppComponent {
    fun inject(mainFragment: MainFragment)
}
