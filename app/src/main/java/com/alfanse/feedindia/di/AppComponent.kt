package com.alfanse.feedindia.di

import com.alfanse.feedindia.ui.comment.CommentFragment
import com.alfanse.feedindia.ui.donor.DonorDetailsActivity
import com.alfanse.feedindia.ui.donor.DonorHomeActivity
import com.alfanse.feedindia.ui.donor.UpdateDonorActivity
import com.alfanse.feedindia.ui.groupdetails.GroupDetailsActivity
import com.alfanse.feedindia.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedindia.ui.member.MemberListActivity
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationActivity
import com.alfanse.feedindia.ui.mobileauth.MobileVerificationActivity
import com.alfanse.feedindia.ui.needier.NeedierDetailActivity
import com.alfanse.feedindia.ui.needier.NeedierListActivity
import com.alfanse.feedindia.ui.splash.SplashActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        ApiModule::class
    ]
)
interface AppComponent {
    fun inject(donorDetailsActivity: DonorDetailsActivity)
    fun inject(codeVerificationActivity: CodeVerificationActivity)
    fun inject(mobileVerificationActivity: MobileVerificationActivity)
    fun inject(splashActivity: SplashActivity)
    fun inject(donorHomeActivity: DonorHomeActivity)
    fun inject(updateDonorActivity: UpdateDonorActivity)
    fun inject(needierListActivity: NeedierListActivity)
    fun inject(groupDetailsActivity: GroupDetailsActivity)
    fun inject(groupHomeActivity: GroupHomeActivity)
    fun inject(memberListActivity: MemberListActivity)
    fun inject(commentFragment: CommentFragment)
    fun inject(needierDetailActivity: NeedierDetailActivity)
}
