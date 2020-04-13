package com.alfanse.feedmycity.ui.donor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.usertypes.UserTypesActivity
import com.alfanse.feedmycity.ui.volunteer.VolunteerHomeActivity
import com.alfanse.feedmycity.utils.User
import kotlinx.android.synthetic.main.activity_donor_home.*
import javax.inject.Inject

class DonorHomeActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: DonorHomeViewModel
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_home)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (application as FeedMyCityApplication).appComponent.inject(this)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DonorHomeViewModel::class.java)
        title = getString(R.string.txt_home)
        txt_user_name.text = User.name
        txt_donation_items.text = User.donateItems
        txt_visibility.text =
            if (User.donorVisibility!!) getString(R.string.txt_visible) else getString(R.string.txt_invisible)
        btnUpdate.setOnClickListener {
            startActivity(Intent(this, UpdateDonorActivity::class.java))
        }

        val plainText = getString(R.string.txt_donor_greeting)
        val greetingsContent = plainText + getString(R.string.txt_click_here)
        val spannableContent = SpannableString(greetingsContent)
        spannableContent.setSpan(ForegroundColorSpan(Color.BLUE), plainText.length,
            greetingsContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableContent.setSpan(UnderlineSpan(), plainText.length,
            greetingsContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableContent.setSpan(object : ClickableSpan(){
            override fun onClick(widget: View) {
                val intent = Intent(context, VolunteerHomeActivity::class.java)
                startActivity(intent)
            }
        }, plainText.length, greetingsContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txt_greeting.movementMethod = LinkMovementMethod.getInstance();
        txt_greeting.setText(spannableContent, TextView.BufferType.SPANNABLE)
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
