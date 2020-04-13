package com.alfanse.feedmycity.ui.volunteer

import android.content.*
import android.content.pm.PackageManager
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
import com.alfanse.feedmycity.data.models.NearByGroupsEntity
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.utils.PermissionUtils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_group_home.*
import javax.inject.Inject

class VolunteerHomeActivity : AppCompatActivity(), OnMapReadyCallback {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var volunteerViewModel: VolunteerViewModel
    private lateinit var googleMap: GoogleMap
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var locationCallback: LocationCallback? = null
    private var settingsClient: SettingsClient? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private lateinit var locationRequest: LocationRequest
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
        setContentView(R.layout.activity_volunteer)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.volunteer_screen_label)
        (application as FeedMyCityApplication).appComponent.inject(this)
        volunteerViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(VolunteerViewModel::class.java)
        volunteerViewModel.nearByGroupsLiveData.observe(this, observer)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)
        createLocationCallback()
        locationRequest = LocationRequest().apply {
            fastestInterval = 2000
            interval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        buildLocationSettingsRequest()
        registerReceiver(locationProviderBroadcastReceiver,  IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    override fun onStart() {
        super.onStart()
        requestPermission()
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
                            rae.startResolutionForResult(this@VolunteerHomeActivity, REQUEST_CHECK_SETTINGS
                            )
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                        Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lat = locationResult.lastLocation.latitude
                lng = locationResult.lastLocation.longitude
                getNearByGroups(locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude)
            }
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    private fun getNearByGroups(lat: Double, lng: Double){
        volunteerViewModel.getNearByUsers(lat, lng, DISTANCE)
    }

    private var observer = Observer<Resource<List<NearByGroupsEntity>>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                if(it.data != null){
                    addMarkersToMap(it.data)
                }
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(findViewById(android.R.id.content), it.message?:getString(R.string.txt_something_wrong),
                    Snackbar.LENGTH_SHORT).show()
            }
            Status.EMPTY -> {
                progressBar.visibility = View.GONE
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.txt_no_group_found), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarkersToMap(users: List<NearByGroupsEntity>){
        val latlng = LatLng(lat, lng)
        moveCamera(latlng)
        animateCamera(latlng)
        for (user in users){
            googleMap.addMarker(
                MarkerOptions().position(LatLng(user.lat.toDouble(), user.lng.toDouble()))
                    .title(user.name)
                    .snippet(user.mobile)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_orange))
            )
        }
    }

    private fun moveCamera(latLng: LatLng?) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng?) {
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(
                    latLng
                ).zoom(10.5f).build()
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        if(PermissionUtils.isLocationEnabled(this)){
            this.googleMap.isMyLocationEnabled = true
        }
    }

    private fun stopLocationUpdates() {
        if (locationCallback != null){
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        }
    }


    override fun onDestroy() {
        stopLocationUpdates()
        unregisterReceiver(locationProviderBroadcastReceiver)
        super.onDestroy()
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

    companion object {
        private const val TAG = "VolunteerActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        private const val DISTANCE = 50
        private const val REQUEST_CHECK_SETTINGS = 0x1
    }
}
