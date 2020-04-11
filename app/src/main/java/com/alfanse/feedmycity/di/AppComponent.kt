package com.alfanse.feedmycity.di

import com.alfanse.feedmycity.ui.comment.CommentFragment
import com.alfanse.feedmycity.ui.donor.DonorDetailsActivity
import com.alfanse.feedmycity.ui.donor.DonorHomeActivity
import com.alfanse.feedmycity.ui.donor.UpdateDonorActivity
import com.alfanse.feedmycity.ui.groupdetails.GroupDetailsActivity
import com.alfanse.feedmycity.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedmycity.ui.member.AddMemberActivity
import com.alfanse.feedmycity.ui.member.MemberListActivity
import com.alfanse.feedmycity.ui.mobileauth.CodeVerificationActivity
import com.alfanse.feedmycity.ui.mobileauth.MobileVerificationActivity
import com.alfanse.feedmycity.ui.needier.AddNeedierDetailActivity
import com.alfanse.feedmycity.ui.needier.NeedierDetailActivity
import com.alfanse.feedmycity.ui.needier.NeedierListActivity
import com.alfanse.feedmycity.ui.splash.SplashActivity
import com.alfanse.feedmycity.ui.volunteer.VolunteerHomeActivity
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
    fun inject(addNeedierDetailActivity: AddNeedierDetailActivity)
    fun inject(addMemberActivity: AddMemberActivity)
    fun inject(volunteerHomeActivity: VolunteerHomeActivity)
}
