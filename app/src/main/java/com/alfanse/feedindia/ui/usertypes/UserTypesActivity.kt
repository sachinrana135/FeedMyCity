package com.alfanse.feedindia.ui.usertypes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfanse.feedindia.R
import com.alfanse.feedindia.ui.mobileauth.MobileVerificationActivity
import kotlinx.android.synthetic.main.activity_joinee_types.*

class UserTypesActivity : AppCompatActivity() {
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joinee_types)
        title = getString(R.string.join_as_screen_label)
        setListener()
    }

    private fun setListener(){
        layoutDonorType.setOnClickListener {
            startActivity(Intent(mContext, MobileVerificationActivity::class.java))
        }
    }
}
