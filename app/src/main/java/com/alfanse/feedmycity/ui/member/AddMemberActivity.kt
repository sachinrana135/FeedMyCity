package com.alfanse.feedmycity.ui.member

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.data.models.SaveMemberRequest
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.groupdetails.GroupHomeActivity
import com.alfanse.feedmycity.ui.mobileauth.CodeVerificationActivity
import com.alfanse.feedmycity.utils.PermissionUtils
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

class AddMemberActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var addMemberViewModel: AddMemberViewModel
    private var lat = 0.0
    private var lng = 0.0
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLatLng: LatLng? = null
    private lateinit var phone: String
    private var locationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)
        title = getString(R.string.add_member_screen_label)
        (application as FeedMyCityApplication).appComponent.inject(this)
        addMemberViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AddMemberViewModel::class.java)
        addMemberViewModel.addMemberLiveData.observe(this, observer)
        initListener()
        readIntent()
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.isLocationEnabled(this)){
            PermissionUtils.showGPSNotEnabledDialog(this)
        }
    }

    private fun readIntent() {
        if (intent != null){
            phone = intent.getStringExtra(CodeVerificationActivity.MOBILE_NUM_KEY)!!
        }
    }

    private fun initListener() {
        etAddress.setOnClickListener {
            requestPermission()
        }

        btnSave.setOnClickListener {
            when {
                etName.text.toString().trim().isEmpty() -> {
                    etName.error = "Enter name"
                    return@setOnClickListener
                }

                etAddress.text.toString().trim().isEmpty() -> {
                    Snackbar.make(
                        findViewById(android.R.id.content), "Please give address",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            val name = etName.text.toString().trim()
            val mobile = phone
            val groupCode = addMemberViewModel.getGroupId()
            val address = etAddress.text.toString().trim()
            addMemberViewModel.saveMember(
                SaveMemberRequest(
                    name = name, mobile = mobile, groupCode = groupCode!!,
                    lat = lat.toString(), lng = lng.toString(), location_address = address, firebaseId = "")
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
                Snackbar.make(findViewById(android.R.id.content), it.message?:getString(R.string.txt_something_wrong),
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
        grantResults: IntArray) {
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
            // If last location is null after turning on GPS, request location update using callback
            if (it == null || it.accuracy > 100){
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        stopLocationUpdates()
                        if (locationResult != null && locationResult.locations.isNotEmpty()) {
                            val newLocation = locationResult.locations[0]
                            currentLatLng = LatLng(newLocation.latitude, newLocation.longitude)
                            startMapPickerActivity(newLocation)
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Please wait...your location is updating",
                                Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }

                fusedLocationProviderClient!!.requestLocationUpdates(getLocationRequest(),
                    locationCallback, Looper.myLooper())
            } else {
                currentLatLng = LatLng(it.latitude, it.longitude)
                startMapPickerActivity(it)
            }
        }
    }

    private fun startMapPickerActivity(it: Location) {
        lat = it.latitude
        lng = it.longitude

        // start map search screen to find address
        startLocationPicker(currentLatLng!!)
    }


    private fun getLocationRequest(): LocationRequest {
        return LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun stopLocationUpdates(){
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
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
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == 1) {
                if (data != null) {
                    lat = data.getDoubleExtra(LATITUDE, 0.0)
                    lng = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    if (address != null) {
                        setAddressToView(address)
                    }
                }
            }
        }
    }

    private fun setAddressToView(address: String) {
        etAddress.setText(address)
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }
    companion object {
        private const val TAG = "NeedierDetailsActivity"
        private const val MAP_BUTTON_REQUEST_CODE = 1
        private const val INDIA_LOCALE_ZONE = "en_in"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }
}
