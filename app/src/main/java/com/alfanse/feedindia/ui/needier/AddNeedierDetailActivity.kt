package com.alfanse.feedindia.ui.needier

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
import com.alfanse.feedindia.utils.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import kotlinx.android.synthetic.main.activity_needier_details.*
import javax.inject.Inject

class AddNeedierDetailActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var needierDetailsViewModel: NeedierDetailsViewModel
    private var lat = 0.0
    private var lng = 0.0
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLatLng: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_needier_details)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.needier_details_screen_label)

        (application as FeedIndiaApplication).appComponent.inject(this)
        needierDetailsViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(NeedierDetailsViewModel::class.java)
        needierDetailsViewModel.saveNeedierLiveData.observe(this, observer)
        initListener()
    }

    private fun initListener(){
        etAddress.setOnClickListener {
            if(!PermissionUtils.isLocationEnabled(this)){
                PermissionUtils.showGPSNotEnabledDialog(this)
                return@setOnClickListener
            }
            setUpLocationListener()
        }

        btnSave.setOnClickListener {
            when {
                etName.text.toString().trim().isEmpty() -> {
                    etName.error = "Enter name"
                    return@setOnClickListener
                }

                etMobile.text.toString().trim().isEmpty() -> {
                    etMobile.error = "Enter Phone"
                    return@setOnClickListener
                }

                etWhatNeeded.text.toString().trim().isEmpty() -> {
                    etWhatNeeded.error = "Enter needed info"
                    return@setOnClickListener
                }

                etAddress.text.toString().trim().isEmpty() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Please give address",
                        Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            val name = etName.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val whatNeededInfo = etWhatNeeded.text.toString().trim()
            val address = etAddress.text.toString().trim()
            needierDetailsViewModel.saveNeedierDetails(name, mobile, whatNeededInfo,
                lat.toString(), lng.toString(), address)
        }
    }

    private var observer = Observer<Resource<String>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                val intent = Intent(this, NeedierListActivity::class.java)
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

    private fun setUpLocationListener() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
            if (it != null) {
                currentLatLng = LatLng(it.latitude, it.longitude)

                // start map search screen to find address
                startLocationPicker(currentLatLng!!)
            }
        }
    }

    private fun startLocationPicker(latLng: LatLng){
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(latLng.latitude, latLng.longitude)
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
                    lat = data.getDoubleExtra(LATITUDE, 0.0)
                    lng = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    if (address != null){
                        setAddressToView(address)
                    }
                }
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

    private fun setAddressToView(address: String){
        etAddress.setText(address)
    }

    companion object {
        private const val TAG = "NeedierDetailsActivity"
        private const val MAP_BUTTON_REQUEST_CODE = 1
        private const val INDIA_LOCALE_ZONE = "en_in"
    }
}
