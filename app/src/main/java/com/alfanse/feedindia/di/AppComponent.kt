package com.alfanse.feedindia.di

import com.alfanse.feedindia.ui.donordetails.DonorDetailsActivity
import com.alfanse.feedindia.ui.donordetails.DonorHomeActivity
import com.alfanse.feedindia.ui.donordetails.UpdateDonorActivity
import com.alfanse.feedindia.ui.groupdetails.GroupDetailsActivity
import com.alfanse.feedindia.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationActivity
import com.alfanse.feedindia.ui.mobileauth.MobileVerificationActivity
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
    fun inject(groupDetailsActivity: GroupDetailsActivity)
    fun inject(groupHomeActivity: GroupHomeActivity)
}
