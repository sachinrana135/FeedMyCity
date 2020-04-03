package com.alfanse.feedindia.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.ui.donordetails.DonorDetailsViewModel
import com.alfanse.feedindia.ui.donordetails.DonorHomeViewModel
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationViewModel
import com.alfanse.feedindia.ui.mobileauth.MobileVerificationViewModel
import com.alfanse.feedindia.ui.splash.SplashViewModel
import com.alfanse.feedindia.utils.Utils
import javax.inject.Inject
import javax.inject.Named

class ViewModelFactory @Inject constructor(
    private val feedAppRepository: FeedAppRepository,
    private val sharedPreferences: ApplicationStorage,
    @Named("memory") private val memoryStorage: ApplicationStorage,
    private val utils: Utils
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(CodeVerificationViewModel::class.java) ->
                    CodeVerificationViewModel(memoryStorage)
                isAssignableFrom(DonorDetailsViewModel::class.java) ->
                    DonorDetailsViewModel(
                        feedAppRepository,
                        sharedPreferences,
                        memoryStorage,
                        utils
                    )
                isAssignableFrom(SplashViewModel::class.java) ->
                    SplashViewModel(
                        feedAppRepository,
                        sharedPreferences,
                        memoryStorage,
                        utils
                    )
                isAssignableFrom(DonorHomeViewModel::class.java) ->
                    DonorHomeViewModel(utils)
                isAssignableFrom(DonorDetailsViewModel::class.java) ->
                    DonorDetailsViewModel(
                        feedAppRepository,
                        sharedPreferences,
                        memoryStorage,
                        utils
                    )
                isAssignableFrom(MobileVerificationViewModel::class.java) ->
                    MobileVerificationViewModel(memoryStorage)
                else ->
                    error("Invalid View Model class")
            }

        } as T
    }
}