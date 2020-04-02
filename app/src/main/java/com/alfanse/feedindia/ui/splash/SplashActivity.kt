package com.alfanse.feedindia.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.alfanse.feedindia.R
import com.alfanse.feedindia.ui.donordetails.DonorDetailsActivity
import com.alfanse.feedindia.ui.usertypes.UserTypesActivity

class SplashActivity : AppCompatActivity() {
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        launchNextScreen()
    }

    private fun launchNextScreen(){
        val handler = Handler()
        handler.postDelayed(Runnable {
            startActivity(Intent(mContext, UserTypesActivity::class.java))
        }, 3000)
    }
}
