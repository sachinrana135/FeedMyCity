package com.alfanse.feedindia.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.alfanse.feedindia.R
import com.alfanse.feedindia.utils.User
import kotlinx.android.synthetic.main.activity_group_profile.*

class GroupProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_profile)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.profile_screen_label)
        bindProfileViewToData()
    }

    private fun bindProfileViewToData(){
        tvGroupName.text = User.groupName
        tvPhone.text = User.mobile
        tvAddress.text = User.address
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
