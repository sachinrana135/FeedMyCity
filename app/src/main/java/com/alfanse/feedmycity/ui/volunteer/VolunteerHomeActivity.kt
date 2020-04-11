package com.alfanse.feedmycity.ui.volunteer

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.google.android.gms.location.FusedLocationProviderClient
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer)
        title = getString(R.string.volunteer_screen_label)
        (application as FeedMyCityApplication).appComponent.inject(this)
        volunteerViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(VolunteerViewModel::class.java)
        volunteerViewModel.nearByGroupsLiveData.observe(this, observer)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        requestPermission()
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
                lat = it.latitude
                lng = it.longitude
                getNearByGroups(lat, lng)
            }
        }
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
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
            }
            Status.EMPTY -> {

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
        this.googleMap.isMyLocationEnabled = true
    }

    companion object {
        private const val TAG = "VolunteerActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        private const val DISTANCE = 50
    }
}
