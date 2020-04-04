package com.alfanse.feedindia.ui.usertypes

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.alfanse.feedindia.R
import com.alfanse.feedindia.ui.mobileauth.MobileVerificationActivity
import kotlinx.android.synthetic.main.activity_joinee_types.*

class UserTypesActivity : AppCompatActivity() {
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joinee_types)
        title = getString(R.string.join_as_screen_label)
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setListener()
    }

    private fun setListener(){
        layoutDonorType.setOnClickListener {
            startActivity(Intent(mContext, MobileVerificationActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
