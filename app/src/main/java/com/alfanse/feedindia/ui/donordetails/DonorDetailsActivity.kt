package com.alfanse.feedindia.ui.donordetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfanse.feedindia.R

class DonorDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_details)
        title = getString(R.string.donor_details_screen_label)
    }
}
