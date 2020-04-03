package com.alfanse.feedindia.ui.donordetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.data.models.SaveDonorResponse
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationActivity
import com.schibstedspain.leku.*
import kotlinx.android.synthetic.main.activity_donor_details.*
import javax.inject.Inject

class DonorDetailsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var donorDetailViewModel: DonorDetailsViewModel
    private var donorLat = 0.0
    private var donorLng = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_details)
        title = getString(R.string.donor_details_screen_label)
        (application as FeedIndiaApplication).appComponent.inject(this)
        donorDetailViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(DonorDetailsViewModel::class.java)
        initListener()
        donorDetailViewModel.saveDonorLiveData.observe(this, observer)
    }

    private fun readPhoneNum(): String? {
        return intent.getStringExtra(CodeVerificationActivity.MOBILE_NUM_KEY)
    }

    private fun initListener(){
        var status = 0
        rbActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                status = 1
            }
        }

        cbAllowLocation.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                startLocationPicker()
            }
        }

        btnSave.setOnClickListener {
            if (etName.text.toString().trim().isEmpty()){
                etName.error = "Enter name"
            } else if (etDonationInfo.text.toString().trim().isEmpty()){
                etDonationInfo.error = "Enter your donation"
            }

            donorDetailViewModel.saveDonorDetails(etName.text.toString(),
                etDonationInfo.text.toString(), status.toString(),
                donorLat.toString(),
                donorLng.toString(),
                readPhoneNum()!!)
        }
    }

    private var observer = Observer<Resource<SaveDonorResponse>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
                //Log.d(TAG, it.data?.response?.userId)
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
            }
            Status.EMPTY, Status.ERROR -> {
                progressBar.visibility = View.GONE
            }
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
                    val postalcode = data.getStringExtra(ZIPCODE)
                }
            }
        }
    }
    companion object {
        private const val TAG = "DonorDetailsActivity"
        private const val MAP_BUTTON_REQUEST_CODE = 1
        private const val DEFAULT_LAT = 28.6429
        private const val DEFAULT_LNG = 77.2191
        private const val INDIA_LOCALE_ZONE = "en_in"
    }
}
