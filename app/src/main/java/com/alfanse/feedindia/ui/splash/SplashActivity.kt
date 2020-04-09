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
import com.alfanse.feedindia.ui.donor.DonorHomeActivity
import com.alfanse.feedindia.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedindia.ui.usertypes.UserTypesActivity
import com.alfanse.feedindia.utils.UserType
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject


class SplashActivity : AppCompatActivity() {
    private val mContext = this

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        (application as FeedIndiaApplication).appComponent.inject(this)
        splashViewModel = ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel::class.java)

        splashViewModel.getLoggedUser().let { loggedUserId ->
            if (loggedUserId != null) {
                splashViewModel.getUserById(loggedUserId)
            }else {
                launchUserTypeScreen(UserTypesActivity::class.java)
            }
        }

        splashViewModel.userLiveData.observe(this, observer)
    }

    private var observer = Observer<Resource<UserEntity>> { resource->
        when (resource.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {

                progressBar.visibility = View.GONE
                when (resource.data?.userType) {
                    UserType.DONOR -> {
                        val intent = Intent(mContext, DonorHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    UserType.MEMBER -> {
                        //navigate to member screen
                        val intent = Intent(mContext, GroupHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        launchUserTypeScreen(UserTypesActivity::class.java)
                    }
                }
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(findViewById(android.R.id.content), resource.message!!,
                    Snackbar.LENGTH_SHORT).show()
            }
            Status.EMPTY -> {

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
