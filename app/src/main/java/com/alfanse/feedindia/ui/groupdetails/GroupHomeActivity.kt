package com.alfanse.feedindia.ui.groupdetails

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedindia.FeedIndiaApplication
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.Resource
import com.alfanse.feedindia.data.Status
import com.alfanse.feedindia.data.models.NearByUsersEntity
import com.alfanse.feedindia.factory.ViewModelFactory
import com.alfanse.feedindia.ui.needier.NeedierListActivity
import com.alfanse.feedindia.utils.UserType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_group_home.*
import javax.inject.Inject

class GroupHomeActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var groupHomeViewModel: GroupHomeViewModel
    private lateinit var googleMap: GoogleMap
    private var lat = 0.0
    private var lng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_home)
        (application as FeedIndiaApplication).appComponent.inject(this)
        groupHomeViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(GroupHomeViewModel::class.java)
        readUserData()
        setUpNavigationDrawer()
        groupHomeViewModel.nearByUsersLiveData.observe(this, observer)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun readUserData(){
        val groupName = intent.getStringExtra(GROUP_NAME_INTENT_EXTRA_KEY)
        title = groupName
        lat = intent.getDoubleExtra(USER_LAT_INTENT_EXTRA_KEY, 0.0)
        lng = intent.getDoubleExtra(USER_LNG_INTENT_EXTRA_KEY, 0.0)
        // Call to get near by users
        getNearByUsers(lat, lng)
    }

    private fun getNearByUsers(lat: Double, lng: Double){
        groupHomeViewModel.getNearByUsers(lat, lng, DISTANCE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.group_home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.invite_member -> //Open invite member screen
            true
            R.id.add_needy -> //Open add needy screen
            true
            R.id.sign_out -> //Call sign out method
            true
            android.R.id.home -> {
                if (layoutDrawer.isDrawerOpen(Gravity.LEFT)){
                    layoutDrawer.closeDrawer(Gravity.LEFT)
                } else {
                    layoutDrawer.openDrawer(Gravity.LEFT)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpNavigationDrawer(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val drawerToggle = object: ActionBarDrawerToggle(this, layoutDrawer,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                invalidateOptionsMenu()
            }
        }

        drawerToggle.isDrawerIndicatorEnabled = true
        layoutDrawer?.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_profile -> {
                //Open profile screen
            }
            R.id.nav_add_member -> {
                //Open add member screen
            }
            R.id.nav_add_needy -> {
                //Open add needy screen
            }
            R.id.nav_needy_list -> {
                startActivity(Intent(this, NeedierListActivity::class.java))
            }
        }
        return true
    }

    private var observer = Observer<Resource<List<NearByUsersEntity>>> {
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

    private fun addMarkersToMap(users: List<NearByUsersEntity>){
        val latlng = LatLng(lat, lng)
        moveCamera(latlng)
        animateCamera(latlng)
        for (user in users){
            if(user.user_type == UserType.DONOR){
                getMarkerOptions(user, R.drawable.map_marker_green)
            } else {
                getMarkerOptions(user, R.drawable.map_marker_orange)
            }
        }
    }

    private fun getMarkerOptions(user: NearByUsersEntity, icon: Int) {
        googleMap.addMarker(
            MarkerOptions().position(LatLng(user.lat.toDouble(), user.lng.toDouble()))
                .title(user.name)
                .snippet(user.mobile)
                .icon(BitmapDescriptorFactory.fromResource(icon))
        )
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
    }

    companion object {
        private const val TAG = "GroupHomeActivity"
        const val GROUP_NAME_INTENT_EXTRA_KEY = "GROUP_NAME_INTENT_EXTRA_KEY"
        const val USER_LAT_INTENT_EXTRA_KEY = "USER_LAT_INTENT_EXTRA_KEY"
        const val USER_LNG_INTENT_EXTRA_KEY = "USER_LNG_INTENT_EXTRA_KEY"
        private const val DISTANCE = 50
    }
}
