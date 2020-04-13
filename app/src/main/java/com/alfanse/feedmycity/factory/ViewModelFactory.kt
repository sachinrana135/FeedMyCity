package com.alfanse.feedmycity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.ui.app_maintenance.AppUpgradeViewModel
import com.alfanse.feedmycity.ui.comment.CommentViewModel
import com.alfanse.feedmycity.ui.donor.DonorDetailsViewModel
import com.alfanse.feedmycity.ui.donor.DonorHomeViewModel
import com.alfanse.feedmycity.ui.donor.UpdateDonorViewModel
import com.alfanse.feedmycity.ui.groupdetails.GroupDetailsViewModel
import com.alfanse.feedmycity.ui.groupdetails.GroupHomeViewModel
import com.alfanse.feedmycity.ui.intro.IntroViewModel
import com.alfanse.feedmycity.ui.member.AddMemberViewModel
import com.alfanse.feedmycity.ui.member.MemberListViewModel
import com.alfanse.feedmycity.ui.mobileauth.CodeVerificationViewModel
import com.alfanse.feedmycity.ui.mobileauth.MobileVerificationViewModel
import com.alfanse.feedmycity.ui.needier.NeedierDetailViewModel
import com.alfanse.feedmycity.ui.needier.NeedierDetailsViewModel
import com.alfanse.feedmycity.ui.needier.NeedierListViewModel
import com.alfanse.feedmycity.ui.splash.SplashViewModel
import com.alfanse.feedmycity.ui.volunteer.VolunteerViewModel
import com.alfanse.feedmycity.utils.Utils
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
                isAssignableFrom(IntroViewModel::class.java) ->
                    IntroViewModel(sharedPreferences)
                isAssignableFrom(AppUpgradeViewModel::class.java) ->
                    AppUpgradeViewModel(sharedPreferences, utils)
                else ->
                    error("Invalid View Model class")
            }

        } as T
    }
}