package com.alfanse.feedmycity.ui.groupdetails

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
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
    private var locationCallback: LocationCallback? = null
    private var currentLatLng: LatLng? = null
    private var geoLocationAddress = ""
    private var groupName = ""
    private var settingsClient: SettingsClient? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private lateinit var locationRequest: LocationRequest
    private var showAddressSelection = false
    private var gpsActionsDoneOnce = false

    private val locationProviderBroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                LocationManager.PROVIDERS_CHANGED_ACTION -> {
                    if (!gpsActionsDoneOnce) {
                        gpsActionsDoneOnce = true;
                        Handler().postDelayed({
                            gpsActionsDoneOnce = false
                            requestPermission()
                        }, 500)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.group_details_screen_label)
        (application as FeedMyCityApplication).appComponent.inject(this)
        groupDetailsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(GroupDetailsViewModel::class.java)

        groupDetailsViewModel.saveGroupLiveData.observe(this, observer)
        readPhoneNum()
        initListener()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
        registerReceiver(locationProviderBroadcastReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    override fun onResume() {
        super.onResume()
    }

    private fun readPhoneNum() {
        if (intent != null) {
            phone = intent.getStringExtra(CodeVerificationActivity.MOBILE_NUM_KEY)!!
        }
    }

    private fun initListener() {
        etAddress.setOnClickListener {
            showAddressSelection = true
            requestPermission()
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
                etAddress.text.toString().trim().isEmpty()-> {
                    Snackbar.make(
                        findViewById(android.R.id.content), "Please give your location",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            groupDetailsViewModel.saveGroupDetails(
                name, registeredAddress, groupName,
                groupLat.toString(), groupLng.toString(),
                etAddress.text.toString(), phone, etGvtRegNumber.text.toString().trim()
            )
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
                Snackbar.make(
                    findViewById(android.R.id.content),
                    it.message ?: getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    private fun requestPermission() {
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                setUpLocationListener()
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
                    setUpLocationListener()
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
        // Begin by checking if the device has the necessary location settings.
        settingsClient!!.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(this) {
                Log.i(TAG, "All location settings are satisfied.")

                fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback, Looper.myLooper()
                )
            }.addOnFailureListener(this) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        // Location settings are not satisfied. Attempting to upgrade location settings
                        try { // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(this@GroupDetailsActivity, REQUEST_CHECK_SETTINGS)
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            errorMessage,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLatLng = LatLng(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude
                )
                startMapPickerActivity(locationResult.lastLocation)
            }
        }
    }

    private fun startMapPickerActivity(it: Location) {
        stopLocationUpdates()
        groupLat = it.latitude
        groupLng = it.longitude

        // start map search screen to find address
        if (showAddressSelection) {
            showAddressSelection = false
            startLocationPicker(currentLatLng!!)
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    private fun stopLocationUpdates() {
        if (locationCallback != null){
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        }
    }

    private fun startLocationPicker(latLng: LatLng) {
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
        when(requestCode){
            1 -> {
                if (resultCode != Activity.RESULT_CANCELED){
                    if (data != null) {
                        groupLat = data.getDoubleExtra(LATITUDE, 0.0)
                        groupLng = data.getDoubleExtra(LONGITUDE, 0.0)
                        val address = data.getStringExtra(LOCATION_ADDRESS)
                        if (address != null) {
                            etAddress.setText(address)
                        }
                    }
                }
            }

            REQUEST_CHECK_SETTINGS -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {// Nothing to do. startLocationupdates() gets called in onResume again. //
                    }
                    Activity.RESULT_CANCELED ->{
                        Log.i(TAG, "User chose not to make required location settings changes.")
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
        stopLocationUpdates()
        unregisterReceiver(locationProviderBroadcastReceiver)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "GroupDetailsActivity"
        private const val INDIA_LOCALE_ZONE = "en_in"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        private const val MAP_BUTTON_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 0x1
    }
}
