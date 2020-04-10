package com.alfanse.feedindia.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alfanse.feedindia.data.repository.FeedAppRepository
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.ui.comment.CommentViewModel
import com.alfanse.feedindia.ui.donor.DonorDetailsViewModel
import com.alfanse.feedindia.ui.donor.DonorHomeViewModel
import com.alfanse.feedindia.ui.donor.UpdateDonorViewModel
import com.alfanse.feedindia.ui.groupdetails.GroupDetailsViewModel
import com.alfanse.feedindia.ui.groupdetails.GroupHomeViewModel
import com.alfanse.feedindia.ui.member.AddMemberViewModel
import com.alfanse.feedindia.ui.member.MemberListViewModel
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationViewModel
import com.alfanse.feedindia.ui.mobileauth.MobileVerificationViewModel
import com.alfanse.feedindia.ui.needier.NeedierDetailViewModel
import com.alfanse.feedindia.ui.needier.NeedierDetailsViewModel
import com.alfanse.feedindia.ui.needier.NeedierListViewModel
import com.alfanse.feedindia.ui.splash.SplashViewModel
import com.alfanse.feedindia.ui.volunteer.VolunteerViewModel
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
                    MobileVerificationViewModel(
                        memoryStorage,
                        feedAppRepository,
                        utils,
                        sharedPreferences
                    )
                isAssignableFrom(UpdateDonorViewModel::class.java) ->
                    UpdateDonorViewModel(feedAppRepository)
                isAssignableFrom(NeedierListViewModel::class.java) ->
                    NeedierListViewModel(feedAppRepository, sharedPreferences)
                isAssignableFrom(GroupDetailsViewModel::class.java) ->
                    GroupDetailsViewModel(
                        feedAppRepository,
                        sharedPreferences,
                        memoryStorage,
                        utils
                    )
                isAssignableFrom(GroupHomeViewModel::class.java) ->
                    GroupHomeViewModel(feedAppRepository, utils)
                isAssignableFrom(MemberListViewModel::class.java) ->
                    MemberListViewModel(feedAppRepository, sharedPreferences)
                isAssignableFrom(NeedierDetailsViewModel::class.java) ->
                    NeedierDetailsViewModel(feedAppRepository)
                isAssignableFrom(AddMemberViewModel::class.java) ->
                    AddMemberViewModel(
                        feedAppRepository,
                        sharedPreferences,
                        memoryStorage,
                        utils
                    )
                isAssignableFrom(CommentViewModel::class.java) ->
                    CommentViewModel(feedAppRepository)
                isAssignableFrom(NeedierDetailViewModel::class.java) ->
                    NeedierDetailViewModel(feedAppRepository)
                isAssignableFrom(VolunteerViewModel::class.java) ->
                    VolunteerViewModel(feedAppRepository)
                else ->
                    error("Invalid View Model class")
            }

        } as T
    }
}