package com.alfanse.feedindia.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.alfanse.feedindia.R
import com.alfanse.feedindia.ui.JoineeTypesActivity

class SplashActivity : AppCompatActivity() {
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        launchNextScreen()
    }

    private fun launchNextScreen(){
        val hanler = Handler()
        hanler.postDelayed(Runnable {
            startActivity(Intent(mContext, JoineeTypesActivity::class.java))
        }, 3000)
    }
}
