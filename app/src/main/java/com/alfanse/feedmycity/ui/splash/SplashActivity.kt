package com.alfanse.feedmycity.ui.splash

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.data.models.ConfigEntity
import com.alfanse.feedmycity.data.models.UserEntity
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.app_maintenance.ApiMaintenanceActivity
import com.alfanse.feedmycity.ui.app_upgrade.AppUpgradeActivity
import com.alfanse.feedmycity.ui.donor.DonorHomeActivity
import com.alfanse.feedmycity.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedmycity.ui.internet_check.NoInternetConnectionActivity
import com.alfanse.feedmycity.ui.intro.IntroActivity
import com.alfanse.feedmycity.ui.mobileauth.MobileVerificationActivity
import com.alfanse.feedmycity.ui.usertypes.UserTypesActivity
import com.alfanse.feedmycity.utils.BUNDLE_KEY_FORCE_UPGRADE
import com.alfanse.feedmycity.utils.BUNDLE_KEY_SKIP_CLICKED
import com.alfanse.feedmycity.utils.UserType
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject


class SplashActivity : AppCompatActivity() {
    private val mContext = this

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var splashViewModel: SplashViewModel
    private var config:ConfigEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        (application as FeedMyCityApplication).appComponent.inject(this)
        splashViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel::class.java)
        splashViewModel.userLiveData.observe(this, observer)
        splashViewModel.configLiveData.observe(this, configObserver)
    }

    override fun onStart() {
        super.onStart()
        checkInternetConnectivity()
    }

    private fun checkInternetConnectivity() {
        val isNetworkConnected = splashViewModel.isNetworkConnected()
        if (!isNetworkConnected!!) {
            val intent = Intent(this, NoInternetConnectionActivity::class.java)
            startActivity(intent)
        } else {
            if(config == null) {
                splashViewModel.getConfig()
            }
        }
    }

    private fun detectDynamicLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    val groupCode = deepLink?.getQueryParameter("groupCode")

                    if (groupCode != null) {
                        splashViewModel.saveGroupCode(groupCode)
                        val intent = Intent(mContext, MobileVerificationActivity::class.java)
                        intent.putExtra(MobileVerificationActivity.USER_TYPE_KEY, UserType.MEMBER)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    defaultNavigation()
                }

            }
            .addOnFailureListener(this) { e ->
                Log.w("Dynamic link", e.message)
            }
    }

    private fun defaultNavigation() {
        splashViewModel.getLoggedUser().let { loggedUserId ->
            if (loggedUserId != null) {
                splashViewModel.getUserById(loggedUserId)
            } else {
                launchUserTypeScreen()
            }
        }
    }

    private var observer = Observer<Resource<UserEntity>> { resource ->
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
                        launchUserTypeScreen()
                    }
                }
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(
                    findViewById(android.R.id.content),
                    resource.message ?: getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    private var configObserver = Observer<Resource<ConfigEntity>> { resource ->
        when (resource.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                config = resource.data
                //check if api is on maintenance
                if (!resource.data?.apiStatus!!) {
                    val intent = Intent(this, ApiMaintenanceActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@Observer
                }

                if (resource.data?.isUpdateAvailable!! && splashViewModel.shouldShowUpgrade()) {
                    val intent = Intent(this, AppUpgradeActivity::class.java)
                    intent.putExtra(BUNDLE_KEY_FORCE_UPGRADE, resource.data.isForceUpdate)
                    startActivityForResult(intent, APP_UPGRADE_REQUEST_CODE)
                } else {
                    detectDynamicLink()
                }

            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(
                    findViewById(android.R.id.content),
                    resource.message ?: getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            APP_UPGRADE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data?.extras != null) {
                        val isSkipClicked = data.getBooleanExtra(BUNDLE_KEY_SKIP_CLICKED, false)
                        if (isSkipClicked) {
                            detectDynamicLink()
                        }
                    }
                }
            }

        }
    }

    private fun launchUserTypeScreen() {
        if (splashViewModel.isFirstLaunch()) {
            startActivity(Intent(mContext, IntroActivity::class.java))
        } else {
            startActivity(Intent(mContext, UserTypesActivity::class.java))
        }
    }

    companion object {
        private const val TAG = "SplashActivity"
        private const val APP_UPGRADE_REQUEST_CODE = 999
    }
}
