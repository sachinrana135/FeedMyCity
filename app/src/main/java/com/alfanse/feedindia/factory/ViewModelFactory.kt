package com.alfanse.feedindia.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.ui.UserViewModel
import com.alfanse.feedindia.ui.donordetails.DonorDetailsViewModel
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationViewModel
import javax.inject.Inject
import javax.inject.Named

class ViewModelFactory @Inject constructor(
    private val feedAppRepository: FeedAppRepository,
    private val sharedPreferences: ApplicationStorage,
    @Named("memory") private val memoryStorage: ApplicationStorage
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(CodeVerificationViewModel::class.java) ->
                    CodeVerificationViewModel(memoryStorage)
                isAssignableFrom(DonorDetailsViewModel::class.java) ->
                    DonorDetailsViewModel(feedAppRepository, sharedPreferences, memoryStorage)
                isAssignableFrom(UserViewModel::class.java) ->
                    UserViewModel(feedAppRepository, sharedPreferences, memoryStorage)
                else ->
                    error("Invalid View Model class")
            }

        } as T
    }
}