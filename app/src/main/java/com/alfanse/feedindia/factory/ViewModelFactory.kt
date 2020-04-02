package com.alfanse.feedindia.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alfanse.feedindia.data.ApplicationStorage

import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.ui.donordetails.DonorDetailsViewModel
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(private val feedAppRepository: FeedAppRepository,
                                           private val sharedPreferences: ApplicationStorage) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(CodeVerificationViewModel::class.java) ->
                    CodeVerificationViewModel(sharedPreferences)
                isAssignableFrom(DonorDetailsViewModel::class.java) ->
                    DonorDetailsViewModel(feedAppRepository, sharedPreferences)
                else ->
                    error("Invalid View Model class")
            }

        } as T
    }
}