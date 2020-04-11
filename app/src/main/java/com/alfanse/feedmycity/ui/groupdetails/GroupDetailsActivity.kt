package com.alfanse.feedmycity.ui.groupdetails

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.mobileauth.CodeVerificationActivity
import com.alfanse.feedmycity.utils.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import kotlinx.android.synthetic.main.activity_group_details.*
import javax.inject.Inject

class GroupDetailsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var groupDetailsViewModel: GroupDetailsViewModel
    private var groupLat = 0.0
    private var groupLng = 0.0
    private var phone = ""
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLatLng: LatLng? = null
    private var geoLocationAddress = ""
    private var groupName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.group_details_screen_label)
        (application as FeedMyCityApplication).appComponent.inject(this)
        groupDetailsViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(GroupDetailsViewModel::class.java)

        groupDetailsViewModel.saveGroupLiveData.observe(this, observer)
        readPhoneNum()
        initListener()
    }

    private fun readPhoneNum() {
        if (intent != null){
            phone = intent.getStringExtra(CodeVerificationActivity.MOBILE_NUM_KEY)!!
        }
    }

    private fun initListener(){
        cbAllowLocationAccess.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                requestPermission()
            }
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            groupName = etGroupInfo.text.toString().trim()
            val registeredAddress = etRegisteredAddress.text.toString().trim()
            when {
                name.isEmpty() -> {
                    etName.error = "Enter name"
                    return@setOnClickListener
                }
                groupName.isEmpty() -> {
                    etGroupInfo.error = "Enter group name"
                    return@setOnClickListener
                }
                registeredAddress.isEmpty() -> {
                    etRegisteredAddress.error = "Enter address"
                    return@setOnClickListener
                }
                geoLocationAddress == "" -> {
                    Snackbar.make(findViewById(android.R.id.content), "Please give your location",
                        Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            groupDetailsViewModel.saveGroupDetails(name, registeredAddress, groupName,
                groupLat.toString(), groupLng.toString(),
                geoLocationAddress, phone, etGvtRegNumber.text.toString().trim())
        }
    }

    private var observer = Observer<Resource<String>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                val intent = Intent(this, GroupHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(findViewById(android.R.id.content), it.message?:"",
                    Snackbar.LENGTH_SHORT).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    private fun requestPermission(){
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.location_permission_not_granted),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setUpLocationListener() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
            if (it != null){
                currentLatLng = LatLng(it.latitude, it.longitude)
                groupLat = it.latitude
                groupLng = it.longitude

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
                    groupLat = data.getDoubleExtra(LATITUDE, 0.0)
                    groupLng = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    if (address != null){
                        geoLocationAddress = address
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

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val TAG = "GroupDetailsActivity"
        private const val INDIA_LOCALE_ZONE = "en_in"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        private const val MAP_BUTTON_REQUEST_CODE = 1
    }
}
