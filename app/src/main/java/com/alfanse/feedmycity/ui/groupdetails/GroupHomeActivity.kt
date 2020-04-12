package com.alfanse.feedmycity.ui.groupdetails

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.data.models.NearByUsersEntity
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.member.MemberListActivity
import com.alfanse.feedmycity.ui.needier.AddNeedierDetailActivity
import com.alfanse.feedmycity.ui.needier.NeedierListActivity
import com.alfanse.feedmycity.ui.profile.GroupProfileActivity
import com.alfanse.feedmycity.ui.usertypes.UserTypesActivity
import com.alfanse.feedmycity.utils.FIREBASE_DYNAMIC_URL
import com.alfanse.feedmycity.utils.PermissionUtils
import com.alfanse.feedmycity.utils.User
import com.alfanse.feedmycity.utils.UserType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
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
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_home)
        title = User.groupName
        (application as FeedMyCityApplication).appComponent.inject(this)
        groupHomeViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(GroupHomeViewModel::class.java)
        groupHomeViewModel.nearByUsersLiveData.observe(this, observer)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setUpNavigationDrawer()
        getNearByUsers()
        if (User.lat != null){
            lat = User.lat!!.toDouble()
        }
        if (User.lng != null){
            lng = User.lng!!.toDouble()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.isLocationEnabled(this)){
            PermissionUtils.showGPSNotEnabledDialog(this)
        }
        requestPermission()
    }

    private fun requestPermission(){
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                if (!PermissionUtils.isLocationEnabled(this)){
                    PermissionUtils.showGPSNotEnabledDialog(this)
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
                    if (!PermissionUtils.isLocationEnabled(this)){
                        PermissionUtils.showGPSNotEnabledDialog(this)
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

    private fun getNearByUsers() {
        groupHomeViewModel.getNearByUsers(DISTANCE, User.groupId!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
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

    private fun shareDynamicLink(){
        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            val builder: Uri.Builder = Uri.Builder()
            link = builder.scheme("https").authority(FIREBASE_DYNAMIC_URL).appendQueryParameter("groupCode", User.groupCode).build()
            domainUriPrefix = FIREBASE_DYNAMIC_URL
        }.addOnSuccessListener { result ->
            // Short link created
            val shortLink = result.shortLink
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            //i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.txt_join_group))
            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.txt_join_group) + " " + shortLink.toString())
            startActivity(Intent.createChooser(i, getString(R.string.txt_share_invite_link)))
        }.addOnFailureListener {
            Snackbar.make(
                findViewById(android.R.id.content),
                it.message.toString(),
                Snackbar.LENGTH_SHORT
            ).show()
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
        viewDrawer.getHeaderView(0).findViewById<TextView>(R.id.tvWelcomeMsg).text =
            "Welcome, ${User.name}"
        viewDrawer.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_profile -> {
                closeDrawer()
                startActivity(Intent(this, GroupProfileActivity::class.java))
            }
            R.id.nav_invite_member -> {
                closeDrawer()
                shareDynamicLink()
            }
            R.id.nav_add_needy -> {
                closeDrawer()
                startActivity(Intent(this, AddNeedierDetailActivity::class.java))
            }
            R.id.nav_needy_list -> {
                closeDrawer()
                startActivity(Intent(this, NeedierListActivity::class.java))
            }
            R.id.nav_member_list -> {
                closeDrawer()
                startActivity(Intent(this, MemberListActivity::class.java))
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
                Snackbar.make(findViewById(android.R.id.content), it.message?:"",
                    Snackbar.LENGTH_SHORT).show()
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
            when(user.user_type){
                UserType.DONOR -> getMarkerOptions(user, R.drawable.map_marker_green)
                UserType.MEMBER -> getMarkerOptions(user, R.drawable.map_marker_orange)
                UserType.NEEDIER -> getMarkerOptions(user, R.drawable.map_marker_red)
            }
        }
    }

    private fun getMarkerOptions(user: NearByUsersEntity, icon: Int) {
        val title = "Name- ${user.name} & " + "Mobile- ${user.mobile}"
        var snippet = ""
        if (user.user_type == UserType.NEEDIER){
            snippet =  "Need Items- ${user.items}"
        } else if (user.user_type == UserType.DONOR){
            snippet = "Donate Items- ${user.items}"
        }
        googleMap.addMarker(
            MarkerOptions().position(LatLng(user.lat.toDouble(), user.lng.toDouble()))
                .title(title)
                .snippet(snippet)
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
        if(PermissionUtils.isAccessFineLocationGranted(mContext)){
            this.googleMap.isMyLocationEnabled = true
        }
    }

    companion object {
        private const val TAG = "GroupHomeActivity"
        private const val DISTANCE = 50
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }
}
