package com.alfanse.feedindia.ui.donordetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationActivity
import com.google.android.material.snackbar.Snackbar
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import kotlinx.android.synthetic.main.activity_donor_details.*
import javax.inject.Inject

class DonorDetailsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var donorDetailViewModel: DonorDetailsViewModel
    private var donorLat = 0.0
    private var donorLng = 0.0
    private var phone = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_details)
        title = getString(R.string.donor_details_screen_label)
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as FeedIndiaApplication).appComponent.inject(this)
        donorDetailViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(DonorDetailsViewModel::class.java)
        initListener()
        donorDetailViewModel.saveDonorLiveData.observe(this, observer)
        readPhoneNum()
    }

    private fun readPhoneNum() {
        if (intent != null){
            phone = intent.getStringExtra(CodeVerificationActivity.MOBILE_NUM_KEY)!!
        }
    }

    private fun initListener(){
        var status = 1
        rbActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) status = 1
        }
        rbInActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) status = 0
        }
        etDonorAddress.setOnClickListener {
            startLocationPicker()
        }

        btnSave.setOnClickListener {
            when {
                etName.text.toString().trim().isEmpty() -> {
                    etName.error = "Enter name"
                    return@setOnClickListener
                }

                etDonationInfo.text.toString().trim().isEmpty() -> {
                    etDonationInfo.error = "Enter your donation"
                    return@setOnClickListener
                }

                etDonorAddress.text.toString().trim().isEmpty() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Please give you location",
                        Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            donorDetailViewModel.saveDonorDetails(etName.text.toString(),
                etDonationInfo.text.toString(), status.toString(),
                donorLat.toString(),
                donorLng.toString(),
                phone,
                etDonorAddress.text.toString())
        }
    }

    private var observer = Observer<Resource<String>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                val intent = Intent(this, DonorHomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
            }
            Status.EMPTY -> {

            }
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

    private fun startLocationPicker(){
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(DEFAULT_LAT, DEFAULT_LNG)
            .withSearchZone(INDIA_LOCALE_ZONE)
            .withDefaultLocaleSearchZone()
            .withVoiceSearchHidden()
            .withUnnamedRoadHidden()
            .build(applicationContext)

        startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED){
            if (requestCode == 1){
                if (data != null){
                    donorLat = data.getDoubleExtra(LATITUDE, 0.0)
                    donorLng = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    if (address != null){
                        setAddressToView(address)
                    }
                }
            }
        }
    }

    private fun setAddressToView(address: String){
        etDonorAddress.setText(address)
    }

    companion object {
        private const val TAG = "DonorDetailsActivity"
        private const val MAP_BUTTON_REQUEST_CODE = 1
        private const val DEFAULT_LAT = 28.6429
        private const val DEFAULT_LNG = 77.2191
        private const val INDIA_LOCALE_ZONE = "en_in"
    }
}
