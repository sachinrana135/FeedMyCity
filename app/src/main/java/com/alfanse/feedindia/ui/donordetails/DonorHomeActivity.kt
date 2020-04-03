package com.alfanse.feedindia.ui.donordetails

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.usertypes.UserTypesActivity
import com.alfanse.feedindia.utils.User
import kotlinx.android.synthetic.main.activity_donor_home.*
import javax.inject.Inject

class DonorHomeActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: DonorHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_home)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (application as FeedIndiaApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).
            get(DonorHomeViewModel::class.java)
        title = getString(R.string.txt_home)
        txt_user_name.text = User.name
        txt_donation_items.text = User.donateItems
        txt_visibility.text =
            if (User.donorVisibility!!) getString(R.string.txt_visible) else getString(R.string.txt_invisible)
        btnUpdate.setOnClickListener {
            //navigate to donor update screen
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.donor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.logout -> {
                viewModel.logoutUser()
                val intent = Intent(this, UserTypesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    companion object {
        private const val TAG = "DonorHomeActivity"
    }
}
