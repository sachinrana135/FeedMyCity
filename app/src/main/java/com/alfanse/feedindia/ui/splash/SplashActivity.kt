package com.alfanse.feedindia.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.data.models.UserEntity
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.UserViewModel
import com.alfanse.feedindia.ui.donordetails.DonorDetailsActivity
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationActivity
import com.alfanse.feedindia.ui.usertypes.UserTypesActivity
import com.alfanse.feedindia.utils.UserType
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject


class SplashActivity : AppCompatActivity() {
    private val mContext = this

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        (application as FeedIndiaApplication).appComponent.inject(this)
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)

        userViewModel.getLoggedUser().let {loggedUserId ->
            if (loggedUserId != null){
                userViewModel.getUserById(loggedUserId)
            } else {
                launchUserTypeScreen(UserTypesActivity::class.java)
            }
        }

        userViewModel.userLiveData.observe(this, observer)
    }

    private var observer = Observer<Resource<UserEntity>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                val phone = it.data?.mobile
                if (phone != null && it.data.userType == UserType.DONOR){
                    val donorDetailIntent = Intent(mContext, DonorDetailsActivity::class.java)
                    donorDetailIntent.putExtra(CodeVerificationActivity.MOBILE_NUM_KEY, donorDetailIntent)
                }
            }
            Status.EMPTY, Status.ERROR -> {
                progressBar.visibility = View.GONE
            }
        }
    }


    private fun launchUserTypeScreen(clazz: Class<out AppCompatActivity>) {
        val handler = Handler()
        handler.postDelayed(Runnable {
            startActivity(Intent(mContext, clazz))
            finish()
        }, 3000)
    }
}
