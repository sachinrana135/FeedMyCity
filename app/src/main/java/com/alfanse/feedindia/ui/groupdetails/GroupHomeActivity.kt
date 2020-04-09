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
import com.alfanse.feedindia.ui.member.AddMemberActivity
import com.alfanse.feedindia.ui.needier.NeedierDetailsActivity
import com.alfanse.feedindia.ui.needier.NeedierListActivity
import com.alfanse.feedindia.ui.profile.GroupProfileActivity
import com.alfanse.feedindia.ui.usertypes.UserTypesActivity
import com.alfanse.feedindia.utils.PermissionUtils
import com.alfanse.feedindia.utils.User
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
    private var lat = User.lat?.toDouble()
    private var lng = User.lng?.toDouble()
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_home)
        title = User.groupName
        (application as FeedIndiaApplication).appComponent.inject(this)
        groupHomeViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(GroupHomeViewModel::class.java)
        groupHomeViewModel.nearByUsersLiveData.observe(this, observer)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setUpNavigationDrawer()
        checkLocation()
        getNearByUsers()
    }


    private fun checkLocation(){
        if (!PermissionUtils.isLocationEnabled(this)){
            PermissionUtils.showGPSNotEnabledDialog(this)
        }
    }

    private fun getNearByUsers(){
        groupHomeViewModel.getNearByUsers(DISTANCE)
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
        viewDrawer.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_profile -> {
                closeDrawer()
                startActivity(Intent(this, GroupProfileActivity::class.java))
            }
            R.id.nav_add_member -> {
                closeDrawer()
                startActivity(Intent(this, AddMemberActivity::class.java))
            }
            R.id.nav_add_needy -> {
                closeDrawer()
                startActivity(Intent(this, NeedierDetailsActivity::class.java))
            }
            R.id.nav_needy_list -> {
                closeDrawer()
                startActivity(Intent(this, NeedierListActivity::class.java))
            }
            R.id.nav_sign_out -> {
                closeDrawer()
                groupHomeViewModel.logOutUserLiveData.observe(this, Observer {
                    if (it) {
                        val intent = Intent(mContext, UserTypesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish()
                        startActivity(intent)
                    }
                })
                groupHomeViewModel.signOut()
            }
        }
        return true
    }

    private fun closeDrawer(){
        if(layoutDrawer.isDrawerOpen(Gravity.LEFT)){
            layoutDrawer.closeDrawer(Gravity.LEFT)
        }
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
        val latlng = LatLng(lat!!, lng!!)
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
        private const val DISTANCE = 50
    }
}
